package org.ttdc.struts.network.actions.admin;

import java.util.List;

import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.biz.network.services.helpers.Paginator;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@ParentPackage("ttdc-admin")
@Results({
	@Result( name="success", value="tiles.adminUserAccounts", type=TilesResult.class)
})
public class AdminUserAccounts extends ActionSupport implements SecurityAware {
	private Person person;
	private List<Person> users;
	private final static String title = Constants.SITE_TITLE + " - User Account Admin";
	private int page = 0;
	private Paginator<Post> paginator;
	private String action = "";

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		try{
			if(action.equals("page")){
				try{
					users = UserService.getInstance().getPaginatedPage(page);
					paginator = Paginator.getActivePaginator();
				}
				catch(ServiceException e){
					//If the paginated page fails just start over
					users = UserService.getInstance().getAllUsers(person);
					paginator = Paginator.getActivePaginator();
				}
			}
			else{
				users = UserService.getInstance().getAllUsers(person);
				paginator = Paginator.getActivePaginator();
				page = 1;
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

	public List<Person> getUsers() {
		return users;
	}

	public void setUsers(List<Person> users) {
		this.users = users;
	}
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Paginator<Post> getPaginator() {
		return paginator;
	}

	public void setPaginator(Paginator<Post> paginator) {
		this.paginator = paginator;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
}
