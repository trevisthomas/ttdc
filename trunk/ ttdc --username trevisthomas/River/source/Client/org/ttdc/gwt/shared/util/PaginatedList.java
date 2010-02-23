package org.ttdc.gwt.shared.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PaginatedList<T> implements IsSerializable{
	private int totalResults;
	private List<T> list = new ArrayList<T>();
	private int currentPage;
	private int pageSize;
	private String phrase;
		
	public PaginatedList(){}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Search for: '"+phrase+"' ");
		if(list.size() == 0){
			sb.append("found no results.");
		}
		else{
			sb.append(
					"now showing "+(nowShowingFrom())+
					" through "+nowShowingTo()+
					" of "+totalResults);
			if(calculateNumberOfPages() > 1){
			sb.append(" (page "+currentPage+
					" of "+calculateNumberOfPages()+
					")");
			}
		}
		return sb.toString();
	}

	private int nowShowingTo() {
		int tmp = pageSize*currentPage;
		if(tmp <= getTotalResults()){
			return tmp;
		}
		return getTotalResults();
	}

	private int nowShowingFrom() {
		int tmp = pageSize*currentPage;
		return tmp > pageSize ? tmp - pageSize : currentPage == 1 ? 1 : currentPage * pageSize;
	}

	private int getResultCount() {
		return list.size();
	}
	public int calculateNumberOfPages(){
		if(list.size() == 0){
			return 0;
		}
		else if(getTotalResults() < getPageSize()){
			return 1;
		}
		else{
			int tmp;
			tmp = getTotalResults() / getPageSize();
			if(getTotalResults() % getPageSize() > 0){
				tmp++;
			}
			return tmp;
		}
	}
	
	public int getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isEmpty() {
		return list.size() == 0;
	}
	
	
}
