package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.constants.PrivilegeConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.client.presenters.util.UnorderedListWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class IconOptionsPanel extends Composite implements PersonEventListener{
	interface MyUiBinder extends UiBinder<Widget, IconOptionsPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private final Injector injector;
	private GPost post;
    
    private Anchor ratingElement = createAnchor("rate");
    private Anchor unRateElement = createAnchor("un-rate");
    private Anchor editElement = createAnchor("edit");
    private Anchor muteThreadElement = createAnchor("mute");
    private Anchor unMuteThreadElement = createAnchor("un-mute");
    private Anchor earmarkElement = createAnchor("earmark");
    private Anchor unEarmarkElement = createAnchor("un-earmark");
    private Anchor tagElement = createAnchor("flag");
    
	@UiField(provided = true) ClickableIconPanel replyElement = new ClickableIconPanel("ui-icon ui-icon-arrowrefresh-1-n");
	@UiField(provided = true) ClickableIconPanel likeElement = new ClickableIconPanel("tt-icon-common tt-icon-thumbsup");
	@UiField(provided = true) ClickableIconPanel unLikeElement = new ClickableIconPanel("tt-icon-common tt-icon-thumbsdown");
	@UiField(provided = true) ClickableIconPanel moreOptionsElement = new ClickableIconPanel("ui-icon ui-icon-plusthick");
	
	private OptionsPopupPanel optionsPanel = null;
	
	@Inject
	public IconOptionsPanel(Injector injector) {
		this.injector = injector;

		initWidget(binder.createAndBindUi(this));
		
		EventBus.getInstance().addListener(this);
		
		replyElement.setTitle("Respond to this post");
		likeElement.setTitle("Like this post");
		unLikeElement.setTitle("Remove like for this post");
		moreOptionsElement.setTitle("Click to view more options");
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.is(PersonEventType.USER_CHANGED)){
			hide();
			init(post);
		}
	}
	
	public void init(GPost post){
    	this.post = post;
    	
    	GPerson user = ConnectionId.getInstance().getCurrentUser();
    	ratingElement.setVisible(false);
    	unRateElement.setVisible(false);
    	unLikeElement.setVisible(false);
    	likeElement.setVisible(false);
    	editElement.setVisible(false);
    	muteThreadElement.setVisible(false);
    	unMuteThreadElement.setVisible(false);
    	unEarmarkElement.setVisible(false);
    	earmarkElement.setVisible(false);
    	tagElement.setVisible(false);
    	if(user.hasPrivilege(PrivilegeConstants.VOTER) || user.isAdministrator()){
    		if(post.isRatable()){
    			if(post.getRatingByPerson(user.getPersonId()) == null)
    				ratingElement.setVisible(true);
    			else
    				unRateElement.setVisible(true);
    		}
    		if(!post.isMovie()){
	    		if(post.getLikedByPerson(user.getPersonId()) == null)
	    			likeElement.setVisible(true);
				else
					unLikeElement.setVisible(true);
    		}
    		
    	}
    	
    	if(!user.isAnonymous()){
    		if(user.isThreadFiltered(post.getRoot().getPostId())){
        		unMuteThreadElement.setVisible(true);
        	}
        	else {
        		muteThreadElement.setVisible(true);
        	}
    		
    		if(post.getEarmarkByPerson(user.getPersonId()) == null){
    			earmarkElement.setVisible(true);
    		}
    		else{
    			unEarmarkElement.setVisible(true);
    		}
    		tagElement.setVisible(true);
    	}
    	
    	if(user.isAdministrator() || (user.equals(post.getCreator()) && post.isInEditWindow())){
    		editElement.setVisible(true);
    	}
    	
//    	if(user.isAdministrator()){
//    		tagElement.setVisible(true);
//    	}
    	
    }
    
	public void addReplyClickHandler(ClickHandler handler){
    	replyElement.addClickHandler(handler);
    }
    
    public void addRatingClickHandler(ClickHandler handler){
    	ratingElement.addClickHandler(handler);
    }
    
    public void addUnRateClickHandler(ClickHandler handler){
    	unRateElement.addClickHandler(handler);
    }
    
    public void addEditClickHandler(ClickHandler handler){
    	editElement.addClickHandler(handler);
    }
    
    public void addMuteThreadClickHandler(ClickHandler handler){
    	muteThreadElement.addClickHandler(handler);
    }
    
    public void addUnMuteThreadClickHandler(ClickHandler handler){
    	unMuteThreadElement.addClickHandler(handler);
    }
    
    public void addLikePostClickHandler(ClickHandler handler){
    	likeElement.addClickHandler(handler);
    }
    
    public void addUnLikePostClickHandler(ClickHandler handler){
    	unLikeElement.addClickHandler(handler);
    }
    
    public void addEarmarkClickHandler(ClickHandler handler){
    	earmarkElement.addClickHandler(handler);
    }
    
    public void addUnEarmarkClickHandler(ClickHandler handler){
    	unEarmarkElement.addClickHandler(handler);
    }
    
    public void addTagClickHandler(ClickHandler handler){
    	tagElement.addClickHandler(handler);
    }
    
    
    private void hide(){
    	//Hide the more options popup
    	if(optionsPanel != null){
    		optionsPanel.hide();	
    		optionsPanel = null;
    	}
    }
    
    private Anchor createAnchor(String text) {
		Anchor anchor = new Anchor(text);
		anchor.addClickHandler(new MyClickHandler());
		return anchor;
	}
    
    @UiHandler("moreOptionsElement")
    public void onClickMore(ClickEvent event){
    	if(optionsPanel == null){
    		optionsPanel = new OptionsPopupPanel();
    		optionsPanel.showRelativeTo(this);
    	}
    	else{
    		hide();
    	}
    }
    
    
    private class MyClickHandler implements ClickHandler{
    	@Override
    	public void onClick(ClickEvent event) {
    		hide();
    	}
    }
    
    private class OptionsPopupPanel extends PopupPanel{
    	public OptionsPopupPanel() {
    		UnorderedListWidget main = new UnorderedListWidget();
    		main.setStyleName("tt-more-options-popup");
    		add(main);
    		setAutoHideEnabled(true);
    		
    		if(ratingElement.isVisible())
    			main.addItem(ratingElement);
    		if(unRateElement.isVisible())
    			main.addItem(unRateElement);
    		if(muteThreadElement.isVisible())
    			main.addItem(muteThreadElement);
    		if(unMuteThreadElement.isVisible())
    			main.addItem(unMuteThreadElement);
    		if(earmarkElement.isVisible())
    			main.addItem(earmarkElement);
    		if(unEarmarkElement.isVisible())
    			main.addItem(unEarmarkElement);
    		if(tagElement.isVisible())
    			main.addItem(tagElement);
    		if(editElement.isVisible())
    			main.addItem(editElement);
    		
		}
		
    	public void showRelativeTo(Widget source){
        	int left = source.getAbsoluteLeft() + 10;
            int top = source.getAbsoluteTop() + 10;
            setPopupPosition(left, top);
            show();
        }
    }
    
