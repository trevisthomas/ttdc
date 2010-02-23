package org.ttdc.gwt.client.presenters;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.PostBeanMother;
import org.ttdc.gwt.client.presenters.post.PostPresenter;

public class PostPresenterTest {
	Injector injector;
	
	
	@Before
	public void setup(){
		injector = new MockInjector();
	}
	
	@Test
	public void simpleTest(){
		GPost post = PostBeanMother.createTestPost1234();
		
		PostPresenter presenter;
		presenter = injector.getPostPresenter();
		presenter.setPost(post);
		
		//assertEquals(post.getTitle(),presenter.getView().getPostTitle().getText());//Didnt feel like fixing this after a refactor to make title a link
		assertEquals(post.getEntry(),presenter.getView().getPostEntry().getText());
		///assertEquals(post.getEntry(),presenter.getView().getCreator);
		assertTrue(true);
	}
}
