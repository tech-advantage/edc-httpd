package fr.techad.edc.httpd.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TokenUtilsTest {
  private final String tokenPath = "./token.info";
  private final String keyPath = "./private.key";
  TokenUtils tutil = TokenUtils.getInstance();

  @Before
  public void createToken() throws IOException {
    tutil.createTokenFile();
  }

  @After
  public void removeFakeKey() throws IOException {
    if (FileUtils.readFileToString(new File(keyPath),"UTF-8").equals("IamAfakePrivateK3Y"))
      FileUtils.writeStringToFile(new File(keyPath), "","UTF-8");;
  }

  @Test
  public void keyCreationLength() {
    Assert.assertEquals(24, tutil.genSecretKey().length());
  }

  @Test
  public void tokenValidationBlankTest() throws IOException {
    Assert.assertFalse(tutil.validateToken(""));
  }

  @Test
  public void tokenValidationSucess() throws IOException {
    Assert.assertTrue(tutil.validateToken(FileUtils.readFileToString(new File(tokenPath),"UTF-8")));
  }

  @Test
  public void tokenValidationFailure() throws IOException {
    Assert.assertFalse(tutil.validateToken("a fake token"));
  }

  @Test
  public void tokenValidationChangedFailure() throws IOException {
    String previousToken = FileUtils.readFileToString(new File(tokenPath),"UTF-8");
    Assert.assertTrue(tutil.validateToken(previousToken));
    FileUtils.writeStringToFile(new File(keyPath), "IamAfakePrivateK3Y","UTF-8");
    tutil.createTokenFile();
    Assert.assertFalse(tutil.validateToken(previousToken));
  }

}
