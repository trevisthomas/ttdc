package org.ttdc.gwt.client.uibinder.post;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.comments.NewCommentPresenter;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.post.LikesPresenter;
import org.ttdc.gwt.client.presenters.post.Mode;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostIconTool;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.topic.TopicHelpers;
import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
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
 * 
 * This class represents conversation starters and thread roots but not movie roots.  Those are custom
 *
 */
public class PostPanel extends PostBaseComposite implements PostPresenterCommon, PostEventListener{
    interface MyUiBinder extends UiBinder<Widget, PostPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private final Injector injector;
    private GPost post;
    private ImagePresenter creatorAvatorImagePresenter;
    private ImagePresenter postImagePresenter;
    private HyperlinkPresenter creatorLinkPresenter;
    private HyperlinkPresenter postLinkPresenter;
    //private DatePresenter createDatePresenter;
    private HyperlinkPresenter createDatePresenter;
    private PostCollectionPresenter postCollectionPresenter;
    private MovieRatingPresenter averageMovieRatingPresenter;
    private TagListPanel tagListPanel;
    private int childPostPage = 1;
    	    
    @UiField(provided = true) Hyperlink titleElement;
    @UiField Label replyCountElement;
    @UiField Label conversationCountElement;
    @UiField SpanElement bodyElement;
    @UiField(provided = true) Widget avatarElement;
    @UiField(provided = true) Hyperlink creatorLinkElement;
    @UiField(provided = true) Widget createDateElement;
    @UiField Anchor moreOptionsElement;
    @UiField Anchor fetchMoreElement;
    @UiField SpanElement embedTargetElement;
    @UiField(provided = true) SimplePanel commentElement = new SimplePanel();
    @UiField(provided = true) Widget repliesElement;
    @UiField(provided = true) Widget postImageElement;
    @UiField(provided = true) Widget ratingElement;
    @UiField(provided = true) Widget tagsElement;
    @UiField Label postNumberElement;
    @UiField SimplePanel likesElement;
    @UiField HTMLPanel postMainElement;
    private final PostIconTool postIconTool = new PostIconTool();
    
    @UiField(provided = true) Label postUnReadElement = postIconTool.getIconUnread();
    @UiField(provided = true) Label postReadElement = postIconTool.getIconRead();
    @UiField(provided = true) Label postPrivateElement = postIconTool.getIconPrivate();
    @UiField(provided = true) Label postNwsElement = postIconTool.getIconNws();
    @UiField(provided = true) Label postInfElement = postIconTool.getIconInf();
    
    private Mode mode;
    
    @Inject
    public PostPanel(Injector injector) {
    	super(injector);
    	this.injector = injector;
    	
    	creatorAvatorImagePresenter = injector.getImagePresenter();
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	postLinkPresenter = injector.getHyperlinkPresenter();
    	createDatePresenter = injector.getHyperlinkPresenter();
    	postCollectionPresenter = injector.getPostCollectionPresenter();
    	postImagePresenter  = injector.getImagePresenter();
    	averageMovieRatingPresenter = injector.getMovieRatingPresenter();
    	tagListPanel = injector.createTagListPanel();
    	//injector.getTa
    	
    	tagsElement = new Label();
    	
    	avatarElement = creatorAvatorImagePresenter.getWidget();
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	createDateElement = createDatePresenter.getWidget();
    	titleElement = postLinkPresenter.getHyperlink();
    	repliesElement = postCollectionPresenter.getWidget();
    	postImageElement = postImagePresenter.getWidget();
    	ratingElement = averageMovieRatingPresenter.getWidget();
    	tagsElement = tagListPanel;
    	
    	//moreOptionsElement = new ClickableHoverSyncPanel("tt-color-options-button","tt-color-options-button-hover");
    	
    	//moreOptionsElement.setText("options");
    	//tagsElement
    	
    	initWidget(binder.createAndBindUi(this));
    	EventBus.getInstance().addListener(this);
    	
    	
    	replyCountElement.setStyleName("tt-reply-count");
    	conversationCountElement.setStyleName("tt-conversation-count");
    	
//    	moreOptionsElement.add(new Label("OPTIONS"));
//		moreOptionsElement.addStyleName("tt-options-button");
    	
    	postUnReadElement.addStyleName("tt-float-left");
    	postReadElement.addStyleName("tt-float-left");
    }
    
