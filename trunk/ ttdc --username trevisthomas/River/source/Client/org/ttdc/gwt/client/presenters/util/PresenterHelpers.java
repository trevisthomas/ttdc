package org.ttdc.gwt.client.presenters.util;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.shared.GenericTabularFlowPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.user.client.ui.HasWidgets;

public class PresenterHelpers {
	/*
	 *  Sharing this bit of logic
	 *  
	 *  Initial users are UserListPresenter and MovieListPresenter
	 * 
	 */
	public static HistoryToken cloneTokenForSort(String sortBy, HistoryToken token) {
		HistoryToken newToken = new HistoryToken();
		
		newToken.load(token);
		newToken.setParameter(HistoryConstants.PAGE_NUMBER_KEY, 1);
		newToken.setParameter(HistoryConstants.SORT_KEY, sortBy);
		
		if(sortBy.equals(newToken.getParameter(HistoryConstants.SORT_KEY))){
			if(!newToken.hasParameter(HistoryConstants.SORT_DIRECTION_KEY)){
				newToken.setParameter(HistoryConstants.SORT_DIRECTION_KEY, HistoryConstants.SORT_DESC);
			}
			else if(HistoryConstants.SORT_ASC.equals(newToken.getParameter(HistoryConstants.SORT_DIRECTION_KEY))){
				newToken.setParameter(HistoryConstants.SORT_DIRECTION_KEY, HistoryConstants.SORT_DESC);
			}
			else{
				newToken.setParameter(HistoryConstants.SORT_DIRECTION_KEY, HistoryConstants.SORT_ASC);
			}
		}
		
		if(!token.getParameter(HistoryConstants.SORT_KEY,HistoryConstants.USERS_SORT_BY_LOGIN).equals(sortBy)){
			newToken.removeParameter(HistoryConstants.SORT_DIRECTION_KEY);
		}
		return newToken;
	}
	
	public static boolean isWidgetEmpty(HasWidgets w) {
		return !w.iterator().hasNext();
	}
	
	/**
	 * Grabs either the rating or the average rating depending on if the personId arg is populated
	 *  
	 * @param ratingPresenter
	 * @param post
	 * @param personId
	 */
	public static void initializeMovieRatingPresenter(MovieRatingPresenter ratingPresenter, GPost post, String personId) {
		GAssociationPostTag ass;
		if(StringUtil.notEmpty(personId)){
			ass = post.loadTagAssociationByPerson(TagConstants.TYPE_RATING, personId);
			ratingPresenter.setRating(ass.getTag().getValue());
		}
		else{
			ass = post.loadTagAssociation(TagConstants.TYPE_AVERAGE_RATING);
			ratingPresenter.setRating(ass.getTag().getValue());
		}
	}
	
	public static GenericTabularFlowPresenter buildWebLinksPresenter(Injector injector, GPerson person) {
		GenericTabularFlowPresenter tabularFlowPresenter = injector.getGenericTabularFlowPresenter();
		tabularFlowPresenter.setMaxColumns(6);
		for(GUserObject uo : person.getWebPageUserObjects()){
			ImagePresenter ip = injector.getImagePresenter();
			ip.setImage(uo.getTemplate().getImage(), 16, 16);
			ip.setLinkUrl(uo.getUrl());
			tabularFlowPresenter.stackWidget(ip.getWidget());
		}
		return tabularFlowPresenter;
	}
	
	/*
	 * It'd be nice to have a way to have just one impl of this but the solution seems like
	 * more bother than it's worth?
	 * 
	 * Leaving them depricated for now just so that when i see them i remember to reconsider.
	 */
	
//	@Deprecated
//	public static void readDateRangeFromToken(HistoryToken token, SearchPostsCommand command) {
//		long start = token.getParameterAsLong(HistoryConstants.SEARCH_START_DATE, 0);
//		long end = token.getParameterAsLong(HistoryConstants.SEARCH_END_DATE, 0);
//		
//		if(start != 0){
//			command.setStartDate(new Date(start));
//		}
//		if(end != 0){
//			command.setEndDate(new Date(end));
//		}
//	}
//	
//	@Deprecated
//	public static void readDateRangeFromToken(HistoryToken token, SearchTagsCommand command) {
//		long start = token.getParameterAsLong(HistoryConstants.SEARCH_START_DATE, 0);
//		long end = token.getParameterAsLong(HistoryConstants.SEARCH_END_DATE, 0);
//		
//		if(start != 0){
//			command.setStartDate(new Date(start));
//		}
//		if(end != 0){
//			command.setEndDate(new Date(end));
//		}
//	}
	
	
}
