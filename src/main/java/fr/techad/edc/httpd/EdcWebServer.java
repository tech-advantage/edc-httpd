package fr.techad.edc.httpd;

import com.networknt.server.Server;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 24/11/2017.
 */
public class EdcWebServer {

    public static void run() {
        // setup system property to redirect undertow logs to slf4j/logback.
        System.setProperty("org.jboss.logging.provider", "slf4j");
        Server.start();
    }

    public static void main(String[] args) {
        run();
    }
}
