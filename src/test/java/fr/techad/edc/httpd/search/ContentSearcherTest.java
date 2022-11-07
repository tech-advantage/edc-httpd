package fr.techad.edc.httpd.search;

import fr.techad.edc.httpd.WebServerConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * TECH ADVANTAGE All right reserved Created by cochon on 24/04/2018.
 */
public class ContentSearcherTest {

  @Test
  public void shouldSearchStorehouse() throws IOException, ParseException {
    File file = new File("src/test/resources/edc-doc");
    WebServerConfig webServerConfig = new WebServerConfig();
    webServerConfig.setBase(file.getAbsolutePath());
    ContentSearcher contentSearcher = new ContentSearcher(webServerConfig);
    List<DocumentationSearchResult> searchResults = contentSearcher.search("storehouse", "", 100, true, "", null);
    Assertions.assertEquals(11, searchResults.size());

    // check weight : first the query word in the label, then in the content
    long nbStorehouseInLabel = 0;
    for (int i = 0; i < 6; i++) {
      DocumentationSearchResult documentationSearchResult = searchResults.get(i);
      if (documentationSearchResult.getLabel().toLowerCase().contains("storehouse"))
        nbStorehouseInLabel++;
    }
    Assertions.assertEquals(6, nbStorehouseInLabel);
    long nbNoStorehouseInLabel = 0;
    for (int i = 6; i < searchResults.size(); i++) {
      DocumentationSearchResult documentationSearchResult = searchResults.get(i);
      if (!documentationSearchResult.getLabel().toLowerCase().contains("storehouse"))
        nbNoStorehouseInLabel++;
    }
    Assertions.assertEquals(5, nbNoStorehouseInLabel);
  }
}
