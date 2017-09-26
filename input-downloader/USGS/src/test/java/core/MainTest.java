package core;

import main.USGSController;
import main.Main;
import model.ImageTask;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.mockito.Mockito.*;

public class MainTest {

    private String sebalResultsPath;

    @Before
    public void setUp(){
        sebalResultsPath = "/tmp/results";
    }

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @After
    public void cleansDataDir() throws IOException {
        File f = new File(sebalResultsPath + "/data");
        FileUtils.deleteDirectory(f);
    }

    @Test
    public void singleArgTest() throws Exception {
        exit.expectSystemExitWithStatus(6);
        String[] args = {"a"};
        Main.main(args);
    }

    @Test
    public void testMalFormedURLDownload() throws Exception {
        exit.expectSystemExitWithStatus(3);
        USGSController USGSController = new USGSController("collectionTierName", "unknownName", sebalResultsPath);
        USGSController.startDownload();
    }

    @Test
    public void testUnknownImageTaskName() throws Exception {
        exit.expectSystemExitWithStatus(4);

        USGSController USGSController = new USGSController("collectionTierName", "unknownName", sebalResultsPath);
        Properties properties = USGSController.loadProperties();

        USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(properties));
        USGSController.setUsgsRepository(usgsNasaRepository);

        doThrow(new IOException()).when(usgsNasaRepository).downloadImage(any(ImageTask.class));

        USGSController.startDownload();
    }
}
