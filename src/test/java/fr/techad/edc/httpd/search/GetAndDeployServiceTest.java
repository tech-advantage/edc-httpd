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

public class GetAndDeployServiceTest {
  static final String CONFIG_NAME = "webserver";
  static WebServerConfig config = (WebServerConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME,
      WebServerConfig.class);
  static GetAndDeployService service = new GetAndDeployService(config);

  @BeforeClass
  public static void initTestFile() throws IOException {
    File testZip = new File("./src/test/resources/testZip.zip");
    service.moveZip(testZip, testZip.getName());
    File goodZip = new File("./src/test/resources/docexample.zip");
    service.moveZip(goodZip, goodZip.getName());
    File testFile = new File("./src/test/resources/testFile.txt");
    service.moveZip(testFile, "." + testZip.getName());
  }

  @Test
  public void moveSucess() throws IOException {
    File testZip = new File("./src/test/resources/testZip.zip");
    Assert.assertTrue(service.moveZip(testZip, testZip.getName()));
  }

  @Test
  public void moveFailure() throws IOException {
    File testFile = new File("./src/test/resources/testFile.txt");
    Assert.assertFalse(service.moveZip(testFile, testFile.getName()));
  }

  @Test
  public void processingFailure() {
    Assert.assertFalse(service.processing("./testZip.zip",true));
    Assert.assertFalse(service.processing("./.testZip.zip",true));
  }

  @Test
  public void processingNonExist() {
    Assert.assertFalse(service.processing("./ghost.zip",true));
  }

  @Test
  public void processingSucess() {
    Assert.assertTrue(service.processing("./docexample.zip",true));
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
