package org.ttdc.struts.network.actions.main;

import java.util.Date;
import java.util.List;

import org.ttdc.biz.network.services.CommentService;
import org.ttdc.persistence.objects.Post;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

public class Rss extends ActionSupport{
	private static final long serialVersionUID = 5726537986336113346L;
	private List<Post> posts;
	private Date now;
	@Override
	public String execute() throws Exception {
		try{
			now = new Date();
			posts = CommentService.getInstance().readRssPosts();
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
		}
		return SUCCESS;
	}
	public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
	public Date getNow() {
		return now;
	}
	public void setNow(Date now) {
		this.now = now;
	}
}
