package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.ImageTask;
import utils.PropertiesConstants;

public class USGSNasaRepository implements Repository {

    private final String sapsResultsPath;

    private final String usgsJsonUrl;
    private final String usgsUserName;
    private final String usgsPassword;
    private String usgsAPIKey;

    /*
      the number of milliseconds until this method will timeout if no connection could be established to the source
     */
    private int downloadConnectionTimeout;
    /*
      the number of milliseconds until this method will timeout if no data could be read from the source
     */
    private int downloadReadTimeout;

    // nodes
    private static final String EARTH_EXPLORER_NODE = "EE";
    // products
    private static final String LEVEL_1_PRODUCT = "STANDARD";

    // conf constants
    private static final String USGS_SEARCH_VERSION = "1.4.0";
    private static final String FIRST_YEAR_SUFFIX = "-01-01";
    private static final String LAST_YEAR_SUFFIX = "-12-31";
    private static final int MAX_RESULTS = 50000;

    private static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    private static final int DEFAULT_READ_TIMEOUT = 300000;

    private static final Logger LOGGER = Logger.getLogger(USGSNasaRepository.class);

    public USGSNasaRepository(Properties properties) {
		this(properties.getProperty(PropertiesConstants.SAPS_RESULTS_PATH),
				properties.getProperty(PropertiesConstants.SAPS_METADATA_PATH),
				properties.getProperty(PropertiesConstants.USGS_JSON_URL),
				properties.getProperty(PropertiesConstants.USGS_USERNAME),
				properties.getProperty(PropertiesConstants.USGS_PASSWORD),
                properties);
    }

	public USGSNasaRepository(String sapsResultsPath, String sapsMetadataPath,
			Properties properties) {
        this(sapsResultsPath, sapsMetadataPath,
				properties.getProperty(PropertiesConstants.USGS_JSON_URL),
				properties.getProperty(PropertiesConstants.USGS_USERNAME),
				properties.getProperty(PropertiesConstants.USGS_PASSWORD),
                properties);
    }

	protected USGSNasaRepository(String sapsResultsPath, String sapsMetadataPath,
			String usgsJsonUrl, String usgsUserName, String usgsPassword, Properties properties) {

        Validate.notNull(usgsJsonUrl, "usgsJsonUrl cannot be null");
        Validate.notNull(usgsUserName, "usgsUserName cannot be null");
        Validate.notNull(sapsResultsPath, "sebalResultsPath cannot be null");
        Validate.notNull(sapsMetadataPath, "sebalMetadataPath cannot be null");
        Validate.notNull(usgsPassword, "usgsPassword cannot be null");

        this.sapsResultsPath = sapsResultsPath;
        this.usgsJsonUrl = usgsJsonUrl;
        this.usgsUserName = usgsUserName;
        this.usgsPassword = usgsPassword;

        if(properties.getProperty(PropertiesConstants.CONNECTION_TIMEOUT) == null ||
                properties.getProperty(PropertiesConstants.CONNECTION_TIMEOUT).isEmpty()){
            this.downloadConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        } else {
            this.downloadConnectionTimeout = Integer.parseInt(properties.getProperty(
                    PropertiesConstants.CONNECTION_TIMEOUT));
        }

        if(properties.getProperty(PropertiesConstants.READ_TIMEOUT) == null ||
                properties.getProperty(PropertiesConstants.READ_TIMEOUT).isEmpty()){
            this.downloadReadTimeout = DEFAULT_READ_TIMEOUT;
        } else {
            this.downloadReadTimeout = Integer.parseInt(properties.getProperty(
                    PropertiesConstants.READ_TIMEOUT));
        }

        createDirectory(sapsResultsPath);
        createDirectory(sapsMetadataPath);
    }

    public void handleAPIKeyUpdate() throws InterruptedException {
        LOGGER.debug("Turning on handle USGS API key update.");
        setUSGSAPIKey(generateAPIKey());
    }

