package fr.techad.edc.httpd.utils;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CaseSensitiveStandardAnalyzer extends StopwordAnalyzerBase {
    /** Default maximum allowed token length */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

    @Override
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        TokenStream tok = src;
        tok = new StopFilter(tok, stopwords);
        return new TokenStreamComponents(
                r -> {
                    src.setMaxTokenLength(CaseSensitiveStandardAnalyzer.this.maxTokenLength);
                    src.setReader(r);
                },
                tok);
    }
}