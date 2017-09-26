package main;

public class Main {

    /**
     * How to run this project:
     *      java -jar usgs.jar arg1 arg2
     *
     * arg1: The collectionTierName from ImageTask
     * arg2: The name from ImageTask
     * arg3: The path to store the image downloaded
     *
      */
    public static void main(String[] args) throws Exception {
        checkNumberOfArgs(args);
        USGSController USGSController = new USGSController(args[0], args[1], args[2]);
        USGSController.startDownload();
    }

    private static void checkNumberOfArgs(String[] args) {
        if(args.length != 3){
            System.exit(6);
        }
    }
}
