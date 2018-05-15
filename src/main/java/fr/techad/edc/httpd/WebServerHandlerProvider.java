package fr.techad.edc.httpd;

import com.networknt.config.Config;
import com.networknt.server.HandlerProvider;
import fr.techad.edc.httpd.search.ContentIndexer;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.builder.PredicatedHandlersParser;
import io.undertow.server.handlers.resource.FileResourceManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        ConfigManager.getInstance().setWebServerConfig(config);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        Runnable indexTask = () -> {
            try {
                ContentIndexer contentIndexer = new ContentIndexer(config);
                contentIndexer.index();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        executor.execute(indexTask);

        return Handlers.predicates(
                PredicatedHandlersParser.parse("not equals(%R, '/help') and " +
                        "not equals(%R, '/help/') and " +
                        "not equals(%R, '/help/index.html') and " +
                        "not path-prefix('/doc', '/httpd') and " +
                        "not path-prefix('/help/assets/') and " +
                        "not path-prefix('/help/i18n/') and " +
                        "not regex('/help/.*(js|css|png|woff|eot|ttf|svg|woff2|txt)$') -> rewrite('/help/index.html')", WebServerHandlerProvider.class.getClassLoader()),
                new PathHandler(resource(new FileResourceManager(new File(config.getBase()), config.getTransferMinSize())))
                        .addPrefixPath("/httpd/api/search", new SearchHandler(Config.getInstance().getMapper()))
                        .addPrefixPath("/httpd/api/text", new TextHandler())
        );
    }
}
