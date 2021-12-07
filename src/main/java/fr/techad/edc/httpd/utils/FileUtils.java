package fr.techad.edc.httpd.utils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
	final static Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
	private static FileUtils instance;

	private FileUtils() {

	}

	public static synchronized FileUtils getInstance() {
		if (instance == null)
			instance = new FileUtils();
		return instance;
	}

	public void writeFile(String path, String contents) {
		FileWriter myWriter = null;
		try {
			myWriter = new FileWriter(path);
			myWriter.write(contents);
		} catch (IOException e) {
			LOGGER.error("Unable to write file", e);
		} finally {
			if (myWriter != null) {
				try {
					myWriter.close();
				} catch (IOException e) {
					LOGGER.error("Unable to close file", e);
				}
			}
		}
	}

	public String readFile(String filePath) {
		String data = "";
		try {
			FileInputStream fis = new FileInputStream(filePath);
			data = IOUtils.toString(fis, "UTF-8");
		} catch (IOException e) {
			LOGGER.error("Unable to found file", e);
		}
		return data;
	}
}
