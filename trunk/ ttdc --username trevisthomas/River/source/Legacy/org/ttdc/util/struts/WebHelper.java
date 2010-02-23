package org.ttdc.util.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

public class WebHelper {
	public static HttpServletRequest getRequest(){
		HttpServletRequest request = null;
		try{
			request = ServletActionContext.getRequest();
		}
		catch(NullPointerException e){
			
		}
		return request; 
	}
	
	public static String getSiteName(){
		HttpServletRequest request = getRequest();
		String url = getRequest().getRequestURL().toString();
		String site = url.substring(0,url.indexOf('/', 8));
		site += request.getContextPath();
		return site;
	}
}
