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
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.server.beanconverters.FastPostBeanConverter;
import org.ttdc.gwt.server.beanconverters.GenericBeanConverter;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.HasGuid;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;

/**
 * @author Trevis
 * 
 * I must have been deep in it when i wrote this.  I just stumbled into this class to day 10/4/09 
 * and i hardly remember creating it.  It has a test which hardly tests it and a lot of code.  
 * 
 * This class appears to translate from hibernate classes to my transport bean types.  I think that i 
 * wrote this to fetch the data in the fastest way i could figure out how to.  Also, i dont 
 * think that it is complete.
 *
 */
@Deprecated  //TODO move the nested functionality out of here and dump this class.

public final class Inflatinator {
	private final static Logger log = Logger.getLogger(Inflatinator.class);
	
	private final Map<String, GEntry> entryMap = new HashMap<String,GEntry>();
	private final Map<String, GPost> postMap = new HashMap<String,GPost>();
	private final Map<String, GTag> tagMap = new HashMap<String,GTag>();
	private final Map<String, GPerson> personMap = new HashMap<String,GPerson>();
	private final Map<String, GImage> imageMap = new HashMap<String,GImage>();
	private final List<Post> list;
	private GPost threadRoot;
	
	private enum Mode {FLAT, HIERARCHY};
	
	private final StopWatch stopwatch = new StopWatch();
	
	public Inflatinator(List<Post> list) {
		this.list = list;
	}
	
	//TODO teach this thing how to do nested.
	
	/**
	 * This method will generate one post object with the full hierarchy 
	 * below it
	 */
	public List<GPost> extractPostHierarchyAtRoot(){
		List<GPost> list = extractPostHierarchy();
		
		if(threadRoot == null) throw new RuntimeException("Post list didnt contain a root!");
		threadRoot.setPosts(list);
		
		List<GPost> rootList = new ArrayList<GPost>();
		rootList.add(threadRoot);
		return rootList;
	}
	
	/**
	 * This version will extract all conversations and bypass the thread root entirely
	 * 
	 */
	public List<GPost> extractPostHierarchy(){
		
		if(list.size() == 0){
			log.debug("extractPostHierarchy() exited because list was empty.");
			return new ArrayList<GPost>();
		}
		stopwatch.start();
		
		log.debug("extractPostHierarchy() started.");
		
		
		List<String> postIds = extractIds(list);

		List<Object[]> objList = loadBundleForPosts(postIds);
		
		List<HasGuid> elementList = detangleHibernateObjectArrayList(objList);
				
		List<GPost> posts = convertElementsToGElements(elementList,Mode.HIERARCHY);
		
		List<String> threadIds = extractThreadIds(list);
		
		List<GPost> threads = makeIntoThreadList(posts, threadIds);
		
		Collections.sort(threads,new GPostByPostIdReferenceComparator(threadIds));
		
		log.debug("extractPostHierarchy() completed. Elapsed time: " +stopwatch);
		return threads;
	}
	
	public List<GPost> makeIntoThreadList(List<GPost> posts, List<String> threadIds){
		List<GPost> threads = new ArrayList<GPost>();
		
		for(GPost post : posts){
			if(threadIds.contains(post.getPostId())){
				threads.add(post);
			}
		}
		
		return threads;
	}
	
	/**
	 * This extractor assumes that the given list of posts is a list with it's children flattened
	 * materalized path style.  I am creating it initially for the nested thread view but
	 * it may be useful in other places, potentially the front page
	 * 
	 * @return
	 */
	public List<GPost> extractPostsNested(){
		if(list.size() == 0){
			log.debug("extractPosts() exited because list was empty.");
			return new ArrayList<GPost>();
		}
		
		stopwatch.start();
		log.debug("extractPostsNested() started.");
		
		List<Post> unNestedList = new ArrayList<Post>();
		unNestedList.addAll(list);
		for(Post p : list){
			if(p.isThreadPost()) //When the post is not a thread root, just ignore the children. This is to fix a bug with the partial child only fetch
				unNestedList.addAll(p.getPosts());
		}
		
//		List<String> postIds = extractIds(unNestedList);
//		List<Object[]> objList = loadBundleForPosts(postIds);
//		List<HasGuid> elementList = detangleHibernateObjectArrayList(objList);
//		List<GPost> posts = convertElementsToGElements(elementList,Mode.FLAT);
		
		List<GPost> posts = FastPostBeanConverter.convertPosts(unNestedList);

		
		List<GPost> resultGPostList = transformFlatListToNestedThreads(posts);
		
		log.debug("extractPostsNested() completed. Elapsed time: " +stopwatch);
		return resultGPostList;
	}

