package fr.techad.edc.httpd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 22/11/2017.
 */
public class WebServerConfig {

    private String base;
    private String indexCaseInsensitivePath = null;
    private String indexCaseSensitivePath = null;
    private long transferMinSize = 100;
    private long requestMaxSize = (long) 20E3;
    private boolean indexUrlEnabled = false;
    private String docFolder = "doc";
    private String helpFolder = "help";

    /** @return index of pattern in s or -1, if not found */
    public static int indexOf(Pattern pattern, String s) {
        Matcher matcher = pattern.matcher(s);
        return matcher.find() ? matcher.start() : -1;
    }

    public static long parseAny(String arg0)
    {
        String units = "BKMGTPEZY";
        int index = indexOf(Pattern.compile("[A-Za-z]"), arg0);
        double ret = Double.parseDouble(arg0.substring(0, index));
        String unitString = arg0.substring(index);
        int unitChar = unitString.charAt(0);
        int power = units.indexOf(unitChar);
        boolean isSi = unitString.indexOf('i')!=-1;
        int factor = 1024;
        if (isSi)
        {
            factor = 1000;
        }

        return (long) (ret * Math.pow(factor, power));
    }

    public WebServerConfig() {
        super();
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getIndexCaseInsensitivePath() {
        return indexCaseInsensitivePath;
    }

    public void setIndexCaseInsensitivePath(String indexPath) {
        this.indexCaseInsensitivePath = indexPath;
    }

    public String getIndexCaseSensitivePath() {
        return indexCaseSensitivePath;
    }

    public void setIndexCaseSensitivePath(String indexPath) {
        this.indexCaseSensitivePath = indexPath;
    }

    public long getRequestMaxSize() {
        return requestMaxSize;
    }

    public void setRequestMaxSize(String requestMaxSize) {
        this.requestMaxSize = parseAny(requestMaxSize);
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

    public long getTransferMinSize() {
        return transferMinSize;
    }

    public void setTransferMinSize(long transferMinSize) {
        this.transferMinSize = transferMinSize;
    }
}