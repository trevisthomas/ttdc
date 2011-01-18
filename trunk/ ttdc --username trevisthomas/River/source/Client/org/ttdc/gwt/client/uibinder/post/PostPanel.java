package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.movies.MovieRatingPresenter;
import org.ttdc.gwt.client.presenters.post.Mode;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostIconTool;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.topic.TopicHelpers;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.TopicCommand;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
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
public class PostPanel extends Composite implements PostPresenterCommon, PostEventListener, MessageEventListener, PersonEventListener{
    interface MyUiBinder extends UiBinder<Widget, PostPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private final Injector injector;
    //private GPost post;
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
    @UiField SimplePanel inReplyPostElement;
    private final PostIconTool postIconTool = new PostIconTool();
    @UiField TableElement postTable;
    @UiField TableCellElement avatarCell;
    

    @UiField (provided = true) PostDetailPanel postDetailPanelElement;    
    @UiField(provided = true) FocusPanel hoverDivElement = new FocusPanel();
        
    private Mode mode;
    private GPost post;
    
    @Inject
    public PostPanel(Injector injector) {
    	this.injector = injector;
    	
    	creatorAvatorImagePresenter = injector.getImagePresenter();
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	postLinkPresenter = injector.getHyperlinkPresenter();
    	createDatePresenter = injector.getHyperlinkPresenter();
    	postCollectionPresenter = injector.getPostCollectionPresenter();
    	postImagePresenter  = injector.getImagePresenter();
    	averageMovieRatingPresenter = injector.getMovieRatingPresenter();
    	tagListPanel = injector.createTagListPanel();
    	postDetailPanelElement = injector.createPostDetailPanel();
    	//injector.getTa
    	
    	tagsElement = new Label();
    	
    	avatarElement = creatorAvatorImagePresenter.getWidget();
    	titleElement = postLinkPresenter.getHyperlink();
    	repliesElement = postCollectionPresenter.getWidget();
    	postImageElement = postImagePresenter.getWidget();
    	ratingElement = averageMovieRatingPresenter.getWidget();
    	tagsElement = tagListPanel;
    	
    	//moreOptionsElement.setText("options");
    	//tagsElement
    	
    	initWidget(binder.createAndBindUi(this));
    	EventBus.getInstance().addListener((PostEventListener)this);
    	EventBus.getInstance().addListener((MessageEventListener)this);
    	EventBus.getInstance().addListener((PersonEventListener)this);
    	
    	
    	replyCountElement.setStyleName("tt-reply-count");
    	conversationCountElement.setStyleName("tt-conversation-count");

    }

	
    
    @Override
    public GPost getPost() {
    	return post;
    }
    
    public void setPost(GPost post) {
		setPost(post, Mode.FLAT);
	}

	public void setPost(GPost post, Mode mode) {
		postDetailPanelElement.init(post, commentElement, tagListPanel, inReplyPostElement);
		this.mode = mode;
		postCollectionPresenter.setConversationStarterPost(post, mode);
		
		if(!mode.equals(Mode.FLAT)){
			addStyleName("tt-post-conversation");
		}
		//this.post = post;
		this.post = post; 
		
		postIconTool.init(post);
		
		//postMainElement.getElement().setId(post.getPostId());
		
		replyCountElement.setText(""+post.getMass());
		
		String value = getFormattedConversationNumber(post);
		
		if(post.isRootPost()){
			conversationCountElement.setVisible(true);
			conversationCountElement.setText(""+post.getReplyCount());
			conversationCountElement.setTitle(post.getReplyCount() + " conversations within this topic.");
			replyCountElement.setTitle( post.getMass() + " total replies on this topic.");
		}
		else if(post.isThreadPost()){
			conversationCountElement.setVisible(false);
			if(post.getMass() == 0){
				replyCountElement.setTitle( "No replies in the "+value+" conversation.");
			}
			else if(post.getMass() == 1){
				replyCountElement.setTitle( "One reply in the "+value+" conversation.");
			}
			else{
				replyCountElement.setTitle( post.getMass() + " replies in the "+value+" conversation.");
			}
		}
		else{
			conversationCountElement.setVisible(false);
			if(post.getMass() == 0){
				replyCountElement.setTitle( "No replies.");
			}
			else if(post.getMass() == 1){
				replyCountElement.setTitle( "One reply.");
			}
			else{
				replyCountElement.setTitle( post.getMass() + " replies.");
			}
			
		}
		
		refreshPost(post, value);
		
		if(post.isReview()){
			postImagePresenter.setImageAsMoviePoster(post);
			postImagePresenter.init();
			postImageElement.setVisible(true);
			GAssociationPostTag ratingAss = post.getRoot().getRatingByPerson(post.getCreator().getPersonId());
			if(ratingAss != null){
				averageMovieRatingPresenter.setRating(ratingAss);
				ratingElement.setVisible(true);
			}
			else{
				ratingElement.setVisible(false);
			}
		}
		else{
			postImageElement.setVisible(false);
			ratingElement.setVisible(false);
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
			postCollectionPresenter.setPostList(post.getPosts(), mode);
			setupFetchMoreClickHandlerTitle();
			
//			//postCollectionPresenter = injector.getPostCollectionPresenter();
//			//postCollectionPresenter.setPostList(post.getPosts(), Mode.FLAT);
//			postCollectionPresenter.setPostList(post.getPosts(), mode);
//			
//			//A post will have only one child widget that widget will be 
//			//a widget containing all of the children
//			//view.getChildWidgetBucket().add(postCollectionPresenter.getWidget());
//			
//			if(Mode.NESTED_SUMMARY.equals(mode) && postCollectionPresenter.size() < post.getMass()){
//				
//				//setupFetchMoreClickHandlerTitle();
//				//view.fetchMoreTarget().addClickHandler(buildFetchMoreResultsClickHandler(cmd));
//				setupFetchMoreClickHandlerTitle();
//			}
//			
//			if(Mode.GROUPED.equals(mode) && postCollectionPresenter.size() < post.getMass()){
//				
//				//setupFetchMoreClickHandlerTitle();
//				//view.fetchMoreTarget().addClickHandler(buildFetchMoreResultsClickHandler(cmd));
//				setupFetchMoreClickHandlerTitle();
//			}
		}
		else{
			//Is this why i had those extra listeners?!
		//	postCollectionPresenter.setPostList(new ArrayList<GPost>(), mode);
		}
		
		
		//TODO secure
		tagListPanel.init(post, TagListPanel.Mode.EDITABLE);
		
		//Dont scroll to root posts. It's crazy making!
		if(!post.isRootPost())
			TopicHelpers.testPost(this);
		
		
		
	}



