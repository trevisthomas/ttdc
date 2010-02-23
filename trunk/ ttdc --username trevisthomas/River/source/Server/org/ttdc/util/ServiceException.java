package org.ttdc.util;

@SuppressWarnings("serial")
public class ServiceException extends Exception {
	private String summary;
	public ServiceException(){
		super();
		summary = this.toString();
	}
	public ServiceException(String msg){
		super(msg);
		summary = msg;
	}
	public ServiceException(Throwable t){
		super(t);
		summary = t.toString();
	}
	public ServiceException(String summary, Throwable t){
		super(t);
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
}
