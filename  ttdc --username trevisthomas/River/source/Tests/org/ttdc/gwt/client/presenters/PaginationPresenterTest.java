package org.ttdc.gwt.client.presenters;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.nongwt.client.rpc.MockRpcServiceAsync;

public class PaginationPresenterTest {
	private final static Logger log = Logger.getLogger(PaginationPresenterTest.class);
	
	MockRpcServiceAsync service;
	MockInjector injector;
	PaginationPresenter.View view;
	PaginationPresenter presenter;
	HistoryToken token;
	MockHasWidgets pageButtons;
	
	@Before
	public void setup(){
		token = new HistoryToken();
		injector = new MockInjector();
		
		view = injector.getPaginationView();
		presenter = injector.getPaginationPresenter();
		
		when(view.prevButton()).thenReturn(new MockHasWidgets());
		when(view.nextButton()).thenReturn(new MockHasWidgets());
		pageButtons = new MockHasWidgets();
		when(view.pageButtons()).thenReturn(pageButtons);
		

		
	}
	
	
	
	
	
	@Test
	public void testTenPages(){
		int currentPage = 1, maxPages = 10;
		PaginatedList paginator = makeFakePaginator(currentPage, maxPages);
		
		presenter.initialize(token, paginator);
		
		verify(view).nextButton();
		verify(view, never()).prevButton();
		//verify(view,times(8)).pageButtons().add(any(Widget.class));
		assertTrue(pageButtons.size() == 8);
	}





	private PaginatedList makeFakePaginator(int currentPage, int maxPages) {
//		PaginatedList paginator = new PaginatedList<String>();
//		paginator.setCurrentPage(currentPage);
//		paginator.setTotalResults(maxPages * paginator.getPageSize());
//		return paginator;
		
		PaginatedList paginator = Mockito.mock(PaginatedList.class);
		when(paginator.getCurrentPage()).thenReturn(currentPage);
		when(paginator.calculateNumberOfPages()).thenReturn(maxPages);
		
		return paginator;
	}
	
	@Test
	public void testFivePages(){
		int currentPage = 1, maxPages = 5;
		presenter.initialize(token, makeFakePaginator(currentPage, maxPages));
		
		verify(view).nextButton();
		verify(view, never()).prevButton();
		assertTrue(pageButtons.size() == 5);
	}
	
	@Test
	public void testFivePagesLastPage(){
		
		int currentPage = 5, maxPages = 5;
		presenter.initialize(token, makeFakePaginator(currentPage, maxPages));
		
		verify(view, never()).nextButton();
		verify(view).prevButton();
		assertTrue(pageButtons.size() == 5);
	}
	
	@Test
	public void testFivePagesNonEdgePage(){
		int currentPage = 3, maxPages = 5;
		HyperlinkPresenter linkPresenter = mock(HyperlinkPresenter.class);
		injector.setHyperlinkPresenter(linkPresenter);
		
		presenter.initialize(token, makeFakePaginator(currentPage, maxPages));
		
		verify(view).nextButton();
		verify(view).prevButton();
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("1"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("2"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("3"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("4"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("5"));
		assertTrue(pageButtons.size() == 5);
	}
	
	@Test
	public void testBigWithBoundary(){
		int currentPage = 15, maxPages = 20;
		HyperlinkPresenter linkPresenter = mock(HyperlinkPresenter.class);
		injector.setHyperlinkPresenter(linkPresenter);
		
		presenter.initialize(token, makeFakePaginator(currentPage, maxPages));
		verify(view).nextButton();
		verify(view).prevButton();
		verify(linkPresenter,never()).setToken(any(HistoryToken.class), eq("1"));
		//etc
		verify(linkPresenter,never()).setToken(any(HistoryToken.class), eq("10"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("13"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("14"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("15"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("16"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("17"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("18"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("19"));
		verify(linkPresenter).setToken(any(HistoryToken.class), eq("20"));
		assertTrue(pageButtons.size() == 8);
	}
	
	

}
