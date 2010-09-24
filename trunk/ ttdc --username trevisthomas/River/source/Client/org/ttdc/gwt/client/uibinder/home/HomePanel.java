package org.ttdc.gwt.client.uibinder.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventListener;
import org.ttdc.gwt.client.presenters.home.EarmarkedPresenter;
import org.ttdc.gwt.client.presenters.home.FlatPresenter;
import org.ttdc.gwt.client.presenters.home.NestedPresenter;
import org.ttdc.gwt.client.presenters.home.TabType;
import org.ttdc.gwt.client.presenters.home.TrafficPresenter;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.uibinder.SiteUpdatePanel;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class HomePanel extends BasePageComposite implements PersonEventListener, PostEventListener, TagEventListener {
	interface MyUiBinder extends UiBinder<Widget, HomePanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	
	@UiField (provided = true) Widget pageHeaderElement;
	@UiField (provided = true) Widget pageFooterElement;
	@UiField TabPanel postTabPanelElement;
	@UiField SimplePanel trafficElement;
	@UiField SimplePanel siteUpdateElement;
	
	
	private final StandardPageHeaderPanel pageHeaderPanel;
	
	private final SimplePanel flatPanel = new SimplePanel();
	private final SimplePanel nestedPanel = new SimplePanel();
	private final SimplePanel earmarksPanel = new SimplePanel();
	
	private HistoryToken token = new HistoryToken();
		
	
	private EarmarkedPresenter earmarksPresenter = null;
	private NestedPresenter latestPresenter = null;
	private FlatPresenter flatPresenter = null;
	private static TrafficPresenter trafficPresenter = null;
	
	
	@Inject
	public HomePanel(Injector injector){
		this.injector = injector;
		
		pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	pageFooterElement = injector.createStandardFooter().getWidget();
    	
    	
    	if(trafficPresenter == null){
    		trafficPresenter = injector.getTrafficPresenter();
    	}
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	trafficElement.add(trafficPresenter.getWidget());
    	
    	postTabPanelElement.add(nestedPanel, "Grouped");
    	postTabPanelElement.add(flatPanel, "Flat");
		
    	postTabPanelElement.setStyleName("tt-fill");
    	
    	token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_HOME);
		
    	postTabPanelElement.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(!postTabPanelElement.isAttached()) 
					return;
				int index = event.getSelectedItem();
				
				updateHistoryToReflectCenterTabSelection(index);
			}
		});
		
		SiteUpdatePanel siteUpdatePanel = injector.createSiteUpdatePanel();
		siteUpdateElement.clear();
		siteUpdateElement.add(siteUpdatePanel);
		
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		if(!user.isAnonymous()){
			enableEarmarkTab(user);
		}
		
		EventBus.getInstance().addListener((PostEventListener)this);
		EventBus.getInstance().addListener((PersonEventListener)this);
		EventBus.getInstance().addListener((TagEventListener)this);
	}
	

	@Override
	protected void onShow(HistoryToken token) {
		pageHeaderPanel.init("TTDC", "feeling the love since 1999");
		pageHeaderPanel.getSearchBoxPresenter().init(token);
		
		initializeTabs(token);
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		if(event.is(PersonEventType.USER_EARMKARK_COUNT_CHANGED) 
				&& event.getSource().getPersonId().equals(user.getPersonId())){
			postTabPanelElement.getTabBar().setTabText(INDEX_EARMARKS, "Earmarked ("+user.getEarmarks()+")");
//			earmarksPresenter.clearCache();
		}
	}
	
	public void enableEarmarkTab(GPerson person){
		postTabPanelElement.add(earmarksPanel,"Earmarked ("+person.getEarmarks()+")");
	}
	
	private void updateHistoryToReflectCenterTabSelection(int index) {
		switch (index){
			case INDEX_NESTED:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_GROUPED_TAB);
				buildGroupedTab();
				break;
			case INDEX_EARMARKS:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_EARMARKS_TAB);
				buildEarmarksTab();
				break;		
			case INDEX_FLAT:
				token.setParameter(HistoryConstants.TAB_KEY, HistoryConstants.HOME_FLAT_TAB);	
				buildFlatTab();
				break;	
			default:
				throw new RuntimeException("Invalid tab index on HomePanel");
		}
		//History.newItem(token.toString(),fireHistoryEvent);
		History.newItem(token.toString(), false);
	}
	
	final static int INDEX_NESTED = 0;
	final static int INDEX_FLAT = 1;
	final static int INDEX_EARMARKS = 2;

	private void displayTab(TabType selected) {
		if(selected.equals(TabType.EARMARKS)){
			postTabPanelElement.selectTab(INDEX_EARMARKS);
		}
		else if(selected.equals(TabType.FLAT)){
			postTabPanelElement.selectTab(INDEX_FLAT);
		}
		else{
			postTabPanelElement.selectTab(INDEX_NESTED);
		}
	}
	
	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.NEW_FORCE_REFRESH)){
			if(latestPresenter != null)
				latestPresenter.refresh();
			if(earmarksPresenter != null)
				earmarksPresenter.refresh();
			if(flatPresenter != null)
				flatPresenter.refresh();
		}
	}

	private void initializeTabs(HistoryToken token) {
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		String tab = token.getParameter(HistoryConstants.TAB_KEY);
		TabType selected;
		
		if(HistoryConstants.HOME_EARMARKS_TAB.equals(tab) && !user.isAnonymous()){
			selected = TabType.EARMARKS;
			buildEarmarksTab();
		}
		else if(HistoryConstants.HOME_FLAT_TAB.equals(tab)){
			selected = TabType.FLAT;
			buildFlatTab();
		}
		else{ 
			selected = TabType.NESTED;
			buildGroupedTab();
		}
		
		displayTab(selected);
	}

	private void buildFlatTab(){
		if(PresenterHelpers.isWidgetEmpty(flatPanel)){
			flatPresenter = injector.getFlatPresenter();
			flatPresenter.init();
			flatPanel.add(flatPresenter.getWidget());
		}
	}
	
	private void buildGroupedTab() {
		if(PresenterHelpers.isWidgetEmpty(nestedPanel)){
			latestPresenter = injector.getNestedPresenter();
			latestPresenter.init();
			nestedPanel.add(latestPresenter.getWidget());
		}
	}

	private void buildEarmarksTab() {
		if(PresenterHelpers.isWidgetEmpty(earmarksPanel)){
			earmarksPresenter = injector.getEarmarkedPresenter();
			earmarksPresenter.init();
			earmarksPanel.add(earmarksPresenter.getWidget());
		}
		else{
			earmarksPresenter.init();
		}
	}
	
	@Override
	public void onTagEvent(TagEvent event) {
//		if(event.isByPerson(ConnectionId.getInstance().getCurrentUser(), TagConstants.TYPE_EARMARK)){
//			earmarksPresenter.clearCache();
//		}
		
	}
}
