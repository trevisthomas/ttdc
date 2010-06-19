package org.ttdc.gwt.client.presenters.post;


import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class LikesPresenter extends BasePresenter<LikesPresenter.View> /*implements TagEventListener*/{
	public interface View extends BaseView{
		HasWidgets getLikersTaget();
		void addLike(List<Widget> likers);
	}
	
	@Inject
	public LikesPresenter(Injector injector){
		super(injector,injector.getLikesView());
	}

	public void init(List<GAssociationPostTag> likeAssociations){
		List<Widget> likers = new ArrayList<Widget>();
		for(GAssociationPostTag ass : likeAssociations){
			HyperlinkPresenter hyperLinkPresenter = injector.getHyperlinkPresenter();
			hyperLinkPresenter.setPerson(ass.getCreator());
			hyperLinkPresenter.init();
			likers.add(hyperLinkPresenter.getWidget());
		}
		
		view.addLike(likers);
	}
}
