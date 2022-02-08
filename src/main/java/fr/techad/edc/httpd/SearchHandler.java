package fr.techad.edc.httpd;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.techad.edc.httpd.search.ContentSearcher;
import fr.techad.edc.httpd.search.DocumentationSearchResult;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

/**
 * TECH ADVANTAGE All right reserved Created by cochon on 24/04/2018.
 */
public class SearchHandler implements HttpHandler {
  static final Logger LOGGER = LoggerFactory.getLogger(SearchHandler.class);
  private final ObjectMapper objectMapper;
  private final WebServerConfig config;

  public SearchHandler(ObjectMapper objectMapper, WebServerConfig config) {
    this.objectMapper = objectMapper;
    this.config = config;
  }

  public void handleRequest(HttpServerExchange exchange) throws Exception {

    Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
    LOGGER.debug("Query Parameters: {}, Query: {}", queryParameters, exchange.getQueryString());

    Deque<String> query = queryParameters.get("query");
    Deque<String> language = queryParameters.get("lang");
    Deque<String> limit = queryParameters.get("limit");
    Deque<String> exact = queryParameters.get("exact");
    Boolean exactMode = false;
    String lang = "";
    int limitResults = 100;
    if (exact != null ) {
      exactMode = BooleanUtils.toBoolean(exact.element());
    }if(language != null){
      lang = language.element();
    }
    if(limit != null) {
      try {
        limitResults = Integer.valueOf(limit.element());
      } catch (NumberFormatException ex) {
        LOGGER.debug("Limit is not a number, using this default limit :{}", limitResults);
      }
    }

    byte[] bytes;
    if (query != null && limitResults > 0) {
      String search = query.element();
      


      ContentSearcher contentSearcher = new ContentSearcher(config);
      List<DocumentationSearchResult> searchResults = contentSearcher.search(search, lang, limitResults, exactMode,getDefaultLanguage());
      bytes = objectMapper.writeValueAsBytes(searchResults);
    } else {
      bytes = objectMapper.writeValueAsBytes(Collections.singletonMap("error", "malformed query"));
    }
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
    exchange.getResponseSender().send(ByteBuffer.wrap(bytes));
  }
  
  private String getDefaultLanguage() throws IOException {
    File docFolder = new File(this.config.getBase() + "/" + this.config.getDocFolder() + "/");
    File[] products = docFolder.listFiles(File::isDirectory);
    String parsed = "";
    if (!products[0].getName().equals("i18n")) {
      parsed = FileUtils.readFileToString(new File(products[0].getCanonicalPath() + "/info.json"),
          StandardCharsets.UTF_8.name());
    } else {
      parsed = FileUtils.readFileToString(new File(products[1].getCanonicalPath() + "/info.json"),
          StandardCharsets.UTF_8.name());
    }
    JSONObject obj = new JSONObject(parsed);
    return obj.getString("defaultLanguage");
  }
}
