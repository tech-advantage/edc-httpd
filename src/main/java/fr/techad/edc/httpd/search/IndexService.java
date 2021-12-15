package fr.techad.edc.httpd.search;

import fr.techad.edc.httpd.WebServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TECH ADVANTAGE All right reserved Created by cochon on 07/08/2018.
 */
public class IndexService {
  private static final Logger LOGGER = LoggerFactory.getLogger(IndexService.class);
  private final WebServerConfig config;

  public IndexService(WebServerConfig config) {
    this.config = config;
  }

  public void indexContent() {
    LOGGER.info("Indexation Request received");
    ExecutorService executor = Executors.newFixedThreadPool(10);

    Runnable indexTask = () -> {
      try {
        LOGGER.info("Start indexation");
        ContentIndexer contentIndexer = new ContentIndexer(config);
        contentIndexer.index();
      } catch (IOException e) {
        LOGGER.error("Error during indexation", e);
      }
    };

    executor.execute(indexTask);
  }
}
