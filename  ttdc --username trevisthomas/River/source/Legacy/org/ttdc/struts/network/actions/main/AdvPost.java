package org.ttdc.struts.network.actions.main;


import java.util.List;

import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Shacktag;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;
import org.ttdc.util.ShackTagger;

import com.opensymphony.xwork2.ActionSupport;


@SuppressWarnings("serial")
public class AdvPost extends ActionSupport implements SecurityAware {
	private Person person;
	private String action = "";
	private String guid;
	private String login;
	private String password;
	private Post post; 
	
	private CommentService.TransientPost transientPost;
	
	private String mode;
	

	public AdvPost() {
		
	}
	@Override
	public String execute() throws Exception {
		try{
			if(action.equals("create")){
				if(person.isAnonymous()){
					person = UserService.getInstance().authenticate(login, password, false);
				}
				post = CommentService.getInstance().createAdvPost(mode, person, transientPost);
				return "created";
			}
			else{
				if(guid.length() > 0){
					transientPost = new CommentService.TransientPost();
					Post p = CommentService.getInstance().readPost(guid);
					transientPost.setTitle(p.getTitle()); 
				}
				return INPUT;
			}
			
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			return INPUT;
		}
	}
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
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

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
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

}
