package org.ttdc.struts.network.actions.ajax;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.InboxService;
import org.ttdc.biz.network.services.UserMessageService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.biz.network.services.helpers.PostFormatter;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Shacktag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;
import org.ttdc.util.ShackTagger;

import com.opensymphony.xwork2.ActionSupport;


@SuppressWarnings("serial")
public class PostAjax extends ActionSupport implements SecurityAware {
	private static Logger log = Logger.getLogger(PostAjax.class);
	private Person person;
	private String action;
	private String guid;
	
	private String body;
	private String title;
	private String imdb;
	private String poster;
	private String year;
	
	private CommentService.TransientPost transientPost;
	
	private String value;
		
	private Post post; 
	private List<Post> posts; //Initially added for refresh
	private List<String> rootIds; //Also added for refresh. So that the client knows the juggle order
	private String sourcePostId;
	private boolean frontPage;
	private String timestamp;
	private int count;
	private String review;
	private List<Post> flatPosts;
	private String login;
	private String password;
	
	
	@Override
	public String execute() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		
		try{
			if(action.equals("input")){
				post = CommentService.getInstance().readPost(guid);
				return INPUT;
			}
			else if(action.equals("input-edit")){
				post = CommentService.getInstance().readPost(guid);
				transientPost = new CommentService.TransientPost();
				transientPost.setBody(post.getEntry().getBody());
				transientPost.setTitle(post.getTitle());
				
				transientPost.setUrl(post.getUrl());
				if(post.getImage() != null)
					transientPost.setImageUrl(post.getImage().getName());
				
				if(post.isMovie()){
//					transientPost.setYear(post.getReleaseYear());
					return "edit-movie";
				}
				else{
					return "edit";
				}
			}
			else if(action.equals("create")){
				boolean bReview = false;
				if(review != null && review.trim().length() > 0)
					bReview = true;
				
				if(person.isAnonymous()){
					person = UserService.getInstance().authenticate(login, password, false);
				}
				
				post = CommentService.getInstance().createPost(person, guid, body);
//				post.setExpanded(true);
//				post.setHidden(false);
				//post.setNewPost(true);
				//return "updated";//Or reply
				posts = new ArrayList<Post>();
				posts.add(post);
				
				//Update the latest post id in this users session to the one they just created so that the page wont refresh
				HttpServletRequest request = ServletActionContext.getRequest();
				HttpSession session = request.getSession(true);
				session.setAttribute(Constants.SESSION_KEY_NEWEST_POST,post.getPostId());
				
				return "refresh";
			}
			else if(action.equals("preview")){
				body = PostFormatter.getInstance().format(body);
				return "preview";
			}
			else if(action.equals("refresh")){
				Date start = new Date();
				post = CommentService.getInstance().readBranchAll(person,guid,sourcePostId,frontPage);
				
				posts = new ArrayList<Post>();
				if(post != null){ //Post is null if the post was filered out. 
					posts.add(post);
				}
					
				Date end = new Date();
				log.info("Root count:"+posts.size());
				log.info("Time taken: "+(end.getTime() - start.getTime())/1000.0);
				log.info("ass count: "+AssociationPostTag.iCount);
				
				return "refresh";
			}
			else if(action.equals("thread-read")){
				InboxService.getInstance().markThreadRead(person, guid);
				post = CommentService.getInstance().readBranchAll(person,guid,sourcePostId,frontPage);
				posts = new ArrayList<Post>();
				posts.add(post);
				
				return "refresh";
			}
			else if(action.equals("site-read")){
				InboxService.getInstance().markSiteRead(person);
				return "reload";
			}
			else if(action.equals("refresh-single-post")){
				post = CommentService.getInstance().readPost(guid,person);
				posts = new ArrayList<Post>();
				posts.add(post);
				return "single";
				
			}
			else if(action.equals("show-replyto-post")){
				post = CommentService.getInstance().readPost(guid,person);
				/*
				posts = new ArrayList<Post>();
				posts.add(post);
				*/
				return "replyto";
				
			}
			else if(action.equals("expand")){
				post = CommentService.getInstance().readPost(guid,person);
//				post.setExpanded(true);
				posts = new ArrayList<Post>();
				posts.add(post);
				return "single";
			}
			else if(action.equals("contract")){
				//post = CommentService.getInstance().readPost(guid,frontPage,person);
				post = CommentService.getInstance().readPost(guid,person);
//				post.setExpanded(false);
				posts = new ArrayList<Post>();
				posts.add(post);
				return "single";
			}
			else if(action.equals("ping")){
				try{
					HttpServletRequest request = ServletActionContext.getRequest();
					HttpSession session = request.getSession(true);
					Object obj = session.getAttribute(Constants.SESSION_KEY_NEWEST_POST);
					
					if(obj != null){
						String postId = (String)obj;
						//String postId = guid;
						posts = new ArrayList<Post>();
						rootIds = new ArrayList<String>();
						
						UserMessageService.getInstance().pingForContent(person,postId,posts,rootIds);
						
						timestamp = ""+new Date().getTime();
						count = posts.size();
						
						session.setAttribute(Constants.SESSION_KEY_NEWEST_POST, CommentService.getInstance().getLatestPostId());
						return "pingupdate";
					}
					else{
						return "reload";
					}
					
					
					
				}
				catch(NumberFormatException e){
					/*
					 * Trevis: Think about this. There seems to be a weird issue when the client cant talk to the server
					 * it's like the pinger goes crazy and tries it even faster. Should probably shut down.
					 */
					response.sendError(HttpServletResponse.SC_CONFLICT,"Bad date in refresh.");
					return ERROR;
				}
				
			}
			else if(action.equals("edit")){
				post = CommentService.getInstance().editPost(person, guid, transientPost);
				posts = new ArrayList<Post>();
				posts.add(post);
				return "single";
			}
			
			if(action.equals("ajaxfrontpageflat")){
				posts = new ArrayList<Post>();
				flatPosts = new ArrayList<Post>();
				person.setFrontPageMode(UserObject.VALUE_FLAT);
				CommentService.getInstance().readFrontPagePosts(person,posts,flatPosts);
				return "ajaxFrontPageFlat";
			}
			else if(action.equals("ajaxfrontpage")){
				posts = new ArrayList<Post>();
				flatPosts = new ArrayList<Post>();
				person.setFrontPageMode(UserObject.VALUE_HIERARCHY);
				CommentService.getInstance().readFrontPagePosts(person,posts,flatPosts);
				return "ajaxFrontPage";
			}
			
			else{
				response.sendError(HttpServletResponse.SC_CONFLICT,"No clue what you want me to do, bubba.");
				return ERROR;
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			if(guid != null){
				post = CommentService.getInstance().readPost(guid);
			}
			if(action.equals("edit")){
				if(post != null && post.isMovie()){
					return "edit-movie";
				}
				else{
					return "edit";
				}
			}
			return INPUT;
		}
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public String getSourcePostId() {
		return sourcePostId;
	}

	public void setSourcePostId(String sourcePostId) {
		this.sourcePostId = sourcePostId;
	}

	public boolean isFrontPage() {
		return frontPage;
	}

	public void setFrontPage(boolean frontPage) {
		this.frontPage = frontPage;
	}

	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public List<String> getRootIds() {
		return rootIds;
	}

	public void setRootIds(List<String> rootIds) {
		this.rootIds = rootIds;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public List<Post> getFlatPosts() {
		return flatPosts;
	}

	public void setFlatPosts(List<Post> flatPosts) {
		this.flatPosts = flatPosts;
	}


	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImdb() {
		return imdb;
	}

	public void setImdb(String imdb) {
		this.imdb = imdb;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public CommentService.TransientPost getTransientPost() {
		return transientPost;
	}

	public void setTransientPost(CommentService.TransientPost transientPost) {
		this.transientPost = transientPost;
	}

	public List<Shacktag> getShacktags() {
		return ShackTagger.getInstance().getShacktags();
	}

	public void setShacktags(List<Shacktag> shacktags) {
		//does nothing 
	}

	
}