    protected String generateAPIKey() {
        LOGGER.debug("Trying to generate USGS API key.");
        try {
            String response = getLoginResponse();
            JSONObject apiKeyRequestResponse = new JSONObject(response);

            return apiKeyRequestResponse.getString(PropertiesConstants.DATA_JSON_KEY);
        } catch (Throwable e) {
            LOGGER.error("Error while generating USGS API key", e);
        }

        return null;
    }

    protected String getLoginResponse() {
        LOGGER.debug("Creating Json for USGS API key request.");
        JSONObject loginJSONObj = new JSONObject();
        try {
            LOGGER.debug("Try to create Json for USGS API key request.");
            loginJSONObj.put(PropertiesConstants.USERNAME_JSON_KEY, usgsUserName);
            loginJSONObj.put(PropertiesConstants.PASSWORD_JSON_KEY, usgsPassword);
            loginJSONObj.put(PropertiesConstants.AUTH_TYPE_JSON_KEY,
                    PropertiesConstants.EROS_JSON_VALUE);
        } catch (JSONException e) {
            LOGGER.error("Error while formatting login JSON", e);
            return null;
        }
        LOGGER.debug("Json for USGS API key created.");

        String loginJsonRequest = "jsonRequest=" + loginJSONObj.toString();
        ProcessBuilder builder = new ProcessBuilder("curl", "-X", "POST", "--data",
                loginJsonRequest, usgsJsonUrl + File.separator + "login");
        LOGGER.debug("Command=" + builder.command());

        return executeProcess(builder);
    }

