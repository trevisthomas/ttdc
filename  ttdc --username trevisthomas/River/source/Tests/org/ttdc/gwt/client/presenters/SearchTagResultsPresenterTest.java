package org.ttdc.gwt.client.presenters;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryEvent;
import org.ttdc.gwt.client.presenters.post.SearchTagResultsPresenter;
import org.ttdc.gwt.client.presenters.tag.SearchTagListPresenter;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.SearchPostsCommand;
import org.ttdc.gwt.shared.commands.SearchTagsCommand;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.nongwt.client.rpc.MockRpcServiceAsync;
import static junit.framework.Assert.*;

public class SearchTagResultsPresenterTest {
	private final static Logger log = Logger.getLogger(SearchTagResultsPresenterTest.class);
	MockInjector injector = new MockInjector();
	MockRpcServiceAsync service;
	@Before
	public void setup(){
		
		HistoryEventPresenterManager.initInstance(injector);
		
		
		service = new MockRpcServiceAsync(new SearchPostsCommandResult());
		injector.setService(service);
	}
	
	@Test
	public void testBasic(){
		List<String> tagIds = new ArrayList<String>();
		tagIds.add(Helpers.tagGeneralStuff);
		
		SearchTagListPresenter presenter = injector.getSearchTagListPresenter();
		
		HistoryEvent event = HistoryEvent.createViewChange(HistoryConstants.VIEW_SEARCH_TAG_RESULTS);
		
		event.getSource().setParameter("tagId", Helpers.tagGeneralStuff);
		
		SearchTagResultsPresenter.View view;
		
		view = injector.getSearchTagResultsView();
		when(view.getStatusText()).thenReturn(new MockHasText());
		injector.setSearchTagResultsView(view);
		
		service.setDisable(true);//Either this or give it a proper response to test
		EventBus.getInstance().fireEvent(event);
		
		verify(view).show();
		//assertEquals(tagIds.get(0),((SearchTagsCommand)service.getCommand()).getTagIdList().get(0));
		//TODO: WTF was this test doing again? sigh.
		
	}
}
