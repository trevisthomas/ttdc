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
	@Result( name="input", value="tiles.resetPassword", type=TilesResult.class ),
	@Result( name="login", value="tiles.login", type=TilesResult.class ),
	@Result( name="main", value="tiles.main", type=TilesResult.class )
})
public class ResetPassword extends ActionSupport{
	private Person person;
	private String magic;
	private String glitter;
	private String action = "";
	private String guid;
	private String password;
	private String verifyPassword;

	@Override
	public String execute() throws Exception {
		try{
			if(magic != null && glitter != null){
				//Password reset fool!
				person = UserService.getInstance().resetPasswordMagicValidator(glitter, magic);
				if(person != null)
					return INPUT;
				else{
					addActionError("Sigh, why you gotta hate, huh?");
					return "login";
				}
			}
			else if(action.equals("reset")){
				try{
					UserService.getInstance().resetPassword(guid, password);
					addActionMessage("Password has been reset");
					return "login";
				}
				catch(ServiceException e){
					person = UserService.getInstance().loadPerson(guid);
					addActionError(e.getSummary());
					return INPUT;
				}
			}
			else{
				addActionError("You're not even trying, are you?");
				return "login";
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			return "main";
		}
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getMagic() {
		return magic;
	}

	public void setMagic(String magic) {
		this.magic = magic;
	}

	public String getGlitter() {
		return glitter;
	}

	public void setGlitter(String glitter) {
		this.glitter = glitter;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerifyPassword() {
		return verifyPassword;
	}

	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}
	
	
}
