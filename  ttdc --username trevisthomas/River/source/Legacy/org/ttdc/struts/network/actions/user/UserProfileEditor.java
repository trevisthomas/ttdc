package org.ttdc.struts.network.actions.user;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.struts.network.common.Constants;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.struts.network.interceptors.SecurityHelper;
import org.ttdc.util.Cookies;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;


@SuppressWarnings("serial")
@Results({
	@Result( name="success", value="tiles.userProfileEditor", type=TilesResult.class),
	@Result( name="anonymous", value="tiles.main", type=TilesResult.class)
})
public class UserProfileEditor extends ActionSupport implements SecurityAware {
	private Person person;
	private String action = "";
	private Person user;
	private String guid; //This uses guid and a separate user object so that admin can edit people's profiles.
	private String verifyEmail;
	
	private File upload;// The actual file
	private String uploadContentType; // The content type of the file
	private String uploadFileName; // The uploaded file name
	
	private String oldPassword;
	private String newPassword;
	private String verifyNewPassword;
	private static Map<String,String> notificationOptions = new HashMap<String,String>();
	
	private static Map<String,String> frontPageModes = new HashMap<String,String>();
	
	static{
		frontPageModes = new LinkedHashMap<String,String>();
		frontPageModes.put(UserObject.VALUE_FLAT, "Flat");
		frontPageModes.put(UserObject.VALUE_HIERARCHY, "Hierarchy");
		
		notificationOptions.put(UserObject.VALUE_AUTO_UPDATE_NONE, "No new content notification." );
		notificationOptions.put(UserObject.VALUE_AUTO_UPDATE_FULL, "Front page is full dynamic.  New content shows as it arrives.");
		notificationOptions.put(UserObject.VALUE_AUTO_UPDATE_NOTIFY, "PostView a notification messgae when new content arrives.");
	}
	
		
	@Override
	public String execute() throws Exception {
		Person originalUser = null;
		try{
			HttpServletResponse response = ServletActionContext.getResponse();
			HttpServletRequest request = ServletActionContext.getRequest();
			if(person.isAnonymous()){
				addActionError("Suck it hacker.");
				return "anonymous";
			}
			if(!person.isAdministrator()){
				guid = person.getPersonId().toString();
			}
			//I do this first and in every case so that there is always data for the form to display
			originalUser = UserService.getInstance().loadPerson(guid);
			if(action.equals("")){
				user = originalUser;
			}
			else{
				//Update
				if(!person.isAdministrator()){
					if(!person.getPersonId().equals(guid)){
						throw new ServiceException("GTFO fool.");
					}
				}
				if(action.equals("update-avatar")){
					user = UserService.getInstance().updateUserAvatar(guid, upload, uploadFileName);
					addActionMessage("Your avatar has been updated.");
					
				}
				else if(action.equals("update")){
					user = UserService.getInstance().updateUser(guid,user);
					
					addActionMessage("Profile has been updated.");
					
				}
				else if(action.equals("reset-password")){
					if(person.isAdministrator())
						user = UserService.getInstance().resetPassword(guid, newPassword);	
					else
						user = UserService.getInstance().resetPassword(guid,oldPassword,newPassword);
					String password = Cookies.getCookieValue(request, Constants.COOKIE_USER_PWD, "");
					if(!password.equals("")){
						//Person has a pwd in their cookie, update it.
						Cookies.setCookieValue(response, Constants.COOKIE_USER_PWD, user.getPassword());
					}
					addActionMessage("Password has been updated.");
				}
				else{
					return SUCCESS;
				}
				//refresh
				if(person.equals(user)){
					SecurityHelper.refreshUserInSession(request,user);
				}
				
			}
			
			return SUCCESS;
		}
		catch(ServiceException e){
			user = originalUser;
			addActionError(e.getSummary());
			return SUCCESS;
		}
		
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Person getUser() {
		return user;
	}

	public void setUser(Person user) {
		this.user = user;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getVerifyEmail() {
		return verifyEmail;
	}

	public void setVerifyEmail(String verifyEmail) {
		this.verifyEmail = verifyEmail;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getVerifyNewPassword() {
		return verifyNewPassword;
	}

	public void setVerifyNewPassword(String verifyNewPassword) {
		this.verifyNewPassword = verifyNewPassword;
	}

	public Map<String, String> getNotificationOptions() {
		return notificationOptions;
	}

	public void setNotificationOptions(Map<String, String> notificationOptions) {
		//this.notificationOptions = notificationOptions;
	}
	public Map<String, String> getFrontPageModes() {
		return frontPageModes;
	}

	public void setFrontPageModes(Map<String, String> frontPageModes) {
		//this.frontPageModes = frontPageModes;
	}


}
