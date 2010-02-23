package org.ttdc.persistence.util;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class EnglishAnalyzer extends Analyzer {
    /**
     * {@inheritDoc}
     */
    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        //TokenStream result = new StandardTokenizer(reader);
    	TokenStream result = new KeywordTokenizer(reader);
        result = new StandardFilter(result);
        result = new LowerCaseFilter(result);
        //result = new SnowballFilter(result, "English");
        return result;
    }
}

