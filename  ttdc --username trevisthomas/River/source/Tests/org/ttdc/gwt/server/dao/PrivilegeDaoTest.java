package org.ttdc.gwt.server.dao;

import java.util.List;

import org.junit.Test;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Privilege;

import static junit.framework.Assert.*;

public class PrivilegeDaoTest {
	@Test
	public void testGetAll(){
		Persistence.beginSession();
		List<Privilege> list = PrivilegeDao.loadAllPrivileges();
		assertTrue(list.size() > 0);
		Persistence.commit();
	}
	
	@Test
	public void loadAPrivilege(){
		Persistence.beginSession();
		Privilege priv = PrivilegeDao.loadPrivilege("0740B9E3-1A4B-442C-AD74-7F97F3DD9238");
		assertEquals("VOTE", priv.getValue());
		Persistence.commit();
	}
}
