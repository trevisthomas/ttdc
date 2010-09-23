package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * This is the new non expanding summary post view that i am creating for flat mode.
 * @author Trevis
 *
 */
public class ChildPostPanel extends PostBaseComposite implements PostEventListener, PostPresenterCommon{
	interface MyUiBinder extends UiBinder<Widget, ChildPostPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    @UiField (provided = true) Widget avatarElement;
    @UiField (provided = true) Widget createDateElement;
    @UiField (provided = true) Hyperlink creatorLinkElement;
    @UiField SimplePanel likesElement;
    @UiField Anchor inReplyToElement;
    @UiField SimplePanel inReplyPostElement;
    @UiField (provided = true) Widget tagsElement;
    @UiField HTMLPanel outerElement;
    
    
    private HyperlinkPresenter creatorLinkPresenter;
    
    private ImagePresenter imagePresenter;
    private HyperlinkPresenter createDatePresenter;
    private TagListPanel tagListPanel;
    
    PostIconTool postIconTool = new PostIconTool();
    
    @UiField(provided = true) Label postPrivateElement = postIconTool.getIconPrivate();
    @UiField(provided = true) Label postNwsElement = postIconTool.getIconNws();
    @UiField(provided = true) Label postInfElement = postIconTool.getIconInf();
    @UiField(provided = true) Label postUnReadElement = postIconTool.getIconUnread();
    @UiField(provided = true) Label postReadElement = postIconTool.getIconRead();
    
    @UiField(provided = true) FocusPanel hoverDivElement = new FocusPanel();
    @UiField(provided = true) SimplePanel actionLinks = createPostActionLinks(hoverDivElement);    
    
	@UiField SpanElement bodyElement;
	@UiField SimplePanel commentElement;
	
	@Inject
	public ChildPostPanel(Injector injector) {
		super(injector);
		imagePresenter = injector.getImagePresenter();
    	
    	imagePresenter = injector.getImagePresenter();
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	createDatePresenter = injector.getHyperlinkPresenter();
    	tagListPanel = injector.createTagListPanel();
    	
    	tagsElement = tagListPanel;
    	
    	
    	avatarElement = imagePresenter.getWidget();
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	createDateElement = createDatePresenter.getWidget();
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	postUnReadElement.addStyleName("tt-float-left");
    	postReadElement.addStyleName("tt-float-left");
    	
    	//commentElement.setVisible(false);
    	inReplyPostElement.setVisible(false);
    	
    	outerElement.addStyleName("tt-post-child-noHover");
    	hoverDivElement.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				outerElement.addStyleName("tt-post-child-hover");
				outerElement.removeStyleName("tt-post-child-noHover");
			}
		});
    	
    	hoverDivElement.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				outerElement.addStyleName("tt-post-child-noHover");
				outerElement.removeStyleName("tt-post-child-hover");
			}
		});
    	
    	
	}
	
	public void init(GPost post){
		super.init(post, commentElement, tagListPanel);
		refreshPost(post);
		EventBus.getInstance().addListener(this);
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
				inReplyPostElement.setVisible(true);
				inReplyPostElement.clear();
				inReplyPostElement.add(panel);
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
		tagListPanel.init(post, TagListPanel.Mode.EDITABLE);
		
		inReplyToElement.setHTML("@"+post.getParentPostCreator());
		inReplyToElement.setTitle("Click to view in reply to");
		
		postIconTool.init(post);
		
		bodyElement.setInnerHTML(post.getEntry());
		
		if(post.isRootPost() || post.isThreadPost()){
			imagePresenter.setImage(post.getCreator().getImage(), post.getCreator().getLogin(), 50, 50);
			avatarElement.setWidth("50px");
			avatarElement.setHeight("50px");
		}
		else{
			imagePresenter.setImage(post.getCreator().getImage(), post.getCreator().getLogin(), 20, 20);
			avatarElement.setWidth("20px");
			avatarElement.setHeight("20px");
		}
		imagePresenter.useThumbnail(true);
		imagePresenter.init();
		createDatePresenter.setDate(post.getDate(), DateFormatUtil.longDateFormatter);
		creatorLinkPresenter.setPerson(post.getCreator());
		creatorLinkPresenter.init();
		
		setupLikesElement(post, likesElement);
		
		actionLinks.clear();
		actionLinks.add(buildBoundOptionsListPanel(post));
	}

	@Override
	public void contractPost() {
		//Deprecated?
	}

	@Override
	public String getPostId() {
		return getPost().getPostId();
	}

	@Override
	public void expandPost() {
		//Deprecated?
	}
	
	@Override
	public Widget getWidget() {
		return this;
	}
}