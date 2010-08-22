package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.topic.TopicHelpers;
import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.calender.CalendarPost;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PostSummaryPanel extends Composite implements PostPresenterCommon{
	interface MyUiBinder extends UiBinder<Widget, PostSummaryPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    private HyperlinkPresenter creatorLinkPresenter;
    private PostExpanded postExpanded;
    private GPost post;
    private CalendarPost cp;
    @UiField(provided = true) Hyperlink creatorLinkElement;
    @UiField HTML bodySummaryElement;
    @UiField SpanElement spacerElement;
    @UiField HTMLPanel summaryElement;
    @UiField(provided = true) SimplePanel expandedElement;
    @UiField(provided = true) SimplePanel commentElement = new SimplePanel();
    @UiField Label postUnReadElement;
    @UiField HTMLPanel parentElement;
    @UiField(provided = true) ClickableHoverSyncPanel hoverTargetElement;
    
    @Inject
    public PostSummaryPanel(Injector injector) { 
    	this.injector = injector;
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	expandedElement = new SimplePanel();
    	
    	hoverTargetElement = new ClickableHoverSyncPanel("tt-color-post-summary","tt-color-post-summary-hover");
//    	hoverTargetElement.addStyleName("tt-inline");
    	initWidget(binder.createAndBindUi(this)); 
	}
    
    public void init(GPost post){
    	this.post = post;
    	bodySummaryElement.setHTML(post.getLatestEntry().getSummary());
    	creatorLinkPresenter.setPerson(post.getCreator());
    	setSpacer(post.getPath().split("\\.").length - 2);
    	
    	GPerson user = ConnectionId.getInstance().getCurrentUser();
		if(!user.isAnonymous() && !post.isRead()){
			postUnReadElement.setVisible(true);
			postUnReadElement.setText("*");
			postUnReadElement.addStyleName("tt-alert");
		}
		
		TopicHelpers.testPost(this);
    }
    
    public void init(CalendarPost cp) {
    	this.cp = cp;
    	bodySummaryElement.setHTML(cp.getSummary());
    	
    	GPerson liteCreator = new GPerson();
    	liteCreator.setLogin(cp.getCreatorLogin());
    	liteCreator.setPersonId(cp.getCreatorId());
    	creatorLinkPresenter.setPerson(liteCreator);
    	setSpacer(-1);
    	
    	GPerson user = ConnectionId.getInstance().getCurrentUser();
//    	Figure out how to make the calender post have the read/unread info!
//		if(!user.isAnonymous() && !post.isRead()){
//			postUnReadElement.setVisible(true);
//			postUnReadElement.setText("*");
//			postUnReadElement.addStyleName("tt-alert");
//		}
    	
    	TopicHelpers.testPost(this);
	}
    
    public void setSpacer(int tabCount) {
    	StringBuilder sb = new StringBuilder();
		
		for(int i = 0 ; i <= tabCount ; i++){
			sb.append("&nbsp;");
		}
		if(sb.length() > 0){
			spacerElement.setInnerHTML(sb.toString());
		}
	}
    
    //@UiHandler("bodySummaryElement")
    @UiHandler("hoverTargetElement")
    public void onClick(ClickEvent event){
    	expandPost();
    	    	
    }
    
    
    @Override
    public Widget getWidget() {
    	return this;
    }
    
    @Override
    public void expandPost() {
		RpcServiceAsync service = injector.getService();
		PostCrudCommand postCmd = new PostCrudCommand();
		postCmd.setPostId(getPostId());
		service.execute(postCmd,buildExpandedPostCallback());
	}
    
    @Override
	public String getPostId() {
		if(post != null){
			return post.getPostId();
		}
		else if(cp != null){
			return cp.getPostId();
		}
		else{
			throw new RuntimeException("PostSummaryPanel cant expand a post that doesnt exist!");
		}
	}

	@Override
	public void contractPost() {
		expandedElement.setVisible(false);
		summaryElement.setVisible(true);
	}
	
	private void notifyListeners(){
		if(post != null){
			PostEvent postEvent = new PostEvent(PostEventType.EXPAND_CONTRACT, post);
			EventBus.getInstance().fireEvent(postEvent);
		}
		else{
			throw new RuntimeException("PostSummaryPanel notifyListeners called without a post.  This is probably working in calendar now. Please fix.");
		}
	}
	
	private CommandResultCallback<PostCommandResult> buildExpandedPostCallback() {
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				post = result.getPost(); //Added 7/1/2010! Why was this not here?
				notifyListeners();
				summaryElement.setVisible(false);
				if(postExpanded == null){
					postExpanded = injector.createPostExpanded();
					postExpanded.init(post,commentElement);
					expandedElement.clear();
					expandedElement.add(postExpanded);
				}
				expandedElement.setVisible(true);
			}
		};
		return rootPostCallback;
	}

	
}
