package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PostSummaryPresenter extends BasePresenter<PostSummaryPresenter.View> implements PostPresenterCommon{
	private GPost post;
	
	@Inject
	public PostSummaryPresenter(Injector injector) {
		super(injector, injector.getPostSummaryView());
	}

	public interface View extends BaseView{
		HasClickHandlers toggleTarget();
		HasWidgets personTarget();
		HasText entrySummaryTarget();
		void setTabCount(int tabCount);
		void replaceMeWith(Widget w);
		void revert();
	}
	
	public void setPost(GPost post){
		this.post = post;
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_USER_PROFILE);
		token.setParameter(HistoryConstants.PERSON_ID, post.getCreator().getPersonId());
		HyperlinkPresenter personLink = injector.getHyperlinkPresenter();
		personLink.setToken(token, post.getCreator().getLogin());
		view.personTarget().add(personLink.getWidget());
		
		token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC);
		token.setParameter(HistoryConstants.POST_ID_KEY, post.getPostId());
		
		view.setTabCount(post.getPath().split("\\.").length - 2);
		//Hack for summary today
		String summary;
		if(post.getEntry().length() > 100){
			summary = post.getEntry().substring(0, 100);
		}
		else{
			summary = post.getEntry();
		}
		view.entrySummaryTarget().setText(summary);
		
		setupExpandPostClickHandler();
	}
	
	
	private void setupExpandPostClickHandler(){
		view.toggleTarget().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				expandPost();
			}
		});
	}
	
	private void notifyListeners(){
		PostEvent postEvent = new PostEvent(PostEventType.EXPAND_CONTRACT, post);
		EventBus.getInstance().fireEvent(postEvent);
	}
	
	public void expandPost() {
		RpcServiceAsync service = injector.getService();
		PostCrudCommand postCmd = new PostCrudCommand();
		postCmd.setPostId(post.getPostId());
		service.execute(postCmd,buildExpandedPostCallback());
	}
	
	public void contractPost() {
		view.revert();
	}
	
	private CommandResultCallback<PostCommandResult> buildExpandedPostCallback() {
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				notifyListeners();
				PostPresenter postPresenter = injector.getPostPresenter();
				postPresenter.setPost(result.getPost());
				view.replaceMeWith(postPresenter.getWidget());
			}
		};
		return rootPostCallback;
	}
	
}
