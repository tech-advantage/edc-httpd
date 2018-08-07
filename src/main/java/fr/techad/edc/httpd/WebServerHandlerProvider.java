package fr.techad.edc.httpd;

import com.networknt.config.Config;
import com.networknt.server.HandlerProvider;
import fr.techad.edc.httpd.search.IndexService;
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
        ConfigManager.getInstance().setWebServerConfig(config);

        IndexService indexService = new IndexService(config);
        indexService.indexContent();
        PathHandler pathHandler = new PathHandler(resource(new FileResourceManager(new File(config.getBase()), config.getTransferMinSize())))
                .addPrefixPath("/httpd/api/search", new SearchHandler(Config.getInstance().getMapper(), config))
                .addPrefixPath("/httpd/api/text", new TextHandler());

        if (config.isIndexUrlEnabled())
            pathHandler.addPrefixPath("/httpd/api/reindex", new IndexerHandler(Config.getInstance().getMapper(), config));

        String docFolder = config.getDocFolder();
        String helpFolder = config.getHelpFolder();

        return Handlers.predicates(
                PredicatedHandlersParser.parse("not equals(%R, '/" + helpFolder + "') and " +
                        "not equals(%R, '/" + helpFolder + "/') and " +
                        "not equals(%R, '/" + helpFolder + "/index.html') and " +
                        "not path-prefix('/" + docFolder + "', '/httpd') and " +
                        "not path-prefix('/" + helpFolder + "/assets/') and " +
                        "not path-prefix('/" + helpFolder + "/i18n/') and " +
                        "not regex('/" + helpFolder + "/.*(js|css|png|woff|eot|ttf|svg|woff2|txt)$') -> rewrite('/" + helpFolder + "/index.html')", WebServerHandlerProvider.class.getClassLoader()),
                pathHandler
        );
    }
}
