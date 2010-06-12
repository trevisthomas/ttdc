package org.ttdc.gwt.client.uibinder.dashboard;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.constants.UserObjectConstants;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.UserObjectCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class FilteredPost extends Composite {
	interface MyUiBinder extends UiBinder<Widget, FilteredPost> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private HyperlinkPresenter threadLinkPresenter;
	private Injector injector;
	private GPost post;
	
	@UiField(provided = true) Hyperlink threadElement;
	@UiField(provided = true) Button unMuteButton;
	
	@Inject
	public FilteredPost(Injector injector) {
		this.injector = injector;
		
		threadLinkPresenter = injector.getHyperlinkPresenter();
		threadElement = threadLinkPresenter.getHyperlink();
		unMuteButton = new Button();
		unMuteButton.setText("Un Mute");
		
		initWidget(binder.createAndBindUi(this));
	}
	
	@UiHandler("unMuteButton")
	void onClickUnMute(ClickEvent event){
		UserObjectCrudCommand cmd = new UserObjectCrudCommand();
		cmd.setType(UserObjectConstants.TYPE_FILTER_THREAD);
		cmd.setAction(ActionType.DELETE);
		cmd.setValue(post.getRoot().getPostId());
		
		injector.getService().execute(cmd, createPostUnMuteCallback());		
	}
	
	public void init(GPost post){
		threadLinkPresenter.setPost(post);
		threadLinkPresenter.init();
		this.post = post;
	}
	
	private CommandResultCallback<GenericCommandResult<GUserObject>> createPostUnMuteCallback() {
		return new CommandResultCallback<GenericCommandResult<GUserObject>>(){
				@Override
				public void onSuccess(GenericCommandResult<GUserObject> result) {
					removeFromParent();
				}
			};
	}

}
