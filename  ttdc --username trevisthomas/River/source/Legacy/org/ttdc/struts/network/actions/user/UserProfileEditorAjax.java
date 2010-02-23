package org.ttdc.struts.network.actions.user;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.ttdc.biz.network.services.ThemeService;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Style;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.UserObjectTemplate;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class UserProfileEditorAjax extends ActionSupport implements SecurityAware {
	private Person person;
	private Person user;
	private String action;
	private List<UserObjectTemplate> templates;
	private String guid;
	private String templateId;
	private String value;  //TODO: make sure that this is used.  I think you can delete it
	private int numCommentsFrontPage;
	private int numCommentsThreadPage;
	private String address;
	private String objectId;
	private String widgetLocation;
	private List<Style> styles;
	private String styleId;
	
	private List<UserObjectTemplate> widgetTemplates;
	private String widgetTemplateId;
	
	private List<Tag> tags;
	private String tagId;
	private String tagValue;
	private boolean nwsEnabled = true;
	private String frontPageMode;
	
	private static final List<Integer> numCommentsFrontPageOptions = Arrays.asList(10,20,30,40,50,75);
	private static final List<Integer> numCommentsThreadPageOptions = Arrays.asList(25,50,75,100);
	
	
	public int getNumCommentsFrontPage() {
		return numCommentsFrontPage;
	}

	public void setNumCommentsFrontPage(int numCommentsFrontPage) {
		this.numCommentsFrontPage = numCommentsFrontPage;
	}

	public int getNumCommentsThreadPage() {
		return numCommentsThreadPage;
	}

	public void setNumCommentsThreadPage(int numCommentsThreadPage) {
		this.numCommentsThreadPage = numCommentsThreadPage;
	}

	public List<Integer> getNumCommentsFrontPageOptions() {
		return numCommentsFrontPageOptions;
	}

	public List<Integer> getNumCommentsThreadPageOptions() {
		return numCommentsThreadPageOptions;
	}

	@Override
	public String execute() throws Exception {
		//HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		String destination = ERROR;
		try{
			if(action.equals("delete")){
				destination = SUCCESS;
				UserService.getInstance().deleteUserObject(objectId);
				user = UserService.getInstance().loadPerson(guid);
				templates = UserService.getInstance().readAvailableUserObjectWebPageTemplates(guid);
			}
			else if(action.equals("add")){
				destination =  SUCCESS;
				user = UserService.getInstance().createUserObjectFromTemplate(guid,templateId,address, null);
				templates = UserService.getInstance().readAvailableUserObjectWebPageTemplates(guid);
			}
			else if (action.equals("view")){
				destination =  SUCCESS;
				user = UserService.getInstance().loadPerson(guid);
				templates = UserService.getInstance().readAvailableUserObjectWebPageTemplates(guid);
			}
			else if(action.equals("view-widgets")){
				destination = "widget";
				user = UserService.getInstance().loadPerson(guid);
				templates = UserService.getInstance().readAvailableUserObjectWidgetTemplates(guid);
			}
			else if(action.equals("delete-widget")){
				destination =  "widget";
				UserService.getInstance().deleteUserObject(objectId);
				user = UserService.getInstance().loadPerson(guid);
				templates = UserService.getInstance().readAvailableUserObjectWidgetTemplates(guid);
			}
			else if(action.equals("add-widget")){
				destination =  "widget";
				user = UserService.getInstance().createUserObjectFromTemplate(guid,templateId,address, widgetLocation);
				templates = UserService.getInstance().readAvailableUserObjectWidgetTemplates(guid);
			}
			else if(action.equals("view-tagFilter")){
				destination =  "tags";
				user = UserService.getInstance().loadPerson(guid);
				tags = UserService.getInstance().readFilteredTags(user);
				nwsEnabled = user.isNwsEnabled();
			}
			else if(action.equals("add-tagFilter")){
				destination =  "tags";
				user = UserService.getInstance().loadPerson(guid);
				UserService.getInstance().enableNws(user, nwsEnabled);
				if(tagValue != null && tagValue.trim().length() > 0)
					UserService.getInstance().addTagFilter(user,tagValue.trim());
				
				tags = UserService.getInstance().readFilteredTags(user);
				nwsEnabled = user.isNwsEnabled();
			}
			else if(action.equals("remove-tagFilter")){
				destination =  "tags";
				user = UserService.getInstance().loadPerson(guid);
				UserService.getInstance().removeTagFilter(user,tagId);
				tags = UserService.getInstance().readFilteredTags(user);
				nwsEnabled = user.isNwsEnabled();
			}
			else if(action.equals("view-styles")){
				destination =  "style";
				user = UserService.getInstance().loadPerson(guid);
				styles = ThemeService.getInstance().getAllStyles();
				if(user.getStyle() != null)
					styleId =  user.getStyle().getStyleId();
				else
					styleId = ThemeService.getInstance().getDefaultStyle().getStyleId();	
			}
			else if(action.equals("choose-style")){
				destination =  "style";
				user = UserService.getInstance().loadPerson(guid);
				styles = ThemeService.getInstance().getAllStyles();
				UserService.getInstance().chooseStyle(user,styleId);
				//person.setStyle(user.getStyle());//I'm doing this so that i can see the change immediately, even if i'm not logged in as this person? Hm	
				
			}
			else if(action.equals("view-num-comments")){
				destination = "numcomments";
				user = UserService.getInstance().loadPerson(guid);
				setNumCommentsFrontPage(user.getNumCommentsFrontpage());
				setNumCommentsThreadPage(user.getNumCommentsThreadPage());
				
			}
			else if(action.equals("num-comments")){
				destination = "numcomments";
				user = UserService.getInstance().loadPerson(guid);
				UserService.getInstance().setNumCommentsFrontpage(person, numCommentsFrontPage);
				UserService.getInstance().setNumCommentsThreadPage(person, numCommentsThreadPage);
				
			}
			else if(action.equals("set-frontpage-mode")){
				UserService.getInstance().updateFrontPageMode(person, frontPageMode);
				destination = "blank";
			}
			else{
				response.sendError(HttpServletResponse.SC_CONFLICT,"No clue what you want me to do, bubba.");
				return ERROR;
			}
			/*
			if(person.equals(user) && (!action.equals("view") || action.equals("view-widgets") || action.equals("view-tagFilter"))){
				SecurityHelper.refreshUserInSession(request,user);
			}
			*/
			return destination;
			
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			//response.sendError(HttpServletResponse.SC_CONFLICT,e.getSummary());
			return destination;
		}
	}
	
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public Person getUser() {
		return user;
	}

	public void setUser(Person user) {
		this.user = user;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}


	public List<UserObjectTemplate> getWidgetTemplates() {
		return widgetTemplates;
	}

	public void setWidgetTemplates(List<UserObjectTemplate> widgetTemplates) {
		this.widgetTemplates = widgetTemplates;
	}

	public String getWidgetTemplateId() {
		return widgetTemplateId;
	}

	public void setWidgetTemplateId(String widgetTemplateId) {
		this.widgetTemplateId = widgetTemplateId;
	}

	public String getWidgetLocation() {
		return widgetLocation;
	}

	public void setWidgetLocation(String widgetLocation) {
		this.widgetLocation = widgetLocation;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public boolean isNwsEnabled() {
		return nwsEnabled;
	}

	public void setNwsEnabled(boolean nwsEnabled) {
		this.nwsEnabled = nwsEnabled;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getTagValue() {
		return tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

	public List<Style> getStyles() {
		return styles;
	}

	public void setStyles(List<Style> styles) {
		this.styles = styles;
	}

	public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}

	public String getFrontPageMode() {
		return frontPageMode;
	}

	public void setFrontPageMode(String frontPageMode) {
		this.frontPageMode = frontPageMode;
	}

}
