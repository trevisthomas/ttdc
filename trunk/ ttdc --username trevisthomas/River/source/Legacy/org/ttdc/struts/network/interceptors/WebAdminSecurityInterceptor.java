package org.ttdc.struts.network.interceptors;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecurityAware;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * 
 * @author Trevis
 *
 * Custom interceptor to protect actions which are only available to administrators.
 */
@SuppressWarnings("serial")
public class WebAdminSecurityInterceptor implements Interceptor {
	private static Logger log = Logger.getLogger(WebAdminSecurityInterceptor.class);
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
		
		HttpServletResponse response = (HttpServletResponse) context.get(org.apache.struts2.StrutsStatics.HTTP_RESPONSE);
		BrowserCacheSettings.browserCache(response);
		
		Person p = null;
		Object action = invocation.getAction();
		
		p = (Person)SecurityHelper.loadUser(context, action);
		
		if (p != null) {
			 
			if(!p.isAdministrator()){
				log.info(p.getLogin()+ " attempted to hit an admin page.  Locking their account!");
				UserService.getInstance().lockUser(p.getPersonId().toString());
				if (action instanceof ActionSupport) {
					((ActionSupport) action).addActionError("You did a bad thing.  Your account is now locked. Beg for forgiveness. BEG FOOL!");
				}
				return Constants.UNAUTHORIZED;
			}
			else{
				log.info("User " + p.getLogin() + " has been authenticated as an administrator.");
				if (action instanceof SecurityAware) {
					((SecurityAware) action).setPerson(p);
				}
				UserService.getInstance().userHit(p);
				String retval = invocation.invoke();
				return retval;
			}
		}
		else {
			log.info("Someone tried to access an administrative function and failed.");
			if (action instanceof ActionSupport) {
				((ActionSupport) action).addActionError("You are not authorized to access this page.");
			}
			return Constants.UNAUTHORIZED;
		}
	}
	
}
