package org.ttdc.util;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie utility class. Adapted from CookieStuff in ttdc.  I'm setting the root path now because IE7 was returning
 * my cookie for the sub-page that created it.  Also, i'm setting the value to blank when i setMaxAge0 to clear it 
 * because IE wasn't behaving properly
 * 
 * @author Trevis
 *
 */
public class Cookies {
	private static final int DEFAULT_COOKIE_LIFE = 365 * 24 * 60 * 60;
	
	/**
	 * Get the cookie object for the provided name.
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookieName == null) {
			return null;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookieName.equals(cookie.getName()))
				return cookie;
		}
		return null;
	}
	
	/**
	 * Get the value of the cookie requested, or return the default value.
	 * 
	 * @param request
	 * @param cookieName
	 * @param defaulValue
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String cookieName, String defaulValue) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookieName == null) {
			return defaulValue;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookieName.equals(cookie.getName())){
				return UrlEncoder.decode(cookie.getValue());
			}
		}
		return defaulValue;
	}
	
	/**
	 * Get the value of the named cookie.
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		return getCookieValue(request, cookieName, "");
	}
	
	/*
	 * Trevis, you were setting the value to "" to fix an ie issue but that broke the 
	 * delete on firefox. (i guess it wont update a cookie to blank?) Anyway, this seems to be working ATM in both.
	 * The path value seems to be required on ie7 or else it seems to create the cookies on sub paths. I have no idea
	 * why ttdc dosent seem to have issue with this.
	 */
	/**
	 * Delete the named cookie.  
	 * 
	 * @param request
	 * @param response
	 * @param cookieName
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
		Cookie cookie = getCookie(request, cookieName);
		if (cookie != null) {
			cookie.setPath("/");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}
	
	/**
	 * Create a root level cookie with the provided name and value.
	 * 
	 * Note, cookie values are url encoded for safety.  I had problems with IE not treating them well if they had slashes
	 * and equal symbols
	 * 
	 * @param response
	 * @param cookieName
	 * @param value
	 */
	public static void setCookieValue(HttpServletResponse response, String cookieName, String value) {
		Cookie cookie;
		cookie = new Cookie(cookieName, UrlEncoder.encode(value));
		cookie.setPath("/");
		cookie.setMaxAge(DEFAULT_COOKIE_LIFE);
		response.addCookie(cookie);
	}
}
