package org.ttdc.gwt.client.presenters.post;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PostView implements PostPresenter.View {
	
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final VerticalPanel postPanel = new VerticalPanel();
	
	
	//private final HorizontalPanel postHeader = new HorizontalPanel();
	private final Grid postHeader = new Grid(1,2);
	private final SimplePanel title = new SimplePanel();
	
	private final Label entry = new Label();
	private final VerticalPanel childPosts = new VerticalPanel();
	private final SimplePanel creatorWidget = new SimplePanel();
	private final Label fetchMore = new Label();
	private final SimplePanel datePanel = new SimplePanel();
	//private final HorizontalPanel optionsButton = new SimplePanel();
	//HasClickHandlers
	private final FlowPanel optionsButtonPanel = new FlowPanel();
	private final FlowPanel creatorInfoPanel = new FlowPanel();
	//private final Grid detailGrid = new Grid(1,2);
	private final Grid detailPanel = new Grid(1,2); 
	private final FlowPanel postAvatarAndBodyPanel = new FlowPanel();
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
	
	private String postBodyContainerStyle = "tt-post-body-container";
	public PostView() {
	}
	
	@Override
	public Widget getWidget() {
		if(!postPanel.isAttached()){
			mainPanel.add(postPanel);
			mainPanel.setStyleName("tt-post");
			postPanel.add(postHeader);
			postPanel.setStyleName("tt-fill");
			//postPanel.addStyleName("tt-fill");
			postHeader.setWidget(0, 0, title);
			
			postPanel.add(postAvatarAndBodyPanel);
			postAvatarAndBodyPanel.add(avatarPanel);
			avatarPanel.setStyleName("tt-float-left");
			postBodyContainer.setStyleName("tt-float-left");
			postBodyContainer.addStyleName(postBodyContainerStyle);
			postAvatarAndBodyPanel.add(postBodyContainer);
			
			
			postBodyContainer.add(detailPanel);
			creatorInfoPanel.add(creatorWidget);
			creatorInfoPanel.add(datePanel);
			optionsButtonPanel.add(postOptionsClick);
			optionsButtonPanel.setStyleName("tt-text-right");
			detailPanel.setStyleName("tt-fill");
			detailPanel.setWidget(0, 0, creatorInfoPanel);
			detailPanel.setWidget(0, 1, optionsButtonPanel);
			postBodyContainer.add(new HTMLPanel(entry.getText()));
			
			
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
	public void init(String postId, boolean isReply) {
		//This is an ugly hack that is in place because of a style issue
		if(isReply)
			postBodyContainerStyle = "tt-post-body-container-reply";
		else
			postBodyContainerStyle = "tt-post-body-container";
			
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
