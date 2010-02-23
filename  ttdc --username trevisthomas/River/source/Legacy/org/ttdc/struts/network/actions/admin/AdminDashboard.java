package org.ttdc.struts.network.actions.admin;

import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.persistence.objects.Person;
import org.ttdc.struts.network.common.SecurityAware;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@ParentPackage("ttdc-admin")
@Results({
	@Result( name="success", value="tiles.adminDashboard", type=TilesResult.class)
})
public class AdminDashboard  extends ActionSupport implements SecurityAware {
	private Person person;

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
}
