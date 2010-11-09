package org.ttdc.gwt.client.icons;

import com.google.gwt.user.client.ui.Label;

public class IconsCommon {
	
	private static Label createIcon(String style) {
		Label icon = new Label();
		icon.setStyleName("tt-icon-common");
		icon.addStyleName(style);
		return icon;
	}
	
	public static Label getIconInf(){
		return createIcon("tt-icon-common-inf");
	}
	
	public static Label getIconNws(){
		return createIcon("tt-icon-common-nws");
	}
	
	public static Label getIconLock(){
		return createIcon("tt-icon-common-lock");
	}
	
	public static Label getIconUnread(){
		return createIcon("tt-icon-common-unread");
	}
	
	public static Label getIconRead(){
		return createIcon("tt-icon-common-read");
	}
	
	public static Label getIconDeleted(){
		return createIcon("tt-icon-common-deleted");
	}
	
}
