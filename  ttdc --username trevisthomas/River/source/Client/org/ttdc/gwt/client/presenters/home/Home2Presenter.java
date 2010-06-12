package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.comments.NewCommentPresenter;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.SiteUpdatePanel;
import org.ttdc.gwt.client.uibinder.post.NewMoviePanel;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class Home2Presenter extends BasePagePresenter<Home2Presenter.View> implements PostEventListener{
	public interface View extends BasePageView{
		HasWidgets nestedPanel();
		HasWidgets flatPanel();
		HasWidgets threadPanel();
		HasWidgets conversationPanel();
		HasWidgets modulePanel();
		HasWidgets searhcPanel();
		HasWidgets loginPanel();
		HasWidgets commentPanel();
		HasClickHandlers commentButton();
		HasClickHandlers markReadButton();
		HasWidgets siteUpdatePanel();
		
		void displayTab(TabType tabs);
		HasClickHandlers movieButton();
	} 
	
	private FlatPresenter flatPresenter = null;
	private NestedPresenter nestedPresenter = null;
	private ThreadPresenter threadPresenter = null;
	private ConversationPresenter conversationPresenter = null;
	
	
	@Inject
	public Home2Presenter(Injector injector) {
		super(injector, injector.getHome2View());
		
		InteractiveCalendarPresenter calendarPresenter = injector.getInteractiveCalendarPresenter();
		calendarPresenter.init(InteractiveCalendarPresenter.Mode.CALENDER_INTERFACE_MODE);
		
		view.modulePanel().add(calendarPresenter.getWidget());
		view.modulePanel().add(injector.getTrafficPresenter().getWidget());
		view.loginPanel().add(injector.getUserIdentityPresenter().getWidget());
		
		view.commentButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showCommentEditor();
			}
		});
		
		view.movieButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showMovieEditor();
			}
		});
		
		view.markReadButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markSiteRead();
			}
		});
		
		
		SiteUpdatePanel siteUpdatePanel = injector.createSiteUpdatePanel();
		view.siteUpdatePanel().add(siteUpdatePanel);
		
//		view.navigationPanel().add(injector.createNavigation());
		
		EventBus.getInstance().addListener(this);
	}

	private void markSiteRead() {
		PersonCommand cmd = new PersonCommand(ConnectionId.getInstance().getCurrentUser().getPersonId(),
				PersonStatusType.MARK_SITE_READ);
		RpcServiceAsync service = injector.getService();
		service.execute(cmd, createStatusUpdateCallback());
	}

	private CommandResultCallback<GenericCommandResult<GPerson>> createStatusUpdateCallback() {
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				EventBus.reload();//TODO: probably should come up with a way to just refresh the parts i care about!
			}
		};
	}

	
	private void showMovieEditor() {
		view.commentPanel().clear();
		NewMoviePanel newMoviePanel = injector.createNewMoviePanel();
		newMoviePanel.init();
		view.commentPanel().add(newMoviePanel);
	}
	
	private void showCommentEditor() {
		view.commentPanel().clear();
		NewCommentPresenter commentPresneter = injector.getNewCommentPresenter();
		commentPresneter.init();
		view.commentPanel().add(commentPresneter.getWidget());
		
	}
	@Override
	public void show(HistoryToken token) {
		SearchBoxPresenter searchBoxPresenter = injector.getSearchBoxPresenter();
		searchBoxPresenter.init();
		view.searhcPanel().clear();
		view.searhcPanel().add(searchBoxPresenter.getWidget());
		view.commentPanel().clear();
		view.show();
		
		initializeTabs(token);
	}
	

	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.NEW_FORCE_REFRESH)){
			if(threadPresenter != null)
				threadPresenter.refresh();	
			if(nestedPresenter != null)
				nestedPresenter.refresh();
			if(conversationPresenter != null)
				conversationPresenter.refresh();
			if(flatPresenter != null)
				flatPresenter.refresh();
		}
	}

	private void initializeTabs(HistoryToken token) {
		String tab = token.getParameter(HistoryConstants.TAB_KEY);
		TabType selected;
		
		if(HistoryConstants.HOME_FLAT_TAB.equals(tab)){
			selected = TabType.FLAT; 
			buildFlatTab();
		}
		else if(HistoryConstants.HOME_NESTED_TAB.equals(tab)){
			selected = TabType.NESTED;
			buildNestedTab();
		}
		else{ // if(HistoryConstants.HOME_NESTED_TAB.equals(tab)){
			selected = TabType.CONVERSATION;
			buildConversationTab();
		}
		
		buildThreadTab();
		
		view.displayTab(selected);

	}

	private void buildNestedTab() {
		if(PresenterHelpers.isWidgetEmpty(view.nestedPanel())){
			nestedPresenter = injector.getNestedPresenter();
			nestedPresenter.init();
			view.nestedPanel().add(nestedPresenter.getWidget());
		}
	}

	private void buildThreadTab() {
		if(PresenterHelpers.isWidgetEmpty(view.threadPanel())){
			threadPresenter = injector.getThreadPresenter();
			threadPresenter.init();
			view.threadPanel().add(threadPresenter.getWidget());
		}
	}

	private void buildConversationTab() {
		if(PresenterHelpers.isWidgetEmpty(view.conversationPanel())){
			conversationPresenter = injector.getConversationPresenter();
			conversationPresenter.init();
			view.conversationPanel().add(conversationPresenter.getWidget());
		}
	}
	
	private void buildFlatTab() {
		if(PresenterHelpers.isWidgetEmpty(view.flatPanel())){
			flatPresenter = injector.getFlatPresenter();
			flatPresenter.init();
			view.flatPanel().add(flatPresenter.getWidget());
		}
	}
}
