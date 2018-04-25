package fr.techad.edc.httpd.search;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 22/03/2018.
 *
 * This is the mapping class to multi-doc.json file
 */
public class MultiDocItem {
    private String productId;
    private String pluginId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("productId", productId)
                .append("pluginId", pluginId)
                .toString();
    }
}