    private String getProcessOutput(Process p) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }

    protected boolean directoryExists(String path) {
        File f = new File(path);
        return (f.exists() && f.isDirectory());
    }

    @Override
    public void downloadImage(ImageTask imageData) throws IOException, InterruptedException {
        createDirectory(sapsResultsPath);
        File file = new File(sapsResultsPath);
        if (file.exists()) {
            System.setProperty("https.protocols", "TLSv1.2");
            String localImageFilePath = imageFilePath(imageData, sapsResultsPath);

            // clean if already exists (garbage collection)
            File localImageFile = new File(localImageFilePath);
            if (localImageFile.exists()) {
                LOGGER.info("File " + localImageFilePath
                        + " already exists. Will be removed before repeating download");
                localImageFile.delete();
            }

            LOGGER.info("Downloading image " + imageData.getName() + " into file "
                    + localImageFilePath);
            int downloadExitValue = downloadInto(imageData, localImageFilePath);
            if (downloadExitValue != 0){
                System.exit(downloadExitValue);
            }
            unpackTargz(localImageFilePath);
            localImageFile.delete();
            String collectionTierName = getCollectionTierName();
            runGetStationData(collectionTierName, sapsResultsPath);
        } else {
            throw new IOException("An error occurred while creating " + sapsResultsPath + " directory");
        }
    }

    private void runGetStationData(String collectionTierName, String localImageFilePath) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder(PropertiesConstants.GET_STATIONS_SCRIPT_PATH,
				collectionTierName, localImageFilePath);
        LOGGER.info("Starting get station data script.");
        LOGGER.info("Executing process: " + builder.command());
        try {
            Process p = builder.start();
            p.waitFor();
            LOGGER.debug("ProcessOutput=" + p.exitValue());
        } catch (Exception e) {
            LOGGER.error("Error while executing get station data script.", e);
            throw e;
        }
    }
    
    private String getCollectionTierName() {
        File imagesDir = new File(sapsResultsPath);
        for(File file: imagesDir.listFiles()){
            String patternString = "_MTL.txt";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.find()){
                return file.getName().replace(patternString, "");
            }
        }
        return "";
    }

    private void unpackTargz(String localImageFilePath) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("tar", "-xzf", localImageFilePath, "-C", sapsResultsPath);
        LOGGER.info("Started to unpack file: " + localImageFilePath);
        try {
            Process p = builder.start();
            p.waitFor();
            LOGGER.debug("ProcessOutput=" + p.exitValue());
        } catch (Exception e) {
            LOGGER.error("Error while unpacking file " + localImageFilePath, e);
            throw e;
        }
    }

    protected String imageFilePath(ImageTask imageData, String imageDirPath) {
        return imageDirPath + File.separator + imageData.getName() + ".tar.gz";
    }

    protected String resultsMetadataDirPath(ImageTask imageData) {
        return sapsResultsPath + File.separator + "metadata" + File.separator
                + imageData.getName();
    }

    private int downloadInto(ImageTask imageData, String targetFilePath) throws IOException {
        try {
            if(isReachable(imageData.getDownloadLink())){
                FileUtils.copyURLToFile(new URL(imageData.getDownloadLink()),
                        new File(targetFilePath), downloadConnectionTimeout, downloadReadTimeout);
            }else{
                LOGGER.info("The given URL: " + imageData.getDownloadLink() + " is not reachable.");
                return 5;
            }
        } catch (IOException e) {
            LOGGER.info("The given URL: " + imageData.getDownloadLink() + " is not valid.");
            throw e;
        }
        return 0;
    }

    public static boolean isReachable(String URLName) throws IOException {
        boolean result = false;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.connect();
            result = con.getResponseCode() >= 200 && con.getResponseCode() < 400;
        } catch (MalformedURLException e){
            LOGGER.error("The given URL: " + URLName + " is not valid.");
            throw new MalformedURLException("The given URL: " + URLName + " is not valid.");
        } catch (UnknownHostException e){
            LOGGER.error("The DNS could not find the following URL: " + URLName);
            throw new UnknownHostException("The DNS could not find the following URL: " + URLName);
        }
        return result;
    }

    protected boolean createDirectory(String imageDirPath) {
        File imageDir = new File(imageDirPath);
        return imageDir.mkdirs();
    }

    public String getImageDownloadLink(String imageName) throws Exception {
        if (usgsAPIKey != null && !usgsAPIKey.isEmpty()) {
            String link = doGetDownloadLink(imageName);
            if (link != null && !link.isEmpty()) {
                return link;
            }
        } else {
            LOGGER.error("USGS API key invalid");
        }

        return new String();
    }

    protected String doGetDownloadLink(String imageName) throws Exception {
        String link = null;
        link = usgsDownloadURL(getDataSet(imageName), imageName, EARTH_EXPLORER_NODE,
                LEVEL_1_PRODUCT);

        if (link != null && !link.isEmpty()) {
            return link;
        }

        return null;
    }

    protected String getMetadataHttpResponse(String dataset, String sceneId, String node,
                                             String product) {

        JSONObject metadataJSONObj = new JSONObject();
        try {
            formatDownloadJSON(dataset, sceneId, node, product, metadataJSONObj);
        } catch (JSONException e) {
            LOGGER.error("Error while formatting metadata JSON", e);
            return null;
        }

        String metadataJsonRequest = "jsonRequest=" + metadataJSONObj.toString();
        ProcessBuilder builder = new ProcessBuilder("curl", "-X", "POST", "--data",
                metadataJsonRequest, usgsJsonUrl + File.separator + "metadata");
        LOGGER.debug("Command=" + builder.command());
        return executeProcess(builder);
    }

    private void formatDownloadJSON(String dataset, String sceneId, String node, String product,
                                    JSONObject downloadJSONObj) throws JSONException {
        JSONArray entityIDs = new JSONArray();
        JSONArray products = new JSONArray();
        entityIDs.put(sceneId);
        products.put(product);

        downloadJSONObj.put(PropertiesConstants.DATASET_NAME_JSON_KEY, dataset);
        downloadJSONObj.put(PropertiesConstants.API_KEY_JSON_KEY, usgsAPIKey);
        downloadJSONObj.put(PropertiesConstants.NODE_JSON_KEY, node);
        downloadJSONObj.put(PropertiesConstants.ENTITY_IDS_JSON_KEY, entityIDs);
        downloadJSONObj.put(PropertiesConstants.PRODUCTS_JSON_KEY, products);
    }

    public List<String> getPossibleStations() {
        List<String> possibleStations = new ArrayList<String>();

        try {
            File file = new File(PropertiesConstants.POSSIBLE_STATIONS_FILE_PATH);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                possibleStations.add(line);
            }
            fileReader.close();
        } catch (IOException e) {
            LOGGER.error("Error while getting possible stations from file", e);
        }

        return possibleStations;
    }

    private String getDataSet(String imageName) {
        if (imageName.startsWith(PropertiesConstants.LANDSAT_5_PREFIX)) {
            return PropertiesConstants.LANDSAT_5_DATASET;
        } else if (imageName.startsWith(PropertiesConstants.LANDSAT_7_PREFIX)) {
            return PropertiesConstants.LANDSAT_7_DATASET;
        } else if (imageName.startsWith(PropertiesConstants.LANDSAT_8_PREFIX)) {
            return PropertiesConstants.LANDSAT_8_DATASET;
        }

        return null;
    }

    private String usgsDownloadURL(String dataset, String sceneId, String node, String product) throws Exception {
        // GET DOWNLOAD LINKS
        String response = getDownloadHttpResponse(dataset, sceneId, node, product);

        try {
            JSONObject downloadRequestResponse = new JSONObject(response);
            //if error code == null

            JSONArray downloadLinkArray = downloadRequestResponse.optJSONArray(
                    PropertiesConstants.DATA_JSON_KEY);

            if(downloadLinkArray.length() > 0) {
                String downloadLink = downloadLinkArray.getString(0).replace("\\/", "/");
                downloadLink = downloadLink.replace("[", "");
                downloadLink = downloadLink.replace("]", "");
                downloadLink = downloadLink.replace("\"", "");

                LOGGER.debug("downloadLink=" + downloadLink);
                if (downloadLink != null && !downloadLink.isEmpty() && !downloadLink.equals("[]")) {
                    LOGGER.debug("Image " + sceneId + "download link" + downloadLink + " obtained");
                    return downloadLink;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while formating request response", e);
            throw e;
        }

        return null;
    }

    protected String getDownloadHttpResponse(String dataset, String sceneId, String node,
                                   String product) {

        JSONObject downloadJSONObj = new JSONObject();
        try {
            formatDownloadJSON(dataset, sceneId, node, product, downloadJSONObj);
        } catch (JSONException e) {
            LOGGER.error("Error while formatting download JSON", e);
            return null;
        }

        String downloadJsonRequest = "jsonRequest=" + downloadJSONObj.toString();
        ProcessBuilder builder = new ProcessBuilder("curl", "-X", "POST", "--data",
                downloadJsonRequest, usgsJsonUrl + File.separator + "download");
        LOGGER.debug("Command=" + builder.command());
        return executeProcess(builder);
    }

    private void setUSGSAPIKey(String usgsAPIKey) {
        this.usgsAPIKey = usgsAPIKey;
    }

    public String getUSGSAPIKey() {
        return this.usgsAPIKey;
    }

    public JSONArray getAvailableImagesInRange(String dataSet, int firstYear, int lastYear,
                                               String region) {
        String latitude;
        String longitude;

        try {
            JSONObject regionJSON = getRegionJSON(region);
            latitude = regionJSON.getString(PropertiesConstants.LATITUDE_JSON_KEY);
            longitude = regionJSON.getString(PropertiesConstants.LONGITUDE_JSON_KEY);
        } catch (JSONException e) {
            LOGGER.error("Error while getting coordinates from region JSON", e);
            return null;
        }

        return searchForImagesInRange(dataSet, firstYear, lastYear, latitude, longitude);
    }

    private JSONObject getRegionJSON(String region) throws JSONException {
        String jsonData = readFile(PropertiesConstants.TILES_COORDINATES_FILE_PATH);
        JSONObject regionsJSON = new JSONObject(jsonData);
        JSONArray tiles = regionsJSON.getJSONArray(PropertiesConstants.TILES_JSON_KEY);
        for (int i = 0; i < tiles.length(); i++) {
            if (tiles.getJSONObject(i).getString(PropertiesConstants.TILE_ID_JSON_KEY)
                    .equals(region)) {
                return tiles.getJSONObject(i);
            }
        }

        return null;
    }

    private static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
            br.close();
        } catch (Exception e) {
            LOGGER.error("Error while reading regions JSON file", e);
        }

        return result;
    }

    private JSONArray searchForImagesInRange(String dataset, int firstYear, int lastYear,
                                             String latitude, String longitude) {

        JSONObject searchJSONObj = new JSONObject();
        try {
            formatSearchJSON(dataset, firstYear, lastYear, latitude, longitude, searchJSONObj);
        } catch (JSONException e) {
            LOGGER.error("Error while formatting search JSON", e);
            return null;
        }

        String searchJsonRequest = "jsonRequest=" + searchJSONObj.toString();
        ProcessBuilder builder = new ProcessBuilder("curl", "-X", "POST", "--data",
                searchJsonRequest, usgsJsonUrl + File.separator + "v" + File.separator
                + USGS_SEARCH_VERSION + File.separator + "search");
        LOGGER.debug("Command=" + builder.command());

        try {
            Process p = builder.start();
            p.waitFor();
            JSONObject searchResponse = new JSONObject(getProcessOutput(p));
            return searchResponse.getJSONObject(PropertiesConstants.DATA_JSON_KEY)
                    .getJSONArray(PropertiesConstants.RESULTS_JSON_KEY);
        } catch (Exception e) {
            LOGGER.error("Error while logging in USGS", e);
        }

        return null;
    }

    private void formatSearchJSON(String dataset, int firstYear, int lastYear, String latitude,
                                  String longitude, JSONObject searchJSONObj) throws JSONException {
        JSONObject spatialFilterObj = new JSONObject();
        JSONObject temporalFilterObj = new JSONObject();
        JSONObject lowerLeftObj = new JSONObject();
        JSONObject upperRightObj = new JSONObject();

        lowerLeftObj.put(PropertiesConstants.LATITUDE_JSON_KEY, latitude);
        lowerLeftObj.put(PropertiesConstants.LONGITUDE_JSON_KEY, longitude);
        upperRightObj.put(PropertiesConstants.LATITUDE_JSON_KEY, latitude);
        upperRightObj.put(PropertiesConstants.LONGITUDE_JSON_KEY, longitude);

        spatialFilterObj.put(PropertiesConstants.FILTER_TYPE_JSON_KEY,
                PropertiesConstants.MBR_JSON_VALUE);
        spatialFilterObj.put(PropertiesConstants.LOWER_LEFT_JSON_KEY, lowerLeftObj);
        spatialFilterObj.put(PropertiesConstants.UPPER_RIGHT_JSON_KEY, upperRightObj);

        temporalFilterObj.put(PropertiesConstants.DATE_FIELD_JSON_KEY,
                PropertiesConstants.SEARCH_DATE_JSON_VALUE);
        temporalFilterObj.put(PropertiesConstants.START_DATE_JSON_KEY, firstYear
                + FIRST_YEAR_SUFFIX);
        temporalFilterObj.put(PropertiesConstants.END_DATE_JSON_KEY, lastYear
                + LAST_YEAR_SUFFIX);

        searchJSONObj.put(PropertiesConstants.API_KEY_JSON_KEY, usgsAPIKey);
        searchJSONObj.put(PropertiesConstants.DATASET_NAME_JSON_KEY, dataset);
        searchJSONObj.put(PropertiesConstants.SPATIAL_FILTER_JSON_KEY, spatialFilterObj);
        searchJSONObj.put(PropertiesConstants.TEMPORAL_FILTER_JSON_KEY, temporalFilterObj);
        searchJSONObj.put(PropertiesConstants.MAX_RESULTS_JSON_KEY, MAX_RESULTS);
        searchJSONObj.put(PropertiesConstants.SORT_ORDER_JSON_KEY,
                PropertiesConstants.ASC_JSON_VALUE);
    }


    private String executeProcess(ProcessBuilder builder){
        try {
            Process p = builder.start();
            p.waitFor();
            return getProcessOutput(p);
        } catch (Exception e) {
            LOGGER.error("Error while logging in USGS", e);
        }
        return new String();
    }
}
