package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import core.ExperimentRepository;
import core.Repository;
import org.apache.log4j.Logger;

import model.ImageTask;
import utils.MetadataUtilImpl;
import utils.PropertiesConstants;

public class USGSController {

	private static final Logger LOGGER = Logger.getLogger(USGSController.class);

	private static final String IMAGE_NAME = "LT05_L1TP_215066_19921114_20170121_01_T1";
	private static final String REPO_URL = "http://www2.lsd.ufcg.edu.br/~lucas.farias/";

	private Repository repository;
	private ImageTask imageTask;
	private Properties properties;

	public USGSController(String dataSet, String region, String date, String pathStorage,
			String pathMetadata) {
		properties = loadProperties();
		properties.setProperty(PropertiesConstants.SAPS_RESULTS_PATH, pathStorage);
		properties.setProperty(PropertiesConstants.SAPS_METADATA_PATH, pathMetadata);
		setRepository(new ExperimentRepository(pathStorage));
		String parsedDataset = formatDataSet(dataSet);
		setImageTask(new ImageTask(IMAGE_NAME, parsedDataset, region, date));
		imageTask.setDownloadLink(REPO_URL + IMAGE_NAME);
	}

	/**
	 * Other errors this program can handle: Status 28: Means an error while
	 * downloading, operation timeout. Check conditions in PropertiesConstants
	 * (SPEED_LIMIT and SPEED_TIME)
	 *
	 */
	public void startDownload() {
		try {
			repository.downloadImage(imageTask);
		} catch (MalformedURLException e) {
			/**
			 * Tried to make download but a Malformed URL was given
			 */
			LOGGER.error("Error while downloading image", e);
			System.exit(3);
		} catch (IOException e) {
			/**
			 * Tried to make download but URL is not Reachable, or Tried to create a
			 * file/directory but got an error. Check logs
			 */
			LOGGER.error("Error while downloading image", e);
			System.exit(4);
		} catch (Exception e) {
			/**
			 * Tried to make download but had an error with Process Builder command
			 */
			LOGGER.error("Error while downloading image", e);
			System.exit(5);
		}
	}

	public void saveMetadata() {
		LOGGER.info("Starting to generate metadata file");

		String resultsDirPath = properties.getProperty(PropertiesConstants.SAPS_RESULTS_PATH);
		String metadataFilePath = properties.getProperty(PropertiesConstants.SAPS_METADATA_PATH)
				+ File.separator + "inputDescription.txt";

		MetadataUtilImpl metadataUtilImpl = new MetadataUtilImpl();
		try {
			metadataUtilImpl.writeMetadata(resultsDirPath, new File(metadataFilePath));
		} catch (Exception e) {
			/**
			 * Tried to generate metadata file but had an error while doing it
			 */
			LOGGER.error("Error while writing metadata file", e);
			System.exit(7);
		}
	}

	public String getImageName(String dataSet, String region, String date) {
		String imageName = null;
		try {
			imageName = IMAGE_NAME;
		} catch (Exception e) {
			/**
			 * Tried to make download but a Malformed URL was given
			 */
			LOGGER.error("Not found the Image in the USGS Repository", e);
			System.exit(3);
		}
		return imageName;
	}
	
	private String formatDataSet(String dataset) {
		if (dataset.equals(PropertiesConstants.DATASET_LT5_TYPE)) {
			return PropertiesConstants.LANDSAT_5_DATASET;
		} else if (dataset.equals(PropertiesConstants.DATASET_LE7_TYPE)) {
			return PropertiesConstants.LANDSAT_7_DATASET;
		} else {
			return PropertiesConstants.LANDSAT_8_DATASET;
		}
	}

	public Repository getUsgsRepository() {
		return repository;
	}

	public void setRepository(Repository usgsRepository) {
		this.repository = usgsRepository;
	}

	public ImageTask getImageTask() {
		return imageTask;
	}

	public void setImageTask(ImageTask imageTask) {
		this.imageTask = imageTask;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public static Properties loadProperties() {
		Properties props = new Properties();
		FileInputStream input;
		try {
			input = new FileInputStream(
					System.getProperty("user.dir") + File.separator + "config/sebal.conf");
			props.load(input);
		} catch (FileNotFoundException e) {
			LOGGER.error("Error while reading conf file", e);
		} catch (IOException e) {
			LOGGER.error("Error while loading properties", e);
		}
		return props;
	}
}
