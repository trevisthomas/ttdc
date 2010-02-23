package org.ttdc.struts.network.actions.test;

import com.opensymphony.xwork2.ActionSupport;

public class AnotherDemo extends ActionSupport {
	@Override
	public String execute() throws Exception {
		throw(new Exception("WAHHHHAA!!"));
	}	
}
