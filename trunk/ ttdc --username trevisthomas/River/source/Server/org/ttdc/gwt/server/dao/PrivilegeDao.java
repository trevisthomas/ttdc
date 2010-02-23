package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.List;

import org.ttdc.persistence.objects.Privilege;

public class PrivilegeDao {
	public static List<Privilege> loadAllPrivileges(){
		@SuppressWarnings("unchecked")	
		List<Privilege> list = session().getNamedQuery("privilege.getAll").list();
		return list;
	}
	
	public static Privilege loadPrivilege(String privilegeId){
		Privilege priv = (Privilege)session()
						  .getNamedQuery("privilege.getByGuid")
						  .setString("guid", privilegeId).uniqueResult();
		return priv;
	}
	
}
