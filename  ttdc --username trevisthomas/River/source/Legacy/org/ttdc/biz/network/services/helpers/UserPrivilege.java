package org.ttdc.biz.network.services.helpers;

import org.ttdc.persistence.objects.Privilege;

/**
 * Just a little helper class to bind if a user is granted with a privilege to the privilege.
 * This was desired for the admin privilege editor.
 *  
 * @author Trevis
 *
 */

public class UserPrivilege{
	private Privilege privilege; 
	private boolean granted = false;
	public UserPrivilege(Privilege privilege, boolean granted){
		this.privilege = privilege;
		this.granted = granted;
	}
	public Privilege getPrivilege() {
		return privilege;
	}
	public void setPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}
	public boolean isGranted() {
		return granted;
	}
	public void setGranted(boolean granted) {
		this.granted = granted;
	}
	
}
