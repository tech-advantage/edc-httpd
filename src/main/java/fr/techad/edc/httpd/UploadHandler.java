package fr.techad.edc.httpd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.body.BodyHandler;
import com.networknt.config.Config;
import com.networknt.handler.Handler;
import com.networknt.httpstring.ContentType;
import com.networknt.utility.NioUtils;

import fr.techad.edc.httpd.search.UploadService;
import fr.techad.edc.httpd.utils.TokenUtils;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.form.FormParserFactory.Builder;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

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

    private Optional<HeaderValues> getTokenInHeader(HttpServerExchange exchange) {
        HeaderValues headerToken = exchange.getRequestHeaders().get("Edc-Token");// get token from Headers
        return Optional.ofNullable(headerToken);
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {
//		String token = "";
//		if (getTokenInHeader(exchange).isPresent())
//			token = getTokenInHeader(exchange).get().getFirst();
//
//		// if(StringUtils.isNoneBlank(token) && this.tokenutils.validateToken(token)) {
//		LOGGER.debug("Request to upload the content");
//		// UploadService service = new UploadService(config);
        String responseBody = "";

        FormData formData = exchange.getAttachment(FormDataParser.FORM_DATA);
        // Iterate through form data

        System.out.println(formData);
        /*
         * for (String data : formData) { for (FormData.FormValue formValue :
         * formData.get(data)) { if (formValue.isFileItem()) { // Process file here File
         * uploadedFile = formValue.getFileItem().getFile().toFile(); } } }
         */

        System.out.println(exchange.getAttachment(BodyHandler.REQUEST_BODY));
        if (exchange.getAttachment(BodyHandler.REQUEST_BODY) != null) {
            // NON REACHED
            System.out.println("JE SUIS PAS NUL");
            responseBody = Config.getInstance().getMapper()
                    .writeValueAsString(exchange.getAttachment(BodyHandler.REQUEST_BODY));
        }
//		List<HttpString> headerNames = exchange.getRequestHeaders().getHeaderNames().stream()
//				.filter(s -> s.toString().startsWith("")).collect(Collectors.toList());
//		for (HttpString headerName : headerNames) {
//			String headerValue = exchange.getRequestHeaders().get(headerName).getFirst();
//			exchange.getResponseHeaders().put(headerName, headerValue);
//		}
//		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ContentType.APPLICATION_JSON.value());
//		exchange.getResponseSender().send(responseBody);
//	}

        // } else{
        // exchange.setStatusCode(StatusCodes.FORBIDDEN);
        // }
    }
}
