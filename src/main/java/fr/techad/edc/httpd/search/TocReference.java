package fr.techad.edc.httpd.search;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 22/03/2018.
 * <p>
 * This is the toc reference defined into toc.json
 */
public class TocReference {
    private String id;
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("file", file)
                .toString();
    }
}
