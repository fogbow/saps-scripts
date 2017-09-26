package core;

import model.ImageTask;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicStatusLine;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.concurrent.Executors;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class USGSNasaRepositoryTest {

    private static final String UTF_8 = "UTF-8";

    private String usgsUserName;
    private String usgsPassword;
    private String usgsLoginUrl;
    private String usgsJsonUrl;
    private String usgsAPIPeriod;
    private String sebalExportPath;
    private String sebalResultsPath;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        usgsUserName = "fake-user-name";
        usgsPassword = "fake-password";
        usgsLoginUrl = "fake-login-url";
        usgsJsonUrl = "https://earthexplorer.usgs.gov/inventory/json";
        usgsAPIPeriod = "10000";
        sebalExportPath = "/tmp";
        sebalResultsPath = "/tmp/results";
    }

    @After
    public void cleansDataDir(){
        File f = new File(sebalExportPath + "/data/collectionTierName/");
        f.delete();
    }

    @Test
    public void testGenerateAPIKeyResponse() throws ClientProtocolException, IOException {
        // set up
        HttpResponse httpResponse = mock(HttpResponse.class);
        HttpEntity httpEntity = mock(HttpEntity.class);

        String content = "{ \"errorCode\":null, \"error\":\"\", \"data\":\"9ccf44a1c7e74d7f94769956b54cd889\", \"api_version\":\"1.0\" }";

        InputStream contentInputStream = new ByteArrayInputStream(content.getBytes(UTF_8));
        doReturn(contentInputStream).when(httpEntity).getContent();
        doReturn(httpEntity).when(httpResponse).getEntity();

        BasicStatusLine basicStatus = new BasicStatusLine(new ProtocolVersion("", 0, 0), HttpStatus.SC_OK, "");
        doReturn(basicStatus).when(httpResponse).getStatusLine();
        doReturn(new Header[0]).when(httpResponse).getAllHeaders();

        USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(
                sebalExportPath, usgsLoginUrl, usgsJsonUrl, usgsUserName,
                usgsPassword));

        doReturn(content).when(usgsNasaRepository).getLoginResponse();

        // exercise
        String apiKey = usgsNasaRepository.generateAPIKey();

        // expect
        Assert.assertNotNull(apiKey);
        Assert.assertEquals("9ccf44a1c7e74d7f94769956b54cd889", apiKey);
    }

    @Test (expected = MalformedURLException.class)
    public void testMalFormedURLDownload() throws Exception {
        USGSNasaRepository usgsNasaRepository = new USGSNasaRepository(
                sebalExportPath, usgsLoginUrl, usgsJsonUrl, usgsUserName,
                usgsPassword);
        usgsNasaRepository.handleAPIKeyUpdate();

        ImageTask imageTask = new ImageTask("collectionTierName", "unknownName");

        imageTask.setDownloadLink(usgsNasaRepository.getImageDownloadLink(imageTask.getName()));
        usgsNasaRepository.downloadImage(imageTask);
    }

    @Test
    public void testUnknownImageTaskName() throws Exception {
        String urlToBeTested = "http://www.google.com/invalidURL";
        USGSNasaRepository usgsNasaRepository = new USGSNasaRepository(
                sebalExportPath, usgsLoginUrl, usgsJsonUrl, usgsUserName,
                usgsPassword);
        usgsNasaRepository.handleAPIKeyUpdate();
        ImageTask imageTask = spy(new ImageTask("collectionTierName", "unknownName"));
        doReturn(urlToBeTested).when(imageTask).getDownloadLink();

        try{
            usgsNasaRepository.downloadImage(imageTask);
        } catch (IOException e){
            Assert.assertEquals("The given URL: " + urlToBeTested + " is not reachable.", e.getMessage());
        }
    }

    @Test
    public void testURLAvailability() throws IOException {
        String urlToBeTested = "http://www.google.com";
        Assert.assertTrue(USGSNasaRepository.isReachable(urlToBeTested));

        urlToBeTested = "http://www.google.com/invalidURL";
        Assert.assertFalse(USGSNasaRepository.isReachable(urlToBeTested));

        try{
            urlToBeTested = "abc";
            USGSNasaRepository.isReachable(urlToBeTested);
        } catch (MalformedURLException e){
            Assert.assertEquals("The given URL: " + urlToBeTested + " is not valid.", e.getMessage());
        }
    }

    @Test
    public void testCorrectDownloadLinkFromResponse() throws Exception {
        String fakeDownloadResponse = "{\"errorCode\":null,\"error\":\"\",\"data\":[\"https:\\/\\/dds.cr.usgs.gov\\/ltaauth\\/hsm\\/lta3\\/lsat_ortho\\/gls_1975\\/046\\/034\\/p046r034_1x19720725.tar.gz?id=erjal92rc4o2070t76uqbispk4&iid=P046R034_1X19720725&did=338419617&ver=production\"],\"api_version\":\"1.3.0\",\"access_level\":\"approved\",\"executionTime\":1.0584261417389}";
        String expectedDownloadLink = "https://dds.cr.usgs.gov/ltaauth/hsm/lta3/lsat_ortho/gls_1975/046/034/p046r034_1x19720725.tar.gz?id=erjal92rc4o2070t76uqbispk4&iid=P046R034_1X19720725&did=338419617&ver=production";

        USGSNasaRepository usgsNasaRepository = spy(new USGSNasaRepository(
                sebalResultsPath, usgsLoginUrl, usgsJsonUrl, usgsUserName,
                usgsPassword));

        doReturn(fakeDownloadResponse).when(usgsNasaRepository).getDownloadHttpResponse(anyString(),anyString(),anyString(),anyString());

        Assert.assertEquals(expectedDownloadLink, usgsNasaRepository.doGetDownloadLink("LT5"));
    }
}
