package org.ttdc.gwt.client.uibinder.post;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.presenters.post.LikesPresenter;

import com.google.gwt.user.client.ui.SimplePanel;

public class PostPanelHelper {
	public static void setupLikesElement(GPost post, SimplePanel likesElement, Injector injector) {
		List<GAssociationPostTag> likeList = post.readTagAssociations(TagConstants.TYPE_LIKE);
		if(likeList.size() > 0){
			likesElement.setVisible(true);
			LikesPresenter likesPresenter = injector.getLikesPresenter();
			likesPresenter.init(likeList);
			likesElement.clear();
			likesElement.add(likesPresenter.getWidget());
		}
		else{
			likesElement.setVisible(false);
		}
	}
}
