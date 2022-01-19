package fr.techad.edc.httpd.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.techad.edc.httpd.WebServerConfig;
import net.lingala.zip4j.ZipFile;

public class GetAndDeployService {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetAndDeployService.class);
  private final WebServerConfig config;
  private final String tempdirPath = FileUtils.getTempDirectoryPath();

  public GetAndDeployService(WebServerConfig config) {
    this.config = config;
  }

  public boolean processing(String name, boolean override) {
    String docPath = config.getBase() + "/" + config.getDocFolder() + "/";
    File zip = new File(tempdirPath + "/" + name);
    File tmpdir = null;
    boolean succeed = false;
    try {
      tmpdir = Files.createTempDirectory("unzip").toFile();
      unzip(tempdirPath + "/" + name, tmpdir.getAbsolutePath());
      if (verifyStructure(tmpdir)) {
        cleanPreviousDoc(tmpdir, docPath, override);
        synchronize(tmpdir, docPath, override);
        succeed = true;
      }
    } catch (IOException e) {
      LOGGER.error("Error in processing operations", e);
    }
    removeTempFiles(tmpdir, zip,docPath);
    LOGGER.info("Processing finished");
    return succeed;
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

  private void removeTempFiles(File tmpdir, File zip,String docPath) {
    try {
      if (tmpdir.exists()) {
        FileUtils.deleteDirectory(tmpdir);
        LOGGER.debug("Cleaning :{}", tmpdir.getCanonicalPath());
      }
      File[] dirToRemove = new File(docPath).listFiles(File::isDirectory);
      for(File f : dirToRemove) {
        if(f.getName().contains("-old")) {
          FileUtils.deleteDirectory(f);
          LOGGER.debug("Cleaning :{}", f.getCanonicalPath());
        }
      }
      zip.delete();
    } catch (IOException e) {
      LOGGER.error("Error in removing temporary Files", e);
    }
  }

  private void unzip(String filename, String tempdirPath) throws IOException {
    LOGGER.info("Unzipping File: {} into {}", filename, tempdirPath);
    ZipFile toextract = new ZipFile(filename);
    toextract.extractAll(tempdirPath);
    toextract.close();
  }

  private void cleanPreviousDoc(File directory, String docPath, boolean override) throws IOException {
    if (new File(docPath).exists()) {
      File[] directoriestoClean = new File(docPath).listFiles(File::isDirectory);
        for (File dir : directoriestoClean) {
            if (dir.getName().equals("i18n")) {
              if (override) {
              LOGGER.debug("Cleaning :{}", dir.getCanonicalPath());
              FileUtils.cleanDirectory(dir);
            }
          }
      }
    }
  }

  private void synchronize(File directory, String docPath, boolean override) throws IOException {
    String[] extensions = { "bmp", "dib", "eps", "ico", "webp", "jpg", "jpeg", "jpe", "jif", "jfif", "jfi", "jp2",
        "j2k", "jpf", "jpx", "jpm", "mj2", "tiff", "tif", "json", "svg", "svgz", "pdf", "css", "html", "png", "txt",
        "gif", "ai" };
    List<File> filestoCopy = (List<File>) FileUtils.listFiles(directory, extensions, true);
    String name = "";
    if (override) {
      LOGGER.info("Copying new Doc with overrinding i18n");
    } else {
      LOGGER.info("Copying new Doc with keeping i18n");
    }
    for (File f : filestoCopy) {
      String path1 = f.getPath();
      path1 = path1.replace(directory.getCanonicalPath(), "");
      path1 = path1.replace("\\", "/");
      String [] tmp=path1.split("/");
       name = tmp[1];
       if(!(name.equals("multi-doc.json")||name.equals("i18n"))) {
         tmp[1]=name+"-new";
         path1=StringUtils.join(tmp,"/");
       }
      if (f.getPath().contains("i18n") && new File(docPath + path1).exists() && !override) {
        LOGGER.debug("Not override :{}", docPath + path1);
      } else {
        LOGGER.debug("Copying {} to {}", f.getPath(), docPath + path1);
        FileUtils.copyFile(f, new File(docPath + path1));
      }
    }
    File[] dirToMove = new File(docPath).listFiles(File::isDirectory);
    for (File f : dirToMove) {
        new File(f.getCanonicalPath().replace("-new", "")).renameTo(new File(f.getCanonicalPath().replace("-new", "-old"))); 
        if(f.getName().contains("-new")){
          f.renameTo(new File(f.getCanonicalPath().replace("-new", "")));
      }
    }
  }

  private boolean verifyStructure(File toverify) {
    LOGGER.debug("Verifying :{}", toverify.getName());
    String directory = "i18n";
    String file = "multi-doc.json";
    return (new File(toverify.getAbsolutePath() + "/" + directory).exists()
        && new File(toverify.getAbsolutePath() + "/" + file).exists());
  }
}
