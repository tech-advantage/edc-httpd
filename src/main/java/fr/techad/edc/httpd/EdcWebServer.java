package fr.techad.edc.httpd;

import com.networknt.server.Server;
import org.apache.commons.lang3.StringUtils;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 24/11/2017.
 */
public class EdcWebServer {

    /**
     * Start the server with default logger: slf4j
     */
    public static void run() {
        // setup system property to redirect undertow logs to slf4j/logback.
        run("slf4j");
    }

    /**
     * Start server with specified logger
     *
     * @param logger
     */
    public static void run(String logger) {
        //
        if (StringUtils.isBlank(logger)) {
            System.err.println("Error, the logger must be defined");
        } else {
            System.setProperty("org.jboss.logging.provider", logger);
            Server.start();
        }
    }

    public static void main(String[] args) {
        run();
    }
}
