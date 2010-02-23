package org.ttdc.biz.network.services.helpers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.util.ServiceException;

/**
 * For paginating threads.  
 * 
 * @author Trevis
 *
 */
public class ThreadPostDecorator implements Paginator.Decorator<Post>{
	private final Person person;
	private final String rootId;
	public ThreadPostDecorator(Person person, String rootId){
		this.person = person;
		this.rootId = rootId;
	}
	
	public List<Post> prepare(List<Post> sublist) {
		Session session = Persistence.beginSession();
		Post root =	(Post)session.load(Post.class,rootId);
		
		List<String> listToShow = PostHelper.extractIds(sublist);
		
		PostHelper.initializePosts(person, root, listToShow);
		
		List<Post> list = new ArrayList<Post>();
		list.add(root);
		return list;
	}
}
