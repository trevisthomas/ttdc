package org.ttdc.persistence.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.ttdc.biz.network.services.CommentService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

public class PostTest extends TestCase {
	private static Logger log = Logger.getLogger(PostTest.class);
	
	private void save(Object obj){
		Session session = Persistence.beginSession();
		session.save(obj);
		Persistence.commit();
	}
	/*
	public Post readPost(String postId){
		Session session = Persistence.session();
		Query query = session.getNamedQuery("post.getByPostId").setString("postId",postId);
		Post p = (Post)query.uniqueResult();
		return p;
	}
	*/
	private Post readPost(String id){
		Session session = Persistence.beginSession();
		
		Query query = session.getNamedQuery("post.getByPostId").setCacheable(true).setString("postId", id);
		//Post p = (Post)query.uniqueResult();
		
		//Post p = (Post)session.get(Post.class, id);
		List<Post> list = query.list();
		Post p = list.get(0);
		
		//log.info(p);
		
		Persistence.commit();
		return p;
	}
	
	/**
	 * An example of using hql to iterate over results, and to select custom data
	 * 
	 * @param rootIds
	 * @return
	 */
	private Post readBranchs(List<String> rootIds){
		Session session = Persistence.beginSession();
		Post p = null;
		//Query query = session.getNamedQuery("post.getBranchByRootId")
		Query query = session.getNamedQuery("post.getBranchsByRootIdTest")
			.setCacheable(true)//.setCacheRegion("test")
			.setParameterList("rootIds", rootIds);
		Iterator itr = query.iterate();
		while(itr.hasNext()){
			Object obj = itr.next();
			log.info(obj);
		}
		
		/*
		List<Object> list = query.list();
		for(Object obj : list){
			if(obj instanceof Post){
				log.info(obj);
			}
			log.info(obj);
		}
		*/
		
		
		//Persistence.getCache("test");
		Persistence.getCache("org.ttdc.biz.network.beans.Post");
	
		Persistence.commit();
		return p;
	}
	
	private void readPostIdsWithTags(List<String> tagIds){
		
		Session session = Persistence.beginSession();
		Post p = null;
		//Query query = session.getNamedQuery("postId.PostsTagUnion")
		//Query query = session.getNamedQuery("postId.rootPostsTagUnion")
		//Query query = session.getNamedQuery("postId.replyPostsTagUnion")
		//Query query = session.getNamedQuery("postId.replyPostsTagUnionByDate")
		//Query query = session.getNamedQuery("postId.rootPostsTagUnionByMass")
		//
		
		Query query = session.getNamedQuery("tagLiteHql.TagMassForSpider")
			.setCacheable(true)//.setCacheRegion("test")
			.setParameterList("tagIds", tagIds).setInteger("count", tagIds.size());
		Iterator itr = query.iterate();
		while(itr.hasNext()){
			Object obj = itr.next();
			log.info(obj);
		}
		
		/*
		List<Object> list = query.list();
		for(Object obj : list){
			if(obj instanceof Post){
				log.info(obj);
			}
			log.info(obj);
		}
		*/
		
		
		//Persistence.getCache("test");
		Persistence.getCache("org.ttdc.biz.network.beans.Post");
	
		Persistence.commit();
		//return p;
	}
	
	private Post readBranch(String rootId){
		Session session = Persistence.beginSession();
		Post p;
		Query query = session.getNamedQuery("post.getBranchByRootId")
			.setCacheable(true)//.setCacheRegion("test")
			.setString("rootId", rootId);
		
		List<Post> list = (ArrayList<Post>)query.list();
		
		//p = buildPostHierarchy(list);
		
		for(Post post : list){
			if(post.getParent() == null)
				p = post;
		}
		p = list.get(0);
		
		//Persistence.getCache("test");
		Persistence.getCache("org.ttdc.biz.network.beans.Post");
		/*
		 	I have the whole branch in memory but they are not in order. The list has all of them, there should be only one
		  	with no parent, that one is the root.  Sorting them by date added should solve this in the real world but during
		  	testing i'm creating them programatically and so they have the same date stamp.
		  */ 
		
		
		/*
		for(Post p : list){
			if(p.getParent() == null){
				System.out.println(p);
				Persistence.commit();
				return p;
			}
		}
		*/
		
		/*
		Post parent = null,child = null;
		for(Post p : list){
			if(p.getParent() != null){
				child=p;
			}else if(p.getParent() == null){
				parent = p;
			}
		}
		parent.addChild(child);
		
		System.out.println(parent);
		*/
		Persistence.commit();
		return p;
	}
	
	
	public void tagPost(Post post){
		TagTest tagTester = new TagTest();
		Tag t= tagTester.load().get(0);
		PersonTester personTester = new PersonTester();
		Person p = personTester.load().get(0);
		
		AssociationPostTag tagass = new AssociationPostTag();
		tagass.setPost(post);
		tagass.setCreator(p);
		tagass.setTag(t);
		
		Persistence.save(tagass);
		
	}
	
	/*
	public Post readBranch2(String rootId){
		Map<String,Post> map = new HashMap<String,Post>();
		List<Post> list = new ArrayList<Post>();
		
	
		Session session = Persistence.session();
		Query query = session.getNamedQuery("post.getBranchByRootId")
			.setCacheable(true)
			.setString("rootId", rootId);
		
		ArrayList<Post> rs = (ArrayList<Post>)query.list();
		
		for(Post p : rs){
			if(p.getParent() == null){
				System.out.println(p);
				Persistence.commit();
				return p;
			}
		}
		
	}
	 */
	
