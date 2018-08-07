package fr.techad.edc.httpd;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.techad.edc.httpd.search.IndexService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 24/04/2018.
 */
public class IndexerHandler implements HttpHandler {
    static final Logger LOGGER = LoggerFactory.getLogger(IndexerHandler.class);
    private final ObjectMapper objectMapper;
    private final WebServerConfig config;

    public IndexerHandler(ObjectMapper objectMapper, WebServerConfig config) {
        this.objectMapper = objectMapper;
        this.config = config;
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
        LOGGER.debug("Request to reindex the content");
        IndexService indexService = new IndexService(config);
        indexService.indexContent();

        byte[] bytes = objectMapper.writeValueAsBytes(Collections.singletonMap("status", "indexation request received"));
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(bytes));
    }
}
