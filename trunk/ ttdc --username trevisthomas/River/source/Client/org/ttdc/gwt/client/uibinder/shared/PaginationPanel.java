package org.ttdc.gwt.client.uibinder.shared;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.PAGE_NUMBER_KEY;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PaginationPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, PaginationPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    private String pageNumberKey = PAGE_NUMBER_KEY;
	private HistoryToken prevToken;
	private HistoryToken nextToken;
	
	@UiField (provided=true) Label morePages;
	@UiField (provided=true) Hyperlink firstPage;
	@UiField (provided=true) Hyperlink lastPage;
	
	private HyperlinkPresenter firstPageLinkPresenter;
	private HyperlinkPresenter lastPageLinkPresenter;
	
	@UiField SimplePanel prevPage;
	@UiField SimplePanel nextPage;
	@UiField FlowPanel pages;
	@UiField HTMLPanel main;
    
	@Inject
    public PaginationPanel(Injector injector) { 
    	this.injector = injector;
    	morePages = new Label("...");
    	
    	firstPageLinkPresenter = injector.getHyperlinkPresenter();
    	lastPageLinkPresenter = injector.getHyperlinkPresenter();
    	
    	firstPage = firstPageLinkPresenter.getHyperlink();
    	lastPage = lastPageLinkPresenter.getHyperlink();
    	
    	initWidget(binder.createAndBindUi(this)); 
    	morePages.setVisible(false);
	}
	
    @Override
    public Widget getWidget() {
    	return this;
    }
   
    public <T> void initialize(final HistoryToken t, final PaginatedList<T> paginator){
		HistoryToken token = new HistoryToken();
		token.load(t);
		
		int currentPage = paginator.getCurrentPage();
		int maxPages = paginator.calculateNumberOfPages();
		
		if(maxPages <= 1){
			main.setVisible(false);
			return;
		}
		
		final int pageLinksToShow = 8;
		if(currentPage > 1){
			buildPrevButton(token, currentPage);
		}
		
		if(currentPage < maxPages){
			buildNextButton(token, currentPage);
		}
		
		int startPage = calculateStartPage(currentPage, maxPages, pageLinksToShow);
		
		HyperlinkPresenter pageButton;
		int i;
		for(i = startPage; i <= maxPages && i < startPage+pageLinksToShow; i++){
			pageButton = injector.getHyperlinkPresenter();
			token.setParameter(pageNumberKey, i);
			pageButton.setToken(token, ""+i);
			pageButton.setStyleType(HyperlinkPresenter.StyleType.PAGINATOR);
			if(currentPage == i){
				pageButton.setHighlighted(true);
			}
			pages.add(pageButton.getWidget());
		}
		
		if(currentPage > 1){
			buildFirstPageButton(token);
		}
		else{
			firstPage.setVisible(false);
		}
		
		if(currentPage < maxPages){
			buildLastPageButton(token, maxPages);
		}
		else{
			lastPage.setVisible(false);
		}
		
		morePages.setVisible(i < maxPages);
		
	}

	private int calculateStartPage(int currentPage, int maxPages, final int pageLinksToShow) {
		int startPage;
		if(currentPage > 1 && maxPages > pageLinksToShow){
			if(currentPage <= (maxPages - (pageLinksToShow/2)))
				startPage = currentPage - (pageLinksToShow/2);
			else
				startPage = 1 + maxPages - pageLinksToShow; 
		}
		else{
			startPage = 1;
		}
		if(startPage < 1){
			startPage = 1;
		}
		return startPage;
	}
	
	public HistoryToken getPrevToken() {
		return prevToken;
	}

	public HistoryToken getNextToken() {
		return nextToken;
	}
	
	private void buildFirstPageButton(HistoryToken token) {
		token.setParameter(pageNumberKey, 1);
		firstPageLinkPresenter.setStyleType(HyperlinkPresenter.StyleType.PAGINATOR);
		firstPageLinkPresenter.setToken(token, "first");
		firstPageLinkPresenter.init();
		firstPage.setVisible(true);
	}
	
	private void buildLastPageButton(HistoryToken token, int pageNumber) {
		token.setParameter(pageNumberKey, pageNumber);
		lastPageLinkPresenter.setStyleType(HyperlinkPresenter.StyleType.PAGINATOR);
		lastPageLinkPresenter.setToken(token, "last");
		lastPageLinkPresenter.init();
		lastPage.setVisible(true);
	}

	private void buildNextButton(HistoryToken token, int currentPage) {
		HyperlinkPresenter nextButtonPresenter = injector.getHyperlinkPresenter();
		token.setParameter(pageNumberKey, currentPage+1);
		nextButtonPresenter.setStyleType(HyperlinkPresenter.StyleType.PAGINATOR);
		nextButtonPresenter.setToken(token, "Next");
		nextPage.add(nextButtonPresenter.getWidget());
		
		nextToken = new HistoryToken();
		nextToken.load(token);
	}

	private void buildPrevButton(HistoryToken token, int currentPage) {
		HyperlinkPresenter prevButtonPresenter = injector.getHyperlinkPresenter();
		token.setParameter(pageNumberKey, currentPage-1);
		prevButtonPresenter.setStyleType(HyperlinkPresenter.StyleType.PAGINATOR);
		prevButtonPresenter.setToken(token, "Prev");
		prevPage.add(prevButtonPresenter.getWidget());
		
		prevToken = new HistoryToken();
		prevToken.load(token);
	}

	public String getPageNumberKey() {
		return pageNumberKey;
	}

	public void setPageNumberKey(String pageNumberKey) {
		this.pageNumberKey = pageNumberKey;
	}
}