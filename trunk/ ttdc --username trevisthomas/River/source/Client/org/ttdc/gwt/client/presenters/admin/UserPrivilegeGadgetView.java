package org.ttdc.gwt.client.presenters.admin;

import org.ttdc.gwt.client.beans.GPrivilege;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserPrivilegeGadgetView implements UserPrivilegeGadgetPresenter.View{
	private VerticalPanel main = new VerticalPanel();
	private DisclosurePanel privilegeFlyout = new DisclosurePanel("Modify");
	private FlowPanel privilegeSummaryPanel = new FlowPanel();
	private VerticalPanel privileges = new VerticalPanel();
	
	public UserPrivilegeGadgetView() {
		main.add(privilegeFlyout);
		main.add(privilegeSummaryPanel);
		privilegeFlyout.add(privileges);
		
		privilegeFlyout.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				privilegeSummaryPanel.setVisible(false);
			}
		});
		
		privilegeFlyout.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				privilegeSummaryPanel.setVisible(true);
			}
		});
		
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public void addPriviledge(GPrivilege privilege, boolean checked, ValueChangeHandler<Boolean> handler) {
		if(checked){
			Label label = new Label(" - "+privilege.getName());
			privilegeSummaryPanel.add(label);
		}
		CheckBox c = new CheckBox(privilege.getName());
		c.addValueChangeHandler(handler);
		c.setValue(checked);
		privileges.add(c);
	}

	@Override
	public void clear() {
		privilegeSummaryPanel.clear();
		privileges.clear();
		
	}
}
