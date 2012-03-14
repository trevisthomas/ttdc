package com.bvg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhotoSet {
	private final Map<String, Object> rawPhotoSet;
	public PhotoSet(Map<String, Object> map){
		this.rawPhotoSet = map;
	}
	
	public List<Photo> getPhotos(){
		List<Photo> photos = new ArrayList<Photo>();
		List<Map<String, Object>> rawPhotos = (List<Map<String, Object>>)rawPhotoSet.get("Photos");
		
		for(Map<String, Object> rawPhoto : rawPhotos){
			photos.add(new Photo(rawPhoto));
		}
		return photos;
	}
}
