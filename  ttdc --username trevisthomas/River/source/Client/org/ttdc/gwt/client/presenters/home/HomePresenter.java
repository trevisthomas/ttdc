package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class HomePresenter extends BasePagePresenter<HomePresenter.View>{
	
	public TabType[] tabs = new TabType[2];
	public interface View extends BasePageView{
		HasWidgets nestedPanel();
		HasWidgets flatPanel();
		HasWidgets threadPanel();
		HasWidgets conversationPanel();
		HasWidgets modulePanel();
		
		void displayTabs(TabType[] tabs);
		
		void displayNestedTab();
		void displayFlatTab();
		void displayThreadTab();
		void displayConversationTab();
	} 
	
	@Inject
	public HomePresenter(Injector injector) {
		super(injector, injector.getHomeView());
	}

	@Override
	public void show(HistoryToken token) {
		view.show();
		initializeTabs(token);
	}

	private void initializeTabs(HistoryToken token) {
		String tab = token.getParameter(HistoryConstants.TAB_KEY);
		String tab2 = token.getParameter(HistoryConstants.TAB2_KEY);
				
		if(HistoryConstants.HOME_FLAT_TAB.equals(tab)){
			tabs[0] = TabType.FLAT; 
			buildFlatTab();
		}
		else{ // if(HistoryConstants.HOME_NESTED_TAB.equals(tab)){
			tabs[0] = TabType.NESTED;
			buildNestedTab();
		}
		
		
		if(HistoryConstants.HOME_THREAD_TAB.equals(tab2)){
			tabs[1] = TabType.THREAD;
			buildThreadTab();
		}
		else{ // if(HistoryConstants.HOME_NESTED_TAB.equals(tab)){
			tabs[1] = TabType.CONVERSATION;
			buildConversationTab();
		}
		
		view.displayTabs(tabs);

	}
	
	
//	private void initializeTabs(HistoryToken token) {
//		String tab = token.getParameter(HistoryConstants.TAB_KEY);
//				
//		if(HistoryConstants.HOME_FLAT_TAB.equals(tab)){
//			tabs[0] = TabType.FLAT; 
//			buildFlatTab();
//		}
//		else if(HistoryConstants.HOME_NESTED_TAB.equals(tab)){
//			tabs[0] = TabType.CONVERSATION;
//			buildConversationTab();
//		}
//		else{ // if(HistoryConstants.HOME_NESTED_TAB.equals(tab)){
//			tabs[0] = TabType.NESTED;
//			buildNestedTab();
//		}
//		
//		tabs[1] = TabType.THREAD;
//		buildThreadTab();
//		
//		view.displayTabs(tabs);
//
//	}

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
