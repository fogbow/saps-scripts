package main;

import core.USGSNasaRepository;
import model.ImageTask;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class USGSControllerTest {

    private String sapsResultsPath;
    private String sapsMetadataPath;

    @Before
    public void setUp(){
        sapsResultsPath = "/tmp/results";
        sapsMetadataPath = "/tmp/metadata";
    }

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @After
    public void cleansDataDir() throws IOException {
        File f = new File(sapsResultsPath + "/data");
        FileUtils.deleteDirectory(f);
    }

    @Test
    public void testDownloadWithMalformedUrl() throws IOException, InterruptedException {
        exit.expectSystemExitWithStatus(3);

        USGSNasaRepository repository = Mockito.mock(USGSNasaRepository.class);
        Mockito.doThrow(new MalformedURLException()).when(repository).downloadImage(Mockito.any(ImageTask.class));

        Properties properties = new Properties();
        ImageTask imageTask = Mockito.mock(ImageTask.class);

        USGSController controller = new USGSController(repository, imageTask, properties);
        controller.startDownload();
    }

    @Test
    public void testDownloadIOException() throws IOException, InterruptedException {
        exit.expectSystemExitWithStatus(4);

        USGSNasaRepository repository = Mockito.mock(USGSNasaRepository.class);
        Mockito.doThrow(new IOException()).when(repository).downloadImage(Mockito.any(ImageTask.class));

        Properties properties = new Properties();
        ImageTask imageTask = Mockito.mock(ImageTask.class);

        USGSController controller = new USGSController(repository, imageTask, properties);
        controller.startDownload();
    }

    @Test
    public void testDownloadException() throws IOException, InterruptedException {
        exit.expectSystemExitWithStatus(5);

        USGSNasaRepository repository = Mockito.mock(USGSNasaRepository.class);
        Mockito.doThrow(new RuntimeException()).when(repository).downloadImage(Mockito.any(ImageTask.class));

        Properties properties = new Properties();
        ImageTask imageTask = Mockito.mock(ImageTask.class);

        USGSController controller = new USGSController(repository, imageTask, properties);
        controller.startDownload();
    }

    @Test
    public void testDownload() throws IOException, InterruptedException {
        USGSNasaRepository repository = Mockito.mock(USGSNasaRepository.class);
        Mockito.doNothing().when(repository).downloadImage(Mockito.any(ImageTask.class));

        Properties properties = new Properties();
        ImageTask imageTask = Mockito.mock(ImageTask.class);

        USGSController controller = new USGSController(repository, imageTask, properties);
        controller.startDownload();
    }
}
