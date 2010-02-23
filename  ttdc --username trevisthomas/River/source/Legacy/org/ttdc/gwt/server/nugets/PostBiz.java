package org.ttdc.gwt.server.nugets;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.gwt.client.forms.PostFormData;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.util.ServiceException;

public class PostBiz {
	private final static  Logger log = Logger.getLogger(PostBiz.class);
	
	public static Post createPost(Person person, PostFormData form) throws Exception{
		Post post;
		if(person.isAnonymous()){
			person = authenticatePerson(form);
		}
		if(form.isComment())
			post = postComment(person, form);
		else
			post = postReply(person, form);
		
		return post;
	}
	
	
	private static Person authenticatePerson(PostFormData form) throws ServiceException {
		return Authentication.authenticate(form.getLogin(), form.getPassword());
	}
	
	private static Post postReply(Person person, PostFormData form) throws ServiceException {
		log.debug(person.getLogin()+" creating a new reply.");
		Post post = CommentService.getInstance().createPost(person, form.getParentId(), form.getBody());
		return post;
	}
	private static Post postComment(Person person, PostFormData form) throws ServiceException{
		log.debug(person.getLogin()+" creating a new comment.");
		CommentService.TransientPost transientPost = translateCommentFormToTransientPost(form);
		Post post = CommentService.getInstance().createAdvPost(form.getType(), person, transientPost);
		return post;
	}
	
	/*
	 * TODO: refactor the backend so that this is not required 
	 */
	private static CommentService.TransientPost translateCommentFormToTransientPost(PostFormData form){
		CommentService.TransientPost tp = new CommentService.TransientPost();
		tp.setBody(form.getBody());
		tp.setImageUrl(form.getImageUrl());
		tp.setTitle(form.getTitle());
		tp.setUrl(form.getUrl());
		tp.setYear(form.getYear());
		return tp;
	}
	
	
}
