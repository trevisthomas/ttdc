package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;

import com.google.gwt.user.client.rpc.IsSerializable;


public class AssociationPostTagCommand extends Command<AssociationPostTagResult> implements IsSerializable{
	public enum Mode{CREATE,REMOVE};
	private String associationId;
	private Mode mode;
	private GTag tag;
	private String userName;
	private String password;
	
	public void setAssociationId(String associationId) {
		this.associationId = associationId;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setTag(GTag tag) {
		this.tag = tag;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	private String postId;
	
	public AssociationPostTagCommand(){}

//	public static AssociationPostTagCommand createRemoveTagCommand(String associationId){
//		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
//		cmd.associationId = associationId;
//		cmd.mode = Mode.REMOVE;
//		return cmd;
//	}
//	
//	public static AssociationPostTagCommand createTagCommand(GTag tag, String postId){
//		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
//		cmd.tag = tag;
//		cmd.postId = postId;
//		cmd.mode = Mode.CREATE;
//		return cmd;
//	}
	
		
	public String getAssociationId() {
		return associationId;
	}
	
	public boolean isRemove(){
		return mode == Mode.REMOVE;
	}
	
	public boolean isCreate(){
		return mode == Mode.CREATE;
	}

	public Mode getMode() {
		return mode;
	}

	public GTag getTag() {
		return tag;
	}

	public String getPostId() {
		return postId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
