package fr.techad.edc.httpd;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.techad.edc.httpd.search.IndexService;
import fr.techad.edc.httpd.utils.TokenUtils;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

/**
 * TECH ADVANTAGE All right reserved Created by cochon on 24/04/2018.
 */
public class IndexerHandler implements HttpHandler {
  static final Logger LOGGER = LoggerFactory.getLogger(IndexerHandler.class);
  private final ObjectMapper objectMapper;
  private final WebServerConfig config;
  private final TokenUtils tokenutils;

  public IndexerHandler(ObjectMapper objectMapper, WebServerConfig config, TokenUtils tokenutils) {
    this.objectMapper = objectMapper;
    this.config = config;
    this.tokenutils = tokenutils;
  }


  public void handleRequest(HttpServerExchange exchange) throws Exception {
    if (tokenutils.verifyToken(exchange)) {
      LOGGER.debug("Request to reindex the content");
      IndexService indexService = new IndexService(config);
      indexService.indexContent();

      byte[] bytes = objectMapper.writeValueAsBytes(Collections.singletonMap("status", "indexation request received"));
      exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
      exchange.getResponseSender().send(ByteBuffer.wrap(bytes));
    } else {
      exchange.setStatusCode(StatusCodes.UNAUTHORIZED);
    }

  }
}
