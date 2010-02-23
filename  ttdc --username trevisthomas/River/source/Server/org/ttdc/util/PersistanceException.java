package org.ttdc.util;

public class PersistanceException extends Exception {
	public PersistanceException(){
		super();
	}
	public PersistanceException(String s){
		super(s);
	}
	public PersistanceException(Throwable t){
		super(t);
	}
	public PersistanceException(String msg, Throwable cause){
		super(msg,cause);
	}
}
