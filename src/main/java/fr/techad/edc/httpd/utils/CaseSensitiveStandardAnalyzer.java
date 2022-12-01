package fr.techad.edc.httpd.utils;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CaseSensitiveStandardAnalyzer extends StopwordAnalyzerBase {
    /** Default maximum allowed token length */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    public StandardTokenizer src;

    public CaseSensitiveStandardAnalyzer(){
        src = new StandardTokenizer();
    }

    @Override
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        return new TokenStreamComponents(
                r -> {
                    src.setMaxTokenLength(DEFAULT_MAX_TOKEN_LENGTH);
                    src.setReader(r);
                },
                new StopFilter(src, stopwords));
    }
}