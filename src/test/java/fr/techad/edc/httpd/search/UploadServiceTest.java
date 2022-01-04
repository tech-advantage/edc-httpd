package fr.techad.edc.httpd.search;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.networknt.config.Config;

import fr.techad.edc.httpd.UploadHandler;
import fr.techad.edc.httpd.WebServerConfig;
import fr.techad.edc.httpd.utils.TokenUtils;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class UploadServiceTest {
  static final String CONFIG_NAME = "webserver";
  static WebServerConfig config = (WebServerConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME,
      WebServerConfig.class);
  static UploadService service = new UploadService(config);

  @BeforeClass
  public static void initTestFile() throws IOException {
    File testZip = new File("./src/test/resources/testingZip.zip");
    service.saveZipFromHeader(testZip, testZip.getName());
    File testFile = new File("./src/test/resources/testFile.txt");
    service.saveZipFromHeader(testFile, "." + testZip.getName());
  }

  @Test
  public void saveSucess() throws IOException {
    File testZip = new File("./src/test/resources/testingZip.zip");
    Assert.assertTrue(service.saveZipFromHeader(testZip, testZip.getName()));
  }

  @Test
  public void saveFailure() throws IOException {
    File testFile = new File("./src/test/resources/testFile.txt");
    Assert.assertFalse(service.saveZipFromHeader(testFile, testFile.getName()));
  }

  @Test
  public void processingFailure() {
    Assert.assertFalse(service.processing("./.testingZip.zip"));
  }

  @Test
  public void processingNonExist() {
    Assert.assertFalse(service.processing("./ghost.zip"));
  }

  @Test
  public void processingSucess() {
    Assert.assertTrue(service.processing("./testingZip.zip"));
  }

  @Test
  public void HandleBadToken() throws Exception {
    UploadHandler uh = new UploadHandler(Config.getInstance().getMapper(), config, TokenUtils.getInstance());
    TokenUtils.getInstance().createTokenFile();
    HttpServerExchange exc = new HttpServerExchange(null);
    exc.getRequestHeaders().put(new HttpString("Edc-Token"), "FakeToken");
    uh.handleRequest(exc);
    Assert.assertEquals(401, exc.getStatusCode());
  }
}
