package fr.techad.edc.httpd.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.techad.edc.httpd.WebServerConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * TECH ADVANTAGE All right reserved Created by cochon on 23/04/2018.
 */
public class ContentIndexer extends ContentBase {
  static final Logger LOGGER = LoggerFactory.getLogger(ContentIndexer.class);

  private final String docBase;
  private IndexWriter indexWriter;
  private long counter;

  public ContentIndexer(WebServerConfig webServerConfig) {
    super(webServerConfig);
    this.docBase = webServerConfig.getBase() + "/" + webServerConfig.getDocFolder() + "/";
  }

  /**
   * Index the help content
   *
   * @return the number of indexed files
   * @throws IOException If an error is occurred to write the index ot to read the
   *                     content.
   */
  public long index() throws IOException {
    LOGGER.info("Start help indexing");
    counter = 0;
    createIndexWriter();
    indexWriter.deleteAll();
    List<MultiDocItem> multiDocItems = getMultiDoc();

    indexMultiDoc(multiDocItems);
    indexWriter.commit();
    indexWriter.close();
    LOGGER.info("Help indexing ending, indexed {} items", counter);
    return counter;
  }

  private List<MultiDocItem> getMultiDoc() throws IOException {
    File multiDocFile = FileUtils.getFile(new File(docBase), "multi-doc.json");
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(multiDocFile, new TypeReference<List<MultiDocItem>>() {
    });
  }

  private void indexMultiDoc(List<MultiDocItem> multiDocItems) throws IOException {
    LOGGER.debug("Index {} products", multiDocItems.size());
    for (MultiDocItem multiDocItem : multiDocItems)
      indexMultiDocItem(multiDocItem);
  }

  private void indexMultiDocItem(MultiDocItem multiDocItem) throws IOException {
    LOGGER.debug("Index the product: '{}' with id: '{}'", multiDocItem.getPluginId(), multiDocItem.getProductId());
    String productFolder = docBase + multiDocItem.getPluginId() + "/";
    File tocJsonFile = new File(productFolder + "toc.json");
    if (tocJsonFile.exists()) {
      ObjectMapper objectMapper = new ObjectMapper();
      Toc toc = objectMapper.readValue(tocJsonFile, Toc.class);
      List<TocReference> tocReferences = toc.getToc();
      LOGGER.debug("Found {} toc files", tocReferences.size());
      for (TocReference tocReference : tocReferences) {
        indexTocReference(productFolder, tocReference);
      }
    } else {
      LOGGER.info("The help product '{}' is not available, skip it", multiDocItem.getPluginId());
    }
  }

  private void indexTocReference(String productFolder, TocReference tocReference) throws IOException {
    File tocXJsonFile = new File(productFolder + tocReference.getFile());
    if (tocXJsonFile.exists()) {
      FileInputStream fileInputStream = FileUtils.openInputStream(tocXJsonFile);
      String content = IOUtils.toString(fileInputStream, Charset.forName("UTF-8"));
      fileInputStream.close();
      ObjectMapper mapper = new ObjectMapper();
      JsonNode actualObj = mapper.readTree(content);
      Long strategyId = actualObj.get("id").asLong();
      String languageCode;
      Iterator<Map.Entry<String, JsonNode>> fields = actualObj.fields();
      for (Iterator<Map.Entry<String, JsonNode>> it = fields; it.hasNext();) {
        Map.Entry<String, JsonNode> field = it.next();
        if (!field.getKey().equals("id")) {
          // The key is the language code
          languageCode = field.getKey();
          // Get the root json object for this language
          JsonNode fieldValue = field.getValue();
          // Get the label
          String strategyLabel = fieldValue.get("label").asText();
          // So go to index the topics
          indexTopics(strategyId, languageCode, strategyLabel, fieldValue.get("topics"));

        }
      }
    } else {
      LOGGER.info("The toc file '{}' doesn't exit, ignore it", tocReference.getFile());
    }
  }

  private void indexTopics(Long strategyId, String languageCode, String strategyLabel, JsonNode rootNode)
      throws IOException {
    ConcurrentLinkedQueue<JsonNode> topicsQueue = new ConcurrentLinkedQueue();
    if (rootNode.isArray()) {
      rootNode.forEach(topicsQueue::add);
    }

    while (!topicsQueue.isEmpty()) {
      JsonNode topic = topicsQueue.poll();
      JsonNode topics = topic.get("topics");
      if (topics.isArray()) {
        topics.forEach(topicsQueue::add);
      }
      indexTopic(strategyId, languageCode, strategyLabel, topic);
    }
  }

  private void indexTopic(Long strategyId, String languageCode, String strategyLabel, JsonNode topicNode)
      throws IOException {
    String id = topicNode.get("id").asText();
    String label = topicNode.get("label").asText();
    String type = topicNode.get("type").asText("CHAPTER");
    String fileName = topicNode.get("url").asText();
    LOGGER.debug("Help document to index: id: {}, label: {}, url: {}, type: {}", id, label, fileName, type);

    Document document = new Document();
    document.add(new StringField(DOC_ID, id, Field.Store.YES));
    document.add(new StringField(DOC_STRATEGY_ID, strategyId.toString(), Field.Store.YES));
    document.add(new StringField(DOC_LANGUAGE_CODE, languageCode, Field.Store.YES));
    document.add(new TextField(DOC_TYPE, type, Field.Store.YES));
    document.add(new TextField(DOC_STRATEGY_LABEL, strategyLabel, Field.Store.YES));
    document.add(new TextField(DOC_LABEL, label, Field.Store.YES));
    if (type.equals("DOCUMENT")) {
      org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(new File(docBase + "/" + fileName), "UTF-8");
      String content = jsoupDoc.text();
      document.add(new TextField(DOC_CONTENT, content, Field.Store.YES));
    }
    document.add(new TextField(DOC_URL, fileName, Field.Store.YES));
    this.indexWriter.addDocument(document);
    counter++;
  }

  private void createIndexWriter() throws IOException {
    FSDirectory dir = FSDirectory.open(getIndexPath());
    IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
    indexWriter = new IndexWriter(dir, config);
  }
}
