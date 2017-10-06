package main;

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
     *
      */
    public static void main(String[] args) throws Exception {
        checkNumberOfArgs(args);
        USGSController USGSController = new USGSController(args[0], args[1], args[2], args[3]);
        USGSController.startDownload();
    }

    private static void checkNumberOfArgs(String[] args) {
        if(args.length != 4){
            System.exit(6);
        }
    }
}
