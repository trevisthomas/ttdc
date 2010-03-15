package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.presenters.post.PostPresenter.Mode;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PostView implements PostPresenter.View {
	private final VerticalPanel postPanel = new VerticalPanel();
	
	
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final HorizontalPanel postHeader = new HorizontalPanel();
	private final SimplePanel title = new SimplePanel();
	
	private final Label entry = new Label();
	private final VerticalPanel childPosts = new VerticalPanel();
	private final SimplePanel creatorWidget = new SimplePanel();
	private final Label fetchMore = new Label();
	private final SimplePanel datePanel = new SimplePanel();
	
	private String postId;
	private Mode mode = Mode.FLAT;
	public PostView() {
	}
	
	@Override
	public Widget getWidget() {
		if(!postPanel.isAttached()){
			mainPanel.setStyleName("tt-post-container");
			
			mainPanel.add(postPanel);
			postPanel.setStyleName("tt-post");
			postPanel.addStyleName("tt-border");
			postPanel.add(creatorWidget);
			postPanel.add(datePanel);
			postPanel.add(postHeader);
			postHeader.add(title);
			postPanel.add(new HTMLPanel(entry.getText()));
			
			//The target for embedded content
			mainPanel.add(new HTML("<center><span id=\""+postId+"\"></span></center>"));
			
			if(Mode.NESTED_SUMMARY.equals(mode)){
				mainPanel.addStyleName("tt-post-container-nested-mode");
			}
			if(fetchMore.getText().length() > 0){
				mainPanel.add(fetchMore);
			}
			
			mainPanel.add(childPosts);
		}
		return mainPanel;
		
	}

	@Override
	public void init(String postId) {
		this.postId = postId;
	}
	public HasWidgets getChildWidgetBucket() {
		return childPosts;
	}
	
	public HasText getPostEntry() {
		return entry;
	}
	
	@Override
	public HasWidgets title() {
		return title;
	}
	@Override
	public HasWidgets creatorWidget() {
		return creatorWidget;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	@Override
	public HasClickHandlers fetchMoreTarget() {
		return fetchMore;
	}
	
	@Override
	public HasText fetchMoreTitle() {
		return fetchMore;
	}

	@Override
	public HasWidgets creationDateTarget() {
		return datePanel;
	}

}
