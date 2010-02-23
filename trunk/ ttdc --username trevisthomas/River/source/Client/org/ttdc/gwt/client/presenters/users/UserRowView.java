package org.ttdc.gwt.client.presenters.users;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserRowView implements UserRowPresenter.View{
	private final HorizontalPanel loginWidgetPanel = new HorizontalPanel();
	private final Label emailPanel = new Label(); 
	private final Label hitCountPanel = new Label();
	private final VerticalPanel memberSinceWidgetPanel = new VerticalPanel();
	private final SimplePanel memberSincePanel = new SimplePanel();
	private final Label namePanel = new Label();
	private final SimplePanel imagePanel = new SimplePanel();
	private final SimplePanel lastAccessedPanel = new SimplePanel();
	
	
	
	private final SimplePanel loginPanel = new SimplePanel();
	public UserRowView() {
		loginWidgetPanel.add(imagePanel);
		loginWidgetPanel.add(loginPanel);
		
		memberSinceWidgetPanel.add(memberSincePanel);
		memberSinceWidgetPanel.add(lastAccessedPanel);
		
	}
	
	@Override
	public Widget getWidget() {
		throw new RuntimeException("Dont use rows this way.");
	}

	@Override
	public Widget getEmailWidget() {
		return emailPanel;
	}

	@Override
	public Widget getHitsWidget() {
		return hitCountPanel;
	}

	@Override
	public Widget getLoginWidget() {
		return loginWidgetPanel;
	}

	@Override
	public Widget getMemberSinceWidget() {
		return memberSinceWidgetPanel;
	}

	@Override
	public Widget getNameWidget() {
		return namePanel;
	}

	@Override
	public void hasPrivateAccess(boolean flag) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public HasWidgets imagePanel() {
		return imagePanel;
	}

	@Override
	public HasText nameText() {
		return namePanel;
	}

	@Override
	public HasText hitCountText() {
		return hitCountPanel;
	}

	@Override
	public HasWidgets loginPanel() {
		return loginPanel;
	}

	@Override
	public HasText emailText() {
		return emailPanel;
	}

	@Override
	public HasWidgets lastAccessedPanel() {
		return lastAccessedPanel;
	}

	@Override
	public HasWidgets memberSincePanel() {
		return memberSincePanel;
	}
	
	
	
}
