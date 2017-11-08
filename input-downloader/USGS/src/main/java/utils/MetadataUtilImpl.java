package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import model.ImageTask;

public class MetadataUtilImpl implements MetadataUtil {

	static final Logger LOGGER = Logger.getLogger(MetadataUtilImplTest.class);

	@Override
	public Properties generateMetadata(ImageTask imageTask, String inputDirPath, String usgsAPIUrl,
			String noaaFTPServerUrl, String elevationUrl, String shapefileUrl) {
		Properties metadataProperties = new Properties();
		metadataProperties.put(PropertiesConstants.METADATA_SCENE_ID, imageTask.getName());
		metadataProperties.put(PropertiesConstants.METADATA_REGION, imageTask.getRegion());
		metadataProperties.put(PropertiesConstants.METADATA_DATASET, imageTask.getDataSet());
		metadataProperties.put(PropertiesConstants.METADATA_IMAGE_DATE, imageTask.getDate());
		metadataProperties.put(PropertiesConstants.METADATA_USGS_API_URL, usgsAPIUrl);
		metadataProperties.put(PropertiesConstants.METADATA_NOAA_FTP_URL, noaaFTPServerUrl);
		metadataProperties.put(PropertiesConstants.METADATA_ELEVATION_ACQUIRE_URL, elevationUrl);
		metadataProperties.put(PropertiesConstants.METADATA_SHAPEFILE_ACQUIRE_URL, shapefileUrl);

		putAcquiredFilePaths(metadataProperties, inputDirPath);

		LOGGER.info("Metadata properties generated");
		return metadataProperties;
	}

	private void putAcquiredFilePaths(Properties metadataProperties, String inputDirPath) {
		File inputDir = new File(inputDirPath);
		for (File inputFile : inputDir.listFiles()) {
			checkBandAndPutInProperties(metadataProperties, inputFile);
			checkMTLAndPutInProperties(metadataProperties, inputFile);
			checkGCPAndPutInProperties(metadataProperties, inputFile);
			checkREADMEAndPutInProperties(metadataProperties, inputFile);
			checkStationAndPutInProperties(metadataProperties, inputFile);
		}
	}

	protected void checkBandAndPutInProperties(Properties metadataProperties, File inputFile) {
		for (int i = 1; i <= 11; i++) {
			if (inputFile.getName().endsWith("B" + i + ".TIF")) {
				metadataProperties.put("scene_band_" + i + "_file_path",
						inputFile.getAbsolutePath());
			}
		}
	}

	protected void checkMTLAndPutInProperties(Properties metadataProperties, File inputFile) {
		if (inputFile.getName().endsWith("MTL.txt")) {
			metadataProperties.put(PropertiesConstants.METADATA_SCENE_MTL_FILE_PATH,
					inputFile.getAbsolutePath());
		}
	}

	protected void checkGCPAndPutInProperties(Properties metadataProperties, File inputFile) {
		if (inputFile.getName().endsWith("GCP.txt")) {
			metadataProperties.put(PropertiesConstants.METADATA_SCENE_GCP_FILE_PATH,
					inputFile.getAbsolutePath());
		}
	}

	protected void checkREADMEAndPutInProperties(Properties metadataProperties, File inputFile) {
		if (inputFile.getName().startsWith("README")) {
			metadataProperties.put(PropertiesConstants.METADATA_SCENE_README_FILE_PATH,
					inputFile.getAbsolutePath());
		}
	}

	protected void checkStationAndPutInProperties(Properties metadataProperties, File inputFile) {
		if (inputFile.getName().endsWith("station.csv")) {
			metadataProperties.put(PropertiesConstants.METADATA_SCENE_STATION_FILE_PATH,
					inputFile.getAbsolutePath());
		}
	}

	@Override
	public boolean writeMetadata(Properties metadataProperties, File metadataFile) {
		LOGGER.debug("Writing metadata in " + metadataFile.getAbsolutePath());

		try {
			OutputStream out = new FileOutputStream(metadataFile);
			metadataProperties.store(out, "This is an optional header comment string");
		} catch (Exception e) {
			LOGGER.error("Error while writing metadata file in " + metadataFile.getAbsolutePath(),
					e);
			return false;
		}

		return true;
	}

}
