package org.ttdc.gwt.shared.commands;

import java.util.Date;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.AccountActionType;

public class AccountCommand extends Command<GenericCommandResult<GPerson>>{
	private AccountActionType action;
	private String personId;
	private Date birthday;
	private String bio;
	private String email;
	private String name;
	private String login;
	private String password;
	private String imageId;
	private String styleId;
	private boolean enableNws;
	
	public boolean isEnableNws() {
		return enableNws;
	}
	public void setEnableNws(boolean enableNws) {
		this.enableNws = enableNws;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public AccountActionType getAction() {
		return action;
	}
	public void setAction(AccountActionType action) {
		this.action = action;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getStyleId() {
		return styleId;
	}
	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}
	
}
