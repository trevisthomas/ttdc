package org.ttdc.gwt.client.presenters.users;

import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.util.HtmlLabel;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PublicUserProfileView implements PublicUserProfilePresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel profilePanel = new SimplePanel();
	private final SimplePanel messagePanel = new SimplePanel();
	private final DecoratedTabPanel tabPanel = new DecoratedTabPanel();
	
	private final HtmlLabel bioHtml = new HtmlLabel();
	private final SimplePanel bestMoviesPanel = new SimplePanel();
	private final SimplePanel worstMoviesPanel = new SimplePanel();
	private final SimplePanel latestCoversationsPanel = new SimplePanel();
	private final SimplePanel latestPostsPanel = new SimplePanel();
	private final SimplePanel latestReviewsPanel = new SimplePanel();
	private final SimplePanel navigationPanel = new SimplePanel();
	
	private String personId;
	
	public PublicUserProfileView() {
		main.add(navigationPanel);
		main.add(messagePanel);
		main.add(profilePanel);
		main.add(tabPanel);
		
		tabPanel.add(bioHtml, "Bio");
		tabPanel.add(bestMoviesPanel, "Best Movies");
		tabPanel.add(worstMoviesPanel, "Worst Movies");
		tabPanel.add(latestCoversationsPanel, "Conversations");
		tabPanel.add(latestReviewsPanel, "Reviews");
		tabPanel.add(latestPostsPanel, "Comments");
		
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!tabPanel.isAttached()) 
					return;
				int index = event.getSelectedItem();
				
				updateHistoryToReflectTabSelection(index);
			}
		});
	}
	
	@Override
	public HasWidgets navigationPanel() {
		return navigationPanel;
	}	
	
	/*
	 * Trevis, be aware that calling History.newItem actually caues a history event to be
	 * fired.  Think about what that means.  You may want to do other things to make better use 
	 * history and ajax
	 * 
	 * (This functionality is now in more than one place, see also AdminToolsView)
	 * 
	 */
	private void updateHistoryToReflectTabSelection(int index) {
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_USER_PROFILE);
		token.addParameter(HistoryConstants.PERSON_ID, personId);
		switch (index){
			case 0:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_BIO_TAB);
				break;
			case 1:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_BEST_MOVIES_TAB);
				break; 
			case 2:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_WORST_MOVIES_TAB);
				break;
			case 3:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_CONVERSATIONS_TAB);
				break;	
			case 4:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_REVIEWS_TAB);
				break;	
			case 5:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.PROFILE_POSTS_TAB);	
				break;	
		}
		History.newItem(token.toString());
	}
	
	@Override
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	
	@Override
	public HasWidgets profile() {
		return profilePanel;
	}

	@Override
	public HasWidgets messagePanel() {
		return messagePanel;
	}

	@Override
	public void show() {
		RootPanel.get("content").clear();
		RootPanel.get("content").add(getWidget());
	}

	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets bestMoviesPanel() {
		return bestMoviesPanel;
	}

	@Override
	public HasWidgets worstMoviesPanel() {
		return worstMoviesPanel;
	}
	
	@Override
	public HasWidgets latestConversationsPanel() {
		return latestCoversationsPanel;
	}

	@Override
	public HasWidgets latestPostsPanel() {
		return latestPostsPanel;
	}

	@Override
	public HasWidgets latestReviewsPanel() {
		return latestReviewsPanel;
	}

	@Override
	public void displayBestMoviesTab() {
		tabPanel.selectTab(1);
	}

	@Override
	public void displayBioTab() {
		tabPanel.selectTab(0);
	}

	@Override
	public void displayLatestConversationsTab() {
		tabPanel.selectTab(3);
		
	}

	@Override
	public void displayLatestPostsTab() {
		tabPanel.selectTab(5);
	}

	@Override
	public void displayLatestReviewsTab() {
		tabPanel.selectTab(4);
	}

	@Override
	public void displayWorstMoviesTab() {
		tabPanel.selectTab(2);
	}

	@Override
	public HasText bioText() {
		return bioHtml;
	}

	@Override
	public void clear() {
		profilePanel.clear();
		messagePanel.clear();
		bioHtml.setText("");
		bestMoviesPanel.clear();
		worstMoviesPanel.clear();
		latestCoversationsPanel.clear();
		latestPostsPanel.clear();
		latestReviewsPanel.clear();
	}
}
