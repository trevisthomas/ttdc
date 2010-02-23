package org.ttdc.struts.network.interceptors;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.struts.network.common.SecurityAware;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

@SuppressWarnings("serial")
public class WebSecurityInterceptor implements Interceptor {
	private static Logger log = Logger.getLogger(WebSecurityInterceptor.class);
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
			if (action instanceof SecurityAware) {
				((SecurityAware) action).setPerson(p);
			}
			UserService.getInstance().userHit(p);
			String retval = invocation.invoke();
			return retval;
		}
		else {
			log.info("Anonymous access granted.");
			if (action instanceof SecurityAware) {
				((SecurityAware) action).setPerson(UserService.getInstance().getAnnonymousUser());
			}
			String str = invocation.invoke();
			return str;
		}
	}
}
