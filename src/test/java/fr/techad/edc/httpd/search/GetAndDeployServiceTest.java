package fr.techad.edc.httpd.search;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
  final String docPath = config.getBase() + "/" + config.getDocFolder() + "/";

  @BeforeClass
  public static void initTests() throws IOException {
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

    File testFile = new File("./src/test/resources/testFile.txt");
    service.moveZip(testFile, "." + testZip.getName());
  }

  @Before
  public void initTestdoc() throws IOException {
    FileUtils.copyDirectory(new File(config.getBase() + "/" + config.getDocFolder()),
        new File("./src/test/resources/backup"));
    FileUtils.copyDirectory(new File("./src/test/resources/docTest"),
        new File(config.getBase() + "/" + config.getDocFolder()));
  }

  @After
  public void cleanTests() throws IOException {
    FileUtils.deleteDirectory(new File(config.getBase() + "/" + config.getDocFolder()));
    FileUtils.copyDirectory(new File("./src/test/resources/backup"),
        new File(config.getBase() + "/" + config.getDocFolder()));
    FileUtils.deleteDirectory(new File("./src/test/resources/backup"));
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
  public void processingFailure() throws IOException {
    File docDir=new File(docPath);
    FileUtils.cleanDirectory(docDir);
    Assert.assertFalse(service.processing("./testZip.zip", true));
    Assert.assertFalse(service.processing("./.testZip.zip", true));
    Assert.assertEquals(0, FileUtils.listFilesAndDirs(docDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size()-1);
  }
//
//  @Test
//  public void processingNonExist() {
//    Assert.assertFalse(service.processing("./ghost.zip", true));
//    Assert.assertTrue(areDirsEqual(new File(docPath), new File("./src/test/resources/docTest")));
//  }
//
  @Test
  public void processingBadStructure() throws IOException {
    File docDir=new File(docPath);
    FileUtils.cleanDirectory(docDir);
    Assert.assertFalse(service.processing("./docexample.zip", true));
    Assert.assertEquals(0, FileUtils.listFilesAndDirs(docDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size()-1);
  }

  @Test
  public void processingSucesswithBadFiles() {
    Assert.assertTrue(service.processing("./docexample2.zip", true));
    String []ext={"exe","js","sh"};
    assertEquals(0, FileUtils.listFiles(new File(docPath),ext , true).size()); 
   
  }

  @Test
  public void processingSucessReplace() throws IOException {
    Assert.assertTrue(service.processing("./docexample3.zip", true));
    File check=new File(docPath+"/a/b.html");
    Assert.assertTrue(check.exists());
    Assert.assertFalse(new File(docPath+"/a/a.html").exists());
    Assert.assertTrue( FileUtils.readFileToString(check, StandardCharsets.UTF_8.name()) .equals("b") );
  }

  @Test
  public void processingSucessNew()  {
    Assert.assertTrue(service.processing("./docexample4.zip", true));
    File newdir=new File(docPath+"/new");
    Assert.assertTrue(newdir.exists());
    Assert.assertEquals(50, FileUtils.listFilesAndDirs(newdir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size()-1);
  }

  @Test
  public void processingSucessNotOverride() throws IOException {
    Assert.assertTrue(service.processing("./docexample5.zip", false));
    File here1=new File(docPath+"/i18n/popover/fr.json");
    File here2=new File(docPath+"/i18n/web-help/fr.json");
    File check=new File(docPath+"/i18n/popover/en.json");
    File check2=new File(docPath+"/i18n/web-help/en.json");
    Assert.assertTrue(here1.exists());
    Assert.assertTrue(here2.exists());
    Assert.assertFalse( FileUtils.readFileToString(check, StandardCharsets.UTF_8.name()) .equals("a") );
    Assert.assertFalse( FileUtils.readFileToString(check2, StandardCharsets.UTF_8.name()) .equals("a") );  
  }
  @Test
  public void processingSucessOverride() throws IOException {
    Assert.assertTrue(service.processing("./docexample6.zip", true));
    File here1=new File(docPath+"/i18n/popover/fr.json");
    File here2=new File(docPath+"/i18n/web-help/fr.json");
    File check=new File(docPath+"/i18n/popover/en.json");
    File check2=new File(docPath+"/i18n/web-help/en.json");
    Assert.assertTrue(here1.exists());
    Assert.assertTrue(here2.exists());
    Assert.assertTrue( FileUtils.readFileToString(check, StandardCharsets.UTF_8.name()) .equals("ne") );
    Assert.assertTrue( FileUtils.readFileToString(check2, StandardCharsets.UTF_8.name()) .equals("ne") );  
  }
  @Test
  public void processingEmptyDoc() throws IOException {
    FileUtils.deleteDirectory(new File(docPath));
    File good4Zip = new File("./src/test/resources/docexample4.zip");
    service.moveZip(good4Zip, good4Zip.getName());
    Assert.assertTrue(service.processing("./docexample4.zip", true));
    File docdir=new File(docPath);
    Assert.assertTrue(FileUtils.listFilesAndDirs(docdir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).size()-1 > 0);
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

  public boolean areDirsEqual(File dir1, File dir2) {
    List<File> filesDir1 = (List<File>) FileUtils.listFiles(dir1, null, true);
    List<File> filesDir2 = (List<File>) FileUtils.listFiles(dir2, null, true);
    List<String> paths1 = new ArrayList<String>();
    List<String> paths2 = new ArrayList<String>();
    for (File f : filesDir1) {
      String path1 = f.getPath();
      String[] split1 = path1.split("doc");
      if (f.getName().equals("multi-doc.json")) {
        paths1.add(split1[1] + "doc" + split1[2]);
      } else {
        paths1.add(split1[1]);
      }
    }
    for (File f : filesDir2) {
      String path2 = f.getPath();
      String splitter = dir2.getName();
      String[] split2 = path2.split(splitter);
      paths2.add(split2[1]);
    }
    boolean res = false;
    if (paths1.containsAll(paths2)) {
      Collections.sort(filesDir1);
      Collections.sort(filesDir2);
      for (int i = 0; i < filesDir1.size(); i++) {
        try {
          if (FileUtils.contentEquals(filesDir1.get(i), filesDir2.get(i))) {
            res = true;
          } else {
            return false;
          }
        } catch (IOException e) {
          res = false;
        }
      }
    }
    return res;
  }
}
