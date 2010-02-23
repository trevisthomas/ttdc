package org.ttdc.struts.network.actions.search;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.helpers.Paginator;
import org.ttdc.biz.network.services.helpers.PostHelper;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")

@Results({
	@Result( name="success", value="tiles.search", type=TilesResult.class)
})
public class Search extends ActionSupport implements SecurityAware {
	private static Logger log = Logger.getLogger(Search.class);
	private Person person;
	private String title = Constants.SITE_TITLE;
	private String phrase;
	private String action = "";
	private List<Post> posts;
	private int page = 0;
	private Paginator<Post> paginator;
	
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		try{
			if(action.equals("search")){
				//posts = CommentService.getInstance().search(phrase);
				paginator = Paginator.getActivePaginator();
				page = 1;
				
			}else if(action.equals("page")){
				posts = PostHelper.getPaginatedPage(page);
				paginator = Paginator.getActivePaginator();
			}
			else{
				
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
		}
		return SUCCESS;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		Search.log = log;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Paginator<Post> getPaginator() {
		return paginator;
	}

	public void setPaginator(Paginator<Post> paginator) {
		this.paginator = paginator;
	}
}
