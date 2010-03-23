package org.ttdc.gwt.client.presenters.tag;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryEvent;
import org.ttdc.gwt.client.messaging.history.HistoryEventListener;
import org.ttdc.gwt.client.messaging.history.HistoryEventType;
import org.ttdc.gwt.client.presenters.MockHasClickHandlers;
import org.ttdc.gwt.client.presenters.MockHasWidgets;
import org.ttdc.gwt.client.presenters.MockInjector;
import org.ttdc.gwt.client.presenters.MockRpcServiceAsync;
import org.ttdc.gwt.client.presenters.post.TagRemovePresenter;

public class SearchTagListPresenterTest {
	MockInjector injector;
	TagListPresenterView view;
	MockHasWidgets tagListWidgets;
	MockHasClickHandlers myMockClickHandler;
	List<String> viewFakeTagIdList;
	
	@Before
	public void startup(){
		injector = new MockInjector();
		
		view = mock(TagListPresenterView.class);
		myMockClickHandler = new MockHasClickHandlers();
		when(view.getAddClickHandler()).thenReturn(myMockClickHandler);
		
		
		
		injector.setCommonTagListView(view);
		
	}
	@After
	public void taredown(){
		EventBus.getInstance().clearAll();
	}
	
	//Adding a tag in browse mode fires a history event with the new tag added to the list
	@Test
	public void testAddButtonInTagBrowse(){
		SearchTagListPresenter presenter = injector.getSearchTagListPresenter();
		final GTag newTag = new GTag();
		newTag.setTagId("123abc");
		newTag.setValue("Browse to me now");
		
		final GTag oldTag = new GTag();
		oldTag.setTagId("456abc");
		oldTag.setValue("Morsels of JUnit Goodness");
		
		List<GTag> tagList = new ArrayList<GTag>();
		tagList.add(oldTag);
		presenter.setTagIdList(tagList);
		
		SuggestionObject tagSuggestion = mock(SuggestionObject.class);
		when(tagSuggestion.getTag()).thenReturn(newTag);
		when(injector.getCommonTagListView().getTagSuggestion()).thenReturn(tagSuggestion);
		
		MockRpcServiceAsync service = new MockRpcServiceAsync();
		
		injector.setService(service);
		
		
		EventBus bus = EventBus.getInstance();
		bus.addListener(
			new HistoryEventListener(){
				public void onHistoryEvent(HistoryEvent event) {
					assertEquals(HistoryEventType.VIEW_CHANGE, event.getType());
					assertEquals(HistoryConstants.VIEW_SEARCH, event.getSource().getParameter(HistoryConstants.VIEW));
					assertEquals(oldTag.getTagId()+","+newTag.getTagId(),event.getSource().getParameter("tagId"));
				}
			}	
		);
		
		MockHasClickHandlers.clickMockButton(presenter.getView().getAddClickHandler());
	}
	
	@Test
	public void testRemoveTagInBrowse(){
		SearchTagListPresenter presenter = injector.getSearchTagListPresenter();
		final GTag newTag = new GTag();
		newTag.setTagId("123abc");
		newTag.setValue("Browse to me now");
		
		final GTag oldTag = new GTag();
		oldTag.setTagId("456abc");
		oldTag.setValue("Morsels of JUnit Goodness");
		
		List<GTag> tagList = new ArrayList<GTag>();
		tagList.add(oldTag);
		tagList.add(newTag);
		presenter.setTagIdList(tagList);
		
		//simple check to see if tagIdList is returning a reasonable list
		assertEquals(tagList.size(),presenter.getTagIdList().size());
		
		//Grab the presenter for the tag you want to click
		TagRemovePresenter tagRemovePresenter = presenter.getTagRemovePresenterMap().get(oldTag.getTagId());
		
		//Setup a history listener to verify proper functionality
		EventBus bus = EventBus.getInstance();
		bus.addListener(
			new HistoryEventListener(){
				public void onHistoryEvent(HistoryEvent event) {
					assertEquals(HistoryEventType.VIEW_CHANGE, event.getType());
					assertEquals(HistoryConstants.VIEW_SEARCH,event.getSource().getParameter(HistoryConstants.VIEW));
					assertEquals(newTag.getTagId(),event.getSource().getParameter("tagId"));
				}
			}	
		);
		
		//Click it
		MockHasClickHandlers.clickMockButton(tagRemovePresenter.getView().getRemoveTagClickHandler());
		
	}
}