	private void refreshPost(GPost post, String conversationNumber) {
		bodyElement.setInnerHTML(post.getEntry());
		PostPanelHelper.setupLikesElement(post, likesElement, injector);
		creatorAvatorImagePresenter.setImage(post.getCreator().getImage(), post.getCreator().getLogin(), 40, 40);
		
		postLinkPresenter.setPost(post);
		postLinkPresenter.init();
		
		if(StringUtil.notEmpty(conversationNumber)){
			postNumberElement.setTitle(conversationNumber +" conversation in "+post.getTitle());
		}
		else{
			postNumberElement.setText("");
		}
		
		PostPanelHelper.highlightReadState(post,postTable,avatarCell);
		
//		if(!ConnectionId.isAnonymous()){
//			GPerson user = ConnectionId.getInstance().getCurrentUser();
//			if(post.isRead(user.getSiteReadDate())){
//				postTable.addClassName("tt-post-unread");
//				avatarCell.addClassName("tt-post-unread-avatar");
//			}
//    	}
	}

	private String getFormattedConversationNumber(GPost post) {
		if(!post.isThreadPost()){
			return "";
		}
		int conversationNumber = (1+Integer.parseInt(post.getPath()));
		postNumberElement.setText("#"+conversationNumber); //Path is the post number for these conversation staters!
		String suffix;
		if(conversationNumber == 1){
			suffix = "'st";
		}
		else if(conversationNumber == 2){
			suffix = "'nd";
		}
		else if(conversationNumber == 3){
			suffix = "'rd";				
		}
		else{
			suffix="'th";
		}
		String value = conversationNumber + suffix;
		return value;
	}

	
	
//	protected void showNewCommentEditor() {
//		NewCommentPresenter commentPresneter = injector.getNewCommentPresenter();
//		commentPresneter.init(post);
//		commentElement.clear();
//		commentElement.add(commentPresneter.getWidget());
//		
//	}

	private void setupFetchMoreClickHandlerTitle() {
		if(postCollectionPresenter.size() < getPost().getMass()){
			fetchMoreElement.setVisible(true);
//			fetchMoreElement.setText("Now showing "+postCollectionPresenter.size()+ " of "+post.getMass() +" comments. Click for more." );
			
				if(getPost().getReplyStartIndex() > 1){
					fetchMoreElement.setText("Now showing "+getPost().getReplyStartIndex()+" to "+(getPost().getReplyStartIndex()+postCollectionPresenter.size())
							+ " of "+getPost().getMass() +" comments. Click to expand." );
					if(childPostPage == 1)
						childPostPage = getPost().getReplyPage();
				}
				else{
					//fetchMoreElement.setText("Now showing "+postCollectionPresenter.size()+ " of "+getPost().getMass() +" comments. Click to expand." );
					fetchMoreElement.setTitle("Showing "+postCollectionPresenter.size()+ " of "+getPost().getMass() + " click for more");
					if(getPost().getMass() < 100){
						fetchMoreElement.setText("show all "+getPost().getMass());
					}
					else{
						fetchMoreElement.setText("show 100 of "+getPost().getMass());
					}
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
				postCollectionPresenter.setPostList(result.getResults().getList(), Mode.GROUPED);
				//setupFetchMoreClickHandlerTitle();
				fetchMoreElement.setText("");
				fetchMoreElement.setVisible(false);
			}
		};
		TopicCommand cmd = new TopicCommand();
		cmd.setPostId(getPost().getPostId());
		cmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY_FETCH_MORE);
		cmd.setPageNumber(-1);
		RpcServiceAsync service = injector.getService();
		service.execute(cmd,fetchMorePostsCallback);
		
	}
//	@UiHandler("replyLinkElement")
//	void onClickReplyLink(ClickEvent event){
//		showCommentEditor();
//	}
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
		if(postEvent.is(PostEventType.EDIT) && postEvent.getSource().getPostId().equals(getPost().getPostId())){
			String value = getFormattedConversationNumber(postEvent.getSource());
			refreshPost(postEvent.getSource(), value);
		}
	}
	
	@Override
	public void onMessageEvent(MessageEvent event) {
		if(event.is(MessageEventType.MARK_SITE_READ)){
			PostPanelHelper.highlightReadState(post,postTable,avatarCell);
		}
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.is(PersonEventType.USER_CHANGED)){
			PostPanelHelper.highlightReadState(post,postTable,avatarCell);
		}
	}
	

	@Override
	public String getPostId() {
		return getPost().getPostId();
	}
	
//	@UiHandler("moreLinkElement")
//	public void onClickMoreLink(ClickEvent e){
//		onClickMoreOptions(e);	
//	}
	
}
