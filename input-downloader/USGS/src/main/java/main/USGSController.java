package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import core.USGSNasaRepository;
import model.ImageTask;
import utils.MetadataUtilImpl;
import utils.PropertiesConstants;

public class USGSController {

	static final Logger LOGGER = Logger.getLogger(USGSController.class);

	private USGSNasaRepository usgsRepository;
	private ImageTask imageTask;
	private Properties properties;

	public USGSController(String dataSet, String region, String date, String pathStorage,
			String pathMetadata) throws Exception {
		properties = loadProperties();
		setUsgsRepository(new USGSNasaRepository(pathStorage, pathMetadata, properties));
		usgsRepository.handleAPIKeyUpdate();
		setImageTask(new ImageTask(dataSet, region, date));
		imageTask.setDownloadLink(usgsRepository.getImageDownloadLink(imageTask.getName()));
	}

	/**
	 * Other errors this program can handle: Status 28: Means an error while
	 * downloading, operation timeout. Check conditions in PropertiesConstants
	 * (SPEED_LIMIT and SPEED_TIME)
	 *
	 */
	public void startDownload() {
		try {
			usgsRepository.downloadImage(imageTask);
		} catch (MalformedURLException e) {
			/**
			 * Tried to make download but a Malformed URL was given
			 */
			System.exit(3);
		} catch (IOException e) {
			/**
			 * Tried to make download but URL is not Reachable, or Tried to create a
			 * file/directory but got an error. Check logs
			 */
			System.exit(4);
		} catch (Exception e) {
			/**
			 * Tried to make download but had an error with Process Builder command
			 */
			System.exit(5);
		}
	}

	public void saveMetadata() {
		String resultsDirPath = properties.getProperty(PropertiesConstants.SAPS_RESULTS_PATH);
		String metadataDirPath = properties.getProperty(PropertiesConstants.SAPS_METADATA_PATH);
		String usgsAPIUrl = properties.getProperty(PropertiesConstants.USGS_JSON_URL);
		String noaaFTPServerUrl = properties.getProperty(PropertiesConstants.NOAA_FTP_SERVER_URL);
		String elevationUrl = properties.getProperty(PropertiesConstants.ELEVATION_ACQUIRE_URL);
		String shapefileUrl = properties.getProperty(PropertiesConstants.SHAPEFILE_ACQUIRE_URL);

		MetadataUtilImpl metadataUtilImpl = new MetadataUtilImpl();
		Properties metadataProperties = null;

		try {
			metadataProperties = metadataUtilImpl.generateMetadata(imageTask, resultsDirPath,
					usgsAPIUrl, noaaFTPServerUrl, elevationUrl, shapefileUrl);
		} catch (Exception e) {
			/**
			 * Tried to generate metadata properties but had an error while doing it
			 */
			System.exit(7);
		}

		try {
			metadataUtilImpl.writeMetadata(metadataProperties, new File(metadataDirPath));
		} catch (Exception e) {
			/**
			 * Tried to write metadata file but had an error while doing it
			 */
			System.exit(8);
		}
	}

	public USGSNasaRepository getUsgsRepository() {
		return usgsRepository;
	}

	public void setUsgsRepository(USGSNasaRepository usgsRepository) {
		this.usgsRepository = usgsRepository;
	}

	public ImageTask getImageTask() {
		return imageTask;
	}

	public void setImageTask(ImageTask imageTask) {
		this.imageTask = imageTask;
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
