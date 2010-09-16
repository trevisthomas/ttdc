package org.ttdc.gwt.client.presenters.post;

import java.util.Comparator;

import org.ttdc.gwt.client.beans.GPost;

import com.google.gwt.user.client.ui.Widget;

public interface PostPresenterCommon {
	
	public static class PostPresenterComparitorByPath implements Comparator<PostPresenterCommon>{
		@Override
		public int compare(PostPresenterCommon o1, PostPresenterCommon o2) {
			return o1.getPost().getPath().compareTo(o2.getPost().getPath());
		}
	}
	
	public Widget getWidget();
	public void contractPost();
	public String getPostId();
	public void expandPost();
	public GPost getPost();
}

