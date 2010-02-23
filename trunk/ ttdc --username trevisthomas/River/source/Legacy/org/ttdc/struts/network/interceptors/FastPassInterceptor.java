package org.ttdc.struts.network.interceptors;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.ttdc.biz.network.services.UserService;

import org.ttdc.persistence.objects.Person;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecureUser;
import org.ttdc.struts.network.common.SecurityAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * Fast pass doesnt update the hit counter at all. It just checks the cookie or session for
 * a person object and sends them in.
 * 
 * If their object isnt found i send them to the WebSecurityInterceptor for a more though check.
 * 
 * @author Trevis
 *
 */
@SuppressWarnings("serial")
public class FastPassInterceptor implements Interceptor {
	private static Logger log = Logger.getLogger(FastPassInterceptor.class);
	public void destroy() {
	// TODO Auto-generated method stub
	}
	public void init() {
	// TODO Auto-generated method stub
	}
	public String intercept(ActionInvocation invocation) throws Exception {
		// Get the action context from the invocation so we can access the
		// HttpServletRequest and HttpSession objects.
		final ActionContext context = invocation.getInvocationContext();

		Object action = invocation.getAction();
		HttpServletRequest request = (HttpServletRequest) context.get(org.apache.struts2.StrutsStatics.HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse) context.get(org.apache.struts2.StrutsStatics.HTTP_RESPONSE);
		BrowserCacheSettings.browserCache(response);
		
		HttpSession session = request.getSession(true);
		Object obj = session.getAttribute(Constants.SESSION_KEY_WEBUSER);
		
		if (obj != null) {
			String personId = (String)obj;
			Person p = UserService.getInstance().loadPerson(personId);
			if (action instanceof SecurityAware) {
				((SecurityAware) action).setPerson(p);
			}
		}
		else {
			if (action instanceof SecurityAware) {
				((SecurityAware) action).setPerson(UserService.getInstance().getAnnonymousUser());
			}
		}
		session.setAttribute(Constants.SESSION_KEY_LAST_ACCESS_TIME,new Date());
		return invocation.invoke();
	}

}
