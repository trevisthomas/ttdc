package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.post.PostIconTool;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
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
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PostDetailPanel extends PostBaseComposite implements PostEventListener, PostPresenterCommon{
	interface MyUiBinder extends UiBinder<Widget, PostDetailPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    @UiField (provided = true) Widget createDateElement;
    @UiField (provided = true) Hyperlink creatorLinkElement;
    @UiField (provided = true) Hyperlink inReplyToCreatorElement;
    
    
    @UiField Anchor inReplyToElement;
    
    private HyperlinkPresenter creatorLinkPresenter;
    private HyperlinkPresenter replyTocreatorLinkPresenter;
    private ImagePresenter imagePresenter;
    private HyperlinkPresenter createDatePresenter;
    
    private SimplePanel inReplyPostTarget;
    
    PostIconTool postIconTool = new PostIconTool();
    
    @UiField(provided = true) Label postPrivateElement = postIconTool.getIconPrivate();
    @UiField(provided = true) Label postNwsElement = postIconTool.getIconNws();
    @UiField(provided = true) Label postInfElement = postIconTool.getIconInf();
    @UiField(provided = true) Label postUnReadElement = postIconTool.getIconUnread();
    @UiField(provided = true) Label postReadElement = postIconTool.getIconRead();
    @UiField SimplePanel actionLinks;
    @UiField SpanElement inResposeWrapperElement;
    @Inject
	public PostDetailPanel(Injector injector) {
		super(injector);
		imagePresenter = injector.getImagePresenter();
    	
    	imagePresenter = injector.getImagePresenter();
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	createDatePresenter = injector.getHyperlinkPresenter();
    	replyTocreatorLinkPresenter = injector.getHyperlinkPresenter();
    	
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	createDateElement = createDatePresenter.getWidget();
    	inReplyToCreatorElement = replyTocreatorLinkPresenter.getHyperlink();
    	
    	initWidget(binder.createAndBindUi(this));
    	
//    	postUnReadElement.addStyleName("tt-float-left");
//    	postReadElement.addStyleName("tt-float-left");
	}
	
	public void init(GPost post, HasWidgets commentElement, TagListPanel tagListPanel, SimplePanel inReplyPostTarget){
		super.init(post, commentElement, tagListPanel);
		refreshPost(post);
		EventBus.getInstance().addListener(this);
		this.inReplyPostTarget = inReplyPostTarget;
		//inReplyPostTarget.setVisible(false);
		
		if(!isInResponseToAvailable(post)){
			inResposeWrapperElement.removeFromParent();
		}
	}
	
	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.EDIT) && postEvent.getSource().getPostId().equals(getPost().getPostId())){
			refreshPost(postEvent.getSource());
		}
	}

	@UiHandler("inReplyToElement")
	public void onClickInReplyTo(ClickEvent e){
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.READ);
		cmd.setPostId(getPost().getParentPostId());
		
		CommandResultCallback<PostCommandResult> callback = buildEditPostCallback();
		injector.getService().execute(cmd,callback);
	}
	
	private CommandResultCallback<PostCommandResult> buildEditPostCallback() {
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
	
	private void refreshPost(GPost post) {
		setPost(post);
		
		if(isInResponseToAvailable(post)){
			inReplyToElement.setHTML("response");
			inReplyToElement.setTitle("Click to view in reply to");
			
			GPerson replyToCreator = new GPerson();
			replyToCreator.setLogin(post.getParentPostCreator());
			replyToCreator.setPersonId(post.getParentPostCreatorId());
			replyTocreatorLinkPresenter.setPerson(replyToCreator);
		}
		
		postIconTool.init(post);
		
		imagePresenter.useThumbnail(true);
		imagePresenter.init();
		createDatePresenter.setDate(post.getDate(), DateFormatUtil.longDateFormatter);
		creatorLinkPresenter.setPerson(post.getCreator());
		creatorLinkPresenter.init();
		
		actionLinks.clear();
		actionLinks.add(buildBoundOptionsIconPanel(post));
	}

	private boolean isInResponseToAvailable(GPost post) {
		return !(post.isRootPost() || post.isThreadPost());
	}
	
	@Override
	public Widget getWidget() {
		return super.getWidget();
	}

	@Override
	public void contractPost() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPostId() {
		return getPost().getPostId();
	}

	@Override
	public void expandPost() {
		// TODO Auto-generated method stub
		
	}

}
