package org.ttdc.gwt.client.presenters.tag;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.ttdc.gwt.client.autocomplete.TagSuggestion;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.beans.PostBeanMother;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventType;
import org.ttdc.gwt.client.presenters.MockHasClickHandlers;
import org.ttdc.gwt.client.presenters.MockHasWidgets;
import org.ttdc.gwt.client.presenters.MockInjector;
import org.ttdc.gwt.client.presenters.MockRpcServiceAsync;
import org.ttdc.gwt.client.presenters.post.TagRemovePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;

public class PostTagListPresenterTest {
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
	
	/**
	 * 
	 * Trevis, this is how you make an arguement matcher for Mockito
	 *
	 */
	class MatcherForTagIdList extends ArgumentMatcher<List<String>> {
		final List<String> tagIds;

		public MatcherForTagIdList(final List<String> tagIds) {
			this.tagIds = tagIds;
		}

		public boolean matches(Object list) {
			List<String> thatList = (List<String>) list;
			return tagIds.equals(thatList);
		}
	}
	
	class MatcherForListOfTagRemovePresenter extends ArgumentMatcher<List<BasePresenter<?>>>{
		@Override
		public boolean matches(Object argument) {
			List<BasePresenter<?>> thatList = (List<BasePresenter<?>>) argument;
			
			return (thatList.get(0) instanceof TagRemovePresenter);
			
		}
	}
	class MatcherForListOfHyperlinkPresenter extends ArgumentMatcher<List<BasePresenter<?>>>{
		@Override
		public boolean matches(Object argument) {
			List<BasePresenter<?>> thatList = (List<BasePresenter<?>>) argument;
			
			return (thatList.get(0) instanceof HyperlinkPresenter);
			
		}
	}
	class MatcherForPresenterList<T> extends ArgumentMatcher<List<BasePresenter<?>>>{
		private final List<T> list;
		MatcherForPresenterList(List<T> list){
			this.list = list;
		}

		public boolean matches(Object argument) {
			List<BasePresenter<?>> thatList = (List<BasePresenter<?>>) argument;
			
			return list.size() == thatList.size();
			//TODO: add a better test? Check for actual values
			
		}
	}
	
	private List<String> extractTagIdListFromAssList(List<GAssociationPostTag> asses){
		List<String> tagIdList = new ArrayList<String>();
		for(GAssociationPostTag ass : asses){
			tagIdList.add(ass.getTag().getTagId());
		}
		return tagIdList;
	}

	
	@Test
	public void testTagListInPostMode(){
		PostTagListPresenter presenter = injector.getPostTagListPresenter();
		List<GAssociationPostTag> list = new ArrayList<GAssociationPostTag>();
		list.add(PostBeanMother.createTopicTagAss("Test this tag"));
		
//		viewFakeTagIdList = new ArrayList<String>();
//		when(view.getExcludeTagIdList()).thenReturn(viewFakeTagIdList);
		
		presenter.setTagAssociationList(list);
		assertTrue("Tag Browse Presenter link list is the wrong size"
				,presenter.getTagLinkPresenterMap().size() == list.size());
		
		assertTrue("Remove Link presenter list is the wrogn size"
				,presenter.getTagRemovePresenterMap().size() == list.size());
		
		
		
		presenter.setEditMode(true);
		assertTrue(presenter.isEditMode());
		presenter.toggleEditMode();
		assertFalse(presenter.isEditMode());
		
		verify(view,times(3)).showPresenters(any(List.class), any(List.class));
		
		String tagId = list.get(0).getTag().getTagId();
		
		//This that the tagId was added to the exclude list
		//verify(view).setExcludeTagIdList(argThat(new MatcherForTagIdList(Arrays.asList(tagId))));//Remember, exclude is for tag mode
		
		//Remember the tags currently in use are in the views tag exclude list. This list's sole purpose is to drive the auto completer
//		assertEquals(Arrays.asList(tagId), view.getExcludeTagIdList());
		
		//Test that showPresenters is called with an appropriate list! (Use this list to get the tags to filter for auto complete)
		verify(view,atMost(4)).showPresenters(any(List.class), argThat(new MatcherForPresenterList<GAssociationPostTag>(list)));
		
	}
	
