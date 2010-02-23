package org.ttdc.persistence.objects;

/**
 * This class used to be mapped to the post table and i was using a named native query but i decided 
 * to make the results cacheable i had to swich it to hql which i did.  Now the query is in Post.java
 * and this class is just a util class out of water. 
 *  
 * 
 * @author Trevis
 *
 */
public class PostCounter {
	private long count;
	private String rootId;
	
	public String getRootId() {
		return rootId;
	}
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}	
}
