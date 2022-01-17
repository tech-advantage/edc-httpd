package fr.techad.edc.httpd.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TokenUtilsTest {
  private final String tokenPath = "./token.info";
  private final static String keyPath = "./private.key";
  TokenUtils tutil = TokenUtils.getInstance();

  @BeforeAll
  public static void backupKey() throws IOException {
    FileUtils.copyFile(new File(keyPath), new File("./src/test/resources/backup/backup.key"));
  }
  @AfterAll
  public static void restoreKey() throws IOException {
    FileUtils.deleteQuietly(new File(keyPath));
    FileUtils.moveFile(new File("./src/test/resources/backup/backup.key"),new File(keyPath));
  }
  
  @BeforeEach
  public void createToken() throws IOException {
    tutil.createTokenFile();
  }

  @AfterEach
  public void removeFakeKey() throws IOException {
    if (FileUtils.readFileToString(new File(keyPath), "UTF-8").equals("IamAfakePrivateK3Y"))
      FileUtils.writeStringToFile(new File(keyPath), "", "UTF-8");
    ;
  }

  @Test
  public void keyCreationLength() {
    Assertions.assertEquals(24, tutil.genSecretKey().length());
  }

  @Test
  public void tokenValidationBlankTest() throws IOException {
    Assertions.assertFalse(tutil.validateToken(""));
  }

  @Test
  public void tokenValidationSucess() throws IOException {
    Assertions.assertTrue(tutil.validateToken(FileUtils.readFileToString(new File(tokenPath), "UTF-8")));
  }

  @Test
  public void tokenValidationFailure() throws IOException {
    Assertions.assertFalse(tutil.validateToken("a fake token"));
  }

  @Test
  public void tokenValidationChangedFailure() throws IOException {
    String previousToken = FileUtils.readFileToString(new File(tokenPath), "UTF-8");
    Assertions.assertTrue(tutil.validateToken(previousToken));
    FileUtils.writeStringToFile(new File(keyPath), "IamAfakePrivateK3Y", "UTF-8");
    tutil.createTokenFile();
    Assertions.assertFalse(tutil.validateToken(previousToken));
  }

}
