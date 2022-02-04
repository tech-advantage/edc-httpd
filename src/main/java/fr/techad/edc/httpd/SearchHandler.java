package fr.techad.edc.httpd;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.techad.edc.httpd.search.ContentSearcher;
import fr.techad.edc.httpd.search.DocumentationSearchResult;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

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
    Deque<String> strict = queryParameters.get("strict");
    Boolean strictMode = false;
    String lang = "";
    int limitNumber = 100;
    if (strict != null && language != null && limit != null) {
      strictMode = BooleanUtils.toBoolean(strict.element());
      lang = language.element();
      try {
        limitNumber = Integer.valueOf(limit.element());
      } catch (NumberFormatException ex) {
        LOGGER.debug("Limit is not a number, using this default limit :{}", limitNumber);
      }
    }

    byte[] bytes;
    if (query != null && limitNumber > 0) {
      String search = query.element();
      // Handle wildcard with StrictMode condition
      if (!strictMode && !search.endsWith("*")) {
        search = search + "*";
      }

      ContentSearcher contentSearcher = new ContentSearcher(config);
      List<DocumentationSearchResult> searchResults = contentSearcher.search(search, lang, limitNumber, strictMode);
      bytes = objectMapper.writeValueAsBytes(searchResults);
    } else {
      bytes = objectMapper.writeValueAsBytes(Collections.singletonMap("error", "malformed query"));
    }
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
    exchange.getResponseSender().send(ByteBuffer.wrap(bytes));
  }
}
