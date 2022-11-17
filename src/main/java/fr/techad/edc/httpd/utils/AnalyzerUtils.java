package fr.techad.edc.httpd.utils;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class AnalyzerUtils extends StopwordAnalyzerBase{
    /** Default maximum allowed token length */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

    @Override
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        src.setMaxTokenLength(maxTokenLength);
        TokenStream tok = src;
        tok = new StopFilter(tok, stopwords);
        return new Analyzer.TokenStreamComponents(
                r -> {
                    src.setMaxTokenLength(AnalyzerUtils.this.maxTokenLength);
                    src.setReader(r);
                },
                tok);
    }
}