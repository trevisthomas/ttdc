package org.ttdc.gwt.client.uibinder.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TrafficPersonPanel  extends Composite{
	interface MyUiBinder extends UiBinder<Widget, TrafficPersonPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField SimplePanel loginElement;
    @UiField SimplePanel dynamicDateElement;
    @UiField SimplePanel avatarElement;
    @UiField SimplePanel linksElement;
        
    @Inject
    public TrafficPersonPanel(Injector injector) { 
    	this.injector = injector;
    	
    	initWidget(binder.createAndBindUi(this));
    	loginElement.setStyleName("tt-traffic-login");
    	dynamicDateElement.setStyleName("tt-traffic-date");
	}

    public void init(GPerson person){
		HyperlinkPresenter personLinkPresenter = injector.getHyperlinkPresenter();
		personLinkPresenter.setPerson(person);
		loginElement.add(personLinkPresenter.getWidget());
		
		//TODO: make dynamic (auto updating)
		DatePresenter datePresenter = injector.getDatePresenter();
		datePresenter.init(person.getLastAccessDate(),DateFormatUtil.dateTimeFormatter);
		
		dynamicDateElement.add(datePresenter.getWidget());
		
		linksElement.add(PresenterHelpers.buildWebLinksPresenter(injector, person).getWidget());
		
		ImagePresenter imagePresenter = injector.getImagePresenter();
		imagePresenter.setImage(person.getImage(), person.getLogin(), 50, 50);
		imagePresenter.useThumbnail(true);
		avatarElement.add(imagePresenter.getWidget());
	}
}
