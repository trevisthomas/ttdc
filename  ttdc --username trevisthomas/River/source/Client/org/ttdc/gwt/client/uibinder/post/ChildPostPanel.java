package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.post.PostIconTool;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * This is the new non expanding summary post view that i am creating for flat mode.
 * @author Trevis
 *
 */
public class ChildPostPanel extends Composite implements PostEventListener, PostPresenterCommon{
	interface MyUiBinder extends UiBinder<Widget, ChildPostPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
//    @UiField (provided = true) Widget avatarElement;
    @UiField (provided = true) PostDetailPanel postDetailPanelElement;
    
    
    @UiField HTMLPanel outerElement;
    
    private HyperlinkPresenter creatorLinkPresenter;
    private HyperlinkPresenter replyTocreatorLinkPresenter;
//    private ImagePresenter imagePresenter;
    private HyperlinkPresenter createDatePresenter;
    private TagListPanel tagListPanel;
    
    PostIconTool postIconTool = new PostIconTool();
    
    @UiField(provided = true) FocusPanel hoverDivElement = new FocusPanel();
    @UiField SimplePanel likesElement;
    @UiField (provided = true) Widget tagsElement ;
    @UiField SimplePanel inReplyPostElement;
    @UiField (provided = true) Widget avatarElement;
    @UiField TableElement postTable;
    @UiField TableCellElement avatarCell;
    
    
	@UiField SpanElement bodyElement;
	@UiField SimplePanel commentElement;
	private Injector injector;
	private GPost post;
	private ImagePresenter creatorAvatorImagePresenter;
	
	@Inject
	public ChildPostPanel(Injector injector) {
		this.injector = injector;
//		imagePresenter = injector.getImagePresenter();
//    	
//    	imagePresenter = injector.getImagePresenter();
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	createDatePresenter = injector.getHyperlinkPresenter();
    	tagListPanel = injector.createTagListPanel();
    	replyTocreatorLinkPresenter = injector.getHyperlinkPresenter();
    	creatorAvatorImagePresenter = injector.getImagePresenter();
    	
    	avatarElement = creatorAvatorImagePresenter.getWidget();
    	
//    	avatarElement = imagePresenter.getWidget();
    	tagsElement = tagListPanel;
    	postDetailPanelElement = injector.createPostDetailPanel();
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	
    	//commentElement.setVisible(false);
    	
    	
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
		this.post = post;
		postDetailPanelElement.init(post, commentElement, tagListPanel, inReplyPostElement);
		refreshPost(post);
		EventBus.getInstance().addListener(this);
		inReplyPostElement.setVisible(false);
	}
	
	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.EDIT) && postEvent.getSource().getPostId().equals(getPost().getPostId())){
			refreshPost(postEvent.getSource());
		}
	}

	
	private void refreshPost(GPost post) {
		tagListPanel.init(post, TagListPanel.Mode.EDITABLE);
		
		GPerson replyToCreator = new GPerson();
		replyToCreator.setLogin(post.getParentPostCreator());
		replyToCreator.setPersonId(post.getParentPostCreatorId());
		replyTocreatorLinkPresenter.setPerson(replyToCreator);
		
		postIconTool.init(post);
		
		bodyElement.setInnerHTML(post.getEntry());
		
		creatorAvatorImagePresenter.useThumbnail(true);
		creatorAvatorImagePresenter.setImage(post.getCreator().getImage(), post.getCreator().getLogin(), 30, 30);
		creatorAvatorImagePresenter.init();
		
		
//		imagePresenter.setImage(post.getCreator().getImage(), post.getCreator().getLogin(), 20, 20);
//		avatarElement.setWidth("20px");
//		avatarElement.setHeight("20px");
		
//		imagePresenter.useThumbnail(true);
//		imagePresenter.init();
		createDatePresenter.setDate(post.getDate(), DateFormatUtil.longDateFormatter);
		creatorLinkPresenter.setPerson(post.getCreator());
		creatorLinkPresenter.init();
		
		PostPanelHelper.setupLikesElement(post, likesElement, injector);
		
		if(!ConnectionId.isAnonymous()){
			if(!post.isRead()){
				postTable.addClassName("tt-post-unread");
				avatarCell.addClassName("tt-post-unread-avatar");
			}
    	}
		
	}
	
	@Override
	public Widget getWidget() {
		return this;
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

	@Override
	public GPost getPost() {
		return post;
	}
}
