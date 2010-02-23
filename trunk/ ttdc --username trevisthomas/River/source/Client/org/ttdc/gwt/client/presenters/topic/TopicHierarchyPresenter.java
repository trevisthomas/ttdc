package org.ttdc.gwt.client.presenters.topic;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class TopicHierarchyPresenter extends BasePresenter<TopicHierarchyPresenter.View>{
	@Inject
	public TopicHierarchyPresenter(Injector injector) {
		super(injector, injector.getTopicHierarchyView());
	}
	public interface View extends BaseView{
		HasWidgets postsTarget();
		HasText threadTitle();
	}

}
