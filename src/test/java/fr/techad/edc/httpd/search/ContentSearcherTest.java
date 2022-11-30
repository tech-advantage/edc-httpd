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

  private ContentSearcher getContentSearcher(){
    File file = new File("src/test/resources/edc-doc");
    WebServerConfig webServerConfig = new WebServerConfig();
    webServerConfig.setBase(file.getAbsolutePath());
    ContentSearcher contentSearcher = new ContentSearcher(webServerConfig);

    return contentSearcher;
  }

  @Test
  public void shouldSearchStorehouse() throws IOException, ParseException {
    ContentSearcher contentSearcher = getContentSearcher();
    List<DocumentationSearchResult> searchResults = contentSearcher.search("storehouse", "", 100, true, false, null, null);
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

  @Test
  public void shouldSearchInstanceExactMatch() throws IOException, ParseException{
    ContentSearcher contentSearcher = getContentSearcher();
    List<DocumentationSearchResult> searchResults = contentSearcher.search("Instance", "", 100, true, false, null, null);
    Assertions.assertEquals(3, searchResults.size());
  }

  @Test
  public void shouldSearchInstanceExactMatchAndMatchCase() throws IOException, ParseException{
    ContentSearcher contentSearcher = getContentSearcher();
    List<DocumentationSearchResult> searchResults = contentSearcher.search("Instance", "", 100, true, true, null, null);
    Assertions.assertEquals(1, searchResults.size());
  }

  @Test
  public void shouldSearchProductMatchCase() throws IOException, ParseException {
    ContentSearcher contentSearcher = getContentSearcher();
    List<DocumentationSearchResult> searchResults = contentSearcher.search("Product", "", 100, true, true, null, null);
    Assertions.assertEquals(5, searchResults.size());

    long nbProductInLabel = 0;
    for (int i = 0; i < searchResults.size(); i++) {
      DocumentationSearchResult documentationSearchResult = searchResults.get(i);
      if (documentationSearchResult.getLabel().contains("Product"))
        nbProductInLabel++;
    }
    Assertions.assertEquals(1, nbProductInLabel);
  }

  @Test
  public void shouldMatchDocumentWithAllWordsInSearch() throws IOException, ParseException{
    ContentSearcher contentSearcher = getContentSearcher();
    List<DocumentationSearchResult> searchResults = contentSearcher.search("Instance products*", "", 100, true, true, null, null);
    Assertions.assertEquals(1, searchResults.size());
  }
}