	@Test
	public void testShowOnlyTopicTagsInPostMode(){
		List<GAssociationPostTag> list = new ArrayList<GAssociationPostTag>();
		list.add(PostBeanMother.createTopicTagAss("Test this tag"));
		list.add(PostBeanMother.createTitleAssociation("Title of the post"));
		list.add(PostBeanMother.createTagAssWithValueAndType("3.1",TagConstants.TYPE_RATING));
		list.add(PostBeanMother.createTagAssWithValueAndType("Janvier",TagConstants.TYPE_DATE_MONTH));
		
		PostTagListPresenter presenter = injector.getPostTagListPresenter();
		presenter.setTagAssociationList(list);
		assertTrue("Non topic tags were shown and shouldnt have been.",presenter.getTagLinkPresenterMap().size() == 1);
		
		
	}
	
	@Test
	public void testRemoveTagInPostMode(){
		List<GAssociationPostTag> list = new ArrayList<GAssociationPostTag>();
		list.add(PostBeanMother.createTopicTagAss("Silly"));
		list.add(PostBeanMother.createTopicTagAss("Political"));
		list.add(PostBeanMother.createTitleAssociation("Title of the post"));
		
		PostTagListPresenter presenter = injector.getPostTagListPresenter();
		presenter.setTagAssociationList(list);
		assertTrue("Non topic tags were shown and shouldnt have been.",presenter.getTagIdList().size() == 2);
		
		GAssociationPostTag ass = list.get(0); 
		String tagId = ass.getTag().getTagId();
		String assId =  ass.getGuid();//Choose one of the associations
		
		//Now click one.
		TagRemovePresenter tagRemovePresenter = presenter.getTagRemovePresenterMap().get(tagId);
		
		//RpcServiceAsync service = mock(RpcServiceAsync.class);
		MockRpcServiceAsync service = new MockRpcServiceAsync();
		
		injector.setService(service);
		
		MockHasClickHandlers.clickMockButton(tagRemovePresenter.getView().getRemoveTagClickHandler());
		
		assertTrue(service.command instanceof AssociationPostTagCommand);
		AssociationPostTagCommand command = (AssociationPostTagCommand)service.command;
		assertEquals(assId,command.getAssociationId());

		assertTrue("Tags should be removed instantly, so after remove your lists should one less in size",presenter.getTagRemovePresenterMap().size() == 1);
		
	}
	

	
	@Test
	public void testVerifyEditModeToggle(){
		PostTagListPresenter presenter = injector.getPostTagListPresenter();
		List<GAssociationPostTag> list = new ArrayList<GAssociationPostTag>();
		list.add(PostBeanMother.createTopicTagAss("Test this tag"));
		
		presenter.setTagAssociationList(list);
		assertTrue("Tag Browse Presenter link list is the wrong size"
				,presenter.getTagLinkPresenterMap().size() == list.size());
		
		assertTrue("Remove Link presenter list is the wrogn size"
				,presenter.getTagRemovePresenterMap().size() == list.size());
		
		
		//Set the edit mode to true and verify that the presenter type is the one for tag remove
		presenter.setEditMode(true);
		assertTrue(presenter.isEditMode());
		verify(view).showPresenters(any(List.class), argThat(new MatcherForListOfTagRemovePresenter()));
		
		
		
		//Set toggle the edit mode and verify that it is now showing the presenter views.
		presenter.toggleEditMode();
		assertFalse(presenter.isEditMode());
		verify(view,times(2)).showPresenters(any(List.class), argThat(new MatcherForListOfHyperlinkPresenter()));
		
		//Trevis: Remember that verify is just verifying that the showPresenters method was called
		//with your expected list at some point. Not that it is currently in that state.
		//I mean you can check both of these after toggeling and still get a passing test
	
	}
	
