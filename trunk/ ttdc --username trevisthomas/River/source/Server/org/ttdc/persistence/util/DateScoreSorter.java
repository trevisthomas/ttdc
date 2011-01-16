package org.ttdc.persistence.util;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.ScoreDocComparator;
import org.apache.lucene.search.SortComparatorSource;
import org.apache.lucene.search.SortField;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.PostDate;
import org.ttdc.persistence.objects.UserObjectTemplate;


/**
 * The BlendSorter can sort on any lucene field and blend its value
 * with the document-score. The blend can be configured.
 */
public class DateScoreSorter implements SortComparatorSource {
	private final static Logger log = Logger.getLogger(DateScoreSorter.class);
    private final static FieldConverter fieldConverter = new FieldConverter();
    
    private final static Map<String, Float> boostCache = new ConcurrentHashMap<String, Float>();
    private final static Date initDate = new Date();
    public static class FieldConverter{
    	float get(String value){
    		Float score = boostCache.get(value);
    		if(score == null){
    			throw new RuntimeException("Date score boost hit a post that isnt in the cache! Something is wrong");
    		}
    		return score;
    	}
    }
    
    static{
    	PostDao.registerPostCreationListener(new PostCreationObserver());
    }
    
    private static class PostCreationObserver implements PostDao.PostCreationListener{
    	@Override
    	public void newPostCreated(Post post) {
    		boostCache.put(post.getPostId(), calculateScore(post.getDate()));
    	}
    }
    
    public static void init(List<PostDate> postDateList){
    	for(PostDate pd : postDateList){
    		boostCache.put(pd.getPostId(), calculateScore(pd.getDate()));
    	}
    }

    private static Float calculateScore(Date date) {
    	return ((float)date.getTime() / (float)initDate.getTime()); 
	}

    public DateScoreSorter() {
        
    }

    
    public ScoreDocComparator newComparator(final IndexReader indexReader, final String field) throws IOException {
        return new ScoreDocComparator() {
            Map<Integer, Float> values = new HashMap<Integer, Float>();

            public int compare(ScoreDoc scoreDoc1, ScoreDoc scoreDoc2) {
                try {
                    Float v1 = getValue(scoreDoc1, indexReader, field);
                    Float v2 = getValue(scoreDoc2, indexReader, field);
                    return v2.compareTo(v1);

                } catch (IOException e) {
                    log.error("Cannot read doc", e);
                }
                return 0;
            }

            public Comparable sortValue(ScoreDoc scoreDoc) {
                return values.get(scoreDoc.doc);
            }

            public int sortType() {
                return SortField.FLOAT;
            }
            /**
             * Constructs a BlendSorter
             * @param fieldConverter    The converter to use wen converting the lucent field
             * @param blendFactor       The factor to blend (0 means 0% fieldvalue and 100%
             *                          document-score, 1 means 100% fieldvalue and 0% document-score).
             */
            float blendFactor = 0.9f;

            // lazily get values, and store them in our value-map
            private Float getValue(ScoreDoc scoreDoc, IndexReader indexReader, String field) throws IOException {
                Float value = values.get(scoreDoc.doc);
                if (value != null) return value;
                final Document doc = indexReader.document(scoreDoc.doc);
                float fieldValue = fieldConverter.get(doc.get(field));
                float queryValue = scoreDoc.score;
                value = blendFactor * fieldValue + (1 - blendFactor) * queryValue;
                //value = queryValue * fieldValue;
                values.put(scoreDoc.doc, value);
                return value;
            }
        };
    }

}