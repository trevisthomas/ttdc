package org.ttdc.gwt.client.presenters.dashboard;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.client.presenters.util.MyDateField;

//import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;



public class EditProfileView implements EditProfilePresenter.View{
	private final Grid grid  = new Grid(6,2);
	private final SimplePanel messages = new SimplePanel();
	private final TextArea bio = new TextArea();
	private final TextBox email = new TextBox();
	private final TextBox vEmail = new TextBox();
	private final MyDateField birthday = new MyDateField();
	private final TextBox name = new TextBox();
	private final VerticalPanel main = new VerticalPanel();
	private final ListBox availableWebLinks = new ListBox();
	private final VerticalPanel currentWebLinksPanel = new VerticalPanel();
	private final TextBox newWebLinkText = new TextBox();
	
	private final SimplePanel avatar = new SimplePanel();
	private final SimplePanel avatarUploader = new SimplePanel();
	
	private final Button updateInfoButton = new Button("Update");
	private final Button addWebLinkTemplateButton = new Button("Add");
	private Map<String,GUserObjectTemplate> templateMap = new HashMap<String,GUserObjectTemplate>();
	
	private final HorizontalPanel avatarGroupPanel = new HorizontalPanel();
	private final HorizontalPanel weblinkGrouoPanel = new HorizontalPanel();
	
	public EditProfileView() {
		avatarGroupPanel.add(avatar);
		avatarGroupPanel.add(avatarUploader);
		
		main.add(avatarGroupPanel);

		grid.setWidget(0, 0, new Label("Name"));
		grid.setWidget(0, 1, name);
		grid.setWidget(1, 0, new Label("Email"));
		grid.setWidget(1, 1, email);
		grid.setWidget(2, 0, new Label("Verify Email"));
		grid.setWidget(2, 1, vEmail);
		grid.setWidget(3, 0, new Label("Birthday"));
		grid.setWidget(3, 1, birthday);
		grid.setWidget(4, 0, new Label("Bio"));
		grid.setWidget(4, 1, bio);
		grid.setWidget(5, 1, updateInfoButton);
		
		main.add(grid);
		
		weblinkGrouoPanel.add(availableWebLinks);
		weblinkGrouoPanel.add(newWebLinkText);
		weblinkGrouoPanel.add(addWebLinkTemplateButton);
		
		main.add(currentWebLinksPanel);
		main.add(weblinkGrouoPanel);
		
		availableWebLinks.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				showActiveUrlPrefix();
			}
		});
	}
	
	@Override
	public void clear() {
		templateMap.clear();
		availableWebLinks.clear();
		avatar.clear();
		avatarUploader.clear();
		currentWebLinksPanel.clear();
	}
	private void showActiveUrlPrefix() {
		String selectedId = availableWebLinks.getValue(availableWebLinks.getSelectedIndex());
		GUserObjectTemplate template = templateMap.get(selectedId);
		newWebLinkText.setText(template.getValue());
	}
	
	@Override
	public HasWidgets WebLinks() {
		return currentWebLinksPanel;
	}

	@Override
	public HasClickHandlers updateUserInfoClickHandler() {
		return updateInfoButton;
	}

	@Override
	public HasClickHandlers addWebLinkClickHandler() {
		return addWebLinkTemplateButton;
	}

	@Override
	public void addWebLinkTemplate(GUserObjectTemplate template) {
		templateMap.put(template.getTemplateId(),template);
		availableWebLinks.addItem(template.getName(),template.getTemplateId());
		showActiveUrlPrefix();
	}
	
	@Override
	public String getSelectedWebLinkTemplate() {
		int ndx = availableWebLinks.getSelectedIndex();
		String tempalateId = availableWebLinks.getValue(ndx);
		return tempalateId;
	}
	
	@Override
	public HasWidgets avatar() {
		return avatar;
	}

	@Override
	public HasWidgets avatarUploader() {
		return avatarUploader;
	}

	@Override
	public HasText bioText() {
		return bio;
	}

	@Override
	public HasValue<Date> birthdayDate() {
		return birthday;
	}

	@Override
	public HasText emailText() {
		return email;
	}

	@Override
	public HasWidgets messages() {
		return messages;
	}

	@Override
	public HasText nameText() {
		return name;
	}

	@Override
	public HasText verifyEmailText() {
		return vEmail;
	}

	@Override
	public HasText webLinkUrlText() {
		return newWebLinkText;
	}

	@Override
	public Widget getWidget() {
		return main;
	}
	
}
