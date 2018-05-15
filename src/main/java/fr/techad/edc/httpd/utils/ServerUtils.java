package fr.techad.edc.httpd.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 15/05/2018.
 */
public class ServerUtils {
    static final Logger LOGGER = LoggerFactory.getLogger(ServerUtils.class);

    /**
     * Create the server yml file to override the default configuration
     *
     * @param port the port to override
     * @return the path which contains the server.yml file
     * @throws IOException if an error is occurred during the configuration file creation.
     */
    public static Path createConfigFile(int port) throws IOException {
        String toWrite = "ip: 0.0.0.0\n" +
                "httpPort: " + port + "\n" +
                "enableHttp: true";
        Path edcConfigPath = Files.createTempDirectory("edc");
        Path pathServer = Paths.get(edcConfigPath.toFile().getAbsolutePath(), "server.yml");
        LOGGER.debug("The override configuration file is saved in {}", pathServer);
        try (FileWriter writer = new FileWriter(pathServer.toFile())) {
            writer.write(toWrite);
        }
        return edcConfigPath;
    }

    /**
     * Scan port in range to find the first available port
     *
     * @param minRangePort the min range
     * @param maxRangePort the max range
     * @return the available port
     * @throws IOException if no free port found
     */
    public static int getAvailablePortInRange(int minRangePort, int maxRangePort) throws IOException {
        int port = minRangePort;
        for (; port <= maxRangePort; port++) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                return serverSocket.getLocalPort();
            } catch (IOException ex) {
                LOGGER.debug("The port {} is not available", port);
            }
        }
        throw new IOException("no free port found in [" + minRangePort + ", " + maxRangePort + "]");
    }

}
