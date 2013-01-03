package com.bvg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class PhotoSet {
	private final Map<String, Object> rawPhotoSet;
	private final String collectionId;
	
	
	public PhotoSet(Map<String, Object> map, String collectionId){
		 this.rawPhotoSet = map;
		Photo titlePhoto = new Photo((Map<String, Object>)rawPhotoSet.get("TitlePhoto"), collectionId);
		this.collectionId = collectionId;
	}
	
	
	
	public String getCreatedOn(){
		return Helpers.getDateAsRssString(rawPhotoSet, "CreatedOn"); 
	}
	
	public String getModifiedOn(){
		return Helpers.getDateAsRssString(rawPhotoSet, "ModifiedOn");
	}
	
	
	
	//Too crazy.  tripples call time.
//	public List<Photo> getPhotos(){
//		ZenfolioService service = new ZenfolioService();
//		List<Photo> photos = new ArrayList<Photo>();
//		List<Map<String, Object>> rawPhotos = (List<Map<String, Object>>)rawPhotoSet.get("Photos");
//		
//		for(Map<String, Object> rawPhoto : rawPhotos){
//			photos.add(service.loadPhoto(((Integer)rawPhoto.get("Id")).toString()));
//		}
//		return photos;
//	}
	
	public List<Photo> getPhotos(){
		List<Photo> photos = new ArrayList<Photo>();
		List<Map<String, Object>> rawPhotos = (List<Map<String, Object>>)rawPhotoSet.get("Photos");
		
		for(Map<String, Object> rawPhoto : rawPhotos){
			photos.add(new Photo(rawPhoto, collectionId));
		}
		return photos;
	}
	
}

