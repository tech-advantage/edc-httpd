package fr.techad.edc.httpd.search;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 23/03/2018.
 * <p>
 * Describe the documentation item define into toc-X.json
 */
public class DocumentationItem {
    private Long id;
    private String label;
    private String publicationId;
    private String url;
    private String languageCode;
    private DocumentationItemType documentationItemType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public DocumentationItemType getDocumentationItemType() {
        return documentationItemType;
    }

    public void setDocumentationItemType(DocumentationItemType documentationItemType) {
        this.documentationItemType = documentationItemType;
    }
}
