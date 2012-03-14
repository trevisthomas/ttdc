package com.bvg;

import org.junit.Test;

public class ZenfolioServiceTest {
	@Test
	public void loadPhotoSet(){
		ZenfolioService zenfolio = new ZenfolioService();
		PhotoSet photoSet = zenfolio.loadPhotoSet("854996360");
		
		for(Photo p : photoSet.getPhotos()){
			System.err.println(p.getOriginalUrl());
			System.err.println(p.getCreator());
			System.err.println(p.getPageUrl());
			System.err.println(p.getThumbUrl());
		}
	}
}
