package org.ttdc.gwt.client.uibinder.post;


import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.post.PostIconTool;
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
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * This is the representation of an expanded reply. Thread roots and 
 * conversation starters dont use this class
 * 
 *
 */
@Deprecated
public class PostExpanded extends PostBaseComposite implements PostEventListener{
	interface MyUiBinder extends UiBinder<Widget, PostExpanded> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    @UiField (provided = true) Widget avatarElement;
    @UiField (provided = true) Widget createDateElement;
    @UiField (provided = true) Hyperlink creatorLinkElement;
    @UiField SimplePanel likesElement;
    @UiField (provided = true) Widget tagsElement;
    @UiField Anchor inReplyToElement;
    @UiField SimplePanel inReplyPostElement;
    
    
    private HyperlinkPresenter creatorLinkPresenter;
    
    private TagListPanel tagListPanel;
    private ImagePresenter imagePresenter;
    private HyperlinkPresenter createDatePresenter;
//    private MoreOptionsPopupPanel optionsPanel;
    
    PostIconTool postIconTool = new PostIconTool();
    
    @UiField(provided = true) Label postPrivateElement = postIconTool.getIconPrivate();
    @UiField(provided = true) Label postNwsElement = postIconTool.getIconNws();
    @UiField(provided = true) Label postInfElement = postIconTool.getIconInf();
    
    @UiField(provided = true) FocusPanel hoverDivElement = new FocusPanel();
    @UiField(provided = true) SimplePanel actionLinks = createPostActionLinks(hoverDivElement);    
    
	@UiField SpanElement bodyElement;
//	private GPost post;
//	private ClickHandler replyClickHandler = null;
	
	@Inject
	public PostExpanded(Injector injector) {
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
    	
    	inReplyPostElement.setVisible(false);
	}
	
	public void init(GPost post, HasWidgets commentElement){
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
		
		inReplyToElement.setHTML("&rArr; reply to "+post.getParentPostCreator());
		
		postIconTool.init(post);
		tagListPanel.init(post, TagListPanel.Mode.EDITABLE);
		
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
		
		PostPanelHelper.setupLikesElement(post, likesElement, injector);
		
		actionLinks.clear();
		//actionLinks.add(buildBoundOptionsListPanel(post));
		
	}
	
//	public void addReplyClickHandler(ClickHandler handler){
//		this.replyClickHandler = handler;
//	}
	
//	@UiHandler("moreOptionsElement")
//	void onClickMoreOptions(ClickEvent event){
//		Widget source = (Widget) event.getSource();
//        showMoreOptionsPopup(post, source);
//        
//	}
	
	/* TODO:
	 * 
	 * Remember this was copied from PostPanel. Refactor so that this logic only
	 * needs to exist on one place.
	 * 
	 * 
	 */
//	private void showMoreOptionsPopup(GPost post, Widget positionRelativeTo) {
//		optionsPanel = injector.createOptionsPanel();
//		optionsPanel.setAutoHideEnabled(true);
//		optionsPanel.showRelativeTo(positionRelativeTo);
//		if(replyClickHandler != null)
//			optionsPanel.addReplyClickHandler(replyClickHandler);
//		optionsPanel.init(post);
//		optionsPanel.show();
//	}
}
