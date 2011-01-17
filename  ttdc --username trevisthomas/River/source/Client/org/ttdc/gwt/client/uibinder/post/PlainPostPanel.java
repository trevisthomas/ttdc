package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PlainPostPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, PlainPostPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private HyperlinkPresenter createDatePresenter;
    private HyperlinkPresenter creatorPresenter;
    private HyperlinkPresenter replyTocreatorLinkPresenter;
    
    private Injector injector;
    
    @UiField SpanElement bodyElement;
    @UiField Label headerLabelElement;
    @UiField HTMLPanel containerElement;
    @UiField Anchor closeElement;
    
    @UiField (provided = true) Hyperlink inReplyToCreatorElement;
    @UiField Anchor inReplyToElement;
    @UiField SpanElement inResposeWrapperElement;
    
    @UiField SimplePanel inReplyPostTarget; 
    @UiField (provided = true) Hyperlink creatorElement;
    @UiField (provided = true) Hyperlink dateElement;
    private GPost post;
    
    
    @Inject
    public PlainPostPanel(Injector injector) { 
    	this.injector = injector;
    	createDatePresenter = injector.getHyperlinkPresenter();
    	creatorPresenter = injector.getHyperlinkPresenter();
    	replyTocreatorLinkPresenter = injector.getHyperlinkPresenter();
    	
    	creatorElement = creatorPresenter.getHyperlink();
    	dateElement = createDatePresenter.getHyperlink();
    	
    	inReplyToCreatorElement = replyTocreatorLinkPresenter.getHyperlink();
    	
    	initWidget(binder.createAndBindUi(this)); 
    	
    	
	}
    
    public void init(GPost post){
    	this.post = post;
    	headerLabelElement.setText("in reply to: ");
    	
    	createDatePresenter.setDate(post.getDate(), DateFormatUtil.longDateFormatter);
    	creatorPresenter.setPerson(post.getCreator());
    	
    	bodyElement.setInnerHTML(post.getEntry());
    	
    	if(PostDetailPanel.isInResponseToAvailable(post)){
			inReplyToElement.setHTML("response");
			inReplyToElement.setTitle("Click to view in reply to");
			
			GPerson replyToCreator = new GPerson();
			replyToCreator.setLogin(post.getParentPostCreator());
			replyToCreator.setPersonId(post.getParentPostCreatorId());
			replyTocreatorLinkPresenter.setPerson(replyToCreator);
		}
    	else{
    		inResposeWrapperElement.removeFromParent();
    	}
    }

    @UiHandler("closeElement")
    public void onClickClose(ClickEvent event){
    	removeFromParent();
    }
    
//    
    @UiHandler("inReplyToElement")
	public void onClickInReplyTo(ClickEvent e){
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.READ);
		cmd.setPostId(post.getParentPostId());
		
		CommandResultCallback<PostCommandResult> callback = buildInReplyPostCallback();
		injector.getService().execute(cmd,callback);
	}
	
	private CommandResultCallback<PostCommandResult> buildInReplyPostCallback() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				// create the inreply dealy and show it
				PlainPostPanel panel = injector.createPlainPostPanel();
				panel.init(result.getPost());
				inReplyPostTarget.setVisible(true);
				inReplyPostTarget.clear();
				inReplyPostTarget.add(panel);
			}
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}
		};
		return callback;
	}
}