    public void setPost(GPost post) {
		setPost(post, Mode.FLAT);
	}

	public void setPost(GPost post, Mode mode) {
		super.init(post, commentElement, tagListPanel);
		this.mode = mode;
		this.post = post;
		
		postIconTool.init(post);
		
		//postMainElement.getElement().setId(post.getPostId());
		
		replyCountElement.setText(""+post.getMass());
		
		replyCountElement.setTitle( post.getMass() + " replies in this conversation.");
		if(post.isRootPost()){
			conversationCountElement.setVisible(true);
			conversationCountElement.setText(""+post.getReplyCount());
			conversationCountElement.setTitle(post.getReplyCount() + " conversations within this topic.");
		}
		else{
			conversationCountElement.setVisible(false);
		}
		
		bodyElement.setInnerHTML(post.getEntry());
		
//		if(post.isRootPost() || post.isThreadPost()){
//			creatorAvatorImagePresenter.setImage(post.getCreator().getImage(), post.getCreator().getLogin(), 50, 50);
//			avatarElement.setWidth("50px");
//			avatarElement.setHeight("50px");
//		}
//		else{
//			creatorAvatorImagePresenter.setImage(post.getCreator().getImage(), post.getCreator().getLogin(), 20, 20);
//			avatarElement.setWidth("20px");
//			avatarElement.setHeight("20px");
//		}
		
		creatorAvatorImagePresenter.setImage(post.getCreator().getImage(), post.getCreator().getLogin(), 40, 40);
//		avatarElement.setWidth("40px");
//		avatarElement.setHeight("40px");
		
		if(post.isReview()){
			postImagePresenter.setImageAsMoviePoster(post);
			postImagePresenter.init();
			GAssociationPostTag ratingAss = post.getParent().getRatingByPerson(post.getCreator().getPersonId());
			if(ratingAss != null){
				averageMovieRatingPresenter.setRating(ratingAss);
			}
			else{
				ratingElement.setVisible(false);
			}
		}
		else{
			postImageElement.setVisible(false);
		}
		
		creatorAvatorImagePresenter.useThumbnail(true);
		creatorAvatorImagePresenter.init();
		//createDatePresenter.init(post.getDate());
		createDatePresenter.setDate(post.getDate(), DateFormatUtil.longDateFormatter);
		creatorLinkPresenter.setPerson(post.getCreator());
		creatorLinkPresenter.init();
		
		postLinkPresenter.setPost(post);
		postLinkPresenter.init();
		
//		moreOptionsElement.setText("More Options");
//		moreOptionsElement.setStyleName("tt-cursor-pointer tt-text-small");
		
		
		
		embedTargetElement.setId(post.getPostId());
							
		if(post.getPosts().size() != 0){
			//postCollectionPresenter = injector.getPostCollectionPresenter();
			postCollectionPresenter.setPostList(post.getPosts(), Mode.FLAT);
			//A post will have only one child widget that widget will be 
			//a widget containing all of the children
			//view.getChildWidgetBucket().add(postCollectionPresenter.getWidget());
			
			if(Mode.NESTED_SUMMARY.equals(mode) && postCollectionPresenter.size() < post.getMass()){
				
				//setupFetchMoreClickHandlerTitle();
				//view.fetchMoreTarget().addClickHandler(buildFetchMoreResultsClickHandler(cmd));
				setupFetchMoreClickHandlerTitle();
			}
		}
		
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		setupLikesElement(post, likesElement);
		
		if(post.isThreadPost()){
			postNumberElement.setText("#"+(1+Integer.parseInt(post.getPath()))); //Path is the post number for these conversation staters!
		}
		else{
			postNumberElement.setText("");
		}
		
		//TODO secure
		tagListPanel.init(post, TagListPanel.Mode.EDITABLE);
		
		//Dont scroll to root posts. It's crazy making!
		if(!post.isRootPost())
			TopicHelpers.testPost(this);
		
	}

	
	
//	protected void showNewCommentEditor() {
//		NewCommentPresenter commentPresneter = injector.getNewCommentPresenter();
//		commentPresneter.init(post);
//		commentElement.clear();
//		commentElement.add(commentPresneter.getWidget());
//		
//	}

