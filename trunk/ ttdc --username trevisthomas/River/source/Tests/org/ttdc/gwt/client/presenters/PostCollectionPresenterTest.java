package org.ttdc.gwt.client.presenters;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.PostBeanMother;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenter;

public class PostCollectionPresenterTest {
	Injector injector;
	private List<GPost> list;
	
	@Before 
	public void setup(){
		list = new ArrayList<GPost>();
		list.addAll(PostBeanMother.createPostListWithTwo());
		
		injector = new MockInjector();
	}
	
	@Test
	public void testExpandToggle(){
		PostCollectionPresenter postCollectionPresenter = injector.getPostCollectionPresenter();
		assertTrue("default state of expanded was false",postCollectionPresenter.isExpanded());
		
		//Simulating a click
		MockHasClickHandlers.clickMockButton(postCollectionPresenter.getView().getToggleExpandHandler());
		
		assertFalse("ExpaneToggleHandler failed",postCollectionPresenter.isExpanded());
		postCollectionPresenter.setPostList(list);
		//assertPostPresenterCount(postCollectionPresenter.getPostPresenters(),list.size());
	}
	
	@Test
	public void testPostCollection(){
		PostCollectionPresenter postCollectionPresenter = injector.getPostCollectionPresenter();
		postCollectionPresenter.setPostList(list);
		
		//TODO make the posts hierarchial and test that they have a widget organized that way
		
		//assertPostPresenterCount(postCollectionPresenter.getPostPresenters(),list.size());
	}

	
	//TODO: Test with MockHasWidgets

	private void assertPostPresenterCount(List<PostPresenter> list,int expected) {
		int count = 0;
		Iterator<PostPresenter> itr = list.iterator();
		while(itr.hasNext()){
			PostPresenter presenter = itr.next();
			count++;
		}
		
		assertEquals("BaseView has the wrong number of PostPresenters",expected,count);
	}
	
}
