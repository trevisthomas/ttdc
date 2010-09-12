package org.ttdc.gwt.client.presenters;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.ServerEventMonitor;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryEvent;
import org.ttdc.gwt.client.messaging.history.HistoryEventListener;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.search.SearchBoxView;
import org.ttdc.gwt.client.presenters.topic.TopicPresenter;
import org.ttdc.gwt.client.presenters.users.UserToolsPresenter;
import org.ttdc.gwt.client.presenters.util.LoadCurrentUser;
import org.ttdc.gwt.client.uibinder.post.TopicPanel;


import com.google.inject.Inject;

/**
 * I listen to the event bus for history events and then use GIN dependency injection 
 * to create the needed presenter instance.
 * 
 * @author Trevis
 *
 */
public class HistoryEventPresenterManager implements HistoryEventListener{
	private Injector injector;
	@Inject
	public static void initInstance(Injector injector){
		new HistoryEventPresenterManager(injector);
	}
	
	private HistoryEventPresenterManager(Injector injector){
		this.injector = injector;
		EventBus.getInstance().addListener(this);
		new ServerEventMonitor();
	}
	
	
		
	public void onHistoryEvent(HistoryEvent event) {
		MessageEvent e = new MessageEvent(MessageEventType.VIEW_CHANGE, null);
		EventBus.fireEvent(e);
//		SearchBoxView.viewChangeNotification();//TODO: Use the event buss fool!
		LoadCurrentUser.load(new Worker(event));
	}
	
	public void processHistoryEvent(HistoryEvent event, GPerson person) {
		String view = event.getSource().getParameter(HistoryConstants.VIEW);
		
		if(HistoryConstants.VIEW_SEARCH.equals(view)){
			injector.createSearchPanel().show(event.getSource());
			//injector.getSearchPresenter().show(event.getSource());
		}
		else if(HistoryConstants.VIEW_SEARCH_RESULTS.equals(view)){
			//injector.getSearchResultsPresenter().show(event.getSource());
			injector.createSearchResultsPanel().show(event.getSource());
		}
		else if(HistoryConstants.VIEW_SEARCH_TAG_RESULTS.equals(view)){
			injector.getSearchTagResultsPresenter().show(event.getSource());
		}
		else if(HistoryConstants.VIEW_CALENDAR.equals(view)){
			//injector.getCalendarPresenter().show(event.getSource());
			injector.createCalendarPanel().show(event.getSource()); //Note: This is the first time use of a uibinder component as a top level view
		}
		else if(HistoryConstants.VIEW_TOPIC.equals(view) || 
				HistoryConstants.VIEW_TOPIC_FLAT.equals(view) ||
				HistoryConstants.VIEW_TOPIC_HIERARCHY.equals(view) || 
				HistoryConstants.VIEW_TOPIC_CONVERSATION.equals(view) ||
				HistoryConstants.VIEW_TOPIC_SUMMARY.equals(view) ||
				HistoryConstants.VIEW_TOPIC_NESTED.equals(view)){
//			TopicPresenter presenter = injector.getTopicPresenter();
//			presenter.show(event.getSource());
			TopicPanel topicPanel = injector.createTopicPanel();
			topicPanel.show(event.getSource());
		}
		else if(HistoryConstants.VIEW_MOVIE_LIST.equals(view)){
			//injector.getMovieListPresenter().show(event.getSource());
			injector.createMovieListPanel().show(event.getSource());
		}
		else if(HistoryConstants.VIEW_ADMIN_TOOLS.equals(view)){
			if(person.isAdministrator()){
				injector.getAdminToolsPresenter().show(event.getSource());
			}
			else{
				EventBus.fireReturnHomeEvent();
			}
		}
		else if(HistoryConstants.VIEW_USER_TOOLS.equals(view)){
			injector.createUserToolsPanel().show(event.getSource());
		}
		else if(HistoryConstants.VIEW_DEMO.equals(view)){
			injector.getDemoPresenter().show(event.getSource());
		}
		else if(HistoryConstants.VIEW_USER_LIST.equals(view)){
			//injector.getUserListPresenter().show(event.getSource());
			injector.createUserListPanel().show(event.getSource());
		}
		else if(HistoryConstants.VIEW_USER_PROFILE.equals(view)){
			injector.createPublicUserProfilePanel().show(event.getSource());
		}
		else if(HistoryConstants.VIEW_DASHBOARD.equals(view)){
			if(person.isAnonymous()){
				injector.createUserToolsPanel().show(new HistoryToken());
				EventBus.fireErrorMessage("Dashboard is for authenticated users.");
			}
			else{
				HistoryToken token = event.getSource();
				token.setParameter(HistoryConstants.PERSON_ID, person.getPersonId());
				injector.createUserDashboardPanel().show(token);
			}
		}
		else if(HistoryConstants.VIEW_HOME.equals(view)){
			injector.createHomePanel().show(event.getSource());
			//injector.getHome2Presenter().show(event.getSource());
			//injector.getHomePresenter().show(event.getSource());
		}
		else{
			/*Just let it go neo*/
			//throw new RuntimeException("No clue what you're trying to do.");
		}
	}

	/**
	 * 
	 * This class takes a history event and makes an asynchronous call to get the user
	 * then it passes that info the the real processHistoryEvent.  
	 *
	 */
	private class Worker implements LoadCurrentUser.CurrentUserResponse{
		private final HistoryEvent event;
		Worker(HistoryEvent event){
			this.event = event;
		}
		
		@Override
		public void setCurrentUser(GPerson person) {
			processHistoryEvent(event,person);
		}
	}
}
