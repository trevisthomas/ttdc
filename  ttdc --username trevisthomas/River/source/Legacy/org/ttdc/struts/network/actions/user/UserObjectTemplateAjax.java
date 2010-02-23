package org.ttdc.struts.network.actions.user;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObjectTemplate;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Results({
	@Result( name="noaccess", value="user/viewUsers", type=ServletActionRedirectResult.class )
})
public class UserObjectTemplateAjax extends ActionSupport implements SecurityAware {
	private Person person;
	private String action;
	private List<UserObjectTemplate> templates;
	private String guid;
	
	@Override
	public String execute() throws Exception {
		
		HttpServletResponse response = ServletActionContext.getResponse();
		try{
			if(action.equals("delete")){
				UserService.getInstance().deleteUserObjectTemplate(guid);
				return "delete";
			}
			else{
				response.sendError(HttpServletResponse.SC_CONFLICT,"No clue what you want me to do, bubba.");
				return ERROR;
			}
		}
		catch(ServiceException e){
			response.sendError(HttpServletResponse.SC_CONFLICT,e.getSummary());
			return ERROR;
		}
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	public Person getPerson() {
		return person;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<UserObjectTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(List<UserObjectTemplate> templates) {
		this.templates = templates;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
 

}
