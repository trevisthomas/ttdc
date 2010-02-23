package org.ttdc.gwt.client.components.widgets;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public final class PersonLinkWidget extends Composite implements ClickHandler{
	private final GPerson person;
	private final Hyperlink widget;
	
	private PersonLinkWidget(GPerson person){
		HistoryToken token = new HistoryToken();
		token.setParameter("view","personDetails");
		token.setParameter("personId",person.getPersonId());
		
		//widget.setTargetHistoryToken(token.toString());
		
		widget = new Hyperlink();
		widget.setText(person.getLogin());
		widget.setTargetHistoryToken(token.toString());
		
		widget.addClickHandler(this);
		this.person = person;
		initWidget(widget);
	}
	
	public static final PersonLinkWidget createInstance(GPerson person){
		PersonLinkWidget me = new PersonLinkWidget(person);
		return me;
	}
	
	//TODO: this still needs to be refactored so that this implementation responds to the eventbus
	public void onClick(ClickEvent event) {
		
		
		RootPanel.get("content").clear();
		RootPanel.get("content").add(createPersonLoadingLabel(person));
		
	}
	
	public Label createPersonLoadingLabel(GPerson person){
		Label loadingLabel = new Label("Loading "+person.getLogin()+"...");
		return loadingLabel;
	}

	public GPerson getPerson() {
		return person;
	}
	
	
}
