package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class Home2Presenter extends BasePagePresenter<Home2Presenter.View>{
	public interface View extends BasePageView{
		HasWidgets nestedPanel();
		HasWidgets flatPanel();
		HasWidgets threadPanel();
		HasWidgets conversationPanel();
		HasWidgets modulePanel();
		HasWidgets searhcPanel();
		HasWidgets loginPanel();
		HasWidgets commentPanel();
		
		void displayTab(TabType tabs);
	} 
	
	
	
	@Inject
	public Home2Presenter(Injector injector) {
		super(injector, injector.getHome2View());
		
		InteractiveCalendarPresenter calendarPresenter = injector.getInteractiveCalendarPresenter();
		calendarPresenter.init();
		
		view.modulePanel().add(calendarPresenter.getWidget());
		view.modulePanel().add(injector.getTrafficPresenter().getWidget());
		view.loginPanel().add(injector.getUserIdentityPresenter().getWidget());
		
	}

	@Override
	public void show(HistoryToken token) {
		SearchBoxPresenter searchBoxPresenter = injector.getSearchBoxPresenter();
		searchBoxPresenter.init();
		view.searhcPanel().clear();
		view.searhcPanel().add(searchBoxPresenter.getWidget());
		view.commentPanel().clear();
		view.commentPanel().add(injector.getNewCommentPresenter().getWidget());
		view.show();
		
		initializeTabs(token);
		
		
	}

//	private void initializeTabs(HistoryToken token) {
//		String tab = token.getParameter(HistoryConstants.TAB_KEY);
//		String tab2 = token.getParameter(HistoryConstants.TAB2_KEY);
//				
//		if(HistoryConstants.HOME_FLAT_TAB.equals(tab)){
//			tabs[0] = TabType.FLAT; 
//			buildFlatTab();
//		}
//		else{ // if(HistoryConstants.HOME_NESTED_TAB.equals(tab)){
//			tabs[0] = TabType.NESTED;
//			buildNestedTab();
//		}
//		
//		
//		if(HistoryConstants.HOME_THREAD_TAB.equals(tab2)){
//			tabs[1] = TabType.THREAD;
//			buildThreadTab();
//		}
//		else{ // if(HistoryConstants.HOME_NESTED_TAB.equals(tab)){
//			tabs[1] = TabType.CONVERSATION;
//			buildConversationTab();
//		}
//		
//		view.displayTabs(tabs);
//
//	}
	
	
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
			NestedPresenter presenter = injector.getNestedPresenter();
			presenter.init();
			view.nestedPanel().add(presenter.getWidget());
		}
	}

	private void buildThreadTab() {
		if(PresenterHelpers.isWidgetEmpty(view.threadPanel())){
			ThreadPresenter presenter = injector.getThreadPresenter();
			presenter.init();
			view.threadPanel().add(presenter.getWidget());
		}
	}

	private void buildConversationTab() {
		if(PresenterHelpers.isWidgetEmpty(view.conversationPanel())){
			ConversationPresenter presenter = injector.getConversationPresenter();
			presenter.init();
			view.conversationPanel().add(presenter.getWidget());
		}
	}

	private void buildFlatTab() {
		if(PresenterHelpers.isWidgetEmpty(view.flatPanel())){
			FlatPresenter presenter = injector.getFlatPresenter();
			presenter.init();
			view.flatPanel().add(presenter.getWidget());
		}
	}
}
