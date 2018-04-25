package fr.techad.edc.httpd;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.techad.edc.httpd.search.ContentSearcher;
import fr.techad.edc.httpd.search.DocumentationSearchResult;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 24/04/2018.
 */
public class SearchHandler implements HttpHandler {
    static final Logger LOGGER = LoggerFactory.getLogger(SearchHandler.class);
    private final ObjectMapper objectMapper;

    public SearchHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {

        Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
        LOGGER.debug("Query Parameters: {}, Query: {}", queryParameters, exchange.getQueryString());

        Deque<String> query = queryParameters.get("query");
        byte[] bytes;
        if (query != null) {
            String search = query.element();
            ContentSearcher contentSearcher = new ContentSearcher(ConfigManager.getInstance().getWebServerConfig());
            List<DocumentationSearchResult> searchResults = contentSearcher.search(search);
            bytes = objectMapper.writeValueAsBytes(searchResults);
        } else {
            bytes = objectMapper.writeValueAsBytes(Collections.singletonMap("error", "malformed query"));
        }
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(bytes));
    }
}
