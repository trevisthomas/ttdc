package org.ttdc.gwt.client.presenters.comments;

public class RichStyleElement {
	private final String name;
	private final String style;
	private final String styleClass;
	private final String tag;
	public RichStyleElement(String name, String tag, String styleClass, String style) {
		this.name = name;
		this.tag = tag;
		this.style = style;
		this.styleClass = styleClass;
	}

	public String getName() {
		return name;
	}

	public String getOpenTag() {
		return "<"+getTag()+" style=\""+getStyle()+"\">";
		//return String.format("<%1 style=\"%2\">", getTag(), getStyle());
	}

	public String getCloseTag() {
		return "</"+getTag()+">";
		//return String.format("</%1>",getTag());
	}

	public String getStyle() {
		return style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public String getTag() {
		return tag;
	}
	
	public String wrap(String text){
		return getOpenTag()+text+getCloseTag();
		//return String.format("%1 %2 %3", getOpenTag(),text,getCloseTag());
	}
	
	
}
