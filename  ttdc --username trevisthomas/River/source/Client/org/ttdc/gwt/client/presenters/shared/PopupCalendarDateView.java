package org.ttdc.gwt.client.presenters.shared;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopupCalendarDateView  implements PopupCalendarDatePresenter.View{
	private final Anchor datePanel = new Anchor("//"); //If it was empty from the start i never seemed to get the right icon from gwt
	private final PopupPanel popupPanel = new PopupPanel(true);
	
	
	@Override
	public void hideLoginPopup() {
		if(popupPanel.isShowing()){
			popupPanel.hide();
		}
	}
	
	public PopupCalendarDateView() {
		datePanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(popupPanel.isShowing()){
					popupPanel.hide();
				}
				else{
					// Reposition the popup relative to the button
		            int left = datePanel.getAbsoluteLeft();
		            int top = datePanel.getAbsoluteTop() + datePanel.getOffsetHeight() - 1;
		            popupPanel.setPopupPosition(left, top);

		            // Show the popup
		            popupPanel.show();	
				}
			}
		});
	}
	
	@Override
	public void setDateValue(String date){
		datePanel.setText(date);
	}
	
	
	@Override
	public void setInteractiveCalendarWidget(Widget calendar) {
		popupPanel.add(calendar);
	}
	
	@Override
	public Widget getWidget() {
		return datePanel;
	}
	
}
