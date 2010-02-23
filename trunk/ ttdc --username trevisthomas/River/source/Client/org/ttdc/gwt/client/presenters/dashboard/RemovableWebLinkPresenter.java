package org.ttdc.gwt.client.presenters.dashboard;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GUserObject;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PersonCommand;
import org.ttdc.gwt.shared.commands.UserObjectCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;
import org.ttdc.gwt.shared.commands.types.PersonStatusType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class RemovableWebLinkPresenter extends BasePresenter<RemovableWebLinkPresenter.View>{
	public interface View extends BaseView{
		HasWidgets webLinkIcon();
		HasClickHandlers deleteButton();
		HasWidgets link();
		void remove();
	}
	
	@Inject
	public RemovableWebLinkPresenter(Injector injector) {
		super(injector, injector.getRemovableWebLinkView());
	}
	
	public void init(GUserObject uo){
		HyperlinkPresenter linkPresenter = injector.getHyperlinkPresenter();
		linkPresenter.setText(uo.getUrl());
		linkPresenter.setUrl(uo.getUrl());
		view.link().add(linkPresenter.getWidget());
		
		ImagePresenter imagePresenter = injector.getImagePresenter();
		imagePresenter.setImage(uo.getTemplate().getImage(), 32, 32);
		view.webLinkIcon().add(imagePresenter.getWidget());
		
		view.deleteButton().addClickHandler(buildDeleteWebLinkClickHandler(uo));
	}

	private ClickHandler buildDeleteWebLinkClickHandler(final GUserObject uo) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				UserObjectCrudCommand cmd = new UserObjectCrudCommand();
				cmd.setAction(ActionType.DELETE);
				cmd.setObjectId(uo.getObjectId());
				injector.getService().execute(cmd, buildDeleteWebLinkCallback());				
			}
		};
	}
	
	private CommandResultCallback<GenericCommandResult<GUserObject>> buildDeleteWebLinkCallback() {
		return new CommandResultCallback<GenericCommandResult<GUserObject>>(){
			@Override
			public void onSuccess(GenericCommandResult<GUserObject> result) {
				EventBus.fireMessage(result.getMessage());
				view.remove();
				
				GPerson person = ConnectionId.getInstance().getCurrentUser();
				PersonCommand cmd = new PersonCommand(person.getPersonId(),PersonStatusType.LOAD);
				injector.getService().execute(cmd, buildCallback());
			}
		};
	}
	CommandResultCallback<GenericCommandResult<GPerson>> buildCallback(){
		return new CommandResultCallback<GenericCommandResult<GPerson>>(){
			@Override
			public void onSuccess(GenericCommandResult<GPerson> result) {
				EventBus.fireEvent(new PersonEvent(PersonEventType.USER_PROFILE_UPDATED, result.getObject()));
			}
		};
	}
}
