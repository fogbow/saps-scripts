package main;

import core.USGSNasaRepository;
import model.ImageTask;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

public class USGSController {

    static final Logger LOGGER = Logger.getLogger(USGSController.class);

    private USGSNasaRepository usgsRepository;
    private ImageTask imageTask;
    private Properties properties;

    public USGSController(String dataSet, String region, String date, String pathStorage) throws Exception {
        properties = loadProperties();
        setUsgsRepository(new USGSNasaRepository(pathStorage, properties));
        usgsRepository.handleAPIKeyUpdate();
        setImageTask(new ImageTask(dataSet, region, date));
        imageTask.setDownloadLink(usgsRepository.getImageDownloadLink(imageTask.getName()));
    }

    /**
     * Other errors this program can handle:
     *  Status 28: Means an error while downloading, operation timeout. Check conditions in PropertiesConstants (SPEED_LIMIT and SPEED_TIME)
     *
     */
    public void startDownload(){
        try {
            usgsRepository.downloadImage(imageTask);
        } catch (MalformedURLException e) {
            /**
             * Tried to make download but a Malformed URL was given
             */
            System.exit(3);
        } catch (IOException e){
            /**
             * Tried to make download but URL is not Reachable, or Tried to create a file/directory but got an error. Check logs
             */
            System.exit(4);
        } catch (Exception e){
            /**
             * Tried to make download but had an error with Process Builder command
             */
            System.exit(5);
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

    public static Properties loadProperties(){
        Properties props = new Properties();
        FileInputStream input;
        try {
            input = new FileInputStream(System.getProperty("user.dir") + File.separator + "config/sebal.conf");
            props.load(input);
        } catch (FileNotFoundException e) {
            LOGGER.error("Error while reading conf file", e);
        } catch (IOException e) {
            LOGGER.error("Error while loading properties", e);
        }
        return props;
    }
}
