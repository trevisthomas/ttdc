package org.ttdc.struts.network.interceptors;

import javax.servlet.http.HttpServletResponse;

public class BrowserCacheSettings {
	public final static void browserCache(HttpServletResponse response){
		if(response != null){
			response.setHeader("Cache-control","no-cache, no-store");
			response.setHeader("Pragma","no-cache");
			response.setHeader("Expires","-1");
		}
	}
}	
