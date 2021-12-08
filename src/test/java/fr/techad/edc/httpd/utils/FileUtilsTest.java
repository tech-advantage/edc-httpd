package fr.techad.edc.httpd.utils;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class FileUtilsTest {
	FileUtils futil = FileUtils.getInstance();
	final String testFilePath = "./src/test/resources/testfile.txt";
	@Test
	public void fileReadingSucess() {
		Assert.assertEquals("edc-token-httpd-java", futil.readFile(testFilePath));
	}

	@Test
	public void fileReadingFailure() {
		Assert.assertNotEquals("nothing", futil.readFile(testFilePath));
	}

	@Test
	public void fileReadingError() {
		Assert.assertEquals("", futil.readFile("./src/test/resources/ghost.txt"));
	}

	@Test
	public void fileWritingSucess() {
		futil.writeFile(testFilePath, "avaj-dptth-nekot-cde");
		String verify = futil.readFile(testFilePath);
		Assert.assertEquals("avaj-dptth-nekot-cde", verify);
		futil.writeFile(testFilePath, "edc-token-httpd-java");
	}

	@Test
	public void fileWritingError() {
		futil.writeFile("C:/test.txt", "some text");
		File f = new File("C:/test.txt");
		Assert.assertFalse(f.exists());
	}
	@Test
	public void fileWritingOverwrite() {
		futil.writeFile(testFilePath, "avaj-dptth-nekot-cde");
		Assert.assertEquals("avaj-dptth-nekot-cde",futil.readFile(testFilePath));
		futil.writeFile(testFilePath, "edc-token-httpd-java");
		Assert.assertEquals("edc-token-httpd-java",futil.readFile(testFilePath));
	}
}
