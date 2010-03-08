package org.ttdc.biz.network.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.ttdc.biz.network.services.helpers.Paginator;
import org.ttdc.biz.network.services.helpers.PostDecorator;
import org.ttdc.biz.network.services.helpers.PostHelper;
import org.ttdc.biz.network.services.helpers.SearchResultsBundle;
import org.ttdc.biz.network.services.helpers.ThreadPostDecorator;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.PostCounter;
import org.ttdc.persistence.objects.PostLite;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.TagLite;
import org.ttdc.util.ServiceException;
import org.ttdc.util.web.HTMLCalendar;
import org.ttdc.util.web.Month;

public final class SearchService {
	private final static Logger log = Logger.getLogger(SearchService.class);
	
	public final static String SORT_YEAR = "year";
	public final static String SORT_TITLE = "title";
	public final static String SORT_RATING = "rating";
	private final static int MAX_SEARCH_RESULTS = 5000;
	
	public static final SearchService getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		private final static SearchService INSTANCE = new SearchService();
	}
	
	/**
	 * This is a helper method to read the postId's from the custom queries which 
	 * do all of the grunt work for the tag browser and search.
	 * 
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> readPostIdsForNamedQuery(Query query){
		List<String> postIds = new ArrayList<String>();
		Iterator<Object> itr = query.iterate();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof String )
				postIds.add((String)obj);
			else
				postIds.add((String)((Object[])obj)[0]);
		}
		return postIds;
	}
	
	/**
	 * Search with a tag filter
	 * 
	 * @param phrase
	 * @param spiderTag
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public SearchResultsBundle search(Person person, String threadId, String phrase, List<String> spiderTag) throws ServiceException{
		try{
			Paginator.remove();
			//If tags are chosen, we just search commments, dont look for tags that match the phrase.
			SearchResultsBundle results = new SearchResultsBundle();
			results.setSearchPhrase(phrase);
			results.setMode(SearchResultsBundle.MODE_SEARCH_SUMMARY);
			Set<String> postIdSet = new HashSet<String>(100);
			Query query;
			
			MultiFieldQueryParser parser = new MultiFieldQueryParser( new String[]{"body"}/*This is an array of which indexes to search*/, 
					  new StandardAnalyzer());
			FullTextSession fullTextSession = Persistence.fullTextSession();
			org.apache.lucene.search.Query indexQuery = parser.parse( phrase );
			
			
			Session session = Persistence.beginSession();
			
			if(threadId != null && !threadId.equals("")){
				results.setThreadId(threadId);
				Post thread = (Post) session.load(Post.class, threadId); 
				results.setThreadTitle(thread.getTitle());
			}
			
			
			
			
			if(spiderTag.size() > 0){
				//Get the postId's for all of the posts that you care about
				//query = session.getNamedQuery("postLite.postsTagUnion").setParameterList("tagIds", spiderTag).setInteger("count", spiderTag.size());
				if(threadId != null && threadId.length() > 0)
					query = session.getNamedQuery("postId.PostsTagUnionThread").setCacheable(true).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag).setString("threadId", threadId);
				else
					query = session.getNamedQuery("postId.PostsTagUnion").setCacheable(true).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
				
				postIdSet.addAll(readPostIdsForNamedQuery(query));
			}
			else if(threadId != null && threadId.length() > 0){
				//Get the post id's for all of the posts in this thread.
				query = session.getNamedQuery("postId.Thread").setString("threadId", threadId);
				postIdSet.addAll(readPostIdsForNamedQuery(query));
			}
			//Only keep the ones you want
			List<Post> posts = new ArrayList<Post>();
			int pagesize = 2000;
			int start = 0;
			int total = 0;
			
			do{
				FullTextQuery ftquery = fullTextSession.createFullTextQuery( indexQuery, Entry.class ).setFirstResult(start).setMaxResults(pagesize);
				if(total == 0)
					total = ftquery.getResultSize();
				
				List<Entry> entryKwMatches = ftquery.list();
				
				for(Entry entry : entryKwMatches){
					if(postIdSet.contains(entry.getPost().getPostId())){
						posts.add(entry.getPost());
					}
				}
				start += pagesize;
			}while(start < total);
			results.setTotalPosts(posts.size());
			
			
			/*
			if(posts.size() == 0){
				throw new ServiceException("Sorry, i didnt find anything for: '"+phrase+"'");
			}
			//Trevis, you should probably put this in the resuls class. Because it's really only 
			// true if it funds nothing. No tags or anything.
			*/
			
			
			if(posts.size() > person.getNumCommentsThreadPage()){
				Paginator paginator = new Paginator<Post>(posts,new PostDecorator(person),person.getNumCommentsThreadPage());
				posts = paginator.getPage(1);
			}
			else{
				PostHelper.initializePostsFlat(person, posts);
			}
			
			//PostHelper.initializePostsFlat(posts, person.getFilteredTagIds());
			results.getPosts().addAll(posts);
			
			
			Persistence.commit();  
			
			return results;
		}
		
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}

	}
	
	/**
	 * This method is called when a tagbrowser user chooses to view all of the threads in their result set
	 * 
	 * @param spiderTag
	 * @return
	 * @throws ServiceException
	 */
	public SearchResultsBundle spiderExpandThreads(Person person, List<String> spiderTag) throws ServiceException{
		try{
			Paginator.remove();
			SearchResultsBundle results = new SearchResultsBundle();
			List<Post> posts = new ArrayList<Post>();
			List<String> postIds = new ArrayList<String>();
			Session session = Persistence.beginSession();
			Query query;
			
			results.setMode(SearchResultsBundle.MODE_FULL_THREAD);
			
			query = session.getNamedQuery("postId.rootPostsTagUnionByMass").setCacheable(true).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			postIds = readPostIdsForNamedQuery(query);
			
			Map<String,PostCounter> totalsMap = PostHelper.loadPostCounters(postIds);
			if(postIds.size() > 0){
				if(postIds.size() > person.getNumCommentsThreadPage()){
					List<Post> pagePosts = new ArrayList<Post>();
					for(String postId : postIds){
						Post p = new Post();
						p.setPostId(postId);
//						p.setPostCounter(totalsMap.get(p.getPostId()));
//						pagePosts.add(p);
					}
//					Collections.sort(pagePosts,new Post.PostCounterComparator());
					Paginator<Post> paginator = new Paginator<Post>(pagePosts,new PostDecorator(person),person.getNumCommentsThreadPage());
					posts = paginator.getPage(1);
				}
				else{
					posts = PostHelper.loadPosts(postIds,person.getFilteredTagIds());
					//PostHelper.initializePostsFlat(posts, person.getFilteredTagIds());
				}
			}
			
			results.getThreads().addAll(posts);
			results.setTotalThreads(postIds.size());
			Persistence.commit();
			return results;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * 
	 * 
	 * @param spiderTag
	 * @return
	 * @throws ServiceException
	 */
	public SearchResultsBundle spiderExpandComments(Person person, List<String> spiderTag, String threadId) throws ServiceException{
		try{
			Paginator.remove();
			SearchResultsBundle results = new SearchResultsBundle();
			List<Post> posts = new ArrayList<Post>();
			List<PostLite> litePosts = new ArrayList<PostLite>();
			List<String> postIds = new ArrayList<String>();
			Session session = Persistence.beginSession();
			Query query;
			
			results.setMode(SearchResultsBundle.MODE_FULL_POST);
		
			if(threadId != null && threadId.length() > 0){
				query = session.getNamedQuery("postId.replyPostsTagUnionByDateThread").setCacheable(true).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag).setString("threadId", threadId);
			}
			else
				query = session.getNamedQuery("postId.replyPostsTagUnionByDate").setCacheable(true).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			postIds = readPostIdsForNamedQuery(query);
			
			if(postIds.size() > 0){
				if(postIds.size() > person.getNumCommentsThreadPage()){
					List<Post> pagePosts = new ArrayList<Post>();
					for(String postId : postIds){
						Post p = new Post();
						p.setPostId(postId);
						pagePosts.add(p);
					}
					Paginator<Post> paginator = new Paginator<Post>(pagePosts,new PostDecorator(person),person.getNumCommentsThreadPage());
					posts = paginator.getPage(1);
				}
				else{
					posts = PostHelper.loadPosts(postIds,person.getFilteredTagIds());
					PostHelper.initializePostsFlat(person,posts);
				}
			}

			results.getPosts().addAll(posts);
			results.setTotalPosts(litePosts.size());
			Persistence.commit();
			return results;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Post> readMovies(Person person, String sort, boolean reverse, String forPersonId, boolean rated) throws ServiceException{
		try{
			Paginator.remove();
			Session session = Persistence.beginSession();
			Query query;
			List<PostLite> litePosts = null;
			
			if(SORT_TITLE.equals(sort))
				if(!reverse)
					query = session.getNamedQuery("postId.moviesByTitle");
				else
					query = session.getNamedQuery("postId.moviesByTitleReverse");
			else if(SORT_RATING.equals(sort)){
				if(forPersonId != null && !"".equals(forPersonId)){
					if(!reverse)
						query = session.getNamedQuery("postId.moviesByRatingForPerson").setString("personId", forPersonId);
					else
						query = session.getNamedQuery("postId.moviesByRatingForPersonReverse").setString("personId", forPersonId);
				}
				else{
					if(!reverse)
						query = session.getNamedQuery("native.postId.moviesByRating");
					else
						query = session.getNamedQuery("native.postId.moviesByRatingReverse");
					
					litePosts = query.list();
				}
			}
			else if(SORT_YEAR.equals(sort))
				if(!reverse)
					query = session.getNamedQuery("postId.moviesByYear");
				else
					query = session.getNamedQuery("postId.moviesByYearReverse");
			else
				throw new ServiceException("No sort mode selected.");
			
			List<String> postIds = new ArrayList<String>();
			if(litePosts == null){
				postIds = readPostIdsForNamedQuery(query);
			}
			else{
				for(PostLite pl : litePosts){
					postIds.add(pl.getPostId());
				}
			}
			
			//If we're filtering for a selected person this query will widdle the list down
			if(forPersonId != null && !"".equals(forPersonId)){
				/*
				if(rated)	
					query = session.getNamedQuery("postId.moviesForUser").setString("personId", forPersonId);
				else
					query = session.getNamedQuery("postId.moviesUnRatedByUser").setString("personId", forPersonId);
				
				
				List<String> userPostIds = readPostIdsForNamedQuery(query);
				List<String> tempPostIds = new ArrayList<String>();
				for(String postId : postIds){
					if(userPostIds.contains(postId))
						tempPostIds.add(postId);
				}
				postIds = tempPostIds;
				*/
				
				query = session.getNamedQuery("postId.moviesForUser").setString("personId", forPersonId);
				
				
				List<String> userPostIds = readPostIdsForNamedQuery(query);
				List<String> tempPostIds = new ArrayList<String>();
				if(rated){
					for(String postId : postIds){
						if(userPostIds.contains(postId))
							tempPostIds.add(postId);
					}
				}	
				else{
					for(String postId : postIds){
						if(!userPostIds.contains(postId))
							tempPostIds.add(postId);
					}
				}

				postIds = tempPostIds;
			}
				
			List<Post> movies = new ArrayList<Post>();
			 
			if(postIds.size() > 0){
				if(postIds.size() > person.getNumCommentsThreadPage()){
					List<Post> pagePosts = new ArrayList<Post>();
					for(String postId : postIds){
						Post p = new Post();
						p.setPostId(postId);
						pagePosts.add(p);
					}
					Paginator<Post> paginator = new Paginator<Post>(pagePosts,new PostDecorator(UserService.getInstance().getAnnonymousUser(), false),person.getNumCommentsThreadPage());
					movies = paginator.getPage(1);
				}
				else{
					movies = PostHelper.loadPosts(postIds, new ArrayList<String>());
					PostHelper.initializePostsFlat(UserService.getInstance().getAnnonymousUser(), movies, false);
				}
			}
			
			Persistence.commit();
			return movies;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}

		
	}
	
	
	public SearchResultsBundle spiderPhrase(Person person, String phrase, List<String> spiderTag) throws ServiceException{
		Session session = Persistence.beginSession();
		
		//Find the tag.
		Query query = session.getNamedQuery("tag.getByValue");
		Tag t = null;
		if(!phrase.trim().equals("")){
			@SuppressWarnings("unchecked") List<Tag> tags = query.setString("value", phrase.trim()).list();
			t = tags.get(0);
			if(tags.size() > 1){
				for(Tag tag:tags){
					//Look for the topic, assume that in case of a duplicate
					if(Tag.TYPE_TOPIC.equals(tag.getType())){
						t = tag;
						break;
					}
				}
			}
		}
		String tagId = "";
		if(t != null)
			tagId = t.getTagId();
		
		return spider(person, tagId,spiderTag);
	}
	
	
	/**
	 * 
	 * Uses the day, month, year to look up tagged results
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @param spiderTag
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public SearchResultsBundle spiderCalendar(Person person, int day, int month, int year, List<String> spiderTag) throws ServiceException{
		Session session = Persistence.beginSession();
		
		Query query = session.getNamedQuery("tag.getByValueAndType");
		Tag t = null;
		List<Tag> tags;
		
//		tags = query.setString("value", HTMLCalendar.MonthNames[month-1]).setString("type", Tag.TYPE_DATE_MONTH).list();
//		if(tags.size() == 1){
//			t = tags.get(0);
//			spiderTag.add(t.getTagId());
//		}
//		
//		tags = query.setString("value", ""+day).setString("type", Tag.TYPE_DATE_DAY).list();
//		if(tags.size() == 1){
//			t = tags.get(0);
//			spiderTag.add(t.getTagId());
//		}
//		
//		tags = query.setString("value", ""+year).setString("type", Tag.TYPE_DATE_YEAR).list();
//		if(tags.size() == 1){
//			t = tags.get(0);
//			spiderTag.add(t.getTagId());
//		}
		
		return spider(person, "",spiderTag);
	}
	
	/**
	 * spider uses the spiderTag attribute to locate a subset of posts.  Phrase is a new
	 * search phrase used to find a tag within this spider web of tags.
	 *  
	 * @param phrase
	 * @param spiderTag a list of TagID;s
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResultsBundle spider(Person person, String tagId, List<String> spiderTag) throws ServiceException{
		try{
			Paginator.remove();
			SearchResultsBundle results = new SearchResultsBundle();
			List<Post> posts = new ArrayList<Post>();
			List<PostLite> liteThreads = new ArrayList<PostLite>();
			List<String> postIds = new ArrayList<String>();
			Session session = Persistence.beginSession();
			
			results.setMode(SearchResultsBundle.MODE_TAG_BROWSER_SUMMARY);
			
			Query query;
			//Find the tag.
			if(!tagId.equals("")){
				spiderTag.add(tagId);
			}
			
			//If phrase is blank, the client probably removed an item... so go ahead and process the rest.
			
			query = session.getNamedQuery("tagLite.TagMassForSpiderTopic").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			results.setTotalSuggestions(query.list().size());
			
			query = session.getNamedQuery("tagLite.TagMassForSpiderTopic").setMaxResults(SearchResultsBundle.MAX_INITIAL_TAG_SUGESTIONS).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			results.getSuggestions().addAll(query.list());
			TagLite.calculatePercentile(results.getSuggestions());
			
			
			query = session.getNamedQuery("tagLite.TagMassForSpiderOthers").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			//results.getSuggestions().addAll(query.list());
			
			List<TagLite> allTags = query.list();
			TagLite.calculatePercentile(allTags);
//			for(TagLite tl : allTags){
//				if(Tag.TYPE_CREATOR.equals(tl.getType())){
//					results.getSuggestionsPeople().add(tl);
//				}
//				else if(Tag.TYPE_DATE_MONTH.equals(tl.getType())){
//					results.getSuggestionsMonths().add(tl);
//				}
//				else if(Tag.TYPE_DATE_YEAR.equals(tl.getType())){
//					results.getSuggestionsYears().add(tl);
//				}
//				else{
//					//Weird
//				}
//			}
			Collections.sort(results.getSuggestionsMonths(), new TagLite.MonthComparator());
			
			List<String> replyIds = new ArrayList<String>(); 
			List<String> threadIds = new ArrayList<String>();
			
			query = session.getNamedQuery("postLite.rootPostsTagUnionByMass").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			results.setTotalThreads(query.list().size());
			
			query = session.getNamedQuery("postLite.rootPostsTagUnionByMass").setMaxResults(SearchResultsBundle.MAX_INITIAL_THREAD_COUNT).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			liteThreads = query.list();
			
			
			query = session.getNamedQuery("postLite.replyPostsTagUnionByDate").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			results.setTotalPosts(query.list().size());
			
			//query = session.getNamedQuery("postLite.replyPostsTagUnionByDate").setMaxResults(SearchResultsBundle.MAX_INITIAL_POST_COUNT).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			query = session.getNamedQuery("postId.replyPostsTagUnionByDate").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			replyIds = PostHelper.loadGuidsFromQuery(query.iterate(),SearchResultsBundle.MAX_INITIAL_POST_COUNT);
			
			
			for(PostLite pl : liteThreads){
				threadIds.add(pl.getPostId());
			}
			
			
			//Determining the size of the root threads that were found
			
			Map<String,PostCounter> totalsMap = PostHelper.loadPostCounters(threadIds);
			/*
			Map<String,PostCounter> totalsMap = new HashMap<String,PostCounter>();
			if(threadIds.size() > 0){
				List<PostCounter> totals = loadPostCounters(threadIds);
				for(PostCounter pct : totals){
					totalsMap.put(pct.getRootId(),pct);
				}
			}
			*/
			//
			
			postIds.addAll(replyIds);
			postIds.addAll(threadIds);
			
			if(postIds.size() > 0){
				query = session.getNamedQuery("post.getByPostIds").setParameterList("postIds", postIds);
				posts = query.list();
				PostHelper.initializePostsFlat(person, posts);
				/*
				for(Post p : posts){
					p.setExpanded(true);
				}
				initializeTagAssociations(postIds);
				*/
			}
			
			
//			if(postIds.size() > 0){
//				PostHelper.loadPosts(postIds, person.getFilteredTagIds());
//			}
			for(Post p : posts){
				if(threadIds.contains(p.getPostId())){
//					p.setPostCounter(totalsMap.get(p.getPostId()));
					results.getThreads().add(p);
				}
				else{
					results.getPosts().add(p);
				}
			}
			
//			Collections.sort(results.getThreads(),new Post.ByLiteReferenceComparator(liteThreads));
//			Collections.sort(results.getPosts(),new Post.DateComparatorDesc());
//			
			Persistence.commit();
			return results;
		}
		
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Spider thread is a special version of spider that allows you to tag browse content located in a single thread.
	 * 
	 * @param threadId
	 * @param tagId
	 * @param spiderTag
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public SearchResultsBundle spiderThread(Person person, String threadId, String tagId, List<String> spiderTag) throws ServiceException{
		try{
			Paginator.remove();
			SearchResultsBundle results = new SearchResultsBundle();
			List<Post> posts = new ArrayList<Post>();
			List<PostLite> liteThreads = new ArrayList<PostLite>();
			List<String> postIds = new ArrayList<String>();
			Session session = Persistence.beginSession();
			
			results.setMode(SearchResultsBundle.MODE_TAG_BROWSER_SUMMARY);
			
			Query query;
			
			if(!tagId.equals("")){
				spiderTag.add(tagId);
			}
			
			
			//If phrase is blank, the client probably removed an item... so go ahead and process the rest.
			
			query = session.getNamedQuery("tagLite.TagMassForSpiderTopicThread").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag).setString("threadId",threadId);
			results.setTotalSuggestions(query.list().size());
			
			query = session.getNamedQuery("tagLite.TagMassForSpiderTopicThread").setMaxResults(SearchResultsBundle.MAX_INITIAL_TAG_SUGESTIONS).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag).setString("threadId",threadId);
			results.getSuggestions().addAll(query.list());
			TagLite.calculatePercentile(results.getSuggestions());
			
			
			query = session.getNamedQuery("tagLite.TagMassForSpiderOthersThread").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag).setString("threadId",threadId);
			//results.getSuggestions().addAll(query.list());
			
			List<TagLite> allTags = query.list();
			TagLite.calculatePercentile(allTags);
			results.loadOtherTags(allTags);
			/*
			for(TagLite tl : allTags){
				if(Tag.TYPE_CREATOR.equals(tl.getType())){
					results.getSuggestionsPeople().add(tl);
				}
				else if(Tag.TYPE_DATE_MONTH.equals(tl.getType())){
					results.getSuggestionsMonths().add(tl);
				}
				else if(Tag.TYPE_DATE_YEAR.equals(tl.getType())){
					results.getSuggestionsYears().add(tl);
				}
				else{
					//Weird
				}
			}
			*/
			
			Collections.sort(results.getSuggestionsMonths(), new TagLite.MonthComparator());
			
			List<String> replyIds = new ArrayList<String>(); 
			List<String> threadIds = new ArrayList<String>();
			
			/*
			query = session.getNamedQuery("postLite.rootPostsTagUnionByMass").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			results.setTotalThreads(query.list().size());
			
			query = session.getNamedQuery("postLite.rootPostsTagUnionByMass").setMaxResults(SearchResultsBundle.MAX_INITIAL_THREAD_COUNT).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			liteThreads = query.list();
			*/
			results.setTotalThreads(1);
			threadIds.add(threadId);
			Post root = (Post)session.load(Post.class,threadId);
			results.setThreadTitle(root.getTitle());
			
			
			query = session.getNamedQuery("postLite.replyPostsTagUnionByDateThread").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag).setString("threadId", threadId);
			results.setTotalPosts(query.list().size());
			
			query = session.getNamedQuery("postLite.replyPostsTagUnionByDateThread")
				.setMaxResults(SearchResultsBundle.MAX_INITIAL_POST_COUNT)
				.setInteger("count",spiderTag.size())
				.setParameterList("tagIds", spiderTag)
				.setString("threadId", threadId);
				
			replyIds = PostHelper.extractIdsLite(query.list());
			/*
			query = session.getNamedQuery("postId.replyPostsTagUnionByDateThread").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			replyIds = PostHelper.loadGuidsFromQuery(query.iterate(),SearchResultsBundle.MAX_INITIAL_POST_COUNT);
			*/
			
			
			
			
			//Determining the size of the root threads that were found
			
			Map<String,PostCounter> totalsMap = PostHelper.loadPostCounters(threadIds);
			/*
			Map<String,PostCounter> totalsMap = new HashMap<String,PostCounter>();
			if(threadIds.size() > 0){
				List<PostCounter> totals = loadPostCounters(threadIds);
				for(PostCounter pct : totals){
					totalsMap.put(pct.getRootId(),pct);
				}
			}
			*/
			//
			
			postIds.addAll(replyIds);
			postIds.addAll(threadIds);
			/*
			if(postIds.size() > 0){
				query = session.getNamedQuery("post.getByPostIds").setParameterList("postIds", postIds);
				posts = query.list();
				
				for(Post p : posts){
					p.setExpanded(true);
				}
				initializeTagAssociations(postIds);
				
			}
			*/
			
			if(postIds.size() > 0){
				posts = PostHelper.loadPosts(postIds, person.getFilteredTagIds());
			}
			for(Post p : posts){
				if(threadIds.contains(p.getPostId())){
//					p.setPostCounter(totalsMap.get(p.getPostId()));
					results.getThreads().add(p);
				}
				else{
					results.getPosts().add(p);
				}
			}
			
