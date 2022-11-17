package fr.techad.edc.httpd.search;

import fr.techad.edc.httpd.WebServerConfig;
import fr.techad.edc.httpd.utils.ServerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TECH ADVANTAGE All right reserved Created by cochon on 24/04/2018.
 */
public class ContentBase {

  public static final String DOC_ID = "id";
  public static final String DOC_STRATEGY_ID = "strategyId";
  public static final String DOC_STRATEGY_LABEL = "strategyLabel";
  public static final String DOC_LANGUAGE_CODE = "languageCode";
  public static final String DOC_LABEL = "label";
  public static final String DOC_TYPE = "type";
  public static final String DOC_CONTENT = "content";
  public static final String DOC_URL = "url";
  private final Path IndexCaseInsensitivePath;
  private final Path IndexCaseSensitivePath;
  private final WebServerConfig config;
  static final Logger LOGGER = LoggerFactory.getLogger(ServerUtils.class);

  protected ContentBase(WebServerConfig webServerConfig) {
    String configCaseInsensitivePath = getConfigIndexPath(webServerConfig.getIndexCaseInsensitivePath(), "\\.edc\\indexCaseInsensitive");
    String configCaseSensitivePath = getConfigIndexPath(webServerConfig.getIndexCaseSensitivePath(), "\\.edc\\indexCaseSensitive");

    this.IndexCaseInsensitivePath = Paths.get(configCaseInsensitivePath);
    this.IndexCaseSensitivePath = Paths.get(configCaseSensitivePath);

    this.config = webServerConfig;
  }

  private String getConfigIndexPath(String indexPath, String folderIndexPath){
    String configIndexPath = indexPath;
    if (StringUtils.isBlank(configIndexPath))
      configIndexPath = System.getProperty("user.home") + folderIndexPath;

    return configIndexPath;
  }

  protected WebServerConfig getConfig(){
    return this.config;
  }

  protected Path getIndexCaseInsensitivePath() {
    return this.IndexCaseInsensitivePath;
  }

  protected Path getIndexCaseSensitivePath() {
    return this.IndexCaseSensitivePath;
  }
}