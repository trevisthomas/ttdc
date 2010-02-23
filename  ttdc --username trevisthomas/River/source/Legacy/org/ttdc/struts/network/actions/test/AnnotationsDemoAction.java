package org.ttdc.struts.network.actions.test;

import java.util.Date;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import com.opensymphony.xwork2.ActionSupport;


@Results({
	@Result(name = "success", value="/WEB-INF/jsp/test/test.jsp")
})
public class AnnotationsDemoAction extends ActionSupport {
	public static final String MESSAGE = "Struts 2 With annotations!";
	private String message;
	
	@Override
	public String execute() throws Exception {
		setMessage(MESSAGE);
		return SUCCESS;
	}
	
	public String myMethod() throws Exception{
		setMessage("Yeah that worked.");
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

