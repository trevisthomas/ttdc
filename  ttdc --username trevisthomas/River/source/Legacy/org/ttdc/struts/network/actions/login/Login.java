package org.ttdc.struts.network.actions.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.Cookies;
import org.ttdc.util.ServiceException;
import com.opensymphony.xwork2.ActionSupport;


@SuppressWarnings("serial")
@Results({
	@Result( name="success", value="/index.jsp"),
	@Result( name="input", value="tiles.login", type=TilesResult.class ),
	@Result( name="resetpwd", value="tiles.resetpwd", type=TilesResult.class )
})
public class Login extends ActionSupport implements SecurityAware{
	private String login;
	private String loginForReset;
	private String password;
	private boolean rememberMe;
	private String action = "";
	private String email;
	private Person person;
		
	@Override
	public String execute() throws Exception {
		try{
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			
			if(action.equals("forgot-password")){
				if(loginForReset != null && loginForReset.length() > 0){
					UserService.getInstance().resetPasswordRequest(loginForReset,email);
					addActionMessage("An email has been sent with instructions for resetting your password.");
				}
				return "resetpwd";
			}
			else if(action.equals("login")){
				HttpSession session = request.getSession(true);
				session.setAttribute(Constants.SESSION_KEY_WEBUSER,null);
				Cookies.deleteCookie(request, response, Constants.COOKIE_USER_GUID);
				Cookies.deleteCookie(request, response, Constants.COOKIE_USER_PWD);
				
				
				Person p = UserService.getInstance().authenticate(login,password,false);
				
				session.setAttribute(Constants.SESSION_KEY_WEBUSER,p.getPersonId());
				
				if(isRememberMe()){
					Cookies.setCookieValue(response, Constants.COOKIE_USER_GUID,p.getPersonId().toString());
					Cookies.setCookieValue(response, Constants.COOKIE_USER_PWD,p.getPassword());
				}
				return SUCCESS;
			}
			else{
				return INPUT;
			}
				
			
			
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			return INPUT;
		}
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLoginForReset() {
		return loginForReset;
	}

	public void setLoginForReset(String loginForReset) {
		this.loginForReset = loginForReset;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	
}
