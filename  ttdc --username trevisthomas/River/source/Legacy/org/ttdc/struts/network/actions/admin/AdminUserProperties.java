package org.ttdc.struts.network.actions.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.biz.network.services.helpers.UserPrivilege;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@ParentPackage("ttdc-admin")
@Results({
	@Result( name="login", value="/index.jsp"),
	@Result( name="main", value="main/main", type=ServletActionRedirectResult.class )
})
public class AdminUserProperties extends ActionSupport implements SecurityAware{
	private String guid;
	private Person person;
	private String action;
	private Person user;
	private List<Privilege> privileges;
	private List<UserPrivilege> userPrivileges;
	private String privilege;
	
	
	@Override
	public String execute() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		try{
			if(action.equals("activate") || action.equals("unlock")){
				user = UserService.getInstance().updateStatus(guid,Person.STATUS_ACTIVE);
				return "status";
			}
			else if(action.equals("lock")){
				user = UserService.getInstance().updateStatus(guid,Person.STATUS_LOCKED);
				return "status";
			}
			else if(action.equals("inactivate")){
				user = UserService.getInstance().updateStatus(guid,Person.STATUS_INACTIVE);
				return "status";
			}
			else if(action.equals("grant")){
				user = UserService.getInstance().grantPrivilege(guid, privilege);
				userPrivileges = UserService.getInstance().readUserPrivileges(guid);
				return "privileges-edit";
			}
			else if(action.equals("revoke")){
				user = UserService.getInstance().revokePrivilege(guid, privilege);
				userPrivileges = UserService.getInstance().readUserPrivileges(guid);
				return "privileges-edit";
			}
			else if(action.equals("view-edit")){
				userPrivileges = UserService.getInstance().readUserPrivileges(guid);
				user = UserService.getInstance().loadPerson(guid);
				return "privileges-edit";
			}
			else if(action.equals("grant-default-priviledges")){
				user = UserService.getInstance().grantDefaultPrivileges(guid);
				return "privileges-view";
			}
			else if(action.equals("view")){
				user = UserService.getInstance().loadPerson(guid);
				return "privileges-view";
			}
			else if(action.equals("loginAsUser")){
				HttpServletRequest request = ServletActionContext.getRequest();
				HttpSession session = request.getSession(true);
				user = UserService.getInstance().loadPerson(guid);
				session.setAttribute(Constants.SESSION_KEY_WEBUSER,user.getPersonId());
				return "login";
			}
			else{
				response.sendError(HttpServletResponse.SC_CONFLICT,"Admin User Properties has no idea how to do that.");
				return ERROR;
			}
		}
		catch(ServiceException e){
			response.sendError(HttpServletResponse.SC_CONFLICT,e.getSummary());
			return ERROR;
		}
	}

	
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}


	public String getGuid() {
		return guid;
	}


	public void setGuid(String guid) {
		this.guid = guid;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public Person getUser() {
		return user;
	}


	public void setUser(Person user) {
		this.user = user;
	}


	public List<Privilege> getPrivileges() {
		return privileges;
	}


	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}


	public String getPrivilege() {
		return privilege;
	}


	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}


	public List<UserPrivilege> getUserPrivileges() {
		return userPrivileges;
	}


	public void setUserPrivileges(List<UserPrivilege> userPrivileges) {
		this.userPrivileges = userPrivileges;
	}
}
