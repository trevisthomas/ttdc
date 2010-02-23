package org.ttdc.gwt.client.presenters.admin;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class PersonStatusGadgetView implements PersonStatusGadgetPresenter.View{
	private final FlowPanel main = new FlowPanel();
	private final Button activateButton = new Button("Activate");
	private final Button deactivateButton = new Button("Deactivate");
	private final Button lockButton = new Button("Lock");
	private final Button unlockButton = new Button("Unlock");
	
	public PersonStatusGadgetView() {
		main.add(activateButton);
		main.add(deactivateButton);
		main.add(lockButton);
		main.add(unlockButton);
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}
	
	@Override
	public HasClickHandlers activateButton() {
		return activateButton;
	}

	@Override
	public HasClickHandlers deactivateButton() {
		return deactivateButton;
	}

	@Override
	public HasClickHandlers lockButton() {
		return lockButton;
	}
	
	@Override
	public HasClickHandlers unlockButton() {
		return unlockButton;
	}

	@Override
	public void setVisableActivate(boolean visible) {
		activateButton.setVisible(visible);
	}

	@Override
	public void setVisableDeactivate(boolean visible) {
		deactivateButton.setVisible(visible);
	}

	@Override
	public void setVisableLock(boolean visible) {
		lockButton.setVisible(visible);
	}

	@Override
	public void setVisableUnlock(boolean visible) {
		unlockButton.setVisible(visible);
	}

}
