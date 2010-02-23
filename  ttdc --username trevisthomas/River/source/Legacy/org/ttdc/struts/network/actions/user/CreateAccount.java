package org.ttdc.struts.network.actions.user;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Results({
	@Result( name="input", value="tiles.createAcct", type=TilesResult.class),
	@Result( name="success", value="tiles.createAcctSuccess", type=TilesResult.class)
})
public class CreateAccount extends ActionSupport{
	private String login;
	private String password;
	private String verifyPassword;
	private String name;
	private String email;
	private String verifyEmail;
	private String birthday;
	private String bio;
	private String title = "Create New Account";

	public String execute() throws Exception {
		try{
			if(login != null){
				UserService.getInstance().createUser(login,password,name,email,birthday,bio);
				return SUCCESS;
			}
			else
				return INPUT;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getVerifyEmail() {
		return verifyEmail;
	}

	public void setVerifyEmail(String verifyEmail) {
		this.verifyEmail = verifyEmail;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVerifyPassword() {
		return verifyPassword;
	}

	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}
	
}
