package org.ttdc.struts.network.actions.user;

import java.io.File;
import java.util.List;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.UserService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.UserObjectTemplate;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Results({
	@Result( name="success", value="tiles.userObjectTemplateEditor", type=TilesResult.class),
	@Result( name="edit", value="tiles.userObjectTemplateEditorEditTemplate", type=TilesResult.class)
})
public class UserObjectTemplateEditor extends ActionSupport implements SecurityAware {
	private Person person;
	private String action = "";
	private List<UserObjectTemplate> templates;
	private UserObjectTemplate template;
	private String templateId;
	private String title = "User Object Template Editor";
	
	private File upload;// The actual file
	private String uploadContentType; // The content type of the file
	private String uploadFileName; // The uploaded file name
	private String templateValue;
	private String templateName;
	
	@Override
	public String execute() throws Exception {
		try{
			templates = UserService.getInstance().readUserObjectTemplates(UserObjectTemplate.TEMPLATE_WEBPAGE);//In case of an error i load this first.
			
			if(action.equals("create")){
				UserService.getInstance().createUserObjectTemplate(person.getPersonId().toString(), templateValue,
								templateName, upload, uploadFileName, UserObjectTemplate.TEMPLATE_WEBPAGE);
				templates = UserService.getInstance().readUserObjectTemplates(UserObjectTemplate.TEMPLATE_WEBPAGE);
				return SUCCESS;
			}
			else if(action.equals("view-edit")){
				template = UserService.getInstance().readUserObjectTemplate(templateId);
				title += " Edit: " + template.getName();
				return "edit";
			}
			else if(action.equals("edit")){
				UserService.getInstance().updateUserObjectTemplate(person.getPersonId().toString(),templateId, template.getValue(),
						template.getName(), upload, uploadFileName, UserObjectTemplate.TEMPLATE_WEBPAGE);
				
				templates = UserService.getInstance().readUserObjectTemplates(UserObjectTemplate.TEMPLATE_WEBPAGE);
				return SUCCESS;
			}
			else{
				return SUCCESS;
			}
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			return SUCCESS;
		}
		
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

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateValue() {
		return templateValue;
	}

	public void setTemplateValue(String templateValue) {
		this.templateValue = templateValue;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public UserObjectTemplate getTemplate() {
		return template;
	}

	public void setTemplate(UserObjectTemplate template) {
		this.template = template;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
