package fr.techad.edc.httpd.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
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
    try {
      String tmpdir = Files.createTempDirectory("unzip").toFile().getAbsolutePath();
      unzip(tempdirPath + "/" + name, tmpdir + "/doc");
      File zip = new File(tempdirPath + "/" + name);
      if(replaceOldDoc(new File(tmpdir), override)) {
        LOGGER.debug("Request to reindex the content");
        IndexService indexService = new IndexService(config);
        indexService.indexContent();
        LOGGER.info("Processing finished");
        return zip.delete();
      };
      return false;
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

  private boolean replaceOldDoc(File unziped, boolean override) throws IOException {
    String docPath = config.getBase() + "/" + config.getDocFolder() + "/";
    if (verifyStructure(unziped)) {
      if (override) {
        LOGGER.info("Copying new Doc with overrinding i18n");
      } else {
        LOGGER.info("Copying new Doc with keeping i18n");
      }
      synchronize(new File(unziped.getAbsolutePath() + "/doc"), docPath, override);
      FileUtils.deleteDirectory(unziped);
      return true;
    }
    FileUtils.deleteDirectory(unziped);
    return false;
  }
  private void cleanPreviousDoc(File directory, String docPath,boolean override) throws IOException {
    List<File> directoriestoCopy = (List<File>) FileUtils.listFilesAndDirs(directory,
        new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
    List<File> directoriestoClean = (List<File>) FileUtils.listFilesAndDirs(new File(docPath),
        new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
    for (int i = directoriestoCopy.size() - 1; i > 0; i--) {
      for (int j = directoriestoClean.size() - 1; j > 0; j--) {
        // Get correct path
        String path1 = directoriestoCopy.get(i).getPath();
        String path2 = directoriestoClean.get(j).getPath();
        String[] split1 = path1.split("doc");
        String[] split2 = path2.split("doc");
        path1 = split1[1];
        path2 = split2[1];
        if (override) {
          if (path1.equals(path2)) {// test if folders are same
            LOGGER.debug("Cleaning :{}", directoriestoClean.get(j).getCanonicalPath());
            FileUtils.cleanDirectory(directoriestoClean.get(j));
          }
        } else {
          if (path1.equals(path2) && !(directoriestoClean.get(j).getPath().contains("i18n"))) {
            LOGGER.debug("Cleaning :{}", directoriestoClean.get(j).getCanonicalPath());
            FileUtils.cleanDirectory(directoriestoClean.get(j));
          }
        }
      }
    }
  }
   private void synchronize(File directory, String docPath, boolean override) throws IOException {
    String[] extensions = { "bmp", "dib", "eps", "ico", "webp", "jpg", "jpeg", "jpe", "jif", "jfif", "jfi", "jp2",
        "j2k", "jpf", "jpx", "jpm", "mj2", "tiff", "tif", "json", "svg", "svgz", "pdf", "css", "html", "png", "txt",
        "gif","ai" };
    List<File> filestoCopy = (List<File>) FileUtils.listFiles(directory, extensions, true);
    cleanPreviousDoc(directory, docPath, override);
    for (File f : filestoCopy) {
      String path1 = f.getPath();
      String[] split1 = path1.split("doc");
      if (f.getName().equals("multi-doc.json")) {
        path1 = split1[1] + "doc" + split1[2];
      } else {
        path1 = split1[1];
      }
      if (f.getPath().contains("i18n") && new File(docPath + path1).exists() && !override) {
        LOGGER.debug("Not overwrinting :{}", docPath + path1);
      } else {
        LOGGER.debug("Copying {} to {}", f.getPath(), docPath + path1);
        FileUtils.copyFile(f, new File(docPath + path1));
      }
    }
  }

  public boolean verifyStructure(File toverify) {
    LOGGER.debug("Verifying :{}", toverify.getName());
    String directory = "i18n";
    String file = "multi-doc.json";
    return (new File(toverify.getAbsolutePath() + "/doc/" + directory).exists()
        && new File(toverify.getAbsolutePath() + "/doc/" + file).exists());
  }
}
