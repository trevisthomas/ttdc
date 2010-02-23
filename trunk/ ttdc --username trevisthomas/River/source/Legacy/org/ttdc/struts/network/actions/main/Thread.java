package org.ttdc.struts.network.actions.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.SearchService;
import org.ttdc.biz.network.services.helpers.Paginator;
import org.ttdc.biz.network.services.helpers.PostHelper;
import org.ttdc.biz.network.services.helpers.SearchResultsBundle;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;
@SuppressWarnings("serial")
@Results({
	@Result( name="success", value="tiles.thread", type=TilesResult.class)
})
public class Thread extends ActionSupport implements SecurityAware{
	private Person person;
	private String action;
	private String threadId;
	private String postId;
	private SearchResultsBundle results = new SearchResultsBundle();
	private List<Post> posts;
	private String title;
	private Paginator<Post> paginator;
	private int page = 0;
	private boolean startAtTop = true;
	private int perPage = -1;
	public final static List<Integer> numCommentsPerPageOptions = Arrays.asList(25,50,75,100,250,500,1000);
	private boolean hierarchy = true;
	private List<String> spiderTag = new ArrayList<String>();
	private List<Tag> tags  = new ArrayList<Tag>();
	private Integer day; 
	
	@Override
	public String execute() throws Exception {
		try{
			if(action.equals("thread")){
				hierarchy = true;
				if(perPage == -1)	
					perPage = person.getNumCommentsThreadPage();
				
				results = SearchService.getInstance().readThread(person,threadId,postId,startAtTop,perPage);
				posts = results.getPosts();
				title = results.getPosts().get(0).getRoot().getTitle(); //That's ugly code mr trevis
				paginator = Paginator.getActivePaginator(threadId);
				if(paginator != null)
					page = paginator.getCurrentPageNumber();
				return SUCCESS;
			}
			else if(action.equals("thread-flat")){
				hierarchy = false;
				if(perPage == -1)	
					perPage = person.getNumCommentsThreadPage();
				
				results = SearchService.getInstance().readThreadFlat(person,threadId,postId,startAtTop,perPage);
				posts = results.getPosts();
				title = results.getPosts().get(0).getRoot().getTitle(); //That's ugly code mr trevis
				paginator = Paginator.getActivePaginator(threadId);
				if(paginator != null)
					page = paginator.getCurrentPageNumber();
				return SUCCESS;
			}
			else if(action.equals("spider")){
				hierarchy = false;
				if(perPage == -1)	
					perPage = person.getNumCommentsThreadPage();
				
				
				results = SearchService.getInstance().readThreadFlat(person,threadId,postId,startAtTop,perPage,spiderTag, day);
				posts = results.getPosts();
				title = results.getPosts().get(0).getRoot().getTitle(); //That's ugly code mr trevis
				paginator = Paginator.getActivePaginator();
				if(paginator != null)
					page = paginator.getCurrentPageNumber();
				
				tags = CommentService.getInstance().readTags(spiderTag);
				return SUCCESS;
			}
			else if(action.equals("page")){
				posts = PostHelper.getPaginatedPageInverse(threadId,page);
				title = posts.get(0).getRoot().getTitle();
				threadId = posts.get(0).getRoot().getPostId();
				paginator = Paginator.getActivePaginator(threadId);
				if(paginator == null)
					paginator = Paginator.getActivePaginator();
				return "content";
			}
			else{
				addActionError("Nothing to do as of yet...");
				return SUCCESS;
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			return SUCCESS;
		}
		
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	
	public SearchResultsBundle getResults() {
		return results;
	}
	public void setResults(SearchResultsBundle results) {
		this.results = results;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Paginator<Post> getPaginator() {
		return paginator;
	}
	public void setPaginator(Paginator<Post> paginator) {
		this.paginator = paginator;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
	public boolean isStartAtTop() {
		return startAtTop;
	}
	public void setStartAtTop(boolean startAtTop) {
		this.startAtTop = startAtTop;
	}
	public List<Integer> getNumCommentsPerPageOptions() {
		return numCommentsPerPageOptions;
	}

	public int getPerPage() {
		return perPage;
	}
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	public boolean isHierarchy() {
		return hierarchy;
	}
	public void setHierarchy(boolean hierarchy) {
		this.hierarchy = hierarchy;
	}
	public List<String> getSpiderTag() {
		return spiderTag;
	}
	public void setSpiderTag(List<String> spiderTag) {
		this.spiderTag = spiderTag;
	}
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	public Integer getDay() {
		return day;
	}
	public void setDay(Integer day) {
		this.day = day;
	}
	
}
