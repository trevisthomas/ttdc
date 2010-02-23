package org.ttdc.struts.webefriends.test;

import java.util.Date;
import com.opensymphony.xwork2.ActionSupport;

public class Test extends ActionSupport {
	
	public static final String MESSAGE = "Struts 2 Hello World Tutorial!";
	private String message;
	
	@Override
	public String execute() throws Exception {
		setMessage(MESSAGE);
		return SUCCESS;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public String getCurrentTime() {
		return new Date().toString();
	}
}

