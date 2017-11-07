package utils;

import java.io.File;
import java.util.Properties;

import model.ImageTask;

public interface MetadataUtil {

	public Properties generateMetadata(ImageTask imageTask, String inputDirPath, String usgsAPIUrl,
			String noaaFTPServerUrl, String elevationUrl, String shapefileUrl);

	public boolean writeMetadata(Properties metadataProperties, File metadataFile);

}
