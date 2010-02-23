package org.ttdc.persistence.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.PopulateCache;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;
import org.ttdc.util.ServiceException;

public class FullSpeedTester {
	private static Logger log = Logger.getLogger(FullSpeedTester.class);
	private void cacheItAll(){
		//PopulateCache.init();
		List<AssociationPostTag> asses;
		List<Tag> tags;
		List<Person> people;
		List<Image> images;
		List<UserObject> objects;
		List<UserObjectTemplate> objectTemplates;
		List<Privilege> privileges;
		List<Post> posts;
		
		
		Session session = Persistence.beginSession();
		Query query;
		Date start,end;
		
		start = new Date();
		query = session.getNamedQuery("privilege.getAll");
		privileges = query.list();
		end = new Date();
		log.info("Loaded all privileges in: "+(end.getTime() - start.getTime())/1000.0);
		
		start = new Date();
		query = session.getNamedQuery("person.getAllForCache");
		people = query.list();
		end = new Date();
		log.info("Loaded all persons in: "+(end.getTime() - start.getTime())/1000.0);
		
		start = new Date();
		query = session.getNamedQuery("userObjectTemplate.getAll");
		objectTemplates = query.list();
		end = new Date();
		log.info("Loaded all user object templates in: "+(end.getTime() - start.getTime())/1000.0);

		start = new Date();
		query = session.getNamedQuery("object.getAll");
		objects = query.list();
		end = new Date();
		log.info("Loaded all user objects in: "+(end.getTime() - start.getTime())/1000.0);

		start = new Date();
		query = session.getNamedQuery("image.getAll");
		images = query.list();
		end = new Date();
		log.info("Loaded all images in: "+(end.getTime() - start.getTime())/1000.0);
		
		start = new Date();
		query = session.getNamedQuery("tag.getAll");
		tags = query.list();
		end = new Date();
		log.info("Loaded all tags in: "+(end.getTime() - start.getTime())/1000.0);
		
		start = new Date();
		query = session.getNamedQuery("post.getAll");
		posts = query.list();
		for(Post post : posts){
			post.getTagAssociations().toString();
			post.getPosts().toString();
		}
		end = new Date();
		log.info("Loaded all posts in: "+(end.getTime() - start.getTime())/1000.0);
		
		/*
		start = new Date();
		query = session.getNamedQuery("ass.getAll");
		asses = query.list();
		end = new Date();
		log.info("Loaded all tag associations in: "+(end.getTime() - start.getTime())/1000.0);
		*/
		
		/*
		start = new Date();
		query = session.getNamedQuery("ass.getByPostId").setString("postId","205D1FCB-8339-4C8B-BD8E-000403DC9904");
		asses = query.list();
		end = new Date();
		log.info("Loaded all tag associations in: "+(end.getTime() - start.getTime())/1000.0);
		*/
		start = new Date();
		Persistence.commit();
		end = new Date();
		log.info("Commit took: "+(end.getTime() - start.getTime())/1000.0);
		
		log.info("Loaded... ");
		//log.info("Tag Associations: "+asses.size());
		//log.info("Posts: "+posts.size());
		log.info("People: "+people.size());
		log.info("Tags: "+tags.size());
		log.info("Images: "+images.size());
		log.info("UserObjects: "+objects.size());
		log.info("User Object Templates: "+objectTemplates.size());
		log.info("Privileges: "+privileges.size());
		log.info("Posts: "+posts.size());
		log.info("Done.");

				
	}
	private List<String> readPostIdsWithTags(List<String> tagIds) {
		Session session = Persistence.beginSession();
		Post p = null;
		// Query query = session.getNamedQuery("postId.PostsTagUnion")
		// Query query = session.getNamedQuery("postId.rootPostsTagUnion")
		// Query query = session.getNamedQuery("postId.replyPostsTagUnion")
		// Query query =
		// session.getNamedQuery("postId.replyPostsTagUnionByDate")
		// Query query = session.getNamedQuery("postId.rootPostsTagUnionByMass")
		//
		Query query = session.getNamedQuery("tagLiteHql.TagMassForSpider").setCacheable(true)// .setCacheRegion("test")
				.setParameterList("tagIds", tagIds).setInteger("count", tagIds.size());
		
		List<String> postIds = new ArrayList<String>();
		Iterator itr = query.iterate();
		while (itr.hasNext()) {
			Object obj = itr.next();
			postIds.add((String)((Object[])obj)[1]);
		}
		
		
		
		/*
		 * List<Object> list = query.list(); for(Object obj : list){ if(obj
		 * instanceof Post){ log.info(obj); } log.info(obj); }
		 */
		// Persistence.getCache("test");
		Persistence.getCache("org.ttdc.biz.network.beans.Post");
		Persistence.commit();
		return postIds;
		// return p;
	}
	
	public Post readBranch(String rootId){
		Session session  = Persistence.beginSession();
		Post branch = null;
		Query query = session.getNamedQuery("post.getBranchByRootId").setCacheable(false).setString("rootId",rootId);
		for(Post p : (List<Post>)query.list()){
			Hibernate.initialize(p);
			Hibernate.initialize(p.getTagAssociations());
			if(p.getPostId().equals(rootId)){
				branch = p; 
			}
		}
		
		Persistence.commit();
		return branch;
	}
	
	public void testPostIdWithTags(){
		Date start,end;
		List<String> postIds = new ArrayList<String>();
		List<String> ids = new ArrayList<String>();
		ids.add("3DA00CA6-4C48-4AEF-B2C9-F13B05DBE0A7");
		ids.add("2B994A54-59BF-4043-8F19-19A6AA599566");
		
		start = new Date();
		postIds = readPostIdsWithTags(ids);
		end = new Date();
		log.info("Loaded "+postIds.size()+" posts in: "+(end.getTime() - start.getTime())/1000.0);
	}
	public FullSpeedTester() {
		//cacheItAll();
	}
	public static void main(String[] args) {
		
		FullSpeedTester test = new FullSpeedTester();
		Date start,end;
		Post branch = null;
		try{
			//test.cacheItAll();
			
			PopulateCache.init();
			
			Post post = new Post();
			
			Session session = Persistence.beginSession();
			post = (Post)session.load(Post.class,"205D1FCB-8339-4C8B-BD8E-000403DC9904");
			//log.info(post.getPostId()+ " " + post.getParent().getPostId());
			post.getTagAssociations().toString();
			post.getPosts().toString();
			
			//log.info(post.getTagAssociations());
			Persistence.commit();
			
			//test.cacheItAll();
			
			
			session = Persistence.beginSession();
			
			log.info("..Start...");
			//post = (Post)session.load(Post.class,"205D1FCB-8339-4C8B-BD8E-000403DC9904");
			
			Query query = session.getNamedQuery("post.getByPostId").setString("postId","205D1FCB-8339-4C8B-BD8E-000403DC9904");
			post = (Post)query.uniqueResult();
			//
			//Hibernate.initialize(post.getPosts());
			log.info(post);
			
			
			start = new Date();
			List<Post> posts = new ArrayList<Post>();
			List<Post> latestPosts = new ArrayList<Post>();
			CommentService.getInstance().readFrontPagePosts(UserService.getInstance().getAnnonymousUser(),posts, latestPosts);
			end = new Date();
			log.info("Loaded front page in: "+(end.getTime() - start.getTime())/1000.0);
			
			Persistence.commit();
		}
		catch(Throwable t){
			log.error(t);
		}
	}
}