	private void setupFetchMoreClickHandlerTitle() {
		if(postCollectionPresenter.size() < post.getMass()){
			fetchMoreElement.setVisible(true);
//			fetchMoreElement.setText("Now showing "+postCollectionPresenter.size()+ " of "+post.getMass() +" comments. Click for more." );
			
				if(post.getReplyStartIndex() > 1){
					fetchMoreElement.setText("Now showing "+post.getReplyStartIndex()+" to "+(post.getReplyStartIndex()+postCollectionPresenter.size())
							+ " of "+post.getMass() +" comments. Click to expand." );
					if(childPostPage == 1)
						childPostPage = post.getReplyPage();
				}
				else{
					fetchMoreElement.setText("Now showing "+postCollectionPresenter.size()+ " of "+post.getMass() +" comments. Click to expand." );
				}
			
		}
		else{
			fetchMoreElement.setText("");
			fetchMoreElement.setVisible(false);
		}
	}

//	@UiHandler("fetchMoreElement")
//	void onClickFetchMore(ClickEvent event){
//		CommandResultCallback<TopicCommandResult> fetchMorePostsCallback = new CommandResultCallback<TopicCommandResult>(){
//			@Override
//			public void onSuccess(TopicCommandResult result) {
//				postCollectionPresenter.insertPostsToPostList(result.getResults().getList(), Mode.NESTED_SUMMARY);
//				setupFetchMoreClickHandlerTitle();
//			}
//		};
//		TopicCommand cmd = new TopicCommand();
//		cmd.setPostId(post.getPostId());
//		cmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY_FETCH_MORE);
//		cmd.setPageNumber(++childPostPage);
//		RpcServiceAsync service = injector.getService();
//		service.execute(cmd,fetchMorePostsCallback);
//	}
	
	@UiHandler("fetchMoreElement")
	void onClickFetchMore(ClickEvent event){
		CommandResultCallback<TopicCommandResult> fetchMorePostsCallback = new CommandResultCallback<TopicCommandResult>(){
			@Override
			public void onSuccess(TopicCommandResult result) {
				//postCollectionPresenter.insertPostsToPostList(result.getResults().getList(), Mode.NESTED_SUMMARY);
				postCollectionPresenter.setPostList(result.getResults().getList(), Mode.NESTED_SUMMARY);
				//setupFetchMoreClickHandlerTitle();
				fetchMoreElement.setText("");
				fetchMoreElement.setVisible(false);
			}
		};
		TopicCommand cmd = new TopicCommand();
		cmd.setPostId(post.getPostId());
		cmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY_FETCH_MORE);
		cmd.setPageNumber(-1);
		RpcServiceAsync service = injector.getService();
		service.execute(cmd,fetchMorePostsCallback);
		
	}
	
	@Override
	public Widget getWidget() {
		return this;
	}
	
	@Override
	public void contractPost() {
		//hmm, no impl?
	}
	
	@Override
	public void expandPost() {
		//hmm, no impl?
	}

	
	//Trevis This hasnt really been tested!!
	@Override
	public void onPostEvent(PostEvent postEvent) {
		if(postEvent.is(PostEventType.EDIT) && postEvent.getSource().getPostId().equals(post.getPostId())){
			setPost(postEvent.getSource(),mode);
		}
	}
	
	@Override
	public String getPostId() {
		return post.getPostId();
	}
	
}
