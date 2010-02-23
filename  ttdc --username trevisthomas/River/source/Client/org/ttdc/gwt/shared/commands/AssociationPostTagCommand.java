package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;

import com.google.gwt.user.client.rpc.IsSerializable;


public class AssociationPostTagCommand extends Command<AssociationPostTagResult> implements IsSerializable{
	private enum Mode{CREATE,REMOVE};
	private String associationId;
	private Mode mode;
	private GTag tag;
	private String postId;
	
	public AssociationPostTagCommand() {}
	
	public static AssociationPostTagCommand createRemoveTagCommand(String associationId){
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.associationId = associationId;
		cmd.mode = Mode.REMOVE;
		return cmd;
	}
	
	public static AssociationPostTagCommand createTagCommand(GTag tag, String postId){
		AssociationPostTagCommand cmd = new AssociationPostTagCommand();
		cmd.tag = tag;
		cmd.postId = postId;
		cmd.mode = Mode.CREATE;
		return cmd;
	}
	
		
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
	
	
}