	@Test
	public void testAddButtonInPostMode(){
		PostTagListPresenter presenter = injector.getPostTagListPresenter();
		List<GAssociationPostTag> list = new ArrayList<GAssociationPostTag>();
		list.add(PostBeanMother.createTopicTagAss("Test this tag"));
		presenter.setTagAssociationList(list);
		
		GTag testTag = new GTag();
		testTag.setTagId("123abc");
		
		TagSuggestion tagSuggestion = mock(TagSuggestion.class);
		when(tagSuggestion.getTag()).thenReturn(testTag);
		when(injector.getCommonTagListView().getTagSuggestion()).thenReturn(tagSuggestion);
		
		MockRpcServiceAsync service = new MockRpcServiceAsync();
		
		injector.setService(service);
		
		MockHasClickHandlers.clickMockButton(presenter.getView().getAddClickHandler());
		
		assertTrue(service.command instanceof AssociationPostTagCommand);
		AssociationPostTagCommand command = (AssociationPostTagCommand)service.command;
		
		assertEquals(testTag.getTagId(),command.getTag().getTagId());
		
		
		GAssociationPostTag ass = new GAssociationPostTag();
		ass.setGuid("1234-new-fake-mock-assid");
		ass.setTag(testTag);
		AssociationPostTagResult result = new AssociationPostTagResult(AssociationPostTagResult.Status.CREATE);
		result.setAssociationPostTag(ass);
		//Call the call back with the mock result
		((CommandResultCallback<AssociationPostTagResult>)service.callback).onSuccess(result);
		
		//The new tag should be added immediately... hm, actually maybe not?
		assertTrue("Ass was not added", presenter.getTagRemovePresenterMap().size() == 2);
		
		//Test that showPresenters is called with an appropriate list!
		List<String> tagIdList = extractTagIdListFromAssList(list);
		verify(view).showPresenters(argThat(new MatcherForTagIdList(tagIdList)), argThat(new MatcherForPresenterList<GAssociationPostTag>(list)));
		//The list does not grow until the call back is executed
		
	}
	
	/**
	 * Simulates a server side execution failure during a tag add.  Presenter should fire
	 * a message to the event bus in case of this kind of error. This should never really happen
	 * unless something wonky is happening on the server
	 */
	@Test
	public void testServerErrorEventFired(){
		PostTagListPresenter presenter = injector.getPostTagListPresenter();
		List<GAssociationPostTag> list = new ArrayList<GAssociationPostTag>();
		list.add(PostBeanMother.createTopicTagAss("Test this tag"));
		presenter.setTagAssociationList(list);
		
		GTag testTag = new GTag();
		testTag.setTagId("123abc");
		
		TagSuggestion tagSuggestion = mock(TagSuggestion.class);
		when(tagSuggestion.getTag()).thenReturn(testTag);
		when(injector.getCommonTagListView().getTagSuggestion()).thenReturn(tagSuggestion);
		
		MockRpcServiceAsync service = new MockRpcServiceAsync();
		
		injector.setService(service);
		
		MockHasClickHandlers.clickMockButton(presenter.getView().getAddClickHandler());
		
		boolean called = false;
		
		//Lister for error events
		EventBus bus = EventBus.getInstance();
		MessageEventListener mockMessageEventListener = mock(MessageEventListener.class);
		bus.addListener(mockMessageEventListener);
		
		//Simulate command failure on server side.
		((CommandResultCallback<AssociationPostTagResult>)service.callback).onSuccess(new AssociationPostTagResult());
		
		verify(mockMessageEventListener,times(1)).onMessageEvent(any(MessageEvent.class));
		
	}
	