//	private void showLess(){
//		showingMore = false;
//		List<Anchor> links = new ArrayList<Anchor>();
//		GPerson user = ConnectionId.getInstance().getCurrentUser();
//		
//		links.add(replyElement);
//		
//		if(!user.isAnonymous() && !post.isMovie()){
//    		if(post.getLikedByPerson(user.getPersonId()) == null)
//    			links.add(likeElement);
//			else
//				links.add(unlikeElement);
//		}
//		
//		links.add(moreElement);
//		
//		optionsList.loadAnchors(links);
//	}
//	private void showMore(){
//		showingMore = true;
//    	GPerson user = ConnectionId.getInstance().getCurrentUser();
//    	List<Anchor> links = new ArrayList<Anchor>();
//    	
//    	links.add(replyElement);
//    	    	    	
//    	if(user.hasPrivilege(PrivilegeConstants.VOTER) || user.isAdministrator()){
//    		
//    		if(post.isRatable()){
//    			if(post.getRatingByPerson(user.getPersonId()) == null)
//    				links.add(ratingElement);
//    			else
//    				links.add(unRateElement);
//    		}
//    		if(!post.isMovie()){
//	    		if(post.getLikedByPerson(user.getPersonId()) == null)
//	    			links.add(likeElement);
//				else
//					links.add(unlikeElement);
//    		}
//    		
//    	}
//    	
//    	if(!user.isAnonymous()){
//    		if(user.isThreadFiltered(post.getRoot().getPostId())){
//    			links.add(unMuteThreadElement);
//        	}
//        	else {
//        		links.add(muteThreadElement);
//        	}
//    		
//    		if(post.getEarmarkByPerson(user.getPersonId()) == null){
//    			links.add(earmarkElement);
//    		}
//    		else{
//    			links.add(unEarmarkElement);
//    		}
//    	}
//    	
//    	if(user.isAdministrator() || (user.equals(post.getCreator()) && post.isInEditWindow())){
//    		links.add(editElement);
//    	}
//    	
//    	if(!user.isAnonymous() && !post.isMovie()){
//    		links.add(tagElement);
//    	}
//    	
//    	links.add(lessElement);
//    	optionsList.loadAnchors(links);
//    }
//	
//	public void addReplyClickHandler(ClickHandler handler){
//    	replyElement.addClickHandler(handler);
//    }
//    
//    public void addRatingClickHandler(ClickHandler handler){
//    	ratingElement.addClickHandler(handler);
//    }
//    
//    public void addUnRateClickHandler(ClickHandler handler){
//    	unRateElement.addClickHandler(handler);
//    }
//    
//    public void addEditClickHandler(ClickHandler handler){
//    	editElement.addClickHandler(handler);
//    }
//    
//    public void addMuteThreadClickHandler(ClickHandler handler){
//    	muteThreadElement.addClickHandler(handler);
//    }
//    
//    public void addUnMuteThreadClickHandler(ClickHandler handler){
//    	unMuteThreadElement.addClickHandler(handler);
//    }
//    
//    public void addLikePostClickHandler(ClickHandler handler){
//    	likeElement.addClickHandler(handler);
//    }
//    
//    public void addUnLikePostClickHandler(ClickHandler handler){
//    	unlikeElement.addClickHandler(handler);
//    }
//    
//    public void addEarmarkClickHandler(ClickHandler handler){
//    	earmarkElement.addClickHandler(handler);
//    }
//    
//    public void addUnEarmarkClickHandler(ClickHandler handler){
//    	unEarmarkElement.addClickHandler(handler);
//    }
//    
//    public void addTagClickHandler(ClickHandler handler){
//    	tagElement.addClickHandler(handler);
//    }
}
