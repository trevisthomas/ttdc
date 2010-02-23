package org.ttdc.gwt.client.services;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RemoteServiceException extends Exception implements IsSerializable{
	private static final long serialVersionUID = 1L;
	
	public RemoteServiceException(){
		super();
	}
	public RemoteServiceException(Throwable t){
		super(t.getMessage());
	}
	public RemoteServiceException(String message){
		super(message);
	}
}
