package main;

import org.apache.log4j.Logger;

public class Main {

	static final Logger LOGGER = Logger.getLogger(Main.class);

    /**
     * How to run this project:
     *      java -jar usgs.jar arg1 arg2 arg3 arg4
     *
     * And inside Docker container:
     *      java -Dlog4j.configuration=file:/home/ubuntu/config/log4j.properties -jar /home/ubuntu/USGS.jar $1 $2 $3 $4
     *
     * arg1: The Image Dataset
     * arg2: The Image Region
     * arg3: The Image date
     * arg4: The path to store the image downloaded
     * arg5: The path to store the execution metadata
     *
     */
	public static void main(String[] args) throws Exception {
		LOGGER.debug("Initiating Input Downloader script");
		checkNumberOfArgs(args);

		USGSController usgsController = new USGSController(
				args[0],
				args[1],
				args[2],
				args[3],
				args[4]
		);
		usgsController.startDownload();
		usgsController.saveMetadata();
	}

	static void checkNumberOfArgs(String[] args) {
		LOGGER.debug("Verifying number of arguments");
		if (args.length != 5) {
			LOGGER.error("Incorrect number of arguments (expected 5, received " + args.length + ")");
			System.exit(6);
		}
	}
}
