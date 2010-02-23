package org.ttdc.struts.network.actions.search;

import java.util.ArrayList;
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
	@Result( name="success", value="tiles.tagbrowser", type=TilesResult.class),
	@Result( name="thread", value="tiles.thread", type=TilesResult.class)
})

public class TagBrowser extends ActionSupport implements SecurityAware {
	private Person person;
	private String action="";
	private Paginator<Post> paginator;
	private int page;
	private List<Tag> tags;
	private List<String> spiderTag = new ArrayList<String>();
	private String phrase = "";
	private List<Post> posts;
	private int total;
	private String tagId = "";
	private SearchResultsBundle results = new SearchResultsBundle();
	private int year;
	private int month;
	private int day;
	private String threadId = "";
	private String title;
	private boolean startAtTop = true; //Sigh, only for showing the thread page.  ToDo: figure out how to redirect to that action from this action
	
	
	@Override
	public String execute() throws Exception {
		try{
			if(action.equals("earmarks")){
				Tag earmark = CommentService.getInstance().readEarmarkTag(person);
				if(earmark == null){
					addActionMessage(person.getLogin()+", you have no earmarked posts.");
					return SUCCESS;
				}
				else{
					tagId = earmark.getTagId();
				}
			}
				
			if(action.equals("spider") || action.equals("earmarks")){
				if(spiderTag == null)
					spiderTag = new ArrayList<String>();
				
				phrase = phrase.trim();
				if("".equals(phrase) && spiderTag.size() == 0 && tagId.equals("") && threadId.equals("")){
					results.setMode(SearchResultsBundle.MODE_TAG_BROWSER_SUMMARY);
					SearchService.getInstance().loadAllTagSugestions(results.getSuggestions(),50);
					return SUCCESS;
				}
				if(threadId != null && !threadId.equals("")){
					//Thread
					if(phrase != null && !phrase.equals("")){
						//phrase
						addActionError("Not implemented yet");
					}
					else{
						if(spiderTag.size() == 0 && tagId.equals("")){
							results = SearchService.getInstance().readThread(person,threadId,"",true,person.getNumCommentsThreadPage());
							posts = results.getPosts();
							title = results.getThreadTitle(); //That's ugly code mr trevis
							paginator = Paginator.getActivePaginator();
							if(paginator != null)
								page = paginator.getCurrentPageNumber();
							return "thread";
														
						}
						else
							results = SearchService.getInstance().spiderThread(person, threadId, tagId, spiderTag);
					}
				}
				else{
					//No thread
					if(tagId != null && !tagId.equals("")){
						results = SearchService.getInstance().spider(person, tagId,spiderTag);
					}
					else{
						//phrase
						results = SearchService.getInstance().spiderPhrase(person, phrase,spiderTag);
					}
					
				}
				tags = CommentService.getInstance().readTags(spiderTag);
			}
			
			else if(action.equals("page")){
				posts = PostHelper.getPaginatedPage(page);
				if(results.isFullThreadListing())
					results.getThreads().addAll(posts);
				else
					results.getPosts().addAll(posts);
				tags = CommentService.getInstance().readTags(spiderTag);
				paginator = Paginator.getActivePaginator();
			}
			else if(action.equals("search")){
				if(spiderTag.size() > 0 || threadId.length() > 0)
					results = SearchService.getInstance().search(person,threadId, phrase, spiderTag);
				else
					results = SearchService.getInstance().search(person,phrase);
				paginator = Paginator.getActivePaginator();
				phrase = "";
				page = 1;
				tags = CommentService.getInstance().readTags(spiderTag);
			}
			/*
			else if(action.equals("searchComments")){
				results = CommentService.getInstance().searchExpandComments(phrase,spiderTag);
				paginator = Paginator.getActivePaginator();
				page = 1;
				tags = CommentService.getInstance().readTags(spiderTag);
			}
			*/
			else if(action.equals("expandThreads")){
				results = SearchService.getInstance().spiderExpandThreads(person,spiderTag);
				paginator = Paginator.getActivePaginator();
				page = 1;
				tags = CommentService.getInstance().readTags(spiderTag);
			}
			else if(action.equals("expandComments")){
				if(spiderTag != null && spiderTag.size() > 0)
					results = SearchService.getInstance().spiderExpandComments(person,spiderTag, threadId);
				else
					results = SearchService.getInstance().searchExpandComments(person,phrase);
				paginator = Paginator.getActivePaginator();
				page = 1;
				tags = CommentService.getInstance().readTags(spiderTag);
			}
			else if(action.equals("calendar")){
				results = SearchService.getInstance().spiderCalendar(person, day, month, year, spiderTag);
				tags = CommentService.getInstance().readTags(spiderTag);
			}
			else{
				results.setMode(SearchResultsBundle.MODE_TAG_BROWSER_SUMMARY);
				SearchService.getInstance().loadAllTagSugestions(results.getSuggestions(),50);
			}
			
			/*
			if(posts != null){
				if(paginator != null){
					if(paginator.getTotal() == CommentService.MAX_TAG_BROWSER_RESULTS)
						addActionMessage("Refine your criteria you hit the "+CommentService.MAX_TAG_BROWSER_RESULTS+" record limit.");
					total = paginator.getTotal();
				}
				else{
					total = posts.size();
				}
			}
			*/
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
		}
		return SUCCESS;
		
	}
	

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
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

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	public List<String> getSpiderTag() {
		return spiderTag;
	}

	public void setSpiderTag(List<String> spiderTag) {
		this.spiderTag = spiderTag;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public SearchResultsBundle getResults() {
		return results;
	}

	public void setResults(SearchResultsBundle results) {
		this.results = results;
	}


	public String getThreadId() {
		return threadId;
	}


	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}
	public List<Integer> getNumCommentsPerPageOptions() {
		return org.ttdc.struts.network.actions.main.Thread.numCommentsPerPageOptions;
	}


	public boolean isStartAtTop() {
		return startAtTop;
	}


	public void setStartAtTop(boolean startAtTop) {
		this.startAtTop = startAtTop;
	}
}
