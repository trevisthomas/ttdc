package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.comments.NewCommentPresenter;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.post.PostPresenter.Mode;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
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
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PostPanel extends PostBaseComposite implements PostPresenterCommon{
    interface MyUiBinder extends UiBinder<Widget, PostPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private final Injector injector;
    private GPost post;
    private ImagePresenter imagePresenter;
    private HyperlinkPresenter creatorLinkPresenter;
    private HyperlinkPresenter postLinkPresenter;
    private DatePresenter createDatePresenter;
    private PostCollectionPresenter postCollectionPresenter;
    private int childPostPage = 1;
    	    
    @UiField(provided = true) Hyperlink titleElement;
    @UiField SpanElement replyCountElement;
    @UiField SpanElement bodyElement;
    @UiField(provided = true) Widget avatarElement;
    @UiField(provided = true) Hyperlink creatorLinkElement;
    @UiField(provided = true) Widget createDateElement;
    @UiField Anchor moreOptionsElement;
    @UiField Anchor fetchMoreElement;
    @UiField SpanElement embedTargetElement;
    @UiField(provided = true) SimplePanel commentElement = new SimplePanel();
    @UiField(provided = true) Widget repliesElement;
    
    @Inject
    public PostPanel(Injector injector) { 
    	super(injector);
    	this.injector = injector;
    	imagePresenter = injector.getImagePresenter();
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	postLinkPresenter = injector.getHyperlinkPresenter();
    	createDatePresenter = injector.getDatePresenter();
    	postCollectionPresenter = injector.getPostCollectionPresenter();
    	
    	avatarElement = imagePresenter.getWidget();
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	createDateElement = createDatePresenter.getWidget();
    	titleElement = postLinkPresenter.getHyperlink();
    	repliesElement = postCollectionPresenter.getWidget();
    	
    	initWidget(binder.createAndBindUi(this)); 
    }
    
    public void setPost(GPost post) {
		setPost(post, Mode.FLAT);
	}

	public void setPost(GPost post, Mode mode) {
		super.init(post, commentElement);
		this.post = post;
		//titleElement.setInnerHTML(post.getTitle());
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
		createDatePresenter.init(post.getDate());
		creatorLinkPresenter.setPerson(post.getCreator());
		creatorLinkPresenter.init();
		
		postLinkPresenter.setPost(post);
		postLinkPresenter.init();
		
		moreOptionsElement.setText("> More Options");
		moreOptionsElement.setStyleName("tt-cursor-pointer");
		
		embedTargetElement.setId(post.getPostId());
					
		if(post.getPosts().size() != 0){
			//postCollectionPresenter = injector.getPostCollectionPresenter();
			postCollectionPresenter.setPostList(post.getPosts());
			//A post will have only one child widget that widget will be 
			//a widget containing all of the children
			//view.getChildWidgetBucket().add(postCollectionPresenter.getWidget());
			
			if(Mode.NESTED_SUMMARY.equals(mode) && postCollectionPresenter.size() < post.getMass()){
				
				//setupFetchMoreClickHandlerTitle();
				//view.fetchMoreTarget().addClickHandler(buildFetchMoreResultsClickHandler(cmd));
				setupFetchMoreClickHandlerTitle();
			}
		}
		
		
	}
	
	protected void showNewCommentEditor() {
		NewCommentPresenter commentPresneter = injector.getNewCommentPresenter();
		commentPresneter.init(post);
		commentElement.clear();
		commentElement.add(commentPresneter.getWidget());
		
	}

	private void setupFetchMoreClickHandlerTitle() {
		if(postCollectionPresenter.size() < post.getMass()){
			fetchMoreElement.setVisible(true);
			fetchMoreElement.setText("Now showing "+postCollectionPresenter.size()+ " of "+post.getMass() +" comments. Click for more." );
		}
		else{
			fetchMoreElement.setText("");
			fetchMoreElement.setVisible(false);
		}
	}

	@UiHandler("fetchMoreElement")
	void onClickFetchMore(ClickEvent event){
		CommandResultCallback<TopicCommandResult> fetchMorePostsCallback = new CommandResultCallback<TopicCommandResult>(){
			@Override
			public void onSuccess(TopicCommandResult result) {
				postCollectionPresenter.insertPostsToPostList(result.getResults().getList(), Mode.NESTED_SUMMARY);
				setupFetchMoreClickHandlerTitle();
			}
		};
		TopicCommand cmd = new TopicCommand();
		cmd.setPostId(post.getPostId());
		cmd.setType(TopicCommandType.NESTED_THREAD_SUMMARY_FETCH_MORE);
		cmd.setPageNumber(++childPostPage);
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
}
