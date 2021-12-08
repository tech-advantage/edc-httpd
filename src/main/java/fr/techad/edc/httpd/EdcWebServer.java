package fr.techad.edc.httpd;

import com.networknt.config.Config;
import com.networknt.server.Server;
import fr.techad.edc.httpd.utils.ServerUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * TECH ADVANTAGE All right reserved Created by cochon on 24/11/2017.
 */
public class EdcWebServer {
	static final Logger LOGGER = LoggerFactory.getLogger(EdcWebServer.class);

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
		LOGGER.debug("Start server with logger: {}", logger);
		if (StringUtils.isBlank(logger)) {
			throw new IllegalArgumentException("The logger must be defined");
		}
		System.setProperty("org.jboss.logging.provider", logger);
		Server.start();
	}

	/**
	 * Start server with specified logger and scan port to find the first available
	 * port in range: [minRangePort, maxRangePort].
	 *
	 * @param logger       the logger
	 * @param minRangePort the minimal port to scan
	 * @param maxRangePort the maximal port to scan
	 */
	public static int run(String logger, int minRangePort, int maxRangePort) throws IOException {
		LOGGER.debug("Start server with scan port");
		if (minRangePort < 0 || maxRangePort < 0)
			throw new IllegalArgumentException(
					"the port range must be positive [" + minRangePort + ", " + maxRangePort + "]");
		if (minRangePort > maxRangePort)
			throw new IllegalArgumentException(
					"the port range is badly defined [" + minRangePort + ", " + maxRangePort + "]");
		int port = ServerUtils.getAvailablePortInRange(minRangePort, maxRangePort);
		Path configFile = ServerUtils.createConfigFile(port);
		System.setProperty(Config.LIGHT_4J_CONFIG_DIR, configFile.toString());
		//
		run(logger);
		LOGGER.info("The server run on port: {}", port);
		return port;
	}

	public static void main(String[] args) {
		run();
	}
}
