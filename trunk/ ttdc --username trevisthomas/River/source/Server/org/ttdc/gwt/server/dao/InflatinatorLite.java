package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GEntry;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.HasGuid;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

/**
 * I'm building this class in an attempt to get mysql to have acceptable performance
 * in the calendar 
 * 
 */

//TODO: DELETE
@Deprecated
public class InflatinatorLite {
	private final static Logger log = Logger.getLogger(InflatinatorLite.class);
	private final StopWatch stopwatch = new StopWatch();
	private List<Post> list;
	
	private final Map<String, GEntry> entryMap = new HashMap<String,GEntry>();
	private final Map<String, GPost> postMap = new HashMap<String,GPost>();
	private final Map<String, GTag> tagMap = new HashMap<String,GTag>();
	private final Map<String, GPerson> personMap = new HashMap<String,GPerson>();
	
	public InflatinatorLite(List<Post> list) {
		this.list = list;
	}
	
	
	public List<GPost> extractPosts(){
		if(list.size() == 0){
			log.debug("extractPosts() exited because list was empty.");
			return new ArrayList<GPost>();
		}
		
		stopwatch.start();
		log.debug("extractPosts() started.");
		
		List<String> postIds = extractIds(list);
		
		List<Object[]> objList = loadBundleForPosts(postIds);
		
		List<HasGuid> elementList = detangleHibernateObjectArrayList(objList);
				
		List<GPost> posts = convertElementsToGElements(elementList);
		
		Collections.sort(posts,new GPostByPostIdReferenceComparator(postIds));
		
		log.debug("extractPosts() completed. Elapsed time: " +stopwatch);
		return posts;
	}
	
	private static List<String> extractIds(List<Post> posts){
		List<String> postIds = new ArrayList<String>();
		for(Post pl : posts){
			postIds.add(pl.getPostId());
		}
		return postIds;
	}
	
	@SuppressWarnings("unchecked")
	private List<Object[]> loadBundleForPosts(List<String> postIds) {
		List<Object []> objList = session().createQuery("SELECT ass,p,t,e,c FROM AssociationPostTag ass INNER JOIN ass.post as p " +
				" INNER JOIN ass.tag as t INNER JOIN ass.post.latestEntry e INNER JOIN ass.creator c WHERE ass.post.postId IN (:postIds)")
				.setParameterList("postIds", postIds).list();
		return objList;
	}
	
	private List<HasGuid> detangleHibernateObjectArrayList(List<Object []> list){
		List<HasGuid> entityList = new ArrayList<HasGuid>();
		for(Object [] innerArray : list){
			for(int j = 0; j< innerArray.length;j++){
				HasGuid instance = (HasGuid)innerArray[j];
				
				if(instance == null){
					throw new RuntimeException("Something is wrong. A null element was found.");
				}
				if(!entityList.contains(instance)){
					entityList.add(instance);
				}
			}
		}
		return entityList;
	}
	
	private List<GPost> convertElementsToGElements(List<HasGuid> elementList) {
		List<GPost> posts = new ArrayList<GPost>();
		for(HasGuid instance : elementList){
			if(instance instanceof Post){
				Post p = (Post) instance;
				GPost gPost;
				gPost = createLazyPost(p);
				posts.add(gPost);
			}
//			else if(instance instanceof Entry){
//				generateLazyEntry((Entry)instance);
//			}
			else if(instance instanceof AssociationPostTag){
				generateLazyAss((AssociationPostTag)instance);
			}
			else if(instance instanceof Tag){
				generateLazyTag((Tag) instance);
			}
			else if(instance instanceof Person){
				generateLazyPerson((Person) instance);
			}
		}
		return posts;
	}
	
