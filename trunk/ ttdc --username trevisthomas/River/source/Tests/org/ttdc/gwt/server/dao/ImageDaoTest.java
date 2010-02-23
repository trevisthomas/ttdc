package org.ttdc.gwt.server.dao;

import org.junit.Test;
import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Image;

import static org.ttdc.persistence.Persistence.*;


import static junit.framework.Assert.*;

public class ImageDaoTest {
	@Test
	public void testImage(){
		beginSession();
		ImageDao dao = new ImageDao();
		dao.setImageId("47176747-38C4-4C2D-83A1-EEE840F95AC7");  //TheInternational.jpg
		Image image = dao.load();
		assertEquals("TheInternational.jpg", image.getName());
		commit();
	}
	
	@Test
	public void testImageByName(){
		beginSession();
		ImageDao dao = new ImageDao();
		dao.setName("TheInternational.jpg");  //TheInternational.jpg
		Image image = dao.load();
		assertEquals("47176747-38C4-4C2D-83A1-EEE840F95AC7", image.getImageId());
		commit();
	}
	
	@Test
	public void testImageByNameTn(){
		beginSession();
		ImageDao dao = new ImageDao();
		dao.setName("TheInternational_stn.jpg");  //TheInternational.jpg
		Image image = dao.load();
		assertEquals("47176747-38C4-4C2D-83A1-EEE840F95AC7", image.getImageId());
		commit();
	}
	
	@Test
	public void testImageAll(){
		beginSession();
		ImageDao dao = new ImageDao();
		PaginatedList<Image> results = dao.loadAll();
		assertEquals(20, results.getList().size());
		commit();
	}
}
