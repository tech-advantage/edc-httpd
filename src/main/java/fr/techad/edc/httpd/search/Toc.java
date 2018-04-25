package fr.techad.edc.httpd.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 22/03/2018.
 *
 * This is the mapping class to toc.json
 */
public class Toc {
    private String label;


    private List<TocReference> toc = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<TocReference> getToc() {
        return Collections.unmodifiableList(toc);
    }

    public void setToc(List<TocReference> tocReferenceList) {
        toc.clear();
        this.toc.addAll(tocReferenceList);
    }
}