	private GPost createLazyPost(Post p){
		GPost gPost = findOrCreateGPost(p.getPostId());
		//gPost.setEntries(convertEntries(p.getEntries()));
		if(gPost.getTitle() == null){
			gPost.setDate(p.getDate());
			gPost.setPostId(p.getPostId());
		//	gPost.setTitle(p.getTitle());//TODO: At the moment this performs a @function to get this value.  If that changes, reconsider
			gPost.setReplyCount(p.getReplyCount());
			gPost.setMass(p.getMass());
			gPost.setRootPost(p.isRootPost());
			gPost.setThreadPost(p.isThreadPost());
			gPost.setPath(p.getPath());
			gPost.setRoot(findOrCreateGPost(p.getRoot().getPostId()));
			if(!gPost.isRootPost())
				gPost.setThread(findOrCreateGPost(p.getThread().getPostId()));
			
//			if(p.getImage() != null)
//				gPost.setImage(FastPostBeanConverter.convertImage(p.getImage()));
//			else if(p.isMovie()){
//				gPost.setImage(FastPostBeanConverter.convertImage(InitConstants.DEFAULT_POSTER));
//			}
		}
		//TODO: gPost needs edit date.
		//gPost.setPosts(convertPosts(p.getPosts()));
		//gPost.setTagAssociations(convertAssociationsPostTag(p.getTagAssociations()));
		return gPost;
	}
	
	private GAssociationPostTag generateLazyAss(AssociationPostTag ass){
		GAssociationPostTag gAss = new GAssociationPostTag();
		gAss.setCreator(findOrCreateGPerson(ass.getCreator().getPersonId()));
		gAss.setDate(ass.getDate());
		gAss.setGuid(ass.getGuid());
				
		GPost gPost = findOrCreateGPost(ass.getPost().getPostId());
		gAss.setPost(gPost);
		gPost.getTagAssociations().add(gAss);
		
		gAss.setTag(findOrCreateGTag(ass.getTag().getTagId()));
		return gAss;
	}
	
	private GTag generateLazyTag(Tag t){
		GTag gTag = findOrCreateGTag(t.getTagId());
//		gTag.setCreator(findOrCreateGPerson(t.getCreator().getPersonId()));
		gTag.setDate(t.getDate());
//		gTag.setDescription(t.getDescription());
		gTag.setTagId(t.getTagId());
		gTag.setType(t.getType());
		gTag.setValue(t.getValue());
//		gTag.setMass(t.getMass());
		
		return gTag;
	}
	
	private GPerson generateLazyPerson(Person p){
		GPerson gPerson = findOrCreateGPerson(p.getPersonId());
		
//		gPerson.setBio(p.getBio());
		gPerson.setBirthday(p.getBirthday());
		gPerson.setDate(p.getDate());
//		gPerson.setEmail(p.getEmail());
		gPerson.setHits(p.getHits());
		gPerson.setLastAccessDate(p.getLastAccessDate());
		gPerson.setLogin(p.getLogin());
		gPerson.setName(p.getName());
		gPerson.setPersonId(p.getPersonId());
		gPerson.setStatus(p.getStatus());
//		gPerson.setObjects(GenericBeanConverter.convertUserObjects(p.getObjects()));
//		gPerson.setPrivileges(GenericBeanConverter.convertPrivileges(p.getPrivileges()));
//		gPerson.setImage(GenericBeanConverter.convertImage(p.getImage()));
		gPerson.setAnonymous(p.isAnonymous());
		
		return gPerson;
		
	}
	
	private GPerson findOrCreateGPerson(String personId){
		GPerson gPerson = personMap.get(personId);
		if(gPerson == null){
			gPerson = new GPerson();
			personMap.put(personId, gPerson);
		}
		return gPerson;
	}
	
	private GPost findOrCreateGPost(String postId){
		GPost gPost = postMap.get(postId);
		if(gPost == null){
			gPost = new GPost();
			gPost.setPostId(postId);//New Added for root/thread search
			postMap.put(postId, gPost);
		}
		return gPost;
	}
	
	private GTag findOrCreateGTag(String tagId){
		GTag gTag = tagMap.get(tagId);
		if(gTag == null){
			gTag = new GTag();
			tagMap.put(tagId, gTag);
		}
		return gTag;
	}
	
	private GEntry findOrCreateGEntry(String entryId){
		GEntry gEntry = entryMap.get(entryId);
		if(gEntry == null){
			gEntry = new GEntry();
			entryMap.put(entryId, gEntry);
		}
		return gEntry;
	}
}
