package org.ttdc.struts.network.actions.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.config.ParentPackage;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.TagLite;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;


@SuppressWarnings("serial")
@ParentPackage("ttdc-lite")
public class TagAjax extends ActionSupport implements SecurityAware {
	private Person person;
	private String action;
	private String postId;
	private String rating;
	private List<AssociationPostTag> ratings;
	private List<TagLite> sugestions;
	private String phrase;
	private Post post;
	private boolean inf;
	private boolean nws;
	private boolean prvt;
	private boolean locked;
	private boolean deleted;
		
	//private boolean frontPage;
	private List<Post> posts; 
	private String q;
	
		
	@Override
	public String execute() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		try{
			if(action.equals("autocompletePlugin")){
				sugestions = CommentService.getInstance().autoCompleteTag(q,Tag.TYPE_TOPIC);
				return "autocompletePlugin";
			}
			if(action.equals("autocompletePlugin-thread")){
				sugestions = CommentService.getInstance().autoCompleteTag(q,Tag.TYPE_TOPIC);
				return "autocompletePlugin";
			}
			else if(action.equals("input")){
				return INPUT;
			}
			else if(action.equals("rate") || action.equals("speed-rate")){
				ratings = CommentService.getInstance().rateContent(person, postId, rating);
				if(action.equals("speed-rate"))
					return "speed-rating";
				else
					return "rating";
			}
			else if(action.equals("unrate") || action.equals("speed-unrate")){
				ratings = CommentService.getInstance().unrateContent(person, postId);
				if(action.equals("speed-unrate"))
					return "speed-rating";
				else	
					return "rating";
			}
			else if(action.equals("autocomplete") || action.equals("autocompleteTitle") || action.equals("autocompleteTopic")){
				sugestions = CommentService.getInstance().autoCompleteTag(phrase,Tag.TYPE_TOPIC);
				return "autocomplete";
			}
			
			
			else if(action.equals("tagform")){
				post = CommentService.getInstance().readPost(postId);
				
				setNws(post.isNWS());
				setInf(post.isINF());
				setPrivate(post.isPrivate());
				setLocked(post.isLocked());
				setDeleted(post.isDeleted());
				
				return "tagform";	
			}
			else if(action.equals("tag")){
				CommentService.getInstance().tagPost(postId, phrase, person);
				
				String displayTag = "";
				if(isNws())
					displayTag = Tag.VALUE_NWS;
				else if(isInf())
					displayTag = Tag.VALUE_INF;
				else if(isPrivate())
					displayTag = Tag.VALUE_PRIVATE;
				else if(isLocked())
					displayTag = Tag.VALUE_LOCKED;
				else if(isDeleted())
					displayTag = Tag.VALUE_DELETED;
				
				CommentService.getInstance().tagPostForDisplay(postId, displayTag, person);
				
				//post = CommentService.getInstance().readBranchAll(person,postId,postId,frontPage);
				post = CommentService.getInstance().readPost(postId);
				
				posts = new ArrayList<Post>();
				if(post != null){ //Post is null if the post was filered out. 
					posts.add(post);
				}
				
				return "refresh";
			}
			else if(action.equals("removeTag")){
				CommentService.getInstance().unTagPost(postId,phrase);
				
				//post = CommentService.getInstance().readBranchAll(person,postId,postId,frontPage);
				post = CommentService.getInstance().readPost(postId);
				posts = new ArrayList<Post>();
				posts.add(post);
				
				return "refresh";
			}
			else if(action.equals("earmarkPost")){
				CommentService.getInstance().tagEarmarkPost(postId,person);
				post = CommentService.getInstance().readPost(postId);
				posts = new ArrayList<Post>();
				return "refresh";
			}
			else if(action.equals("removeEarmark")){
				Tag earmark = CommentService.getInstance().readEarmarkTag(person);
				if(earmark != null){
					String tagId = earmark.getTagId();
					CommentService.getInstance().unTagPost(postId,tagId);
				}
				post = CommentService.getInstance().readPost(postId);
				posts = new ArrayList<Post>();
				posts.add(post);
				return "refresh";
			}
			else if(action.equals("muteThread")){
				String tagId;
				Post post = CommentService.getInstance().readPost(postId);
				AssociationPostTag ass = post.loadTitleTagAssociation();
				if(ass != null){
					tagId = ass.getTag().getTagId();
					
					UserService.getInstance().muteThread(person, tagId);
				}
				post = CommentService.getInstance().readPost(postId);
				posts = new ArrayList<Post>();
				return "refresh";
			}
			
			else{
				response.sendError(HttpServletResponse.SC_CONFLICT,"No clue what you want me to do, bubba.");
				return ERROR;
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			return action;
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

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public List<AssociationPostTag> getRatings() {
		return ratings;
	}

	public void setRatings(List<AssociationPostTag> ratings) {
		this.ratings = ratings;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public List<TagLite> getSugestions() {
		return sugestions;
	}

	public void setSugestions(List<TagLite> sugestions) {
		this.sugestions = sugestions;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public boolean isInf() {
		return inf;
	}

	public void setInf(boolean inf) {
		this.inf = inf;
	}

	public boolean isNws() {
		return nws;
	}

	public void setNws(boolean nws) {
		this.nws = nws;
	}
	
	public boolean isPrivate(){
		return prvt;		
	}
	
	public void setPrivate(boolean prvt){
		this.prvt = prvt;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
