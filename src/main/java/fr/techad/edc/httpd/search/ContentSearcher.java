package fr.techad.edc.httpd.search;

import java.io.IOException;
import java.util.*;

import fr.techad.edc.httpd.utils.CaseSensitiveStandardAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.techad.edc.httpd.WebServerConfig;

/**
 * TECH ADVANTAGE All right reserved Created by cochon on 24/04/2018.
 */
public class ContentSearcher extends ContentBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(ContentSearcher.class);
  private final static String[] SEARCH_FIELD_NORMAL_CASE = { DOC_LABEL, DOC_CONTENT_NORMAL_CASE, DOC_TYPE };
  private final static String[] SEARCH_FIELD_LOWER_CASE = { DOC_LABEL, DOC_CONTENT_LOWER_CASE, DOC_TYPE };
  private static final Map<String, Float> BOOTS_NORMAL_CASE;
  private static final Map<String, Float> BOOTS_LOWER_CASE;
  static {
    Map<String, Float> normalCaseMap = new HashMap<>();
    Map<String, Float> lowerCaseMap = new HashMap<>();
    normalCaseMap.put(DOC_LABEL, 2f);
    normalCaseMap.put(DOC_CONTENT_NORMAL_CASE, 1f);
    normalCaseMap.put(DOC_TYPE, .5f);
    lowerCaseMap.put(DOC_LABEL, 2f);
    lowerCaseMap.put(DOC_CONTENT_LOWER_CASE, 1f);
    lowerCaseMap.put(DOC_TYPE, .5f);
    BOOTS_NORMAL_CASE = Collections.unmodifiableMap(normalCaseMap);
    BOOTS_LOWER_CASE = Collections.unmodifiableMap(lowerCaseMap);
  }

  private IndexSearcher indexSearcher;
  private QueryParser qp;

  public ContentSearcher(WebServerConfig webServerConfig) {
    super(webServerConfig);
  }

  /**
   * Search in the help content.
   *
   * @param search the query search
   * @return the list of result
   * @throws IOException    is an error is occurred to read indexed file
   * @throws ParseException if the search parameter is malformed
   */
  public List<DocumentationSearchResult> search(String search, String lang, int limit, boolean exact,
      boolean matchCase, String defaultLanguage, Set<String> languages) throws IOException, ParseException {
    // Handle wildcard with exacttMode condition
    if (!exact && !search.endsWith("*")) {
      search = search + "*";
    }

    List<DocumentationSearchResult> results = new ArrayList<>();
    LOGGER.debug("Search {}", search);
    createSearcher();

    if(matchCase){
      qp = new MultiFieldQueryParser(SEARCH_FIELD_NORMAL_CASE, new CaseSensitiveStandardAnalyzer(), BOOTS_NORMAL_CASE);
    } else {
      qp = new MultiFieldQueryParser(SEARCH_FIELD_LOWER_CASE, new StandardAnalyzer(), BOOTS_LOWER_CASE);
    }

    qp.setAllowLeadingWildcard(true);
    qp.setDefaultOperator(QueryParser.Operator.AND);

    String langSearch = "";
    if (StringUtils.isNotBlank(lang)) {
      langSearch = " AND languageCode:" + lang;
    }
    Query query = qp.parse(search + langSearch);
    TopDocs hits = indexSearcher.search(query, limit);
    LOGGER.debug("Found {} results for the search '{}'", hits.totalHits, search);

    for (ScoreDoc sd : hits.scoreDocs) {
      Document d = indexSearcher.doc(sd.doc);
      DocumentationSearchResult documentationSearchResult = new DocumentationSearchResult();
      String idStr = d.get(DOC_ID);
      documentationSearchResult.setId(Long.valueOf(idStr));
      documentationSearchResult.setLabel(d.get(DOC_LABEL));
      documentationSearchResult.setStrategyId(Long.valueOf(d.get(DOC_STRATEGY_ID)));
      documentationSearchResult.setStrategyLabel(d.get(DOC_STRATEGY_LABEL));
      documentationSearchResult.setLanguageCode(d.get(DOC_LANGUAGE_CODE));
      documentationSearchResult.setUrl(d.get(DOC_URL));
      documentationSearchResult.setType(d.get(DOC_TYPE));
      results.add(documentationSearchResult);

    }
    if (results.isEmpty() && !defaultLanguage.equals(lang) && !languages.contains(lang)) {
      return search(search, defaultLanguage, limit, exact, matchCase, defaultLanguage, languages);
    }
    return results;
  }

  private void createSearcher() throws IOException {
    if (indexSearcher == null) {
      Directory dir = FSDirectory.open(getIndexPath());
      IndexReader reader = DirectoryReader.open(dir);
      indexSearcher = new IndexSearcher(reader);
    }
  }

}
