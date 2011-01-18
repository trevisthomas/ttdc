package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.icons.IconsCommon;

import com.google.gwt.user.client.ui.Label;

public class PostIconTool {
    private final Label postPrivateElement = IconsCommon.getIconPrivate();
    private final Label postNwsElement = IconsCommon.getIconNws();
    private final Label postInfElement = IconsCommon.getIconInf();
    private final Label postDeletedElement = IconsCommon.getIconDeleted();
    private final Label postLockedElement = IconsCommon.getIconLock();
    
    
    
    public PostIconTool() {
    	postPrivateElement.setTitle("Private");
    	postNwsElement.setTitle("Not Work Safe!");
    	postInfElement.setTitle("Informative");
    	postDeletedElement.setTitle("Deleted");
    	postLockedElement.setTitle("Locked");
	}
    
    public void init(final GPost post){

    	postPrivateElement.setVisible(false);
        postNwsElement.setVisible(false);
        postInfElement.setVisible(false);
        postDeletedElement.setVisible(false);
        postLockedElement.setVisible(false);

  
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
	
	public Label getIconLocked(){
		return postLockedElement;
	}
}
