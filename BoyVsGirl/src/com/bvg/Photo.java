package com.bvg;

import java.util.Map;

public class Photo {
	private final Map<String, Object> rawPhoto;
	public Photo(Map<String, Object> rawPhoto) {
		this.rawPhoto = rawPhoto;
	}

	public String getOriginalUrl() {
		return (String) rawPhoto.get("OriginalUrl");
	}

	public String getXxLarge(){
		return getOriginalUrl().replaceFirst(".jpg", "-6.jpg");  
	}
	
	public String getThumbUrl(){
		return getOriginalUrl().replaceFirst(".jpg", "-11.jpg");  
	}
	
	public String getCreator(){
		//Not sure what field to use here.
		return (String) rawPhoto.get("Copyright");
	}
	
	public String getPageUrl(){
		return (String) rawPhoto.get("PageUrl");
	}

}
