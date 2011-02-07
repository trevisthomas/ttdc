package org.ttdc.gwt.client.uibinder.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.DatePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.client.uibinder.home.TrafficPersonPanel.MyUiBinder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TrafficPersonAvatarOnlyPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, TrafficPersonAvatarOnlyPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
//    @UiField SimplePanel loginElement;
//    @UiField SimplePanel dynamicDateElement;
//    @UiField SimplePanel linksElement;
    
    @UiField SimplePanel avatarElement;
    @UiField SimplePanel starElement;
        
    @Inject
    public TrafficPersonAvatarOnlyPanel(Injector injector) { 
    	this.injector = injector;
    	
    	initWidget(binder.createAndBindUi(this));
    	//starElement.setVisible(false);
	}

    public void init(GPerson person){
		HyperlinkPresenter personLinkPresenter = injector.getHyperlinkPresenter();
		personLinkPresenter.setPerson(person);
//		loginElement.add(personLinkPresenter.getWidget());
		
		//TODO: make dynamic (auto updating)
//		DatePresenter datePresenter = injector.getDatePresenter();
//		datePresenter.init(person.getLastAccessDate(),DateFormatUtil.dateTimeFormatter);
		
//		dynamicDateElement.add(datePresenter.getWidget());
		
//		linksElement.add(PresenterHelpers.buildWebLinksPresenter(injector, person).getWidget());
		
		ImagePresenter imagePresenter = injector.getImagePresenter();
		imagePresenter.setImage(person.getImage(), person.getLogin(), 50, 50);
		imagePresenter.useThumbnail(true);
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW,HistoryConstants.VIEW_USER_PROFILE); 
		token.setParameter(HistoryConstants.PERSON_ID,person.getPersonId());
		imagePresenter.setLinkToken(token);
		
		avatarElement.add(imagePresenter.getWidget());
		
		if(person.isPrivateAccessAccount() && ConnectionId.getInstance().getCurrentUser().isPrivateAccessAccount()){
			starElement.setVisible(true);
		}
		else{
			starElement.setVisible(false);
		}
	}

}
