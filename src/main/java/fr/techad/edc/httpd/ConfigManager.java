package fr.techad.edc.httpd;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 25/04/2018.
 */
public class ConfigManager {
    private static ConfigManager instance;

    private WebServerConfig webServerConfig;

    private ConfigManager() {
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null)
            instance = new ConfigManager();
        return instance;
    }

    public WebServerConfig getWebServerConfig() {
        return webServerConfig;
    }

    public void setWebServerConfig(WebServerConfig webServerConfig) {
        this.webServerConfig = webServerConfig;
    }
}
