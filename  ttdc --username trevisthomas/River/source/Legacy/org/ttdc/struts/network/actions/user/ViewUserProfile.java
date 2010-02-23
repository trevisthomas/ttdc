package org.ttdc.struts.network.actions.user;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Results({
	@Result( name="noaccess", value="tiles.viewUsers", type=TilesResult.class ),
	@Result( name="success", value="tiles.viewUserProfile", type=TilesResult.class )
})
public class ViewUserProfile extends ActionSupport implements SecurityAware {
	private Person person;
	private String guid;
	private String title;
	private Person user;
	
	@Override
	public String execute() throws Exception {
		try{
			if(!person.isAnonymous()){
				user = UserService.getInstance().loadPerson(guid);
				title = user.getLogin() + "'s Profile";
			}
			else{
				addActionError("Anonymous users can not access user profiles.");
				return "noaccess";
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
		}
		return SUCCESS;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Person getUser() {
		return user;
	}

	public void setUser(Person user) {
		this.user = user;
	}
	
}
