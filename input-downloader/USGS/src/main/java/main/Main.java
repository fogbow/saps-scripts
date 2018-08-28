package main;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Main {

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
	
	static final Logger LOGGER = Logger.getLogger(Main.class);
	
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		checkNumberOfArgs(args);

		USGSController USGSController = new USGSController(args[0], args[1], args[2], args[3],
				args[4]);
		USGSController.startDownload();
		USGSController.saveMetadata();
	}

	private static void checkNumberOfArgs(String[] args) {
		if (args.length != 5) {
			LOGGER.error("Missing parameters, expected 5, found " + args.length);
			LOGGER.info("\n arg1: The Image Dataset\n arg2: The Image Region\n arg3: The Image date\n arg4: The path to store the image downloaded\n"
					+ " arg5: The path to store the execution metadata");
			System.exit(6);
		}
	}
}
