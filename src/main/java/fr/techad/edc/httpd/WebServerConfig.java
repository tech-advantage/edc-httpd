package fr.techad.edc.httpd;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 22/11/2017.
 */
public class WebServerConfig {

    private String base;
    private String indexPath = null;
    private int transferMinSize = 100;
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

    public int getTransferMinSize() {
        return transferMinSize;
    }

    public void setTransferMinSize(int transferMinSize) {
        this.transferMinSize = transferMinSize;
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
