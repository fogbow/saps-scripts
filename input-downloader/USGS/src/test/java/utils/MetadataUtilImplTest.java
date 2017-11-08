package utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import model.ImageTask;

public class MetadataUtilImplTest {

	private Properties properties;
	private String imageName;
	private String region;
	private String dataset;
	private String imageDate;
	private String usgsAPIUrl;
	private String noaaFTPServerUrl;
	private String elevationUrl;
	private String shapefileUrl;
	private static File inputDir;
	private static File metadataDir;
	private static File bandOneFile;
	private static File bandTwoFile;
	private static File mtlFile;
	private static File gcpFile;
	private static File readmeFile;
	private static File stationFile;

	@Before
	public void setUp() throws IOException {
		// Create temporary inputDir and metadataDir folders
		inputDir = new File("results");
		if (!inputDir.exists()) {
			inputDir.mkdir();
		}

		metadataDir = new File("metadata");
		if (!metadataDir.exists()) {
			metadataDir.mkdir();
		}

		// Create temporary input files
		bandOneFile = new File(inputDir.getAbsolutePath() + File.separator + "test_B1.TIF");
		bandTwoFile = new File(inputDir.getAbsolutePath() + File.separator + "test_B2.TIF");
		mtlFile = new File(inputDir.getAbsolutePath() + File.separator + "test_MTL.txt");
		gcpFile = new File(inputDir.getAbsolutePath() + File.separator + "test_GCP.txt");
		readmeFile = new File(inputDir.getAbsolutePath() + File.separator + "README.txt");
		stationFile = new File(inputDir.getAbsolutePath() + File.separator + "test_station.csv");

		bandOneFile.createNewFile();
		bandTwoFile.createNewFile();
		mtlFile.createNewFile();
		gcpFile.createNewFile();
		readmeFile.createNewFile();
		stationFile.createNewFile();

		// Create base properties
		properties = new Properties();
		properties.put(PropertiesConstants.SAPS_RESULTS_PATH, inputDir.getAbsolutePath());
		properties.put(PropertiesConstants.SAPS_METADATA_PATH, metadataDir.getAbsolutePath());
		properties.put(PropertiesConstants.USGS_JSON_URL, "usgs-json-url");
		properties.put(PropertiesConstants.USGS_USERNAME, "usgs-username");
		properties.put(PropertiesConstants.USGS_PASSWORD, "usgs-password");
		properties.put(PropertiesConstants.NOAA_FTP_SERVER_URL, "noaa-ftp-url");
		properties.put(PropertiesConstants.ELEVATION_ACQUIRE_URL, "elevation-url");
		properties.put(PropertiesConstants.SHAPEFILE_ACQUIRE_URL, "shapefile-url");

		// Fake data
		imageName = "image-name";
		region = "image-region";
		dataset = "image-dataset";
		imageDate = "image-date";
		usgsAPIUrl = "image-usgs-url";
		noaaFTPServerUrl = "image-noaa-ftp-url";
		elevationUrl = "image-elevation-url";
		shapefileUrl = "image-shapefile-url";
	}

	@Test
	public void testGenerateMetadata() throws Exception {
		// Expected metadataProperties
		Properties metadataProperties = new Properties();
		metadataProperties.put(PropertiesConstants.METADATA_SCENE_ID, imageName);
		metadataProperties.put(PropertiesConstants.METADATA_REGION, region);
		metadataProperties.put(PropertiesConstants.METADATA_DATASET, dataset);
		metadataProperties.put(PropertiesConstants.METADATA_IMAGE_DATE, imageDate);
		metadataProperties.put(PropertiesConstants.METADATA_USGS_API_URL, usgsAPIUrl);
		metadataProperties.put(PropertiesConstants.METADATA_NOAA_FTP_URL, noaaFTPServerUrl);
		metadataProperties.put(PropertiesConstants.METADATA_ELEVATION_ACQUIRE_URL, elevationUrl);
		metadataProperties.put(PropertiesConstants.METADATA_SHAPEFILE_ACQUIRE_URL, shapefileUrl);

		metadataProperties.put("scene_band_1_file_path", bandOneFile.getAbsolutePath());
		metadataProperties.put("scene_band_2_file_path", bandTwoFile.getAbsolutePath());
		metadataProperties.put(PropertiesConstants.METADATA_SCENE_MTL_FILE_PATH,
				mtlFile.getAbsolutePath());
		metadataProperties.put(PropertiesConstants.METADATA_SCENE_GCP_FILE_PATH,
				gcpFile.getAbsolutePath());
		metadataProperties.put(PropertiesConstants.METADATA_SCENE_README_FILE_PATH,
				readmeFile.getAbsolutePath());
		metadataProperties.put(PropertiesConstants.METADATA_SCENE_STATION_FILE_PATH,
				stationFile.getAbsolutePath());

		ImageTask imageTask = new ImageTask(imageName, dataset, region, imageDate);
		MetadataUtilImpl metadataUtilImpl = new MetadataUtilImpl();

		// Exercise
		Properties acutalMetadata = metadataUtilImpl.generateMetadata(imageTask,
				inputDir.getAbsolutePath(), usgsAPIUrl, noaaFTPServerUrl, elevationUrl,
				shapefileUrl);

		// Expect
		Assert.assertEquals(metadataProperties, acutalMetadata);
	}

	@After
	public void teardown() throws IOException {
		if (inputDir.exists()) {
			FileUtils.deleteDirectory(inputDir);
		}

		if (metadataDir.exists()) {
			FileUtils.deleteDirectory(metadataDir);
		}
	}
}
