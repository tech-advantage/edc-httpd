package fr.techad.edc.httpd.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.highlight.SpanGradientFormatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
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
  private static final String[] SEARCH_FIELDS = { DOC_LABEL, DOC_CONTENT, DOC_TYPE };
  private static final Map<String, Float> BOOTS;

  static {
    Map<String, Float> aMap = new HashMap<>();
    aMap.put(DOC_LABEL, 2f);
    aMap.put(DOC_CONTENT, 1f);
    aMap.put(DOC_TYPE, .5f);
    BOOTS = Collections.unmodifiableMap(aMap);
  }

  private IndexSearcher indexSearcher;
  private IndexReader reader;

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
      String defaultLanguage) throws IOException, ParseException, InvalidTokenOffsetsException {
    // Handle wildcard with exacttMode condition
    if (!exact && !search.endsWith("*")) {
      search = search + "*";
    }

    List<DocumentationSearchResult> results = new ArrayList<>();
    createSearcher();
    QueryParser qp = new MultiFieldQueryParser(SEARCH_FIELDS, new StandardAnalyzer(), BOOTS);
    String langSearch = "";
    if (StringUtils.isNotBlank(lang)) {
      langSearch = " AND languageCode:" + lang;
    }
    Query query = qp.parse(search + langSearch);
    TopDocs hits = indexSearcher.search(query, limit);
    LOGGER.debug("Found {} results for the search '{}'", hits.totalHits, search);

    SpanGradientFormatter formatter = new SpanGradientFormatter(0, null, null, "#FFFFFF", "#fff2a8");

    QueryScorer labelScorer = new QueryScorer(query, DOC_LABEL);
    Fragmenter labelFragmenter = new SimpleSpanFragmenter(labelScorer);
    Highlighter labelHighlighter = new Highlighter(formatter, labelScorer);
    labelHighlighter.setTextFragmenter(labelFragmenter);

    QueryScorer contentScorer = new QueryScorer(query, DOC_CONTENT);
    Fragmenter contentFragmenter = new SimpleSpanFragmenter(contentScorer, search.length());
    Highlighter contentHighlighter = new Highlighter(formatter, contentScorer);
    contentHighlighter.setTextFragmenter(contentFragmenter);

    for (ScoreDoc sd : hits.scoreDocs) {
      Document d = indexSearcher.doc(sd.doc);
      String label = d.get(DOC_LABEL);
      String content = d.get(DOC_CONTENT);

      TokenStream labelTokenStream = TokenSources.getAnyTokenStream(reader,
                sd.doc, DOC_LABEL, d, new StandardAnalyzer());

      TokenStream contentTokenStream = TokenSources.getAnyTokenStream(reader,
              sd.doc, DOC_CONTENT, d, new StandardAnalyzer());

      String labelFragment = labelHighlighter.getBestFragment(labelTokenStream, label);
      String contentFragment = contentHighlighter.getBestFragment(contentTokenStream, content);

      DocumentationSearchResult documentationSearchResult = new DocumentationSearchResult();
      String idStr = d.get(DOC_ID);
      documentationSearchResult.setId(Long.valueOf(idStr));
      documentationSearchResult.setLabel(labelFragment != null ? labelFragment : d.get(DOC_LABEL));
      documentationSearchResult.setStrategyId(Long.valueOf(d.get(DOC_STRATEGY_ID)));
      documentationSearchResult.setStrategyLabel(d.get(DOC_STRATEGY_LABEL));
      documentationSearchResult.setLanguageCode(d.get(DOC_LANGUAGE_CODE));
      documentationSearchResult.setUrl(d.get(DOC_URL));
      documentationSearchResult.setHighlightContent(contentFragment != null ? contentFragment : "");
      documentationSearchResult.setType(d.get(DOC_TYPE));
      results.add(documentationSearchResult);
    }
    if (results.isEmpty() && !defaultLanguage.equals(lang)) {
      return search(search, defaultLanguage, limit, exact, defaultLanguage);
    }

    return results;
  }

  private void createSearcher() throws IOException {
    if (indexSearcher == null) {
      Directory dir = FSDirectory.open(getIndexPath());
      reader = DirectoryReader.open(dir);
      indexSearcher = new IndexSearcher(reader);
    }
  }

}
