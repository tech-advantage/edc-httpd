package fr.techad.edc.httpd;

import java.nio.ByteBuffer;
import java.util.*;

import fr.techad.edc.httpd.utils.LangUtils;
import org.apache.commons.lang3.BooleanUtils;
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
  private WebServerConfig config;

  public SearchHandler(ObjectMapper objectMapper, WebServerConfig config) {
    this.objectMapper = objectMapper;
    this.config = config;
  }

  public void handleRequest(HttpServerExchange exchange) throws Exception {

    Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
    LOGGER.debug("Query Parameters: {}, Query: {}", queryParameters, exchange.getQueryString());

    Deque<String> query = queryParameters.get("query");

    Boolean exactMatch = BooleanUtils.toBoolean(getParamValue("match-whole-word", queryParameters));
    String lang = getParamValue("lang", queryParameters);

    int limitResults = 100;
    try {
      limitResults = Integer.valueOf(getParamValue("max-result-number", queryParameters));
    } catch (NumberFormatException ex) {
      LOGGER.error("Limit is not a number, using this default limit :{}", limitResults);
    }
    byte[] bytes;
    if (query != null && limitResults > 0) {
      String search = query.element();
      ContentSearcher contentSearcher = new ContentSearcher(config);
      List<DocumentationSearchResult> searchResults = contentSearcher.search(search, lang, limitResults, exactMatch,
          LangUtils.getDefaultLanguage(config), LangUtils.findLanguages(config));
      bytes = objectMapper.writeValueAsBytes(searchResults);
    } else {
      bytes = objectMapper.writeValueAsBytes(Collections.singletonMap("error", "malformed query"));
    }
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
    exchange.getResponseSender().send(ByteBuffer.wrap(bytes));
  }

  private String getParamValue(String parameterName, Map<String, Deque<String>> queryParameters) {
    Deque<String> param = queryParameters.get(parameterName);
    if (param != null) {
      return param.element();
    } else {
      return "";
    }
  }
}
