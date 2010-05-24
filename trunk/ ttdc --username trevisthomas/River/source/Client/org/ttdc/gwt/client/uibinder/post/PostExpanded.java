package org.ttdc.gwt.client.uibinder.post;


import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * This is the representation of an expanded reply. Thread roots and 
 * conversation starters dont use this class
 * 
 *
 */
public class PostExpanded extends PostBaseComposite{
	interface MyUiBinder extends UiBinder<Widget, PostExpanded> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    @UiField(provided = true) Widget avatarElement;
    @UiField(provided = true) Widget createDateElement;
    @UiField Anchor moreOptionsElement;
    @UiField(provided = true) Hyperlink creatorLinkElement;
    private HyperlinkPresenter creatorLinkPresenter;
    
    private ImagePresenter imagePresenter;
    private DatePresenter createDatePresenter;
//    private MoreOptionsPopupPanel optionsPanel;
    
    
	@UiField SpanElement bodyElement;
//	private GPost post;
//	private ClickHandler replyClickHandler = null;
	
	@Inject
	public PostExpanded(Injector injector) {
		super(injector);
		createDatePresenter = injector.getDatePresenter();
    	imagePresenter = injector.getImagePresenter();
    	
    	imagePresenter = injector.getImagePresenter();
    	creatorLinkPresenter = injector.getHyperlinkPresenter();
    	createDatePresenter = injector.getDatePresenter();
    	
    	avatarElement = imagePresenter.getWidget();
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	createDateElement = createDatePresenter.getWidget();
    	creatorLinkElement = creatorLinkPresenter.getHyperlink();
    	
    	
    	
		initWidget(binder.createAndBindUi(this));
	}
	
	public void init(GPost post, HasWidgets commentElement){
		super.init(post, commentElement);
		bodyElement.setInnerHTML(post.getEntry());
//		this.post = post;
		moreOptionsElement.setText("> More Options");
		moreOptionsElement.setStyleName("tt-cursor-pointer");
		
		
		
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
