package fr.techad.edc.httpd;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.techad.edc.httpd.search.UploadService;
import fr.techad.edc.httpd.utils.TokenUtils;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.FormParserFactory.Builder;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public class UploadHandler implements HttpHandler {

  static final Logger LOGGER = LoggerFactory.getLogger(UploadHandler.class);
  private TokenUtils tokenutils;
  private WebServerConfig config;
  private ObjectMapper objectMapper;

  public UploadHandler(ObjectMapper objectMapper, WebServerConfig config, TokenUtils tokenutils) {
    this.objectMapper = objectMapper;
    this.config = config;
    this.tokenutils = tokenutils;
  }

  public void handleRequest(HttpServerExchange exchange) throws Exception {
    Builder builder = FormParserFactory.builder();
    final FormDataParser formDataParser = builder.build().createParser(exchange);
    if (exchange.getConnection() != null)
      exchange.setMaxEntitySize(config.getRequestMaxSize());
    // If not present Test can't be done
    if (tokenutils.getAndVerifyToken(exchange)) {
      exchange.dispatch((e) -> {
        exchange.startBlocking();
        if (formDataParser != null) {
          LOGGER.debug("Request to upload a file");
          FormData formData = formDataParser.parseBlocking();
          for (String data : formData) {
            for (FormData.FormValue formValue : formData.get(data)) {
              if (formValue.isFileItem()) {
                File uploadedFile = formValue.getFileItem().getFile().toFile();
                UploadService service = new UploadService(config);
                String name = formValue.getFileName();
                Thread thread = new Thread(() -> {
                  service.processing(name);
                });
                if (service.saveZipFromHeader(uploadedFile, name)) {
                  thread.start();

                  byte[] bytes = objectMapper.writeValueAsBytes(Collections.singletonMap("status", "Upload Complete"));
                  exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                  exchange.getResponseSender().send(ByteBuffer.wrap(bytes));
                } else
                  exchange.setStatusCode(StatusCodes.UNSUPPORTED_MEDIA_TYPE);
              }
            }
          }
          formDataParser.close();
        } else {
          exchange.setStatusCode(StatusCodes.BAD_REQUEST);
        }
      });
    } else {
      exchange.setStatusCode(StatusCodes.UNAUTHORIZED);
    }

  }

}
