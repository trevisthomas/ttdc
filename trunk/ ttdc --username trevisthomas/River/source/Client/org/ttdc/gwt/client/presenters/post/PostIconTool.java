package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.icons.IconsCommon;
import org.ttdc.gwt.client.messaging.ConnectionId;

import com.google.gwt.user.client.ui.Label;

public class PostIconTool {
    private final Label postUnReadElement = IconsCommon.getIconUnread();
    private final Label postReadElement = IconsCommon.getIconRead();
    private final Label postPrivateElement = IconsCommon.getIconPrivate();
    private final Label postNwsElement = IconsCommon.getIconNws();
    private final Label postInfElement = IconsCommon.getIconInf();
    private final Label postDeletedElement = IconsCommon.getIconDeleted();
    private final Label postLockedElement = IconsCommon.getIconLock();
    
    
    
    public PostIconTool() {
    	postUnReadElement.setTitle("Unread");
    	postReadElement.setTitle("Already read");
    	postPrivateElement.setTitle("Private");
    	postNwsElement.setTitle("Not Work Safe!");
    	postInfElement.setTitle("Informative");
    	postDeletedElement.setTitle("Deleted");
    	postLockedElement.setTitle("Locked");
	}
    
    public void init(final GPost post){
    	GPerson user = ConnectionId.getInstance().getCurrentUser();
    	postUnReadElement.setVisible(false);
        postReadElement.setVisible(false);
        postPrivateElement.setVisible(false);
        postNwsElement.setVisible(false);
        postInfElement.setVisible(false);
        postDeletedElement.setVisible(false);
        postLockedElement.setVisible(false);
        
    	if(!user.isAnonymous()){
			if(!post.isRead()){
				postUnReadElement.setVisible(true);
			}
			else{
		        postReadElement.setVisible(true);
			}
    	}
  
    	if(post.isPrivate()){
    		postPrivateElement.setVisible(true);
    	}
    	
    	if(post.isNWS()){
    		postNwsElement.setVisible(true);
    	}
    	
    	if(post.isINF()){
    		postInfElement.setVisible(true);
    	}
    	if(post.isDeleted()){
    		postDeletedElement.setVisible(true);
    	}
    	
    	if(post.isLocked()){
    		postLockedElement.setVisible(true);
    	}
    }
    
    public void showPostAsRead(){
    	postUnReadElement.setVisible(false);
    	postReadElement.setVisible(true);
    }
    
    public  Label getIconInf(){
		return postInfElement;
	}
	
	public  Label getIconNws(){
		return postNwsElement;
	}
	
	public Label getIconDeleted(){
		return postDeletedElement;
	}
	
	public  Label getIconPrivate(){
		return postPrivateElement;
	}
	
	public  Label getIconUnread(){
		return postUnReadElement;
	}
	
	public  Label getIconRead(){
		return postReadElement;
	}
	
	public Label getIconLocked(){
		return postLockedElement;
	}
}