	@Test
	public void testSimulateNewTagAssociationForTagListOwner(){
		List<GAssociationPostTag> list = new ArrayList<GAssociationPostTag>();
		list.add(PostBeanMother.createTopicTagAss("Silly"));
		list.add(PostBeanMother.createTopicTagAss("Political"));
		
		PostTagListPresenter presenter = injector.getPostTagListPresenter();
		presenter.setTagAssociationList(list);
		
		assertTrue("Precondition not satisfied.",presenter.getTagIdList().size() == 2);
		
		GAssociationPostTag gAss = PostBeanMother.createTopicTagAss("Test this tag");
		TagEvent event = new TagEvent(TagEventType.NEW,gAss);
		EventBus bus = EventBus.getInstance();
		bus.fireEvent(event);
		
		assertTrue("Tag list didnt grow.",presenter.getTagIdList().size() == 3);
		
		//Test that showPresenters is called with an appropriate list!
		List<String> tagIdList = extractTagIdListFromAssList(list);
		verify(view).showPresenters(argThat(new MatcherForTagIdList(tagIdList)), argThat(new MatcherForPresenterList<GAssociationPostTag>(list)));//Called with 2
		list.add(gAss);
		tagIdList = extractTagIdListFromAssList(list);
		verify(view).showPresenters(argThat(new MatcherForTagIdList(tagIdList)), argThat(new MatcherForPresenterList<GAssociationPostTag>(list)));//also called with 3
	}
	
	@Test
	public void testSimulateRemoteTagRemovalTagListOwner(){
		List<GAssociationPostTag> list = new ArrayList<GAssociationPostTag>();
		list.add(PostBeanMother.createTopicTagAss("Silly"));
		list.add(PostBeanMother.createTopicTagAss("Political"));
		GAssociationPostTag gAss = PostBeanMother.createTopicTagAss("Test this tag");
		list.add(gAss);
		
		PostTagListPresenter presenter = injector.getPostTagListPresenter();
		presenter.setTagAssociationList(list);
		
		assertTrue("Precondition not satisfied.",presenter.getTagIdList().size() == 3);
		
		TagEvent event = new TagEvent(TagEventType.REMOVED,gAss);
		EventBus bus = EventBus.getInstance();
		bus.fireEvent(event);
		
		assertTrue("Tag list didnt shrink.",presenter.getTagIdList().size() == 2);
		
		//Test that showPresenters is called with an appropriate list!
		List<String> tagIdList = extractTagIdListFromAssList(list);
		verify(view).showPresenters(argThat(new MatcherForTagIdList(tagIdList)),argThat(new MatcherForPresenterList<GAssociationPostTag>(list))); //Called with 3
		
		list.remove(gAss);
		tagIdList = extractTagIdListFromAssList(list);
		verify(view).showPresenters(argThat(new MatcherForTagIdList(tagIdList)), argThat(new MatcherForPresenterList<GAssociationPostTag>(list))); //also called with 2
		
	}
	
	/**
	 * Test tag highlighting. This is for showing which tags of a post were chosen as
	 * part of the search.  (delicious style)
	 *  
	 */
	@Test
	public void testTagHighlighting(){
		PostTagListPresenter presenter = injector.getPostTagListPresenter();
		List<GAssociationPostTag> list = new ArrayList<GAssociationPostTag>();
		list.add(PostBeanMother.createTopicTagAss("Silly"));
		list.add(PostBeanMother.createTopicTagAss("Political"));

		List<String> tagIdList = extractTagIdListFromAssList(list);
		
		GAssociationPostTag notHighlit = PostBeanMother.createTopicTagAss("Test this tag");
		list.add(notHighlit);
		
		presenter.setEditMode(false);
		
		presenter.setTagAssociationList(list);
		presenter.setHighlightedTagIdList(tagIdList);
		
		List<HyperlinkPresenter> linkPresenters = new ArrayList<HyperlinkPresenter>(presenter.getTagLinkPresenterMap().values());
		
		assertEquals(list.size(), linkPresenters.size());
		
		
		for(HyperlinkPresenter linkPresenter : linkPresenters){
			if(linkPresenter.getTag().getTagId().equals(notHighlit.getTag().getTagId())){
				verify(linkPresenter.getView()).setHighlighted(false);
			}
			else{
				verify(linkPresenter.getView()).setHighlighted(true);
			}
		}
	}
	
	//Remember to do an integration test to verify that the server isnt firing tag delete messages back to the initiator.
	//When some tags are highlighted they should be sorted to the end of the list.
}
