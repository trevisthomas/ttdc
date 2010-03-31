package org.ttdc.gwt.client.presenters.post;

import org.apache.struts2.views.jsp.ui.AnchorTag;
import org.ttdc.gwt.client.presenters.post.PostPresenter.Mode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
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
	//private final HorizontalPanel optionsButton = new SimplePanel();
	//HasClickHandlers
	private final HorizontalPanel optionsButtonPanel = new HorizontalPanel();
	private final HorizontalPanel creatorInfoPanel = new HorizontalPanel();
	//private final Grid detailGrid = new Grid(1,2);
	private final FlowPanel detailPanel = new FlowPanel(); 
	private final HorizontalPanel postAvatarAndBodyPanel = new HorizontalPanel();
	//private final FlowPanel postAvatarAndBodyPanel = new FlowPanel();
	private final SimplePanel avatarPanel = new SimplePanel();
	private final VerticalPanel postBodyContainer = new VerticalPanel();
	
	private final Anchor postOptionsClick = new Anchor("> more options");
	private final VerticalPanel optionsContainerPanel = new VerticalPanel();
	private final Anchor replyClick = new Anchor("reply");
	private final Anchor likeClick = new Anchor("like");
	
	private final PopupPanel optionsPopup = new PopupPanel();
	
	private String postId;
	private Mode mode = Mode.FLAT;
	public PostView() {
	}
	
	@Override
	public Widget getWidget() {
		if(!postPanel.isAttached()){
			mainPanel.setStyleName("tt-post-container");
			mainPanel.addStyleName("tt-fill");
			mainPanel.add(postPanel);
			postHeader.add(title);
			postPanel.add(postHeader);
			postPanel.setStyleName("tt-post");
			postPanel.addStyleName("tt-border");
			postPanel.addStyleName("tt-fill");
			
			
			
//			postPanel.add(detailGrid);
						
			creatorInfoPanel.add(creatorWidget);
			creatorInfoPanel.add(datePanel);
			//postPanel.add(datePanel);
			
			optionsButtonPanel.add(postOptionsClick);
//			optionsButtonPanel.setStyleName("tt-test");
//			optionsButtonPanel.setWidth("100%");
//			optionsButtonPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
			
			postPanel.add(postAvatarAndBodyPanel);
			postAvatarAndBodyPanel.setStyleName("tt-fill");
			//postBodyContainer.add(postBodyContainer);
			
			//avatarPanel.setStyleName("tt-float-left");
			//postBodyContainer.setStyleName("tt-float-left");
			postBodyContainer.setStyleName("tt-fill");
			postAvatarAndBodyPanel.add(avatarPanel);
			postAvatarAndBodyPanel.add(postBodyContainer);
			
			
			postBodyContainer.add(detailPanel);
			//detailPanel.setWidth("100%");
			
			postBodyContainer.add(new HTMLPanel(entry.getText()));
			
//			detailGrid.setWidget(0, 0, creatorInfoPanel);
//			detailGrid.setWidget(0, 1, optionsButtonPanel);
			
			detailPanel.add(creatorInfoPanel);
			creatorInfoPanel.setStyleName("tt-float-left");
			detailPanel.add(optionsButtonPanel);
			optionsButtonPanel.setStyleName("tt-float-right");
			
//			dock.add(new HTML(constants.cwDockPanelNorth1()), DockPanel.NORTH);

			
			//The target for embedded content
			mainPanel.add(new HTML("<center><span id=\""+postId+"\"></span></center>"));
			
			if(Mode.NESTED_SUMMARY.equals(mode)){
				mainPanel.addStyleName("tt-post-container-nested-mode");
			}
			if(fetchMore.getText().length() > 0){
				mainPanel.add(fetchMore);
			}
			
			mainPanel.add(childPosts);
			
			
			initializeOptionsPopup();
		}
		return mainPanel;
		
	}

	private void initializeOptionsPopup() {
		//Setup the options popup panel
		optionsPopup.setAutoHideEnabled(true);
		optionsPopup.add(optionsContainerPanel);
		optionsContainerPanel.add(likeClick);
		optionsContainerPanel.add(replyClick);
		
		postOptionsClick.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Widget source = (Widget) event.getSource();
		        int left = source.getAbsoluteLeft() + 10;
		        int top = source.getAbsoluteTop() + 10;
		        optionsPopup.setPopupPosition(left, top);

		        // Show the popup
		        optionsPopup.show();
			}
		});
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
	public HasWidgets creatorLogin() {
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

	@Override
	public HasClickHandlers postOptionsClick() {
		return postOptionsClick;
	}

	@Override
	public HasClickHandlers likeButton() {
		return likeClick;
	}

	@Override
	public HasClickHandlers replyButton() {
		return replyClick;
	}

	@Override
	public HasWidgets creatorAvatorPanel() {
		return avatarPanel;
	}
}
