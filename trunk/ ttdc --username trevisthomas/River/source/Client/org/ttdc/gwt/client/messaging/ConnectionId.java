package org.ttdc.gwt.client.messaging;

import org.ttdc.gwt.client.beans.GPerson;
/**
 * This class name may not be appropriate with this thing holding the person object.
 */
public class ConnectionId{
	private String connId;
	private GPerson currentUser = null; //Trevis... you should probably init this with connId (look at ServerEventMonitor and consider)
	private static ConnectionId me;
	private ConnectionId(){} 
	
	public static ConnectionId getInstance(){
		if(me == null)
			me = new ConnectionId();
		return me;
	}
	
	public static boolean isAdministrator(){
		return getInstance().getCurrentUser().isAdministrator();
	}
	
	public static boolean isAnonymous(){
		return getInstance().getCurrentUser().isAnonymous();
	}

	public String getConnectionId() {
		return connId;
	}

	public void setConnectionId(String connectionId) {
		this.connId = connectionId;
	}

	public void setCurrentUser(GPerson newCurrentUser) {
		this.currentUser = newCurrentUser;
	}

	public GPerson getCurrentUser() {
		return currentUser;
	}
	
	
}
