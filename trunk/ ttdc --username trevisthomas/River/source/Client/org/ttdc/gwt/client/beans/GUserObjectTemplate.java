package org.ttdc.gwt.client.beans;

public class GUserObjectTemplate extends GBase{
	private String templateId;
	private String value;
	private GImage image;
	private String name;
	private GPerson creator;
	private String type;
	
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
	public GImage getImage() {
		return image;
	}
	public void setImage(GImage image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public GPerson getCreator() {
		return creator;
	}
	public void setCreator(GPerson creator) {
		this.creator = creator;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
