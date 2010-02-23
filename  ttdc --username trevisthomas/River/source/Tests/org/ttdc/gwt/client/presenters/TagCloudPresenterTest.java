package org.ttdc.gwt.client.presenters;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.beans.PostBeanMother;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.presenters.post.TagCloudPresenter;

import static junit.framework.Assert.*;

public class TagCloudPresenterTest {
	MockInjector injector;
	private List<GTag> list;
	TagCloudPresenter.View tagCloudView;
	
	@Before 
	public void setup(){
		
		list = new ArrayList<GTag>();
		list.add(PostBeanMother.createTag("Test",TagConstants.TYPE_TOPIC));
		list.add(PostBeanMother.createTag("Anoother typic",TagConstants.TYPE_TOPIC));
		list.add(PostBeanMother.createTag("Some political or something",TagConstants.TYPE_TOPIC));
		
		injector = new MockInjector();
		
		tagCloudView = mock(TagCloudPresenter.View.class);
		injector.setTagCloudView(tagCloudView);
	}
	
	@Test
	public void testTagCloudBasic(){
		MockHasWidgets hasWidgets = new MockHasWidgets();
		when(tagCloudView.getTagWidgets()).thenReturn(hasWidgets);
		TagCloudPresenter presenter = injector.getTagCloudPresenter();
		
		presenter.setTagList(list);
		
		assertTrue("Hm the view didnt get the widgets...", hasWidgets.size() == list.size());
		
	}
	
	//Real test is to grab one of the things and click it, just to see that it is firing history
	
}
