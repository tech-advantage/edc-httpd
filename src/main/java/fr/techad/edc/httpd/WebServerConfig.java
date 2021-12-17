package fr.techad.edc.httpd;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 22/11/2017.
 */
public class WebServerConfig {

    private String base;
    private String indexPath = null;
    private long transferMaxSize = (long) 20E3;
    private boolean indexUrlEnabled = false;
    private String docFolder = "doc";
    private String helpFolder = "help";


    public WebServerConfig() {
        super();
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    public long getTransferMaxSize() {
        return transferMaxSize;
    }

    public void setTransferMaxSize(long transferMaxSize) {
        this.transferMaxSize = transferMaxSize;
    }

    public boolean isIndexUrlEnabled() {
        return indexUrlEnabled;
    }

    public void setIndexUrlEnabled(boolean indexUrlEnabled) {
        this.indexUrlEnabled = indexUrlEnabled;
    }

    public String getDocFolder() {
        return docFolder;
    }

    public void setDocFolder(String docFolder) {
        this.docFolder = docFolder;
    }

    public String getHelpFolder() {
        return helpFolder;
    }

    public void setHelpFolder(String helpFolder) {
        this.helpFolder = helpFolder;
    }

}
