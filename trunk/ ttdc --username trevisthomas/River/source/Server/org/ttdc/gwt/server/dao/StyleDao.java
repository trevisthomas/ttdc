package org.ttdc.gwt.server.dao;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Style;

import static org.ttdc.persistence.Persistence.*;

public class StyleDao {
	private String styleId;
	private String cssFileName;
	private String displayName;
	private String description;
	private String creatorId;
	private Boolean defaultStyle;
	
	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public StyleDao(){
		
	}
	
	public static Style load(String styleId){
		Style style = (Style)session().load(Style.class, styleId);
		Hibernate.initialize(style);
		return style;
	}
	
	private void validateDisplayName(String name){
		if(session().getNamedQuery("style.getByName").setString("name", name).list().size() != 0){
			throw new RuntimeException(name+" is already in use.");
		}
	}
	
	public static List<Style> loadAll(){
		Query query = session().getNamedQuery("style.getAll");
		@SuppressWarnings("unchecked") List<Style> styles = query.list();
		return styles;
	}
	
	public Style create(){
		//validate?
		validateDisplayName(displayName);
		
		Person creator = PersonDao.loadPerson(creatorId);
		Style s = new Style();
		s.setCreator(creator);
		s.setCss(cssFileName);
		s.setDescription(description);
		s.setName(displayName);
		s.setDefaultStyle(defaultStyle);
		session().save(s);
		return s;
	}
	
	public static void delete(String styleId){
		Style style = load(styleId);
		session().delete(style);
	}
	
	public Style update(){
		if(defaultStyle){
			session().getNamedQuery("style.clearDefaultStyle").executeUpdate();
		}
		
		Style style = load(styleId);
		if(StringUtil.notEmpty(cssFileName))
			style.setCss(cssFileName);
		if(StringUtil.notEmpty(displayName))
			style.setName(displayName);
		if(StringUtil.notEmpty(description))
			style.setDescription(description);
		if(defaultStyle != null)
			style.setDefaultStyle(defaultStyle);
		
		session().update(style);
		
		return style;
	}
	
	public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}

	public String getCssFileName() {
		return cssFileName;
	}

	public void setCssFileName(String cssFileName) {
		this.cssFileName = cssFileName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isDefaultStyle() {
		return defaultStyle;
	}

	public void setDefaultStyle(Boolean defaultStyle) {
		this.defaultStyle = defaultStyle;
	}

}
