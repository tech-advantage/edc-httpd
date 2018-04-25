package fr.techad.edc.httpd.search;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 24/04/2018.
 */
public class DocumentationSearchResult {

    private Long id;
    private String label;
    private Long strategyId;
    private String strategyLabel;
    private String languageCode;
    private String url;
    private String type;


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

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public String getStrategyLabel() {
        return strategyLabel;
    }

    public void setStrategyLabel(String strategyLabel) {
        this.strategyLabel = strategyLabel;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("label", label)
                .append("strategyId", strategyId)
                .append("strategyLabel", strategyLabel)
                .append("languageCode", languageCode)
                .append("url", url)
                .append("type", type)
                .toString();
    }
}
