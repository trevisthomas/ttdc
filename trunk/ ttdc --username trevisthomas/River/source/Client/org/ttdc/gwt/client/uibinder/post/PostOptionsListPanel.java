package org.ttdc.gwt.client.uibinder.post;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.constants.PrivilegeConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.util.UnorderedListWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PostOptionsListPanel extends Composite implements PersonEventListener{
	interface MyUiBinder extends UiBinder<Widget, PostOptionsListPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private final Injector injector;
	private GPost post;
    
	private Anchor replyElement = new Anchor("reply");
	private Anchor likeElement = new Anchor("like");
	private Anchor unLikeElement = new Anchor("un-like");
    private Anchor ratingElement = new Anchor("rate");
    private Anchor unRateElement = new Anchor("un-rate");
    private Anchor editElement = new Anchor("like");
    private Anchor muteThreadElement = new Anchor("mute");
    private Anchor unMuteThreadElement = new Anchor("un-mute");
    private Anchor earmarkElement = new Anchor("earmark");
    private Anchor unEarmarkElement = new Anchor("un-earmark");
    private Anchor tagElement = new Anchor("tag");
    private Anchor moreElement = new Anchor("more");
    private Anchor lessElement = new Anchor("less");
    
	@UiField(provided = true) UnorderedListWidget optionsList = new UnorderedListWidget();
	
	private boolean showingMore = false; 
	@Inject
	public PostOptionsListPanel(Injector injector) {
		this.injector = injector;

		initWidget(binder.createAndBindUi(this));
		
		EventBus.getInstance().addListener(this);
		
		moreElement.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showMore();
			}
		});
		
		lessElement.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showLess();
			}
		});
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.is(PersonEventType.USER_CHANGED)){
			if(showingMore){
				showMore();
			}
			else{
				showLess();
			}
		}
	}
	
	public void init(GPost post){
		this.post = post;
		showLess();
	}
	
	private void showLess(){
		showingMore = false;
		List<Anchor> links = new ArrayList<Anchor>();
		GPerson user = ConnectionId.getInstance().getCurrentUser();
		
		links.add(replyElement);
		
		if(!user.isAnonymous() && !post.isMovie()){
    		if(post.getLikedByPerson(user.getPersonId()) == null)
    			links.add(likeElement);
			else
				links.add(unLikeElement);
		}
		
		links.add(moreElement);
		
		optionsList.loadAnchors(links);
	}
	private void showMore(){
		showingMore = true;
    	GPerson user = ConnectionId.getInstance().getCurrentUser();
    	List<Anchor> links = new ArrayList<Anchor>();
    	
    	links.add(replyElement);
    	    	    	
    	if(user.hasPrivilege(PrivilegeConstants.VOTER) || user.isAdministrator()){
    		
    		if(post.isRatable()){
    			if(post.getRatingByPerson(user.getPersonId()) == null)
    				links.add(ratingElement);
    			else
    				links.add(unRateElement);
    		}
    		if(!post.isMovie()){
	    		if(post.getLikedByPerson(user.getPersonId()) == null)
	    			links.add(likeElement);
				else
					links.add(unLikeElement);
    		}
    		
    	}
    	
    	if(!user.isAnonymous()){
    		if(user.isThreadFiltered(post.getRoot().getPostId())){
    			links.add(unMuteThreadElement);
        	}
        	else {
        		links.add(muteThreadElement);
        	}
    		
    		if(post.getEarmarkByPerson(user.getPersonId()) == null){
    			links.add(earmarkElement);
    		}
    		else{
    			links.add(unEarmarkElement);
    		}
    	}
    	
    	if(user.isAdministrator() || (user.equals(post.getCreator()) && post.isInEditWindow())){
    		links.add(editElement);
    	}
    	
    	if(!user.isAnonymous() && !post.isMovie()){
    		links.add(tagElement);
    	}
    	
    	links.add(lessElement);
    	optionsList.loadAnchors(links);
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
}
