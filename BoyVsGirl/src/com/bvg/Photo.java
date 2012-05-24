package com.bvg;

import java.util.Map;

public class Photo {
	private final Map<String, Object> rawPhoto;
	private final String collectionId; 
	
	/* This collection id business is an ugly hack that i used to make the url's show the photo in the collection and not in the gallery!
	 * 
	 * If you have trouble with it check out the way it builds the page url.  If the PageUrl isnt of the format expected (with the p) then it might not work right.
	 */
	public Photo(Map<String, Object> rawPhoto, String collectionId) {
		this.rawPhoto = rawPhoto;
		this.collectionId = collectionId;
	}
	
	public Photo(Map<String, Object> rawPhoto) {
		this.rawPhoto = rawPhoto;
		this.collectionId = null;
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
		String url;
		url = (String) rawPhoto.get("PageUrl");
		if(collectionId != null){
			url = "http://photos.boyvsgirlphotography.com/p" + collectionId + url.substring(url.indexOf("/", 40));
		}
		return url;
	}

}