	/*
	public Post addTwoChildren(Post post){
		Post p1 = new Post();
		p1.setBody("Twin Uno");
		post.addChild(p1);
		
		p1 = new Post();
		p1.setBody("Twin Dos");
		post.addChild(p1);
		
		Persistence.update(post);
		return post;
	}
	 */
	public final void testCreatePost(){
		try{
			/*
			Post p = new Post();
			save(p);
			Entry e = new Entry();
			e.setBody("Ok. Entry is a seperate table now.");
			e.setPost(p);
			save(e);
			Persistence.update(p);
			*/
			
			//Persistence.update(e);
			//log.info(p);
			
			
			//p = readPost("BFCC33C7-C756-4082-BAB4-293A17BE66DB");
			//p = readPost("04DCC11B-F69B-4DCF-8F2A-51023C132E3E");
			
			//log.info(p);
			
			
			/*
			Post p = new Post();
			p.setBody("Root");
			//save(p);
			
			
			Post p2 = new Post();
			p2.setBody("I'm one deep");
			p.addChild(p2);
			
			Post p3 = new Post();
			p3.setBody("And i'm 2 deep");
			p2.addChild(p3);
			
			save(p);
			log.info(p.toString());
			
			Post readPost = readPost(p.getPostId());
			//log.info(readPost);
			*/
			
			//Post readPost = readPost("f2632def-f85e-42b3-aa44-c6db8fa4033c");
			//Post readBranch = readBranch("2853f071-518c-4abe-9099-4d8b20d60a2b");
			/*
			Post readPost = readPost("2853f071-518c-4abe-9099-4d8b20d60a2b");
			readPost("2853f071-518c-4abe-9099-4d8b20d60a2b");
			readPost("2853f071-518c-4abe-9099-4d8b20d60a2b");
			*/
			
			//readPost("40a48958-9871-43c2-9abd-6c6709e3dea2");
			//Post branch = readBranch("40a48958-9871-43c2-9abd-6c6709e3dea2");
			//log.info(branch);
			//readPost("40a48958-9871-43c2-9abd-6c6709e3dea2");
			
			//Post post = readPost("40a48958-9871-43c2-9abd-6c6709e3dea2");
			//tagPost(post);
			
			//readBranch("40a48958-9871-43c2-9abd-6c6709e3dea2");
			//Post post = readPost("40a48958-9871-43c2-9abd-6c6709e3dea2");
			
			
			
			//log.info(readBranch("2853f071-518c-4abe-9099-4d8b20d60a2b"));
			//Post post = readPost("2853f071-518c-4abe-9099-4d8b20d60a2b");
			//addTwoChildren(post.getChildren().get(1));
		
			
			//loadPerson("8C06A4D5-1824-4631-B093-FE012AEC8B45");//Admin
			Person p = UserService.getInstance().getAnnonymousUser();
			Date start = new Date();
			List<Post> frontPageRoots = new ArrayList<Post>();
			List<Post> flatPosts = new ArrayList<Post>();
				
			CommentService.getInstance().readFrontPagePosts(p,frontPageRoots,flatPosts);
			
			Date end = new Date();
			//log.info(frontPageRoots);
			log.info("Root count:"+frontPageRoots.size());
			log.info("Time taken: "+(end.getTime() - start.getTime())/1000.0);
			log.info("ass count: "+AssociationPostTag.iCount);
			log.info("post count: "+Post.iCount);
			
			/*
			
			start = new Date();
			frontPageRoots = CommentService.getInstance().readFrontPagePosts(null);
			end = new Date();			
			log.info("Root count:"+frontPageRoots.size());
			log.info("2nd Run Time taken: "+(end.getTime() - start.getTime())/1000.0);
			*/
			
			/*
			List<String> ids = new ArrayList<String>();
			ids.add("E83AE867-81B7-4350-9091-00807CE32578");
			Post branch = readBranchs(ids);
			log.info(branch);
			*/
			/*
			List<String> ids = new ArrayList<String>();
			ids.add("3DA00CA6-4C48-4AEF-B2C9-F13B05DBE0A7");
			ids.add("2B994A54-59BF-4043-8F19-19A6AA599566");
			readPostIdsWithTags(ids);
			*/
			//tagPost(post);
			
			//log.info(post);
			
			System.exit(0);
		}
		catch(Throwable t){
			log.error(t);
			fail(t.getMessage());
		}
	}
	
	/**
	 * 
	 * @param posts the unorginized list of posts
	 * 
	 * I came up with an alternative solution.
	 * @return
	 */
	/*
	public Post buildPostHierarchy(List<Post> posts){
		Post root = new Post();
	
		Map<String,Post> map = new HashMap<String,Post>();
		List<String> keys = new ArrayList<String>();
		
		for(Post p : posts){
			map.put(p.getPostId(), p);
			keys.add(p.getPostId());
		}
		
		for(String key: keys){
			Post p = map.get(key);
			Post parent = p.getParent(); 
			if(parent == null){
				root = p;
			}
			else{
				//parent.addChildSpecial(p); //Breaking encapsulation for demo. Fix later
			}
		}
		return root;
	}
	*/
}
