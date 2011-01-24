package org.ttdc.gwt.client.uibinder.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.HistoryEventPresenterManager;
import org.ttdc.gwt.client.presenters.util.CookieTool;
import org.ttdc.gwt.client.presenters.util.MyListBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PageSizeComponent extends Composite{
	interface MyUiBinder extends UiBinder<Widget, PageSizeComponent> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    private Injector injector;
    
    @UiField (provided = true)ListBox pageSizeListBox;
    @UiField HTMLPanel main;
    private PageType pageType;
    private HistoryToken token;
    private static final int DEFAULT_PAGE_SIZE = 25; 
    
    private int perPage = -1;
    
    @Inject
    public PageSizeComponent(Injector injector) { 
    	this.injector = injector;
    	
    	pageSizeListBox = buildListBox();
		initWidget(binder.createAndBindUi(this)); 
		
	}
    
    private ListBox buildListBox() {
    	ListBox listBox =  new MyListBox(false);;
    	listBox.addItem("5","5");
    	listBox.addItem("10","10");
    	listBox.addItem("15","15");
    	listBox.addItem(""+DEFAULT_PAGE_SIZE,""+DEFAULT_PAGE_SIZE);
    	listBox.addItem("50","50");
    	listBox.addItem("100","100");
    	listBox.addItem("500","500");
    	listBox.addItem("1000","1000");
    	
    	listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				MyListBox lb = (MyListBox)pageSizeListBox;
				setRecordsPerPage(lb.getSelectedValue());
			}
		});
		return listBox;
	}

	public void init(HistoryToken t, PageType pageType){
		HistoryToken token = new HistoryToken();
		token.load(t);
		
		this.token = token;
    	this.pageType = pageType;
    	
    	if(token.hasParameter(HistoryConstants.PAGE_SIZE)){
    		perPage = token.getParameterAsInt(HistoryConstants.PAGE_SIZE);
    	}
    	else{
    		String cookiePageSize = CookieTool.readCookie(pageType+HistoryConstants.PAGE_SIZE);
    		if(cookiePageSize != null){
    			perPage = Integer.parseInt(cookiePageSize);
    		}
    		else{
    			perPage = DEFAULT_PAGE_SIZE; //DEFAULT
    		}
    	}
    	
    	((MyListBox)pageSizeListBox).setSelectedValue(""+perPage);
    }

    public int getRecordsPerPage(){
    	return perPage; 
    }
    
    private void setRecordsPerPage(String perPage){
    	CookieTool.saveCookie(pageType+HistoryConstants.PAGE_SIZE, perPage);
    	token.setParameter(HistoryConstants.PAGE_SIZE, perPage);
    	token.setParameter(HistoryConstants.PAGE_NUMBER_KEY, 1);
    	EventBus.fireHistoryToken(token);
    }
}
