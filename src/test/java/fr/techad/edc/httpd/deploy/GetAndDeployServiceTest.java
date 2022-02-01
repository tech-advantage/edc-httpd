package fr.techad.edc.httpd.deploy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.networknt.config.Config;

import fr.techad.edc.httpd.UploadHandler;
import fr.techad.edc.httpd.WebServerConfig;
import fr.techad.edc.httpd.utils.TokenUtils;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetAndDeployServiceTest {
  final String CONFIG_NAME = "webserver";
  WebServerConfig config = (WebServerConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME,
      WebServerConfig.class);
  GetAndDeployService service = new GetAndDeployService(config);
  final String docPath = config.getBase() + "/" + config.getDocFolder() + "/";

  @BeforeAll
  public void initTests() throws IOException {
    File testZip = new File("./src/test/resources/testZip.zip");
    service.moveZip(testZip, testZip.getName());
    File goodZip = new File("./src/test/resources/docexample.zip");
    service.moveZip(goodZip, goodZip.getName());
    File good2Zip = new File("./src/test/resources/docexample2.zip");
    service.moveZip(good2Zip, good2Zip.getName());
    File good3Zip = new File("./src/test/resources/docexample3.zip");
    service.moveZip(good3Zip, good3Zip.getName());
    File good4Zip = new File("./src/test/resources/docexample4.zip");
    service.moveZip(good4Zip, good4Zip.getName());
    File good5Zip = new File("./src/test/resources/docexample5.zip");
    service.moveZip(good5Zip, good5Zip.getName());
    File good6Zip = new File("./src/test/resources/docexample6.zip");
    service.moveZip(good6Zip, good6Zip.getName());

    File testFile = new File("./src/test/resources/testfile.txt");
    service.moveZip(testFile, "." + testZip.getName());
    if(new File(config.getBase() + "/" + config.getDocFolder()).exists()){
          FileUtils.copyDirectory(new File(config.getBase() + "/" + config.getDocFolder()),
        new File("./src/test/resources/backup"));
    }
  }

  @AfterAll
  public void restoreBackup() throws IOException {
    FileUtils.moveDirectory(new File("./src/test/resources/backup"),
        new File(config.getBase() + "/" + config.getDocFolder()));
  }

  @BeforeEach
  public void initTestdoc() throws IOException {

    FileUtils.copyDirectory(new File("./src/test/resources/docTest"),
        new File(config.getBase() + "/" + config.getDocFolder()));
  }

  @AfterEach
  public void cleanTests() throws IOException {
    FileUtils.deleteDirectory(new File(config.getBase() + "/" + config.getDocFolder()));
  }

  @Test
  public void moveSuccess() throws IOException {
    File testZip = new File("./src/test/resources/testZip.zip");
    Assertions.assertTrue(service.moveZip(testZip, testZip.getName()));
  }

  @Test
  public void moveFailure() throws IOException {
    File testFile = new File("./src/test/resources/testFile.txt");
    Assertions.assertFalse(service.moveZip(testFile, testFile.getName()));
  }

  @Test
  public void processingFailure() throws IOException {
    File docDir = new File(docPath);
    FileUtils.cleanDirectory(docDir);
    Assertions.assertFalse(service.processing("./testZip.zip", true));
    Assertions.assertFalse(service.processing("./.testZip.zip", true));
    Assertions.assertEquals(0,
        FileUtils.listFilesAndDirs(docDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size() - 1);
  }

  @Test
  public void processingBadStructure() throws IOException {
    File docDir = new File(docPath);
    FileUtils.cleanDirectory(docDir);
    Assertions.assertFalse(service.processing("./docexample.zip", true));
    Assertions.assertEquals(0,
        FileUtils.listFilesAndDirs(docDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size() - 1);
  }

  @Test
  public void processingSuccessWithBadFiles() {
    Assertions.assertTrue(service.processing("./docexample2.zip", true));
    String[] ext = { "exe", "js", "sh" };
    Assertions.assertEquals(0, FileUtils.listFiles(new File(docPath), ext, true).size());

  }

  @Test
  public void processingSuccessReplace() throws IOException {
    Assertions.assertTrue(service.processing("./docexample3.zip", true));
    File check = new File(docPath + "/a/b.html");
    Assertions.assertTrue(check.exists());
    Assertions.assertFalse(new File(docPath + "/a/a.html").exists());
    Assertions.assertTrue(FileUtils.readFileToString(check, StandardCharsets.UTF_8.name()).equals("b"));
  }

  @Test
  public void processingSuccessNew() {
    Assertions.assertTrue(service.processing("./docexample4.zip", true));
    File newdir = new File(docPath + "/new");
    Assertions.assertTrue(newdir.exists());
    Assertions.assertEquals(50,
        FileUtils.listFilesAndDirs(newdir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size() - 1);
  }

  @Test
  public void processingSuccessNotOverride() throws IOException {
    Assertions.assertTrue(service.processing("./docexample5.zip", false));
    File here1 = new File(docPath + "/i18n/popover/fr.json");
    File here2 = new File(docPath + "/i18n/web-help/fr.json");
    File check = new File(docPath + "/i18n/popover/en.json");
    File check2 = new File(docPath + "/i18n/web-help/en.json");
    Assertions.assertTrue(here1.exists());
    Assertions.assertTrue(here2.exists());
    Assertions.assertFalse(FileUtils.readFileToString(check, StandardCharsets.UTF_8.name()).equals("a"));
    Assertions.assertFalse(FileUtils.readFileToString(check2, StandardCharsets.UTF_8.name()).equals("a"));
  }

  @Test
  public void processingSuccessOverride() throws IOException {
    Assertions.assertTrue(service.processing("./docexample6.zip", true));
    File here1 = new File(docPath + "/i18n/popover/fr.json");
    File here2 = new File(docPath + "/i18n/web-help/fr.json");
    File check = new File(docPath + "/i18n/popover/en.json");
    File check2 = new File(docPath + "/i18n/web-help/en.json");
    Assertions.assertTrue(here1.exists());
    Assertions.assertTrue(here2.exists());
    Assertions.assertTrue(FileUtils.readFileToString(check, StandardCharsets.UTF_8.name()).equals("ne"));
    Assertions.assertTrue(FileUtils.readFileToString(check2, StandardCharsets.UTF_8.name()).equals("ne"));
  }

  @Test
  public void processingEmptyDoc() throws IOException {
    FileUtils.deleteDirectory(new File(docPath));
    File good4Zip = new File("./src/test/resources/docexample4.zip");
    service.moveZip(good4Zip, good4Zip.getName());
    Assertions.assertTrue(service.processing("./docexample4.zip", true));
    File docdir = new File(docPath);
    Assertions.assertTrue(
        FileUtils.listFilesAndDirs(docdir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size() - 1 > 0);
  }

  @Test
  public void HandleBadToken() throws Exception {
    UploadHandler uh = new UploadHandler(Config.getInstance().getMapper(), config, TokenUtils.getInstance());
    TokenUtils.getInstance().createTokenFile();
    HttpServerExchange exc = new HttpServerExchange(null);
    exc.getRequestHeaders().put(new HttpString("Edc-Token"), "FakeToken");
    uh.handleRequest(exc);
    Assertions.assertEquals(401, exc.getStatusCode());
  }

}
