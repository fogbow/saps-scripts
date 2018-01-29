package core;

import model.ImageTask;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import utils.PropertiesConstants;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExperimentRepository implements Repository {

    private static final Logger LOGGER = Logger.getLogger(ExperimentRepository.class);

    private static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    private static final int DEFAULT_READ_TIMEOUT = 300000;

    private final String sapsResultsPath;

    public ExperimentRepository(String sapsResultsPath) {
        this.sapsResultsPath = sapsResultsPath;
    }
    @Override
    public void downloadImage(ImageTask imageData) throws Exception {
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
        ProcessBuilder builder = new ProcessBuilder("touch",
                localImageFilePath + collectionTierName + "_station.csv");
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

    private int downloadInto(ImageTask imageData, String targetFilePath) throws IOException {
        try {
            if(isReachable(imageData.getDownloadLink())){
                FileUtils.copyURLToFile(new URL(imageData.getDownloadLink()),
                        new File(targetFilePath), DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
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

    private boolean createDirectory(String imageDirPath) {
        File imageDir = new File(imageDirPath);
        return imageDir.mkdirs();
    }

    private String imageFilePath(ImageTask imageData, String imageDirPath) {
        return imageDirPath + File.separator + imageData.getName() + ".tar.gz";
    }

    private static boolean isReachable(String URLName) throws IOException {
        boolean result;
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
}
