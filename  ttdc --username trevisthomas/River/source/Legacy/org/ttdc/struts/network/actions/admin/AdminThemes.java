package org.ttdc.struts.network.actions.admin;

import java.util.List;

import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.views.tiles.TilesResult;
import org.ttdc.biz.network.services.ThemeService;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Style;
import org.ttdc.struts.network.common.SecurityAware;
import org.ttdc.util.ServiceException;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@ParentPackage("ttdc-admin")
@Results({
	@Result( name="success", value="tiles.adminThemes", type=TilesResult.class),
	@Result( name="edit", value="tiles.adminThemeEdit", type=TilesResult.class)
})
public class AdminThemes  extends ActionSupport implements SecurityAware {
	private Person person;
	private String action = "";
	private String styleId;
	private List<Style> styles;
	private Style style;
	private String cssFileName;
	private String description;
	private String displayName;

	@Override
	public String execute() throws Exception {
		try{
			styles = ThemeService.getInstance().getAllStyles();
			if(action.equals("create")){
				ThemeService.getInstance().createStyle(person, cssFileName, description, displayName);
				styles = ThemeService.getInstance().getAllStyles();
			}
			else if(action.equals("delete-style")){
				ThemeService.getInstance().deleteStyle(styleId);
				styles = ThemeService.getInstance().getAllStyles();
			}
			else if(action.equals("view-edit")){
				style = ThemeService.getInstance().readStyle(styleId);
				return "edit";
			}
			else if(action.equals("edit")){
				ThemeService.getInstance().updateStyle(styleId,style);	
				styles = ThemeService.getInstance().getAllStyles();
			}
			return SUCCESS;
		}
		catch(ServiceException e){
			addActionError(e.getSummary());
			return SUCCESS;
		}
			
	}
	
	public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}

	public List<Style> getStyles() {
		return styles;
	}

	public void setStyles(List<Style> styles) {
		this.styles = styles;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public String getCssFileName() {
		return cssFileName;
	}

	public void setCssFileName(String cssFileName) {
		this.cssFileName = cssFileName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
}