//			Collections.sort(results.getThreads(),new Post.ByLiteReferenceComparator(liteThreads));
//			Collections.sort(results.getPosts(),new Post.DateComparatorDesc());
			
			Persistence.commit();
			return results;
		}
		
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}

	
	
	/**
	 * Straight search. No tag filtering on the results 
	 * 
	 * @param phrase
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public SearchResultsBundle search(Person person, String phrase) throws ServiceException{
		try{
			Paginator.remove();
			SearchResultsBundle results = new SearchResultsBundle();
			results.setMode(SearchResultsBundle.MODE_SEARCH_SUMMARY);
			results.setSearchPhrase(phrase);
			
			Query query;
			
			FullTextSession fullTextSession = Persistence.fullTextSession();

			QueryParser parser = new QueryParser("value", new StandardAnalyzer() );
			org.apache.lucene.search.Query luceneQuery = parser.parse( phrase );
			query = fullTextSession.createFullTextQuery( luceneQuery, Tag.class ).setMaxResults(MAX_SEARCH_RESULTS);
			List<Tag> tags = query.list();
			
			Persistence.commit();  
			Session session = Persistence.beginSession();
			List<String> tagIds = new ArrayList<String>();
			List<String> titleTagIds = new ArrayList<String>();
			/*
			for(Tag t : tags){
				if(Tag.TYPE_TOPIC.equals(t.getType()))
					tagIds.add(t.getTagId());
				else if(Tag.TYPE_TITLE.equals(t.getType()))
					titleTagIds.add(t.getTagId());
			}
			*/
			for(Tag t : tags){
				tagIds.add(t.getTagId());
				titleTagIds.add(t.getTagId()); //This was a quick hack to keep the thread search stuff working like it did after the TITLES to TOPICS project (build 013)  
			}
			
			//Get the tags with their masses
			if(tagIds.size() != 0){
				query = session.getNamedQuery("tagLite.TagMassSubset").setParameterList("tagIds", tagIds);
				List<TagLite> liteTags = query.list();
				results.getTags().addAll(liteTags);
				results.setTotalTags(liteTags.size());
			}
			
			//Load threads
			if(titleTagIds.size() != 0){
				query = session.getNamedQuery("post.getWithTitleTags").setParameterList("tagIds", titleTagIds);
				List<Post> threads = query.list();
				
				threads = sortPostsByTitleTagList(threads,tags);
				
				PostHelper.initializePostsFlat(person, threads);
				
				PostHelper.loadPostCounters(threads);
				
				results.setThreads(threads);
				results.setTotalThreads(threads.size());
			}
			Persistence.commit();  
			
			parser = new MultiFieldQueryParser( new String[]{"body"}/*This is an array of which indexes to search*/, new StandardAnalyzer());
			fullTextSession = Persistence.fullTextSession();
			org.apache.lucene.search.Query indexQuery = parser.parse( phrase );
			
			
			//Only keep the ones you want
			List<Post> posts = new ArrayList<Post>();
			
			FullTextQuery ftquery = fullTextSession.createFullTextQuery( indexQuery, Entry.class ).setMaxResults(SearchResultsBundle.MAX_INITIAL_POST_COUNT);
			List<Entry> entries = ftquery.list();
			for(Entry entry : entries){
				posts.add(entry.getPost());
			}
			results.setTotalPosts(ftquery.getResultSize());
			
			/*
			 * not paginating here anymore.  coments are limited on this first go
			 */
			/*
			if(posts.size() > Paginator.PER_PAGE){
				Paginator paginator = new Paginator<Post>(posts,new PostDecorator());
				posts = paginator.getPage(1);
			}
			else{
				initializePostsFlat(posts);
			}
			*/
			
			
			PostHelper.initializePostsFlat(person, posts);
			results.getPosts().addAll(posts);
			
			
			Persistence.commit();  
			
			return results;
		}
		/*
		catch(ServiceException e){
			throw e;
		}
		*/
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}

	}
	
	private List<Post> sortPostsByTitleTagList(List<Post> sort, List<Tag> tags){
		List<Post> sorted = new ArrayList<Post>();
		for(Tag t : tags){
			sorted.addAll(getPostsWithTitleTag(sort,t));
		}
		return sorted;
	}
	private List<Post> getPostsWithTitleTag(List<Post> posts, Tag t){
		List<Post> found = new ArrayList<Post>();
		for(Post p : posts){
//			AssociationPostTag ass = p.loadTitleTagAssociation();
//			if(ass!=null && ass.getTag().equals(t))
//				found.add(p);
		}
		return found;
	}
	/**
	 * This method is called when a user chooses to actually browse all of the comments
	 * from a search result. The original result shows a subset and provides a link which
	 * will execute this method 
	 * 
	 * @param phrase
	 * @throws ServiceException
	 */
	public SearchResultsBundle searchExpandComments(Person person, String phrase) throws ServiceException{
		try{
			Paginator.remove();
			SearchResultsBundle results = new SearchResultsBundle();
			results.setMode(SearchResultsBundle.MODE_SEARCH_SUMMARY);
			
			FullTextSession fullTextSession = Persistence.fullTextSession();

			QueryParser parser = new MultiFieldQueryParser( new String[]{"body"}/*This is an array of which indexes to search*/, new StandardAnalyzer());
			fullTextSession = Persistence.fullTextSession();
			org.apache.lucene.search.Query indexQuery = parser.parse( phrase );
			
			/*
			List<Post> posts = new ArrayList<Post>();
			FullTextQuery ftquery = fullTextSession.createFullTextQuery( indexQuery, Entry.class ).setMaxResults(SearchResultsBundle.MAX_INITIAL_POST_COUNT);
			List<Entry> entries = ftquery.list();
			for(Entry entry : entries){
				posts.add(entry.getPost());
			}
			results.setTotalPosts(ftquery.getResultSize());
			*/
			
			List<Post> posts = new ArrayList<Post>();
			int pagesize = 2000;
			int start = 0;
			int total = 0;
			
			do{
				FullTextQuery ftquery = fullTextSession.createFullTextQuery( indexQuery, Entry.class ).setFirstResult(start).setMaxResults(pagesize);
				if(total == 0)
					total = ftquery.getResultSize();
				
				@SuppressWarnings("unchecked") List<Entry> entryKwMatches = ftquery.list();
				
				for(Entry entry : entryKwMatches){
					posts.add(entry.getPost());
				}
				start += pagesize;
			}while(start < total);
			results.setTotalPosts(posts.size());
			
			
			if(posts.size() > person.getNumCommentsThreadPage()){
				Paginator<Post> paginator = new Paginator<Post>(posts,new PostDecorator(person),person.getNumCommentsThreadPage());
				posts = paginator.getPage(1);
			}
			else{
				PostHelper.initializePostsFlat(person, posts);
			}
			
			results.getPosts().addAll(posts);
			
			Persistence.commit();  
			
			return results;
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Load the tag sugestions for this list of tags.  This is designed for pagination.
	 * 
	 * @param spiderTag
	 * @param sugestions
	 * @throws ServiceException
	 */
	/*
	@SuppressWarnings("unchecked")
	public void loadTagSugestions(List<String> spiderTag, List<TagLite> sugestions, int maxCount) throws ServiceException{
		try{
			Session session = Persistence.session();
			Query query;
			if(maxCount > 0)
				query = session.getNamedQuery("tagLite.TagMassForSpiderTopic").setMaxResults(maxCount).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
			else
				query = session.getNamedQuery("tagLite.TagMassForSpiderTopic").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag);
				
			sugestions.addAll(query.list());
			TagLite.calculatePercentile(sugestions);
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
		
	}
	*/
	
	/**
	 * 
	 * @param sugestions
	 * @param maxCount
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void loadAllTagSugestions(List<TagLite> sugestions, int maxCount) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query;
			if(maxCount > 0)
				query = session.getNamedQuery("tagLite.TagMass").setMaxResults(maxCount);
			else
				query = session.getNamedQuery("tagLite.TagMass");
			sugestions.addAll(query.list());
			TagLite.calculatePercentile(sugestions);
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
		
	}
	
	/**
	 * Search for a thread.  findPostId says which post should be in focus, startAtTop will start at the last page of the inverted list
	 * (aka the beginning of the thread)
	 * 
	 * @param person
	 * @param rootId
	 * @param findPostId
	 * @param startAtTop
	 * @param perPage - number of comments to show per page.  The value in the person object can be overridden at runtime so i pass it in
	 * 					instead of just using the version in person.
	 * @return
	 * @throws ServiceException
	 */
	public SearchResultsBundle readThread(Person person, String rootId, String findPostId, boolean startAtTop, int perPage) throws ServiceException{
		try{
			Paginator.remove();
			SearchResultsBundle results = new SearchResultsBundle();
			Session session = Persistence.beginSession();
			Post root =	(Post)session.load(Post.class,rootId);
			List<Post> flat = new ArrayList<Post>();
			PostHelper.loadPostsFlat(root,flat);
			
			/*
			int start, end;
			end = flat.size();
			if(end >= THREAD_POSTS_PER_PAGE){
				start = end - THREAD_POSTS_PER_PAGE;
			}
			else{
				start = 0;
			}
			
			
			//return flat.subList(start, end);
			List<String> listToShow = flat.subList(start, end);
			
			List<String> filteredTagIds = person.getFilteredTagIds();
			initializePosts(root,listToShow,filteredTagIds, true);
			return root;
			
			*/
			
			
			if(flat.size() > perPage){
				Paginator<Post> paginator = new Paginator<Post>(rootId,flat,new ThreadPostDecorator(person,rootId),perPage);
				int page = -1;
				if(findPostId != null && findPostId.length() > 0){
					Post p = new Post();
					p.setPostId(findPostId);
					page = paginator.findPageContainingInverse(p);
				}
				else{
					if(!startAtTop)
						page = 1;
					else
						page = paginator.getNumPages();
				}
				List<Post> list = paginator.getPageInverse(page);
				root = list.get(0);
			}
			else{
				List<String> listToShow = PostHelper.extractIds(flat);
				PostHelper.initializePosts(person,root,listToShow);
				/*
				if(!person.isAnonymous()){
					InboxService.getInstance().flagUnreadPosts(person,root);
				}
				*/
			}
			
			/*
			//Remember. Threads are the only posts with titles. Title Tag ID is what i use as Thread Id
			// Ok i realized that the replies were being missed when linking to tagbrowser with the title
			//tag so i'm adding this hack to add the topic tag who's value matches the title
			AssociationPostTag threadTagAss = root.loadTagAssociation(Tag.TYPE_TITLE);
			//results.setThreadId(threadTagAss.getTag().getTagId());
			List<AssociationPostTag> asses = root.loadTagAssociations(Tag.TYPE_TOPIC);
			for(AssociationPostTag ass : asses){
				if(threadTagAss.getTag().getValue().equals(ass.getTag().getValue())){
					results.setThreadId(ass.getTag().getTagId());
				}
			}
			*/
			results.setThreadId(rootId);
			results.setThreadTitle(root.getTitle());
			
			
			
			
			/*
			Post post =	(Post)session.load(Post.class,guid);
			
			PostCounter pc = loadPostCounter(post.getRoot().getPostId());
			
			List<Post> temp = new ArrayList<Post>();
			Set<String> postIds = new HashSet<String>();
			*/
			/*
			Query query;
			
			query = session.getNamedQuery("post.getBranchByRootId").setCacheable(true).setString("rootId",rootId);
			
			query.list();
			*/
			
			Query query = session.getNamedQuery("tagLite.getForThread").setString("rootId", root.getPostId());
			@SuppressWarnings("unchecked") List<TagLite> litetags = query.list();
			results.loadOtherTags(litetags);
			
			results.setTotalTags(results.getTags().size());
			
			/*
			List<Post> siblingThreads = getThreadSiblings(root);
			PostHelper.initializePosts(person,siblingThreads,PostHelper.extractIds(siblingThreads));
			loadPostCounters(siblingThreads);
			results.setThreads(siblingThreads);
			*/
			
			Persistence.commit();
			List<Post> list = new ArrayList<Post>();
			list.add(root);
			
			results.setPosts(list);
			
			
			
			return results;
			
		}/*
		catch(ServiceException e){
			throw e;
		}*/
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	/*
	private List<Post> getThreadSiblings(Post thread){
		List<Post> posts = new ArrayList<Post>();
		
		AssociationPostTag ass = thread.loadTagAssociation(Tag.TYPE_TITLE);
		if(ass != null){
			Session session = Persistence.currentSession();
			Query query = session.getNamedQuery("post.getThreadSiblings").setString("tagId", ass.getTag().getTagId()).setString("threadId", thread.getPostId());
			posts = query.list();
		}
		
		return posts;
	}
	*/
	
	private List<Post> loadPostCounters(List<Post> posts){
		Map<String,PostCounter> totalsMap = PostHelper.loadPostCounters(PostHelper.extractIds(posts));
//		for(Post p : posts){
//			p.setPostCounter(totalsMap.get(p.getPostId()));
//		}
		return posts;
	}
	
	public SearchResultsBundle readThreadFlat(Person person, String rootId, String findPostId, boolean startAtTop, int perPage) throws ServiceException{
		try{
			Paginator.remove();
			SearchResultsBundle results = new SearchResultsBundle();
			Session session = Persistence.beginSession();
			
			
			Query query = session.getNamedQuery("post.getThreadByRootId").setString("rootId", rootId);
			@SuppressWarnings("unchecked") List<Post> flat = query.list();
			
			Post root =	(Post)session.load(Post.class,rootId);
			
			if(flat.size() > perPage){
				Paginator<Post> paginator = new Paginator<Post>(rootId,flat,new PostDecorator(person,true),perPage);
				int page = -1;
				if(findPostId != null && findPostId.length() > 0){
					Post p = new Post();
					p.setPostId(findPostId);
					page = paginator.findPageContainingInverse(p);
				}
				else{
					if(!startAtTop)
						page = 1;
					else
						page = paginator.getNumPages();
				}
				flat = paginator.getPageInverse(page);
			}
			else{
				PostHelper.initializePostsFlat(person,flat);
			}
			
			results.setThreadId(rootId);
			results.setThreadTitle(root.getTitle());
			
			query = session.getNamedQuery("tagLite.getForThread").setString("rootId", root.getPostId());
			@SuppressWarnings("unchecked") List<TagLite> litetags = query.list();
			results.loadOtherTags(litetags);
			
			results.setTotalTags(results.getTags().size());
			/*
			List<Post> siblingThreads = getThreadSiblings(root);
			PostHelper.initializePosts(person,siblingThreads,PostHelper.extractIds(siblingThreads));
			loadPostCounters(siblingThreads);
			results.setThreads(siblingThreads);
			*/
			Persistence.commit();
			
			results.setPosts(flat);
			
			return results;
			
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * The early version of v6 used tag browser for filtering threads but i'm modifying it so that you can filter a thread in side of the thread view
	 * This method searches for all of the comments in a thread contain the tags passed in via the spider list.
	 * 
	 * @param person
	 * @param threadId
	 * @param findPostId
	 * @param startAtTop
	 * @param perPage
	 * @param spiderTag
	 * @return
	 * @throws ServiceException
	 */
	public SearchResultsBundle readThreadFlat(Person person, String threadId, String findPostId, boolean startAtTop, int perPage, List<String> spiderTag, Integer day) throws ServiceException{
		if(spiderTag.size() == 0)
			return readThreadFlat(person, threadId, findPostId, startAtTop, perPage);
		try{
			Paginator.remove();
			SearchResultsBundle results = new SearchResultsBundle();
			Session session = Persistence.beginSession();
			Query query;
			Tag dayTag = null;
			if(day != null){
				query = session.getNamedQuery("tag.getByValueAndType");
//				Tag t = (Tag)query.setString("value", ""+day).setString("type",Tag.TYPE_DATE_DAY).uniqueResult();
//				if(t != null){
//					dayTag = t;
//					spiderTag.add(t.getTagId());
//				}
			}
			
			
			//Query query = session.getNamedQuery("post.getThreadByRootId").setString("rootId", rootId);
			query = session.getNamedQuery("postId.replyPostsTagUnionByDateThread").setCacheable(true).setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag).setString("threadId", threadId);
			
			Post root =	(Post)session.load(Post.class,threadId);
			
			List<Post> flat = null;
			
			List<String> postIds = readPostIdsForNamedQuery(query);
			
			postIds.add(root.getPostId());
			
			if(postIds.size() > 0){
				if(postIds.size() > person.getNumCommentsThreadPage()){
					List<Post> pagePosts = new ArrayList<Post>();
					for(String postId : postIds){
						Post p = new Post();
						p.setPostId(postId);
						pagePosts.add(p);
					}
					Paginator<Post> paginator = new Paginator<Post>(threadId,pagePosts,new PostDecorator(person),person.getNumCommentsThreadPage());
					
					int page;					
					if(!startAtTop)
						page = 1;
					else
						page = paginator.getNumPages();
					
					flat = paginator.getPageInverse(page);
				}
				else{
					flat = PostHelper.loadPosts(postIds,person.getFilteredTagIds());
					PostHelper.initializePostsFlat(person,flat);
				}
			}
				
			

			
			
			
//			if(flat.size() > perPage){
//				Paginator<Post> paginator = new Paginator<Post>(flat,new PostDecorator(person,true),perPage);
//				int page = -1;
//				if(findPostId != null && findPostId.length() > 0){
//					Post p = new Post();
//					p.setPostId(findPostId);
//					page = paginator.findPageContainingInverse(p);
//				}
//				else{
//					if(!startAtTop)
//						page = 1;
//					else
//						page = paginator.getNumPages();
//				}
//				flat = paginator.getPageInverse(page);
//			}
//			else{
//				PostHelper.initializePostsFlat(person,flat);
//			}
			
			results.setThreadId(threadId);
			results.setThreadTitle(root.getTitle());
			
			
			/*
			query = session.getNamedQuery("tagLite.getForThread").setString("rootId", root.getPostId());
			@SuppressWarnings("unchecked") List<TagLite> litetags = query.list();
			results.loadOtherTags(litetags);
			
			results.setTotalTags(results.getTags().size());
			*/
			
			if(dayTag != null)
				spiderTag.remove(dayTag.getTagId());
			
			query = session.getNamedQuery("tagLite.TagMassForSpiderOthersThread").setInteger("count",spiderTag.size()).setParameterList("tagIds", spiderTag).setString("threadId",threadId);
			@SuppressWarnings("unchecked") List<TagLite> allTags = query.list();
			TagLite.calculatePercentile(allTags);
			results.loadOtherTags(allTags);
			/*
			if(dayTag != null)
				spiderTag.add(dayTag.getTagId());
			 */
			
			//See if the filter is limited to a single month
			query = session.getNamedQuery("tag.getByTagIds");
			List<Tag> tags = query.setParameterList("tagIds", spiderTag).list();
			
			int m= -1;
			int y= -1;
			
//			for(Tag t : tags){
//				if(Tag.TYPE_DATE_YEAR.equals(t.getType()))
//					y = Integer.parseInt(t.getValue());
//				else if(Tag.TYPE_DATE_MONTH.equals(t.getType()))
//					m = TagLite.getMonths().indexOf(t.getValue())+1;
//			}
				
			if(y > -1 && m > -1){
				List<TagLite> dayTags = results.getSuggestionsDays();
				List<Integer> days = new ArrayList<Integer>();
				
				for(TagLite d : dayTags){
					days.add(new Integer(d.getValue()));
				}
				Month month;
				if(day != null)
					month = HTMLCalendar.buildMonthObject(m, y, day, days, true);
				else
					month = HTMLCalendar.buildMonthObject(m, y, days, true);
				
				results.setMonth(month);
			}
			///
			
			
			Persistence.commit();
			
			results.setPosts(flat);
			
			return results;
			
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	

}
