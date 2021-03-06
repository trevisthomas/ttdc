package org.ttdc.gwt.client.presenters.shared;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.*;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.shared.util.PaginatedList;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

@Deprecated
public class PaginationPresenter extends BasePresenter<PaginationPresenter.View>{
	private String pageNumberKey = PAGE_NUMBER_KEY;
	private HistoryToken prevToken;
	private HistoryToken nextToken;
	
	public interface View extends BaseView{
		HasWidgets prevButton();
		HasWidgets nextButton();
		HasWidgets pageButtons();
		void setVisible(boolean b);
	} 

	@Inject
	public PaginationPresenter(Injector injector) {
		super(injector,injector.getPaginationView());
	}
	
	
	
	public <T> void initialize(final HistoryToken t, final PaginatedList<T> paginator){
//		view.prevButton().clear();
//		view.nextButton().clear();
//		view.pageButtons().clear();
		
		HistoryToken token = new HistoryToken();
		token.load(t);
		
		int currentPage = paginator.getCurrentPage();
		int maxPages = paginator.calculateNumberOfPages();
		
		if(maxPages <= 1){
			view.setVisible(false);
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
		for(int i = startPage; i <= maxPages && i < startPage+pageLinksToShow; i++){
			pageButton = injector.getHyperlinkPresenter();
			token.setParameter(pageNumberKey, i);
			pageButton.setToken(token, ""+i);
			pageButton.setStyleType(HyperlinkPresenter.StyleType.PAGINATOR);
			if(currentPage == i){
				pageButton.setHighlighted(true);
			}
			view.pageButtons().add(pageButton.getWidget());
		}
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

	private void buildNextButton(HistoryToken token, int currentPage) {
		HyperlinkPresenter nextButtonPresenter = injector.getHyperlinkPresenter();
		token.setParameter(pageNumberKey, currentPage+1);
		nextButtonPresenter.setStyleType(HyperlinkPresenter.StyleType.PAGINATOR);
		nextButtonPresenter.setToken(token, "Next");
		view.nextButton().add(nextButtonPresenter.getWidget());
		
		nextToken = new HistoryToken();
		nextToken.load(token);
	}

	private void buildPrevButton(HistoryToken token, int currentPage) {
		HyperlinkPresenter prevButtonPresenter = injector.getHyperlinkPresenter();
		token.setParameter(pageNumberKey, currentPage-1);
		prevButtonPresenter.setStyleType(HyperlinkPresenter.StyleType.PAGINATOR);
		prevButtonPresenter.setToken(token, "Prev");
		view.prevButton().add(prevButtonPresenter.getWidget());
		
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
