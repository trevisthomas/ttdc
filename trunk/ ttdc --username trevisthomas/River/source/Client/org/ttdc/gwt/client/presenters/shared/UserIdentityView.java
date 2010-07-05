package org.ttdc.gwt.client.presenters.shared;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 *  * SEE User IdentityPanel!
 * 
 * @deprecated
 *
 */
public class UserIdentityView implements UserIdentityPresenter.View{
	private final FlowPanel main = new FlowPanel(); 
	private final Button loginButton = new Button("Go");
	private final SimplePanel logoutPanel = new SimplePanel();
	private final SimplePanel userPanel = new SimplePanel();
	private final Anchor logoutLink = new Anchor("logout");
	private final static PopupPanel loginPopup = new PopupPanel(true);
	private final Anchor loginLink = new Anchor("login");
	private final HTML seperator = new HTML();
	private final HTML welcomeMessage = new HTML();
	private final SimplePanel createLink = new SimplePanel();
	
	@Override
	public void hideLoginPopup() {
		if(loginPopup.isShowing()){
			loginPopup.hide();
		}
	}
	
	@Override
	public void setLoginWidget(Widget w) {
		loginPopup.clear();
		loginPopup.add(w);
	}
	
	public UserIdentityView() {
		welcomeMessage.setHTML("Sup, ");
		seperator.setHTML(" | ");
		
		createLink.addStyleName("tt-inline");
		welcomeMessage.addStyleName("tt-inline");
		seperator.addStyleName("tt-inline");
		userPanel.addStyleName("tt-inline");
		
		loginLink.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
				if(loginPopup.isShowing()){
					loginPopup.hide();
				}
				else{
					// Reposition the popup relative to the button
		            //Widget source = (Widget) event.getSource();
		            
		            int left = main.getAbsoluteLeft();
		            int top = main.getAbsoluteTop() + main.getOffsetHeight() - 1;
		            loginPopup.setPopupPosition(left, top);

		            // Show the popup
		            loginPopup.show();	
				}
			}
		});
	}
	
	@Override
	public HasWidgets accountCreatePanel(){
		return createLink;
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public void modeLogin(){
		main.clear();
		main.add(loginLink);
		main.add(seperator);
		main.add(createLink);
	}
	
	@Override
	public void modeLogout(){
		main.clear();
		main.add(welcomeMessage);
		main.add(userPanel);
		main.add(seperator);
		main.add(logoutLink);
	}
	
	@Override
	public HasClickHandlers loginButton() {
		return loginButton;
	}
	
	@Override
	public HasWidgets authenticatedUserPanel() {
		return userPanel;
	}

	@Override
	public HasWidgets logoutPanel() {
		return logoutPanel;
	}

	@Override
	public HasClickHandlers logoutButton() {
		return logoutLink;
	}

	@Override
	public void clear() {
		userPanel.clear();
	}

}
