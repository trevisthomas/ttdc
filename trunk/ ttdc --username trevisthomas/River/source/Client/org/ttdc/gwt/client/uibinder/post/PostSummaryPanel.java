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
import org.ttdc.gwt.client.services.RpcServiceAsync;
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
    @UiField(provided = true) Hyperlink creatorLinkElement;
    @UiField HTML bodySummaryElement;
    @UiField SpanElement spacerElement;
    @UiField HTMLPanel summaryElement;
    @UiField(provided = true) SimplePanel expandedElement;
    @UiField(provided = true) SimplePanel commentElement = new SimplePanel();
    @UiField Label postUnReadElement;
    
    @Inject
    public PostSummaryPanel(Injector injector) { 
    	this.injector = injector;
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	expandedElement = new SimplePanel();
    	
    	initWidget(binder.createAndBindUi(this)); 
	}
    
    public void init(GPost post){
    	this.post = post;
    	//bodySummaryElement.setInnerHTML(post.getLatestEntry().getSummary());
    	bodySummaryElement.setHTML(post.getLatestEntry().getSummary());
    	creatorLinkPresenter.setPerson(post.getCreator());
    	setSpacer(post.getPath().split("\\.").length - 2);
    	
    	GPerson user = ConnectionId.getInstance().getCurrentUser();
		if(!user.isAnonymous() && !post.isRead()){
			postUnReadElement.setVisible(true);
			postUnReadElement.setText("*");
			postUnReadElement.addStyleName("tt-alert");
		}
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
    
    @UiHandler("bodySummaryElement")
    public void onClick(ClickEvent event){
    	expandPost();
    	    	
    }
    
    
    @Override
    public Widget getWidget() {
    	return this;
    }
    
    public void expandPost() {
		RpcServiceAsync service = injector.getService();
		PostCrudCommand postCmd = new PostCrudCommand();
		postCmd.setPostId(post.getPostId());
		service.execute(postCmd,buildExpandedPostCallback());
	}
    
	@Override
	public void contractPost() {
		expandedElement.setVisible(false);
		summaryElement.setVisible(true);
	}
	
	private void notifyListeners(){
		PostEvent postEvent = new PostEvent(PostEventType.EXPAND_CONTRACT, post);
		EventBus.getInstance().fireEvent(postEvent);
	}
	
	private CommandResultCallback<PostCommandResult> buildExpandedPostCallback() {
		CommandResultCallback<PostCommandResult> rootPostCallback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
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
