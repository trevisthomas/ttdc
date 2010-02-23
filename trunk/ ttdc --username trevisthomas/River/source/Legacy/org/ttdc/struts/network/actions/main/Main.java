package org.ttdc.struts.network.actions.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")

@Results({
	@Result( name="success", value="tiles.main", type=TilesResult.class)
})
 final public class Main extends ActionSupport implements SecurityAware {
	private static Logger log = Logger.getLogger(Main.class);
	private Person person;
	private List<Post> posts;
	private List<Post> flatPosts;
	private String title = Constants.SITE_TITLE;
	private String postId;//For viewing a single thread.
	private String action = "";
	private boolean frontPage = false;
	private long timestamp;
	private boolean showWidgets = true;
	
	@Override
	public String execute() throws Exception {
		try{
			/*
			if(action.equals("ajaxfrontpageflat")){
				posts = new ArrayList<Post>();
				flatPosts = new ArrayList<Post>();
				CommentService.getInstance().readFrontPagePosts(person,posts,flatPosts);
				return "ajaxFrontPageFlat";
			}
			else if(action.equals("ajaxfrontpage")){
				posts = new ArrayList<Post>();
				flatPosts = new ArrayList<Post>();
				CommentService.getInstance().readFrontPagePosts(person,posts,flatPosts);
				return "ajaxFrontPage";
			}
			else*/ if(action.equals("single")){
				setFrontPage(false);
				Post.iCount = 0;
				Date start = new Date();
				Post p = CommentService.getInstance().readBranchAll(person,postId,postId,frontPage);
				title += p.getTitle();
				posts = new ArrayList<Post>();
				posts.add(p);
				Date end = new Date();
				
				log.info("Root count:"+posts.size());
				log.info("Time taken: "+(end.getTime() - start.getTime())/1000.0);
				log.info("ass count: "+AssociationPostTag.iCount);
				log.info("post count: "+Post.iCount);
				timestamp = -1;
			}
			else{
				setFrontPage(true);
				AssociationPostTag.iCount = 0;
				Post.iCount = 0;
				Date start = new Date();
				posts = new ArrayList<Post>();
				flatPosts = new ArrayList<Post>();
				String postId = CommentService.getInstance().readFrontPagePosts(person,posts,flatPosts);
				
				Date end = new Date();
				
				HttpServletRequest request = ServletActionContext.getRequest();
				HttpSession session = request.getSession(true);
				session.setAttribute(Constants.SESSION_KEY_NEWEST_POST, postId);
				
				log.info("Root count:"+posts.size());
				log.info("Time taken: "+(end.getTime() - start.getTime())/1000.0);
				log.info("ass count: "+AssociationPostTag.iCount);
				log.info("post count: "+Post.iCount);
				Date d = new Date();
				timestamp = d.getTime();
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
		}
		return SUCCESS;
	}
	
	public String getPostId() {
		return postId;
	}


	public void setPostId(String postId) {
		this.postId = postId;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
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

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public boolean isFrontPage() {
		return frontPage;
	}

	public void setFrontPage(boolean frontPage) {
		this.frontPage = frontPage;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<Post> getFlatPosts() {
		return flatPosts;
	}

	public void setFlatPosts(List<Post> flatPosts) {
		this.flatPosts = flatPosts;
	}

	public boolean isShowWidgets() {
		return showWidgets;
	}

	public void setShowWidgets(boolean showWidgets) {
		this.showWidgets = showWidgets;
	}

	
}
