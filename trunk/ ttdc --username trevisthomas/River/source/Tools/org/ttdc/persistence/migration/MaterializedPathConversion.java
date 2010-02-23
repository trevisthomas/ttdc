package org.ttdc.persistence.migration;


import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ttdc.biz.network.services.helpers.PostHelper;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Post;

public class MaterializedPathConversion {
	Session session;
	public static void main(String[] args) {
		MaterializedPathConversion converter = new MaterializedPathConversion();
		converter.go();
	}
	
	void go(){
		try{
			
			session = Persistence.beginSession();
			@SuppressWarnings("unchecked") List<Post> threads = session.createCriteria(Post.class).add(Restrictions.isNull("parent")).list();
			List<String> ids = PostHelper.extractIds(threads);
			
			for(String id : ids){
				
				//Trevis: i think that the below query was for performance, so that the recusive call could go faster.
				@SuppressWarnings("unchecked") List<Post> conversations = session.createCriteria(Post.class).add(Restrictions.eq("root.postId", id)).addOrder(Order.asc("date")).list();
				
				List<Post> posts = session.createCriteria(Post.class).add(Restrictions.eq("parent.postId", id)).addOrder(Order.asc("date")).list();
				materialize(posts);
				Persistence.commit();
				session = Persistence.beginSession();
			}
			Persistence.commit();
		}
		catch (Throwable t) {
			Persistence.commit();
			t.printStackTrace();
		}
		
	}
	
	
	
	void materialize(List<Post> posts){
		int val = 0;
		for(Post p : posts){
			if(p.getPath().length() > 0) return;
			if(p.getParent() != null){
				p.setPath(formatPath(p, val++));
				session.update(p);
				System.out.println("Id: "+p.getPostId() +" " + p.getPath());
			}
			materialize(p.getPosts());
		}
	}

	private String formatPath(Post p, int current) {
		String currStr = String.format("%05d", current);
		if(p.getParent().getPath().length() > 1)
			return p.getParent().getPath() + '.' + currStr;
		else
			return currStr;	
	}
}
