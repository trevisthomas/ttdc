package org.ttdc.gwt.client.presenters.util;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This class will expire the widget referenced and remove it from the main
 * panel
 *
 */
public final class DisapearingWidget extends Timer{
	
	private final Widget w;
	public DisapearingWidget(Widget w, int ttl_ms) {
		this.w = w;
		schedule(ttl_ms);
	}
	@Override
	public void run() {
		if(w.isAttached()){
			w.removeFromParent();
		}
	}
	
	public static Widget expire(Widget w, int ttl_ms){
		new DisapearingWidget(w, ttl_ms);
		return w;
	}
}