package org.ttdc.servlets;

import java.io.OutputStream;
import java.util.Date;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.server.dao.FastLatestPostsDao;
import org.ttdc.gwt.server.dao.FastRssDao;
import org.ttdc.gwt.server.dao.PostDao;
import org.ttdc.gwt.server.util.PostFormatter;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Post;
import org.ttdc.util.rss.Feed;
import org.ttdc.util.rss.FeedMessage;
import org.ttdc.util.rss.RSSFeedWriter;

public class RssFeedGenerator {
	private final static String LATEST = "latest";
	private final static String TOPIC = "rss=topic&postId=";
	private final static String DEFAULT_FEED = LATEST;
	
	public static void buildFeed(OutputStream outputStream, String feedIdentifier) {
		String query = feedIdentifier;
		Feed rssfeed = null;
		if(StringUtil.empty(query) || query.equalsIgnoreCase(LATEST) || query.equalsIgnoreCase("rss")){
			rssfeed = buildLatest(outputStream);
		}
		else if(query.startsWith(TOPIC)){
			String guid = query.substring(TOPIC.length());
			rssfeed = buildTopic(outputStream, guid);
			//rss=topic&postId=F253D8D1-81F4-42E7-BFB1-808469A12048
			
		}
		
		if(rssfeed == null){
			throw new RuntimeException("Feed was built null... weird.");
		}
		RSSFeedWriter writer = new RSSFeedWriter(rssfeed, outputStream);
		try {
			writer.write();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Feed buildTopic(OutputStream outputStream, String guid){
		Persistence.beginSession();
		
		FastRssDao dao = new FastRssDao();
		Post sourcePost = PostDao.loadPost(guid);
		dao.setSourcePost(sourcePost);
		PaginatedList<GPost> posts = dao.loadTopicFlat();
		
		//Trevis: See http://www.rssboard.org/rss-specification.  lastBuildDate makes more sense than pub date for ttdc.
		Date lastBuildDate = posts.getList().get(0).getDate();
		
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC); 
		token.setParameter(HistoryConstants.POST_ID_KEY, sourcePost.getPostId());
		
		Feed rssfeed = new Feed(PostFormatter.getInstance().format(sourcePost.getTitle()) + " - TTDC", "http://trevisthomas.com/#"+token.toString(), 
				PostFormatter.getInstance().format(sourcePost.getEntry().getBody())+" on TTDC", "en-US",
				"CC BY-NC", lastBuildDate);
		
		for(GPost post : posts.getList()){
			FeedMessage message = translatePostToFeedMessage(post);
			rssfeed.addFeedMessage(message);
		}
		
		Persistence.commit();
		
		return rssfeed;
	}

	private static Feed buildLatest(OutputStream outputStream){
		Persistence.beginSession();
		
		FastLatestPostsDao dao = new FastLatestPostsDao();
		PaginatedList<GPost> posts = dao.loadFlat();
		
		//Trevis: See http://www.rssboard.org/rss-specification.  lastBuildDate makes more sense than pub date for ttdc.
		Date lastBuildDate = posts.getList().get(0).getDate();
		
		Feed rssfeed = new Feed("Latest Comments on TTDC", "http://trevisthomas.com", 
				"RSS feed which continuously publishes the latest posts added to Trevis Thomas Dot Com", "en-US",
				"CC BY-NC", lastBuildDate);
		
		for(GPost post : posts.getList()){
			FeedMessage message = translatePostToFeedMessage(post);
			rssfeed.addFeedMessage(message);
		}
		
		Persistence.commit();
		
		return rssfeed;
	}

	private static FeedMessage translatePostToFeedMessage(GPost post) {
		FeedMessage message = new FeedMessage();
		message.setAuthor("webmaster@trevisthomas.com" + " (" +post.getCreator().getLogin() + ")");
		message.setTitle(post.getTitle());
		message.setDescription(post.getEntry());
		
		
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC); 
		token.setParameter(HistoryConstants.POST_ID_KEY,post.getPostId());
		String link = "http://trevisthomas.com/#"+token.toString();
		
		message.setGuid(link);
		message.setLink(link);	
		message.setPubDate(post.getDate());
		return message;
	}

}
