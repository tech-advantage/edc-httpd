package fr.techad.edc.httpd.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;


import fr.techad.edc.httpd.WebServerConfig;
import net.lingala.zip4j.ZipFile;

public class UploadService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
  private final WebServerConfig config;
  private final String tempdirPath = System.getProperty("java.io.tmpdir");
  
  public UploadService(WebServerConfig config) {
    this.config = config;
  }
  
  public boolean processing(File toSave,String name) {
    try {
      unzip(tempdirPath+"/"+name,tempdirPath+"/test_unzip");
      File zip = new File(tempdirPath+"/"+name);
      LOGGER.info("Processing finished");
      return zip.delete();
    } catch (IOException e) {
      LOGGER.error("Error with files",e);
      return false;
    }

  }
  //Non multi-thread
  public void saveFile(File toSave,String name) throws IOException {
    FileUtils.copyFile(toSave, new File(tempdirPath+"/"+name));
    LOGGER.info("Saving File: {}",name);
  }

  private void unzip(String filename,String tempdirPath) throws IOException {
    LOGGER.info("Unzipping File: {} into {}",filename,tempdirPath);
    ZipFile toextract= new ZipFile(filename);
    toextract.extractAll(tempdirPath);
    toextract.close();
  }
  private void replaceOldDoc(String unzipPath) throws IOException {
    //TODO supprimer l'ancienne doc puis copier la nouvelle
    LOGGER.info("Delete old Doc...");
    String docPath = config.getBase() + "/" + config.getDocFolder() + "/";
    FileUtils.deleteDirectory(new File(docPath));
    LOGGER.info("Copying new Doc...");
    FileUtils.copyDirectory(new File(unzipPath+"/"),new File(docPath));
    FileUtils.deleteDirectory(new File(unzipPath));
  }
}
