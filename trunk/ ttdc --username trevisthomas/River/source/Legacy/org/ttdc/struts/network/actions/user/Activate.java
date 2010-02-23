package org.ttdc.struts.network.actions.user;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")

@Results({
	@Result( name="success", value="tiles.login", type=TilesResult.class )
})
public class Activate extends ActionSupport{
	private String key;
	private String title = "Activation";
	private Person person;
	private String action = "";
	
	@Override
	public String execute() throws Exception {
		try{
			if(key != null){
				person = UserService.getInstance().activateUser(key);
				key=null;
				addActionMessage("Your account is active and ready for fun.");
				return SUCCESS; 
			}
			else{
				addActionError("You're not even trying, are you?");
				return SUCCESS;
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			return SUCCESS;
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
}