	/**
	 * 
	 * Arrange flat list of GPost objects into the nest order
	 * 
	 * @param posts
	 * @return
	 */
	private List<GPost> transformFlatListToNestedThreads(List<GPost> posts) {
		List<GPost> resultGPostList = new ArrayList<GPost>();
		for(Post thread : list){
			GPost gThread = findMyGPost(thread,posts);
			
			resultGPostList.add(gThread);
			for(Post post : thread.getPosts()){
				//initFromPost
				GPost gPost = findMyGPost(post,posts);
				gPost.setSuggestSummary(true);
				gThread.getPosts().add(gPost);
			}
		}
		return resultGPostList;
	}
	
	//locates a the GPost of a Post from a provided GPost list. 
	//Method currently has no boundary checks what so ever.
	private GPost findMyGPost(Post post, List<GPost> gPosts){
		GPost gp = new GPost();
		gp.setPostId(post.getPostId());
		return gPosts.get(gPosts.indexOf(gp));
	}
	
	public List<GPost> extractPosts(){
		throw new RuntimeException("Disabled... use FastPostBeanConverter instaed");
		
//		if(list.size() == 0){
//			log.debug("extractPosts() exited because list was empty.");
//			return new ArrayList<GPost>();
//		}
//		
//		stopwatch.start();
//		log.debug("extractPosts() started.");
//		
//		List<String> postIds = extractIds(list);
//		List<Object[]> objList = loadBundleForPosts(postIds);
//		List<HasGuid> elementList = detangleHibernateObjectArrayList(objList);
//		List<GPost> posts = convertElementsToGElements(elementList,Mode.FLAT);
//		
//		
//		Collections.sort(posts,new GPostByPostIdReferenceComparator(postIds));
//		
//		log.debug("extractPosts() completed. Elapsed time: " +stopwatch);
//		return posts;
	}
	
	private static List<String> extractIds(List<Post> posts){
		List<String> postIds = new ArrayList<String>();
		for(Post pl : posts){
			postIds.add(pl.getPostId());
		}
		return postIds;
	}
	private static List<String> extractThreadIds(List<Post> posts){
		List<String> threadIds = new ArrayList<String>();
		for(Post pl : posts){
			if(pl.isPostThreadRoot())
				threadIds.add(pl.getPostId());
		}
		return threadIds;
	}
	

