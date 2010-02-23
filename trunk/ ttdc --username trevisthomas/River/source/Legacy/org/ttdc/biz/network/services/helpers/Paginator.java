package org.ttdc.biz.network.services.helpers;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.util.ServiceException;

public final class Paginator<T> {
	public static interface Decorator<T>{
		/**
		 * Decorator is intended to allow paganated classes to be stored incomplete, but
		 * then they can be completely populated before each page is shown.  Initial use for this
		 * is in paginating posts. Tags are not read until the page is accessed.
		 * 
		 * @param sublist
		 */
		public List<T> prepare(List<T> sublist);
		
	}
	private List<T> list;
	private Decorator<T> decorator = null;
	//public final static int PER_PAGE = 50;
	private final int PER_PAGE;
	private int currentPageSize;
	private int currentPageNumber = 1;
	private int currentFromRecord;
	private int currentToRecord;
	
	private Paginator(){ PER_PAGE = -1; }
	
	public Paginator(List<T> list, int perPage){
		this("",list,perPage);
	}
	public Paginator(String key, List<T> list, int perPage){
		this.PER_PAGE = perPage;
		this.list = list;
		this.decorator = null;
		currentPageSize = -1;
		currentPageNumber = -1;
		store(key);
		
	}
	public Paginator(List<T> list, Decorator<T> decorator, int perPage){
		this("",list,decorator,perPage);
	}
	public Paginator(String key, List<T> list, Decorator<T> decorator, int perPage){
		this.PER_PAGE = perPage;
		this.list = list;
		this.decorator = decorator;
		store(key);
	}
	
	public static <T> Paginator<T> getActivePaginator(){
		return getActivePaginator("");
	}
	/**
	 * Finds the paginator in the session and returns it if it exists.  
	 * If not, null is returned;
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public static <T> Paginator<T> getActivePaginator(String key){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession httpSession = request.getSession(true);
		Object obj = httpSession.getAttribute(Constants.SESSION_KEY_PAGINATOR+key);
		if(obj != null && obj instanceof Paginator){
			@SuppressWarnings("unchecked") Paginator<T> paginator = (Paginator<T>)obj;
			return paginator;
		}
		else{
			return null;
		}	
	}
	private void store(String key){
		try{
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession httpSession = request.getSession(true);
			httpSession.setAttribute(Constants.SESSION_KEY_PAGINATOR+key, this);
		}
		catch(NullPointerException e){
			//This happens if you try to run this code outside of a web server. So i mute it
		}
	}
	public static void remove(){
		try{
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession httpSession = request.getSession(true);
			httpSession.removeAttribute(Constants.SESSION_KEY_PAGINATOR);
		}
		catch(NullPointerException e){
			//This happens if you try to run this code outside of a web server. So i mute it
		}
	}
	public int getCurrentPageSize(){
		return currentPageSize;
	}
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}
	public int getCurrentFromRecord() {
		return currentFromRecord;
	}
	
	public int getCurrentToRecord() {
		return currentToRecord;
	}
	
	public int getTotal(){
		return list.size();
	}
	public int getNumPages(){
		int pages = list.size() / PER_PAGE;
		if(list.size()%PER_PAGE != 0)
			pages++;
		return pages;
	}
	public List<T> getPage(int pageNumber) throws ServiceException{
		this.currentPageNumber = pageNumber;
		if(pageNumber < 1 || pageNumber > getNumPages())
			throw new ServiceException("Invalid page number");
		
		pageNumber = pageNumber - 1;
		int fromIndex;
		int toIndex;
		
		fromIndex = pageNumber * PER_PAGE;
		toIndex = fromIndex+PER_PAGE;
		
		if(toIndex > list.size())
			toIndex = list.size();
		
		currentFromRecord = fromIndex+1;
		currentToRecord = toIndex+1;
		
		List<T> sublist = list.subList(fromIndex, toIndex);
		
		if(this.decorator != null)
			sublist = decorator.prepare(sublist);
		currentPageSize = sublist.size();
		
		return sublist;
	}
	
	/**
	 * Reverse paginate.  Implemented for thread view.  Modifed on 4/8 so that the page which shows less than
	 *  per page number of records is the last page instead of the first one.  
	 * 
	 * @param pageNumber
	 * @return
	 * @throws ServiceException
	 */
	public List<T> getPageInverse(int pageNumber) throws ServiceException{
		this.currentPageNumber = pageNumber;
		if(pageNumber < 1 || pageNumber > getNumPages())
			throw new ServiceException("Invalid page number");
		
		pageNumber = pageNumber - 1;
		int fromIndex;
		int toIndex;
		
		int offset = PER_PAGE - (list.size() % PER_PAGE); //offset is how short the last page is of being a full PER_PAGE length
		if(offset == PER_PAGE)
			offset = 0;
		
		toIndex = list.size()+offset;
		toIndex = toIndex - (pageNumber * PER_PAGE);
		fromIndex = toIndex - PER_PAGE;
		if(fromIndex < 0){
			fromIndex = 0;
		}
		
		currentFromRecord = fromIndex+1;
		currentToRecord = toIndex+1;
		
		if(toIndex > list.size())
			toIndex = list.size();
		List<T> sublist = list.subList(fromIndex, toIndex);
		
		if(this.decorator != null)
			sublist = decorator.prepare(sublist);
		currentPageSize = sublist.size();
		
		return sublist;
	}
	
	
	/**
	 * Determines which page the object t is on when using inverse page methods (used for the thread view) 
	 * 
	 * @param t
	 * @return
	 * @throws ServiceException
	 */
	public int findPageContainingInverse(T t) throws ServiceException{
		int page;
		int ndx = -1;
		
		ndx = list.indexOf(t);
		if(ndx >= 0){
			page = getNumPages() - ( ndx / PER_PAGE);
		}
		else{
			throw new ServiceException("Paginator could not locate object in list.");
		}
		return page;
	}
	
	/**
	 * I'm cheating a little bit here.  I wanted a list of strings to
	 * make the view code cleaner.  It's a bit of a hack to put this here
	 * but... i'm gonna do it anyway.
	 *
	 * @return
	 */
	public List<String> getPageNames(){
		List<String> list = new ArrayList<String>();
		int numPages = getNumPages();
		for(int i = 1;i<=numPages;i++){
			list.add(""+i);
		}
		return list;
	}
	
	/**
	 * See comment on getPageNames... this is an inverse version of that
	 *
	 * @return
	 */
	public List<String> getPageNamesInverse(){
		List<String> list = new ArrayList<String>();
		int numPages = getNumPages();
		for(int i = numPages;i > 0;i--){
			list.add(""+i);
		}
		return list;
	}
	
}	
