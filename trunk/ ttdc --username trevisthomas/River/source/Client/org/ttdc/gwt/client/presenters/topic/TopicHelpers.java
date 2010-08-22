package org.ttdc.gwt.client.presenters.topic;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.post.SearchWithinSubsetPresenter;

public class TopicHelpers {
	public static HistoryToken buildFlatPageToken(String postId){
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC);
		token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.TOPIC_FLAT_TAB);
		token.setParameter(HistoryConstants.POST_ID_KEY, postId);
		return token;
	}
	
	public static HistoryToken buildHierarchyPageToken(String postId){
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC_HIERARCHY);
		token.setParameter(HistoryConstants.POST_ID_KEY, postId);
		return token;
	}
	
	public static HistoryToken buildConversationPageToken(String postId) {
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC_CONVERSATION);
		token.setParameter(HistoryConstants.POST_ID_KEY, postId);
		return token;
	}
	
	public static void configureSearchWithinSubsetPresenter(GPost post, SearchWithinSubsetPresenter searchResultsPresenter) {
		if(post.isRootPost())
			searchResultsPresenter.setRootId(post.getPostId());
		else if(post.isThreadPost())
			searchResultsPresenter.setThreadId(post.getPostId());
	}
	
	public static boolean compareHistoryKeyValues(String key, HistoryToken then, HistoryToken now) {
		if(then == null || now == null) return false;
		if(then.getParameter(key) == now.getParameter(key))
			return true;
		else{
			if(then.getParameter(key) != null)
				return then.getParameter(key).equals(now.getParameter(key));
			else
				return false;
			
		}
	}

	public static HistoryToken buildNestedPageToken(String postId) {
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC);
		token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.TOPIC_NESTED_TAB);
		token.setParameter(HistoryConstants.POST_ID_KEY, postId);
		return token;
	}
	
	
	/* The bit of funkyness below is so that PostPresenterCommon imlementers (PostPanel, PostSummeryPanel) 
	 * can have a place to connect with Topic*Presenter's so that the presenters can scroll
	 * the proper post into view.   
	 */
	private static PostPresenterCommon postComponent = null;
	private static String sourcePostId;

	public static PostPresenterCommon getPostComponent() {
		return postComponent;
	}

	public static void testPost(PostPresenterCommon postComponent){
		if(sourcePostId != null && sourcePostId.equals(postComponent.getPostId())){
			TopicHelpers.postComponent = postComponent;
		}
	}
	
	public static void setSourcePostId(String sourcePostId){
		TopicHelpers.sourcePostId = sourcePostId;
		TopicHelpers.postComponent = null;
	}
}
