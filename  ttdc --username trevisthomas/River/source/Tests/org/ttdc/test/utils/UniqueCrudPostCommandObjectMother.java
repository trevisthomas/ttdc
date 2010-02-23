package org.ttdc.test.utils;

import java.util.Date;

import org.ttdc.gwt.client.forms.PostFormData;
import org.ttdc.gwt.shared.commands.PostCrudCommand;

public class UniqueCrudPostCommandObjectMother {
	public static PostCrudCommand createNewTopic(){
		PostCrudCommand form = new PostCrudCommand();
		Date now = new Date();
		form.setBody("I'm a comment created by a unit test. Timestamp:"+now);
		form.setTitle("New Comment - " + now);
		//form.setType(PostFormData.TYPE_NORMAL);
		return form;
	}
	
	public static PostCrudCommand createNewReply(){
		PostCrudCommand form = new PostCrudCommand();
		Date now = new Date();
		form.setBody(now + " Reply created by a unit test");
		form.setParentId("C772E164-294D-4B8D-9345-0E2599564118");//TTDC Version 6 Beta! 
		return form;
	}
}
