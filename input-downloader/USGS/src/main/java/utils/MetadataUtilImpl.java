package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class MetadataUtilImpl implements MetadataUtil {

	static final Logger LOGGER = Logger.getLogger(MetadataUtilImpl.class);

	@Override
	public void writeMetadata(String inputDirPath, File metadataFile) throws IOException {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(metadataFile));
			writeInputFilesPaths(inputDirPath, writer);
			LOGGER.info("Metadata file " + metadataFile.getAbsolutePath() + " generated");
		} finally {
			writer.close();
		}
	}

	private void writeInputFilesPaths(String inputDirPath, BufferedWriter writer)
			throws IOException {
		File inputDir = new File(inputDirPath);
		for (File inputFile : inputDir.listFiles()) {
			checkBandAndPutInProperties(inputFile, writer);
			checkMTLAndPutInProperties(inputFile, writer);
			checkGCPAndPutInProperties(inputFile, writer);
			checkREADMEAndPutInProperties(inputFile, writer);
			checkStationAndPutInProperties(inputFile, writer);
		}
	}

	protected void checkBandAndPutInProperties(File inputFile, BufferedWriter writer)
			throws IOException {
		for (int i = 1; i <= 11; i++) {
			if (inputFile.getName().endsWith("B" + i + ".TIF")) {
				writer.write(inputFile.getAbsolutePath() + " # Scene band " + i + " file path");
			}
		}
	}

	protected void checkMTLAndPutInProperties(File inputFile, BufferedWriter writer)
			throws IOException {
		if (inputFile.getName().endsWith("MTL.txt")) {
			writer.write(inputFile.getAbsolutePath() + " # Scene metadata file path");
		}
	}

	protected void checkGCPAndPutInProperties(File inputFile, BufferedWriter writer)
			throws IOException {
		if (inputFile.getName().endsWith("GCP.txt")) {
			writer.write(inputFile.getAbsolutePath() + " # Scene GCP file path");
		}
	}

	protected void checkREADMEAndPutInProperties(File inputFile, BufferedWriter writer)
			throws IOException {
		if (inputFile.getName().startsWith("README")) {
			writer.write(inputFile.getAbsolutePath() + " # Complete data description file path");
		}
	}

	protected void checkStationAndPutInProperties(File inputFile, BufferedWriter writer)
			throws IOException {
		if (inputFile.getName().endsWith("station.csv")) {
			writer.write(inputFile.getAbsolutePath() + " # Meteorological data file path");
		}
	}
}
