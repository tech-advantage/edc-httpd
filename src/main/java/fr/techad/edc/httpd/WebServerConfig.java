package fr.techad.edc.httpd;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 22/11/2017.
 */
public class WebServerConfig {

    String base;
    int transferMinSize = 100;

    public WebServerConfig() {
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public int getTransferMinSize() {
        return transferMinSize;
    }

    public void setTransferMinSize(int transferMinSize) {
        this.transferMinSize = transferMinSize;
    }
}
