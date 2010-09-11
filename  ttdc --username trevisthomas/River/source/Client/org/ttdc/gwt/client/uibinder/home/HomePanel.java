package org.ttdc.gwt.client.uibinder.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.home.ConversationPresenter;
import org.ttdc.gwt.client.presenters.home.EarmarkedPresenter;
import org.ttdc.gwt.client.presenters.home.FlatPresenter;
import org.ttdc.gwt.client.presenters.home.NestedPresenter;
import org.ttdc.gwt.client.presenters.home.TabType;
import org.ttdc.gwt.client.presenters.home.ThreadPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.SiteUpdatePanel;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class HomePanel extends BasePageComposite implements PersonEventListener, PostEventListener {
	interface MyUiBinder extends UiBinder<Widget, HomePanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField (provided = true) Widget pageHeaderElement;
	@UiField TabPanel rightTabPanelElement;
	@UiField TabPanel centerTabPanelElement;
	@UiField SimplePanel trafficElement;
	@UiField SimplePanel siteUpdateElement;
	@UiField (provided = true) Widget pageFooterElement;
	
	private final StandardPageHeaderPanel pageHeaderPanel;
	
	private final SimplePanel conversationPanel = new SimplePanel();
	private final SimplePanel nestedPanel = new SimplePanel();
	private final SimplePanel flatPanel = new SimplePanel();
	private final SimplePanel threadPanel = new SimplePanel();
	private final SimplePanel earmarksPanel = new SimplePanel();
	
//	private final Button markReadButton = new Button("Read");
	
	private HistoryToken token = new HistoryToken();
		
	private boolean fireHistoryEvent = true;
	
	private FlatPresenter flatPresenter = null;
	private EarmarkedPresenter earmarksPresenter = null;
	private NestedPresenter nestedPresenter = null;
	private ThreadPresenter threadPresenter = null;
	private ConversationPresenter conversationPresenter = null;
	
	@Inject
	public HomePanel(Injector injector){
		this.injector = injector;
		
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	
    	pageFooterElement = injector.createStandardFooter().getWidget();
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	centerTabPanelElement.add(nestedPanel, "Nested");
    	centerTabPanelElement.add(flatPanel, "Flat");
    	//centerTabPanelElement.add(conversationPanel,"Conversations");
    	rightTabPanelElement.add(threadPanel,"Threads");
		
    	centerTabPanelElement.setStyleName("tt-fill");
    	rightTabPanelElement.setStyleName("tt-fill");
    	
    	token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_HOME);
		
    	centerTabPanelElement.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!centerTabPanelElement.isAttached()) 
					return;
				int index = event.getSelectedItem();
				
				updateHistoryToReflectCenterTabSelection(index);
			}
		});
		
//		view.markReadButton().addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				markSiteRead();
//			}
//		});
		
		
		SiteUpdatePanel siteUpdatePanel = injector.createSiteUpdatePanel();
		siteUpdateElement.clear();
		siteUpdateElement.add(siteUpdatePanel);
		
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		if(!user.isAnonymous())
			enableEarmarkTab();
		
		
		if(!ConnectionId.isAnonymous()){
			
		}
		trafficElement.clear();
		trafficElement.add(injector.getTrafficPresenter().getWidget());
		
		EventBus.getInstance().addListener((PostEventListener)this);
		EventBus.getInstance().addListener((PersonEventListener)this);
		
		
	}
	
	@Override
	protected void onShow(HistoryToken token) {
		pageHeaderPanel.init("TTDC", "feeling the love since 1999");
		pageHeaderPanel.getSearchBoxPresenter().init(token);
		
		initializeTabs(token);
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public void enableEarmarkTab(){
		centerTabPanelElement.add(earmarksPanel,"Earmarked");
	}
	
	private void updateHistoryToReflectCenterTabSelection(int index) {
		switch (index){
			case INDEX_NESTED:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_NESTED_TAB);
				break;
			case INDEX_FLAT:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_FLAT_TAB);
				break;
			case INDEX_CONVERSATION:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_CONVERSATION_TAB);
				break;
			case INDEX_EARMARKS:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_EARMARKS_TAB);	
				break;				
		}
		History.newItem(token.toString(),fireHistoryEvent);
	}
	
	final static int INDEX_FLAT = 1;
	final static int INDEX_THREAD = 0;
	final static int INDEX_NESTED = 0;
	final static int INDEX_CONVERSATION = 3;
	final static int INDEX_EARMARKS = 2;

	private void displayTab(TabType selected) {
		fireHistoryEvent = false;
		if(selected.equals(TabType.FLAT)){
			centerTabPanelElement.selectTab(INDEX_FLAT);
		}else if(selected.equals(TabType.NESTED)){
			centerTabPanelElement.selectTab(INDEX_NESTED);
		}
		else if(selected.equals(TabType.EARMARKS)){
			centerTabPanelElement.selectTab(INDEX_EARMARKS);
		}
		else if(selected.equals(TabType.CONVERSATION)){
			centerTabPanelElement.selectTab(INDEX_CONVERSATION);
		}
		else{
			centerTabPanelElement.selectTab(INDEX_NESTED);
		}
		
		rightTabPanelElement.selectTab(INDEX_THREAD);
		fireHistoryEvent = true;
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
			if(earmarksPresenter != null)
				earmarksPresenter.refresh();
		}
	}

	private void initializeTabs(HistoryToken token) {
		GPerson user = ConnectionId.getInstance().getCurrentUser();
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
		else if(HistoryConstants.HOME_CONVERSATION_TAB.equals(tab)){
			selected = TabType.CONVERSATION;
			buildNestedTab();
		}
		else if(HistoryConstants.HOME_EARMARKS_TAB.equals(tab) && !user.isAnonymous()){
			selected = TabType.EARMARKS;
			buildEarmarksTab();
		}
		else{ // if(HistoryConstants.HOME_NESTED_TAB.equals(tab)){
			selected = TabType.NESTED;
			buildNestedTab();
		}
		
		buildThreadTab();
		
		displayTab(selected);
	}

	private void buildNestedTab() {
		if(PresenterHelpers.isWidgetEmpty(nestedPanel)){
			nestedPresenter = injector.getNestedPresenter();
			nestedPresenter.init();
			nestedPanel.add(nestedPresenter.getWidget());
		}
	}

	private void buildThreadTab() {
		if(PresenterHelpers.isWidgetEmpty(threadPanel)){
			threadPresenter = injector.getThreadPresenter();
			threadPresenter.init();
			threadPanel.add(threadPresenter.getWidget());
		}
	}

	private void buildConversationTab() {
		if(PresenterHelpers.isWidgetEmpty(conversationPanel)){
			conversationPresenter = injector.getConversationPresenter();
			conversationPresenter.init();
			conversationPanel.add(conversationPresenter.getWidget());
		}
	}
	
	private void buildFlatTab() {
		if(PresenterHelpers.isWidgetEmpty(flatPanel)){
			flatPresenter = injector.getFlatPresenter();
			flatPresenter.init();
			flatPanel.add(flatPresenter.getWidget());
		}
	}
	
	private void buildEarmarksTab() {
		if(PresenterHelpers.isWidgetEmpty(earmarksPanel)){
			earmarksPresenter = injector.getEarmarkedPresenter();
			earmarksPresenter.init();
			earmarksPanel.add(earmarksPresenter.getWidget());
		}
	}
}
