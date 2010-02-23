package org.ttdc.util;

public class BizException extends Exception {
	public BizException(){
		super();
	}
	public BizException(String s){
		super(s);
	}
	public BizException(Throwable t){
		super(t);
	}
	public BizException(String msg, Throwable cause){
		super(msg,cause);
	}
}
