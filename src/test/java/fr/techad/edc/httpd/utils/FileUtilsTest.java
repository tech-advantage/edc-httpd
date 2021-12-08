package fr.techad.edc.httpd.utils;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class FileUtilsTest {
	FileUtils futil = FileUtils.getInstance();
	@Test
	public void fileReadingSucess() {
		Assert.assertEquals("edc-token-httpd-java", futil.readFile("./src/test/resources/file.txt"));
	}

	@Test
	public void fileReadingFailure() {
		Assert.assertNotEquals("nothing", futil.readFile("./src/test/resources/file.txt"));
	}

	@Test
	public void fileReadingError() {
		Assert.assertEquals("", futil.readFile("./src/test/resources/ghost.txt"));
	}

	@Test
	public void fileWritingSucess() {
		futil.writeFile("./src/test/resources/file.txt", "avaj-dptth-nekot-cde");
		String verify = futil.readFile("./src/test/resources/file.txt");
		Assert.assertEquals("avaj-dptth-nekot-cde", verify);
		futil.writeFile("./src/test/resources/file.txt", "edc-token-httpd-java");
	}

	@Test
	public void fileWritingError() {
		futil.writeFile("C:/test.txt", "some text");
		File f = new File("C:/test.txt");
		Assert.assertFalse(f.exists());
	}
}
