package org.ttdc.gwt.client.presenters.topic;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class TopicPresenter extends BasePagePresenter<TopicPresenter.View> implements PostEventListener{
	@Inject
	public TopicPresenter(Injector injector) {
		super(injector, injector.getTopicView());
		EventBus.getInstance().addListener(this);
	}

	public interface View extends BasePageView{
		HasWidgets topicTarget();
	}

	@Override
	public void show(HistoryToken token) {
		//Decide which mode to use...
		
		//If there is a specific one on the history list use it, 
		//If not use the for the specific user
		//Else, use a canned default
		String topicView = token.getParameter(HistoryConstants.VIEW);
		if(HistoryConstants.VIEW_TOPIC_FLAT.equals(topicView)){
			showTopicFlat(token);
		}
		else if(HistoryConstants.VIEW_TOPIC_CONVERSATION.equals(topicView)){
			showTopicConversation(token);
		}
		else if(HistoryConstants.VIEW_TOPIC_HIERARCHY.equals(topicView)){
			
		}
		else if(HistoryConstants.VIEW_TOPIC_SUMMARY.equals(topicView)){
			
		}
		else if(HistoryConstants.VIEW_TOPIC_NESTED.equals(topicView)){
			showTopicNested(token);
		}
		else{
			showTopicNested(token); // Default.
		}
		
		
	}

	private void showTopicNested(HistoryToken token) {
		TopicNestedPresenter presenter = injector.getTopicNestedPresenter();
		presenter.init(token);
		view.topicTarget().add(presenter.getWidget());
		view.show();
	}

	private void showTopicConversation(HistoryToken token) {
		TopicConversationPresenter presenter = injector.getTopicConversationPresenter();
		presenter.init(token);
		view.topicTarget().add(presenter.getWidget());
		view.show();
	}

	private void showTopicFlat(HistoryToken token) {
		TopicFlatPresenter flatPresenter = injector.getTopicFlatPreseter();
		flatPresenter.init(token);
		view.topicTarget().add(flatPresenter.getWidget());
		view.show();
	}

	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.NEW_FORCE_REFRESH)){
			EventBus.reload();
		}
		else if(postEvent.is(PostEventType.NEW)){
			//This is a bit hacky... i'm just gonna refresh everything for now
			//GPost gPost = postEvent.getSource();
			//if(postEvent.getSource().equals(post))
			//Trevis, figure out a way to determine what post this thread is showing!!! 
			//This implementation will refresh for any new post!
			EventBus.reload();
		}
		
	}
}
