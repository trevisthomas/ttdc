package org.ttdc.gwt.client.uibinder.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PaginationNanoPanel  extends Composite{
	interface MyUiBinder extends UiBinder<Widget, PaginationNanoPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    private Injector injector;
    @UiField (provided = true) Hyperlink prevElement;
    @UiField (provided = true) Hyperlink nextElement;
    @UiField SpanElement nextDisabledElement;
    @UiField SpanElement prevDisabledElement;
    @UiField HTMLPanel main;
    
    private final HyperlinkPresenter prevLinkPresenter;
    private final HyperlinkPresenter nextLinkPresenter;
    
    
    	
	@Inject
    public PaginationNanoPanel(Injector injector) { 
    	this.injector = injector;
    	
    	prevLinkPresenter = injector.getHyperlinkPresenter();
		nextLinkPresenter = injector.getHyperlinkPresenter();
				
		prevElement = prevLinkPresenter.getHyperlink();
		nextElement = nextLinkPresenter.getHyperlink();
		
		initWidget(binder.createAndBindUi(this)); 
		hideLinks();
    	main.setVisible(false);
	}



	private void hideLinks() {
		nextDisabledElement.addClassName("tt-hidden");
		prevDisabledElement.addClassName("tt-hidden");
		prevElement.setVisible(false);
		nextElement.setVisible(false);
	}
    
	
	
    @Override
    public Widget getWidget() {
    	return this;
    }
    
    public <T> void init(PaginationPanel paginationPanel, final PaginatedList<T> paginator){
    	hideLinks();
    	if(paginator.calculateNumberOfPages() > 1){
    		main.setVisible(true);
    		HistoryToken prevToken = paginationPanel.getPrevToken();
			HistoryToken nextToken = paginationPanel.getNextToken();
			
			if(prevToken != null){
				prevLinkPresenter.setToken(prevToken, "prev");
				prevElement.setVisible(true);
				prevDisabledElement.addClassName("tt-hidden");
			}
			else{
				prevElement.setVisible(false); 
				prevDisabledElement.removeClassName("tt-hidden");
			}
			
			if(nextToken != null){
				nextLinkPresenter.setToken(nextToken, "next");
				nextElement.setVisible(true);
				nextDisabledElement.addClassName("tt-hidden");
			}
			else{
				nextElement.setVisible(false);
				nextDisabledElement.removeClassName("tt-hidden");
			}
			
		}
		else{
			main.setVisible(false);
			prevElement.setVisible(false); 
			nextElement.setVisible(false); 
		}
	}
}
