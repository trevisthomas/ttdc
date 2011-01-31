package org.ttdc.gwt.client.uibinder.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.util.CookieTool;
import org.ttdc.gwt.client.presenters.util.MyListBox;
import org.ttdc.gwt.shared.commands.TopicCommand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SortOrderComponent extends Composite{
	interface MyUiBinder extends UiBinder<Widget, SortOrderComponent> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    private Injector injector;
    
    @UiField (provided = true)ListBox sortOrderListBox;
    @UiField HTMLPanel main;
    private PageType pageType;
    private HistoryToken token;
    //private static final int DEFAULT_PAGE_SIZE = 25;
    private static final String DEFAULT_SORT_ORDER = HistoryConstants.SORT_BY_REPLY_DATE;
    private GPost rootPost;
    
    private String sortOrder;
    
    @Inject
    public SortOrderComponent(Injector injector) { 
    	this.injector = injector;
    	
    	sortOrderListBox = buildListBox();
		initWidget(binder.createAndBindUi(this)); 
		
	}
    
    private ListBox buildListBox() {
    	ListBox listBox =  new MyListBox(false);;
    	listBox.addItem("Recent activity",HistoryConstants.SORT_BY_REPLY_DATE);
    	listBox.addItem("New to old",HistoryConstants.SORT_BY_CREATE_DATE);
    	listBox.addItem("Old to new",HistoryConstants.SORT_BY_CREATE_DATE_ASC);
    	
    	listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				MyListBox lb = (MyListBox)sortOrderListBox;
				setSortOrder(lb.getSelectedValue());
			}
		});
		return listBox;
	}

	public void init(HistoryToken t, PageType pageType){
		HistoryToken token = new HistoryToken();
		token.load(t);
		
		this.token = token;
    	this.pageType = pageType;
    	
    	if(token.hasParameter(HistoryConstants.SORT_KEY)){
    		sortOrder = token.getParameter(HistoryConstants.SORT_KEY);
    	}
    	else{
    		String cookieSortOrder = CookieTool.readCookie(pageType+HistoryConstants.SORT_KEY);
    		if(cookieSortOrder != null){
    			sortOrder = cookieSortOrder;
    		}
    		else{
    			sortOrder = DEFAULT_SORT_ORDER; //DEFAULT
    		}
    	}
    	((MyListBox)sortOrderListBox).setSelectedValue(sortOrder);
    }

    public TopicCommand.SortOrder getSortOrder(){
    	if(sortOrder.equals(HistoryConstants.SORT_BY_CREATE_DATE)){
    		return TopicCommand.SortOrder.BY_DATE;
    	}
    	else if(sortOrder.equals(HistoryConstants.SORT_BY_CREATE_DATE_ASC)){
    		return TopicCommand.SortOrder.BY_DATE_ASC;
    	}
    	else if(sortOrder.equals(HistoryConstants.SORT_BY_REPLY_DATE)){
    		return TopicCommand.SortOrder.BY_REPLY;
    	}
    	else{
    		throw new RuntimeException("Unknown sort type");
    	}
    }
    
    private void setSortOrder(String selectedValue) {
    	CookieTool.saveCookie(pageType+HistoryConstants.SORT_KEY, selectedValue);
//    	token.setParameter(HistoryConstants.SORT_KEY, selectedValue);
//    	token.setParameter(HistoryConstants.PAGE_NUMBER_KEY, 1);
    	
    	prepTokenForSort(selectedValue);
    	
    	//EventBus.fireHistoryToken(token);
	}
    
    private void prepTokenForSort(String sortValue) {
		token.setParameter(HistoryConstants.SORT_KEY, sortValue);
		token.removeParameter(HistoryConstants.PAGE_NUMBER_KEY);
		if(rootPost != null){
			token.setParameter(HistoryConstants.POST_ID_KEY, rootPost.getPostId());
		}
		//History.newItem(token.toString(), false);
		EventBus.fireHistoryToken(token);
	}

	public GPost getRootPost() {
		return rootPost;
	}

	public void setRootPost(GPost rootPost) {
		this.rootPost = rootPost;
	}
    
    
}
