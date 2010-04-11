package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ImageView implements ImagePresenter.View{
	private String title;
	private int height = -1;
	private int width = -1;
	private final HTML html = new HTML();
	private GImage image;
	boolean asThumbnail = false;
	boolean linkToFull = false;
	private final FocusPanel main = new FocusPanel(); 
	private boolean showClickableCursor = false;
	private String linkUrl = null;
	
		
	@Override
	public Widget getWidget() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("<img ");
//		if(height > 0){
//			sb.append("height=").append(height).append("\" ");
//		}
//		if(width > 0){
//			sb.append("width=\"").append(width).append("\" ");
//		}
//		if(StringUtil.notEmpty(title)){
//			sb.append("title=\"").append(title).append("\" ");
//		}
//		if(asThumbnail)
//			sb.append("src=\"").append(getThumbnailImagePath()).append("\"");
//		else
//			sb.append("src=\"").append(getFullImagePath()).append("\"");
//		sb.append("/>");
//		
//		if(linkToFull)
//			html.setHTML(linkImageToFullImage(sb.toString()));
//		else if(StringUtil.notEmpty(linkUrl)){
//			html.setHTML(linkImageToUrl(sb.toString(),linkUrl));
//		}
//		else
//			html.setHTML(sb.toString());
//		
//		main.add(html);
		renderHtml();
		return main;
		
	}
	
	
	
	private String linkImageToUrl(String imgTag,String url) {
		StringBuilder sb = new StringBuilder();
		sb.append("<a target=\"_blank\" href=\"").append(url).append("\">").append(imgTag).append("</a>");
		return sb.toString();
	}

	private String linkImageToFullImage(String imgTag){
//		StringBuilder sb = new StringBuilder();
//		sb.append("<a target=\"_blank\" href=\"").append(getFullImagePath()).append("\">").append(imgTag).append("</a>");
//		return sb.toString();
		return linkImageToUrl(imgTag, getFullImagePath());
	}
	
	private String getFullImagePath(){
		StringBuilder sb = new StringBuilder();
		sb.append(GWT.getHostPageBaseURL()).append("images/").append(image.getName());
		return sb.toString();
	}
	private String getThumbnailImagePath(){
		StringBuilder sb = new StringBuilder();
		sb.append(GWT.getHostPageBaseURL()).append("images/").append(image.getThumbnailName());
		return sb.toString();
	}
	
	@Override
	public void setImageHeight(int height) {
		this.height = height;
	}

	@Override
	public void setImageTitle(String title) {
		this.title = title;		
	}

	@Override
	public void setImageWidth(int width) {
		this.width = width;
	}

	@Override
	public void setImage(GImage image) {
		this.image = image;
	}



	@Override
	public void renderHtml() {
		if(image == null || main.toString().trim().equals(""))
			return;
		StringBuilder sb = new StringBuilder();
		sb.append("<img ");
		if(height > 0){
			sb.append("height=").append(height).append("\" ");
		}
		if(width > 0){
			sb.append("width=\"").append(width).append("\" ");
		}
		if(StringUtil.notEmpty(title)){
			sb.append("title=\"").append(title).append("\" ");
		}
		if(asThumbnail)
			sb.append("src=\"").append(getThumbnailImagePath()).append("\"");
		else
			sb.append("src=\"").append(getFullImagePath()).append("\"");
		sb.append("/>");
		
		if(linkToFull)
			html.setHTML(linkImageToFullImage(sb.toString()));
		else if(StringUtil.notEmpty(linkUrl)){
			html.setHTML(linkImageToUrl(sb.toString(),linkUrl));
		}
		else
			html.setHTML(sb.toString());
		
		main.add(html);
	}

	@Override
	public void setShowAsThumbnail(boolean asThumbnail) {
		this.asThumbnail = asThumbnail;
		setClickableCursor(asThumbnail);
	}

	@Override
	public void setLinkToFull(boolean enable) {
		linkToFull = enable;
	}

	@Override
	public HasClickHandlers imageClickHandler() {
		return main;
	}

	@Override
	public void setClickableCursor(boolean clickable) {
		showClickableCursor = clickable;
		if(showClickableCursor)
			main.addStyleName("tt-cursor-pointer");
	}

	@Override
	public void setLinkUrl(String url) {
		linkUrl = url;
	}
}
