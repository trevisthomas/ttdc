package org.ttdc.gwt.server.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ttdc.gwt.client.beans.GPost;

/**
 * 
 * Intended for sorting GPosts back into their query order. 
 *  
 * @author Trevis
 */
public class GPostByPostIdReferenceComparator implements Comparator<GPost>{
	private final List<String> sortedList;
	
	public GPostByPostIdReferenceComparator(List<String> reference){
		sortedList = Collections.unmodifiableList(reference);
	}
	
	public int compare(GPost o1, GPost o2) {
		Integer r1 = sortedList.indexOf(o1.getPostId());
		Integer r2 = sortedList.indexOf(o2.getPostId());
		return r1.compareTo(r2);
	}
}