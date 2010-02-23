package org.ttdc.nongwt.shared;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.ttdc.gwt.shared.util.PaginatedList;

public class SearchResultsTest {
	private final static List<String> smallList = new ArrayList<String>();
	private final static List<String> bigList = new ArrayList<String>();

	private final static int LARGE = 503;
	private final static int SMALL = 20;
	
	static {
		for(int i = 0 ; i < LARGE ; i++){
			if(i < SMALL)
				smallList.add("Value_"+i);
			bigList.add("Value_"+i);
		}
	}
	
	@Test
	public void testSearchResult(){
		PaginatedList<String> result = new PaginatedList<String>();
		result.setCurrentPage(25);
		result.setList(smallList);
		result.setTotalResults(LARGE);
		result.setPageSize(20);
		result.setPhrase("MockSearch");
		
		assertEquals("Search for: 'MockSearch' now showing 480 through 500 of 503 (page 25 of 26)", result.toString());
	}
	
	@Test
	public void testSearchRemainderResult(){
		PaginatedList<String> result = new PaginatedList<String>();
		result.setCurrentPage(26);
		result.setList(smallList);
		result.setTotalResults(LARGE);
		result.setPageSize(20);
		result.setPhrase("MockSearch");
		
		assertEquals("Search for: 'MockSearch' now showing 500 through 503 of 503 (page 26 of 26)", result.toString());
	}
	
	@Test
	public void testSearchResutLessThanAPage(){
		PaginatedList<String> result = new PaginatedList<String>();
		result.setCurrentPage(1);
		result.setList(smallList);
		result.setTotalResults(SMALL);
		result.setPageSize(25);
		result.setPhrase("MockSearch");
		
		assertEquals("Number of pages should be 1", 1, result.calculateNumberOfPages());
		assertEquals("Search for: 'MockSearch' now showing 1 through 20 of 20", result.toString());
	}
	
//	@Test
//	public void testSearchResultsWithRemainder(){
//		 
//		SearchResults<String> result = new SearchResults<String>();
//		result.setCurrentPage(1);
//		result.setList(smallList);
//		result.setTotalResults(SMALL);
//		result.setPageSize(20);
//		result.setPhrase("MockSearch");
//		
//		assertEquals("Number of pages should be 1", 1, result.calculateNumberOfPages());
//		assertEquals("Search for: 'MockSearch' now showing 1 through 10 of 503", result.toString());
//	}
	
	@Test
	public void testEmptySearchResults(){
		PaginatedList<String> result = new PaginatedList<String>();
		result.setCurrentPage(1);
		result.setList(new ArrayList<String>());
		result.setTotalResults(SMALL);
		result.setPageSize(20);
		result.setPhrase("MockSearch");
		
		assertEquals("Number of pages should be 0", 0, result.calculateNumberOfPages());
		assertEquals("Search for: 'MockSearch' found no results.", result.toString());
	}
}

