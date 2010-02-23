package org.ttdc.struts.network.actions.user;

import java.util.List;

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
	@Result( name="success", value="tiles.viewUsers", type=TilesResult.class)
})
public class ViewUsers extends ActionSupport implements SecurityAware {
	private Person person;
	private List<Person> users;
	
	@Override
	public String execute() throws Exception {
		try{
			users = UserService.getInstance().getActiveUsers();
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

	public List<Person> getUsers() {
		return users;
	}

	public void setUsers(List<Person> users) {
		this.users = users;
	}
	
}
