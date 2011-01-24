package org.ttdc.gwt.client.common;

import org.ttdc.gwt.client.messaging.ConnectionId;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class TitleCycleTool extends Timer{
	private String altTitle;
	private String realTitle;
	private boolean real = false;
	
	public TitleCycleTool(String realTitle, String altTitle) {
		this.altTitle = altTitle;
		this.realTitle =  realTitle;
		
		Window.setTitle(altTitle);
		
		if(ConnectionId.getInstance().getCurrentUser().isFlashOnUpdate()){
			scheduleRepeating(1500);
			
		}
		else{
			Window.setTitle(altTitle);
		}
		
		//EventBus.getInstance().addListener(this);
	}
	
	@Override
	public void run() {
		if(real == false){
			Window.setTitle(realTitle);
			real = true;
		}		
		else{
			real = false;
			Window.setTitle(altTitle);
		}
	}
	
	@Override
	public void cancel() {
		Window.setTitle(realTitle);
		super.cancel();
	}

	public String getAltTitle() {
		return altTitle;
	}

	public void setAltTitle(String altTitle) {
		this.altTitle = altTitle;
	}

	public String getRealTitle() {
		return realTitle;
	}

	public void setRealTitle(String realTitle) {
		this.realTitle = realTitle;
	}

//	@Override
//	public void onPersonEvent(PersonEvent event) {
//		if(PersonEventType.USER_CHANGED.equals(event)){
//			GPerson p = event.getSource();
//			p.isFlashOnUpdate();
//		}
//	}
	
}

