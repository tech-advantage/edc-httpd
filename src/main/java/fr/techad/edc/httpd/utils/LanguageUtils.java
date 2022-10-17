package fr.techad.edc.httpd.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.techad.edc.httpd.WebServerConfig;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LanguageUtils {

    private static Optional<File> getProductPath(WebServerConfig config) {
        File docFolder = new File(config.getBase() + "/" + config.getDocFolder() + "/");
        File[] products = docFolder.listFiles(File::isDirectory);
        String parsed = "";
        Optional<File> product = Arrays.stream(products).filter(p -> !p.getName().equals("i18n")).findFirst();

        return product;
    }

    public static String getDefaultLanguage(WebServerConfig config) throws IOException {
        Optional<File> product = getProductPath(config);
        String parsed;
        if (product.isPresent()) {
            parsed = FileUtils.readFileToString(new File(product.get().getCanonicalPath() + "/info.json"),
                    StandardCharsets.UTF_8.name());
            JSONObject obj = new JSONObject(parsed);
            return obj.getString("defaultLanguage");
        }
        return "";
    }

    public static List<String> findLanguages(WebServerConfig config) throws IOException {
        Optional<File> product = getProductPath(config);
        String parsed;

        if (product.isPresent()) {
            parsed = FileUtils.readFileToString(new File(product.get().getCanonicalPath() + "/info.json"),
                    StandardCharsets.UTF_8.name());
            JSONObject obj = new JSONObject(parsed);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<String> languages = objectMapper.readValue(obj.getJSONArray("languages").toString(), List.class);
                return languages;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }
}
