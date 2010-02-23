package org.ttdc.gwt.server.dao;

import org.junit.Test;
import org.ttdc.persistence.objects.Style;


import static org.ttdc.persistence.Persistence.*;
import static junit.framework.Assert.*;

public class StyleDaoTest {
	@Test
	public void testCreateStyle(){
		try{
			String styleId;
			beginSession();
			StyleDao dao = new StyleDao();
			dao.setCreatorId(Helpers.personIdTrevis);
			dao.setCssFileName("unittest.css");
			dao.setDescription("Unit test test");
			dao.setDisplayName("UnitTest");
			Style s = dao.create();
			styleId = s.getStyleId();
			
//			commit();
//			beginSession();
			
			dao.setStyleId(styleId);
			String newName = "DName";
			dao.setDisplayName(newName);
			dao.setCssFileName("unittest_2.css");
			dao.setDescription("Unit test not");
			dao.setDefaultStyle(false);
			s = dao.update();
			assertEquals(newName,s.getName());
			
//			commit();
//			beginSession();
			
			StyleDao.delete(styleId);
			
//			commit();
//			beginSession();
			
			try{
				s = StyleDao.load(styleId);
				fail("Doesnt exist");
			}
			catch(Exception e){
				//expected
			}
			
		}
		finally{
			rollback();
		}
	}
	
	//Test name duplicate.
}
