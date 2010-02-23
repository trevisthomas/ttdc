package org.ttdc.struts.network.actions.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.Cookies;
import org.ttdc.struts.network.common.Constants;

import com.opensymphony.xwork2.ActionSupport;


@SuppressWarnings("serial")
@Results({
	@Result( name="success", value="/index.jsp" )
})
public class Logout extends ActionSupport implements SecurityAware {
	private static Logger log = Logger.getLogger(Logout.class);
	private Person person;
	
	@Override
	public String execute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession(true);
		session.setAttribute(Constants.SESSION_KEY_WEBUSER,null);
		session.removeAttribute(Constants.SESSION_KEY_WEBUSER);
		
		Cookies.deleteCookie(request, response, Constants.COOKIE_USER_GUID);
		Cookies.deleteCookie(request, response, Constants.COOKIE_USER_PWD);
		
		log.info("User \""+person.getLogin()+"\" removed from session and cookies cleared.");
		person = UserService.getInstance().getAnnonymousUser();
		return SUCCESS;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
