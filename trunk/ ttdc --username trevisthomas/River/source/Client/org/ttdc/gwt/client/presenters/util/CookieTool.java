package org.ttdc.gwt.client.presenters.util;

import java.util.Date;

import com.google.gwt.user.client.Cookies;

public class CookieTool {
	public static final String COOKIE_USER_GUID = "mkafdsfb";
	public static final String COOKIE_USER_PWD = "asdfasdf";
	private static final Date today = new Date();
	private static final Date expires = new Date(today.getYear()+10, today.getMonth(), today.getDate());
	
	public static void savePwd(String pwd){
		Cookies.setCookie(COOKIE_USER_PWD, pwd, expires);
	}
	public static void saveGuid(String guid){
		Cookies.setCookie(COOKIE_USER_GUID, guid, expires);	
	}
	
	public static String readPwd(){
		return Cookies.getCookie(COOKIE_USER_PWD);
	}
	
	public static String readGuid(){
		return Cookies.getCookie(COOKIE_USER_GUID);
	}
	
	public static void clear(){
		Cookies.removeCookie(COOKIE_USER_GUID);
		Cookies.removeCookie(COOKIE_USER_PWD);
	}
}
