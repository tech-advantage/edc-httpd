package fr.techad.edc.httpd;

import com.networknt.config.Config;
import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.builder.PredicatedHandlersParser;
import io.undertow.server.handlers.resource.FileResourceManager;

import java.io.File;

import static io.undertow.Handlers.resource;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 22/11/2017.
 */
public class WebServerHandlerProvider implements HandlerProvider {
    static final String CONFIG_NAME = "webserver";

    static WebServerConfig config =
            (WebServerConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME, WebServerConfig.class);

    public HttpHandler getHandler() {

        return Handlers.predicates(
                PredicatedHandlersParser.parse("not equals(%R, '/help') and " +
                        "not equals(%R, '/help/') and " +
                        "not equals(%R, '/help/index.html') and " +
                        "not path-prefix('/doc', '/api') and " +
                        "not path-prefix('/help/assets/') and " +
                        "not path-prefix('/help/i18n/') and " +
                        "not regex('/help/.*(js|css|png|woff|eot|ttf|svg|woff2|txt)$') -> rewrite('/help/index.html')", WebServerHandlerProvider.class.getClassLoader()),
                new PathHandler(resource(new FileResourceManager(new File(config.getBase()), config.getTransferMinSize())))
                        .addPrefixPath("/api/json", new JsonHandler(Config.getInstance().getMapper()))
                        .addPrefixPath("/api/text", new TextHandler())
        );
    }
}
