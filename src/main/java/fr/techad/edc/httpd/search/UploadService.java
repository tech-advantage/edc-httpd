package fr.techad.edc.httpd.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.techad.edc.httpd.WebServerConfig;
import net.lingala.zip4j.ZipFile;

public class UploadService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
  private final WebServerConfig config;
  private final String tempdirPath = FileUtils.getTempDirectoryPath();

  public UploadService(WebServerConfig config) {
    this.config = config;
  }

  public boolean processing(String name) {
    try {
      String tmpdir = Files.createTempDirectory("unzip").toFile().getAbsolutePath();
      unzip(tempdirPath + "/" + name, tmpdir);
      File zip = new File(tempdirPath + "/" + name);

      LOGGER.info("Processing finished");
      return zip.delete();
    } catch (IOException e) {
      LOGGER.error("Error with files", e);
      return false;
    }

  }

  // Non multi-thread
  public boolean moveZip(File toSave, String name) throws IOException {
    if (FilenameUtils.getExtension(name).equals("zip")) {
      FileUtils.copyFile(toSave, new File(tempdirPath + "/" + name));
      LOGGER.info("Saving File: {}", name);
      return true;
    } else
      return false;
  }

  private void unzip(String filename, String tempdirPath) throws IOException {
    LOGGER.info("Unzipping File: {} into {}", filename, tempdirPath);
    ZipFile toextract = new ZipFile(filename);
    toextract.extractAll(tempdirPath);
    toextract.close();
  }

}
