package org.ttdc.gwt.client.presenters.admin;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.client.constants.UserObjectTemplateConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.ImageActivityObserver;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.UserObjectTemplateListCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserObjectTemplateEditorPresenter extends BasePresenter<UserObjectTemplateEditorPresenter.View> implements ImageActivityObserver{
	public interface View extends BaseView{
		void addTemplate(Widget imageWidget, Widget urlPrefix, Widget displayName, Widget controlsWidget);
		HasClickHandlers createAddRowClickHandler();
		void clear();
	}
	
	@Inject
	public UserObjectTemplateEditorPresenter(Injector injector) {
		super(injector,injector.getUserObjectTemplateEditorView());
		
		view.createAddRowClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createNewAddRow();
			}
		});
	}
	
	protected void createNewAddRow() {
		//This is to get the "add" row
		UserObjectTemplateRowPresenter rowPresenter = injector.getUserObjectTemplateRowPresenter();
		rowPresenter.init();
		view.addTemplate(rowPresenter.getImageWidget(), rowPresenter.getUrlPrefixWidget(),
				rowPresenter.getDisplayNameWidget(), rowPresenter.getControlsWidget());
		
	}

	public void init(HistoryToken token) {
		UserObjectTemplateListCommand cmd = new UserObjectTemplateListCommand();
		cmd.setAction(UserObjectTemplateListCommand.UserObjectTemplateListAction.GET_ALL_OF_TYPE);
		cmd.setTemplateType(UserObjectTemplateConstants.TEMPLATE_WEBPAGE);
		
		RpcServiceAsync service = injector.getService();
		
		service.execute(cmd, buildTemplateListCallback());
	}
	
	private CommandResultCallback<GenericListCommandResult<GUserObjectTemplate>> buildTemplateListCallback() {
		return new CommandResultCallback<GenericListCommandResult<GUserObjectTemplate>>(){
			@Override
			public void onSuccess(GenericListCommandResult<GUserObjectTemplate> result) {
				for(GUserObjectTemplate template : result.getList()){
					UserObjectTemplateRowPresenter rowPresenter = injector.getUserObjectTemplateRowPresenter();
					rowPresenter.init(template);
					view.addTemplate(rowPresenter.getImageWidget(), rowPresenter.getUrlPrefixWidget(),
							rowPresenter.getDisplayNameWidget(), rowPresenter.getControlsWidget());
				}
			}
		};
	}
	
	@Override
	public void notifyImageActionCompletWithStatus(String imageId) {
		//init(previousToken);
		//Refresh
	}	
}