	private List<GPost> convertElementsToGElements(List<HasGuid> elementList, Mode mode) {
		List<GPost> posts = new ArrayList<GPost>();
		for(HasGuid instance : elementList){
			if(instance instanceof Post){
				Post p = (Post) instance;
				GPost gPost;
				if(mode.equals(Mode.HIERARCHY))
					gPost = createLazyPostWithFamily(p);
				else
					gPost = createLazyPost(p);
				
				posts.add(gPost);
			}
			else if(instance instanceof Entry){
				generateLazyEntry((Entry)instance);
			}
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

	@SuppressWarnings("unchecked")
	private List<Object[]> loadBundleForPosts(List<String> postIds) {
		List<Object []> objList = session().createQuery("SELECT ass,p,t,e,c FROM AssociationPostTag ass INNER JOIN ass.post as p " +
				" INNER JOIN ass.tag as t INNER JOIN ass.post.latestEntry e INNER JOIN ass.creator c WHERE ass.post.postId IN (:postIds)")
				.setParameterList("postIds", postIds).list();
		return objList;
	}
	
	/**
	 * This takes one of those ugly nested array's from hibernate and flattens it out. 
	 *  
	 *  
	 * @param list
	 * @return
	 */
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
	
	private GPost createLazyPostWithFamily(Post p){
		GPost gPost = createLazyPost(p); 
			//findOrCreateGPost(p.getPostId());
		
		if(p.getParent() != null){
			gPost.setParent(findOrCreateGPost(p.getParent().getPostId()));
			gPost.getParent().getPosts().add(gPost);
			//gPost.setEntries(convertEntries(p.getEntries()));
			
	//		gPost.setDate(p.getDate());
	//		gPost.setPostId(p.getPostId());
	//		gPost.setTitle(p.getTitle());
	//		gPost.setReplyCount(p.getReplyCount());
	//		gPost.setMass(p.getMass());
			
			//TODO: gPost needs edit date.
			//gPost.setPosts(convertPosts(p.getPosts()));
			//gPost.setTagAssociations(convertAssociationsPostTag(p.getTagAssociations()));
		}
		else{
			threadRoot = gPost;
		}
		return gPost;
	}
	
	private GPost createLazyPost(Post p){
		GPost gPost = findOrCreateGPost(p.getPostId());
		//gPost.setEntries(convertEntries(p.getEntries()));
		if(gPost.getTitleTag() == null){
			//gPost.setLatestEntry(generateLazyEntry(p.getEntry()));
			generateLazyEntry(p.getEntry());
			gPost.setDate(p.getDate());
			gPost.setPostId(p.getPostId());
			//gPost.setTitle(p.getTitle());
			gPost.setTitleTag(findOrCreateGTag(p.getTitleTag().getTagId()));
			gPost.setCreator(findOrCreateGPerson(p.getCreator().getPersonId()));
			if(p.getAvgRatingTag() != null)
				gPost.setAvgRatingTag(findOrCreateGTag(p.getAvgRatingTag().getTagId()));
			gPost.setPublishYear(p.getPublishYear());
			gPost.setMetaMask(p.getMetaMask());
			gPost.setUrl(p.getUrl());
			
			gPost.setReplyCount(p.getReplyCount());
			gPost.setMass(p.getMass());
			gPost.setRootPost(p.isRootPost());
			gPost.setThreadPost(p.isThreadPost());
			gPost.setPath(p.getPath());
			gPost.setRoot(findOrCreateGPost(p.getRoot().getPostId()));
			if(!gPost.isRootPost())
				gPost.setThread(findOrCreateGPost(p.getThread().getPostId()));
			
			if(p.getImage() != null)
				gPost.setImage(FastPostBeanConverter.convertImage(p.getImage()));
			else if(p.isMovie()){
				gPost.setImage(FastPostBeanConverter.convertImage(InitConstants.DEFAULT_POSTER));
			}
		}
		//TODO: gPost needs edit date.
		//gPost.setPosts(convertPosts(p.getPosts()));
		//gPost.setTagAssociations(convertAssociationsPostTag(p.getTagAssociations()));
		return gPost;
	}
	
	private GEntry generateLazyEntry(Entry e){
		GEntry gEntry = findOrCreateGEntry(e.getEntryId());
		gEntry.setBody(e.getBody());
		//gEntry.setBodyFormatted(e.getBody());
		gEntry.setSummary(e.getSummary());
		gEntry.setDate(e.getDate());
		gEntry.setEntryId(e.getEntryId());
		
		GPost gPost = findOrCreateGPost(e.getPost().getPostId());
		
		gPost.setLatestEntry(gEntry);
		
		return gEntry;
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
		gTag.setMass(t.getMass());
		
		return gTag;
	}
	
	private GPerson generateLazyPerson(Person p){
		GPerson gPerson = findOrCreateGPerson(p.getPersonId());
		
		gPerson.setBio(p.getBio());
		gPerson.setBirthday(p.getBirthday());
		gPerson.setDate(p.getDate());
		gPerson.setEmail(p.getEmail());
		gPerson.setHits(p.getHits());
		gPerson.setLastAccessDate(p.getLastAccessDate());
		gPerson.setLogin(p.getLogin());
		gPerson.setName(p.getName());
		gPerson.setPersonId(p.getPersonId());
		gPerson.setStatus(p.getStatus());
		gPerson.setObjects(GenericBeanConverter.convertUserObjects(p.getObjects()));
		gPerson.setPrivileges(GenericBeanConverter.convertPrivileges(p.getPrivileges()));
		gPerson.setImage(GenericBeanConverter.convertImage(p.getImage()));
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
	
//	private GImage findOrCreateGImage(String imageId){
//		GImage gImage = imageMap.get(imageId);
//		if(gImage == null){
//			gImage = new GImage();
//			imageMap.put(imageId, gImage);
//		}
//		return gImage;
//	}
	
	/*
	private GAssociationPostTag findOrCreateGAss(String assId){
		GAssociationPostTag gAss = assMap.get(assId);
		if(gAss == null){
			gAss = new GAssociationPostTag();
			assMap.put(assId, gAss);
		}
		return gAss;
	}
	*/
	
	
}	
