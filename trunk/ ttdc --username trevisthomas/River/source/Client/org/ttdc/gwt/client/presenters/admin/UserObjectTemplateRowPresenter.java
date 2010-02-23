package org.ttdc.gwt.client.presenters.admin;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GUserObjectTemplate;
import org.ttdc.gwt.client.constants.UserObjectTemplateConstants;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.ImageActivityObserver;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.shared.ImageUploadPresenter;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.UserObjectTemplateCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class UserObjectTemplateRowPresenter extends BasePresenter<UserObjectTemplateRowPresenter.View> implements ImageActivityObserver{
	public interface View extends BaseView{
		Widget getImageWidget();
		Widget getUrlPrefixWidget();
		Widget getDisplayNameWidget();
		Widget getControlsWidget();
		
		HasClickHandlers updateButton();
		HasClickHandlers deleteButton();
		HasClickHandlers cancelButton();
		HasClickHandlers editButton();
		HasClickHandlers addButton();
		
		HasWidgets imageUploadWidget();
		HasWidgets staticImageWidget();
		HasText displayNameText();
		HasText urlPrefixText();
		
		void delete();
		void setMode(EditableTableMode editableTableMode);
	}
	
	private ImageUploadPresenter imageUploadPresenter;
	private ActionType action = null;
	private GUserObjectTemplate template = null;
	
	@Inject
	public UserObjectTemplateRowPresenter(Injector injector) {
		super(injector, injector.getUserObjectTemplateRowView());
		
		view.editButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.setMode(EditableTableMode.EDIT);
			}
		});
		
		view.cancelButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.setMode(EditableTableMode.VIEW);
			}
		});
		
		view.updateButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				action = ActionType.UPDATE;
				imageUploadPresenter.submit();
			}
		});
		
		view.addButton().addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				action = ActionType.CREATE;
				imageUploadPresenter.submit();
			}
		});
		
		view.deleteButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processDeleteRequest();
			}
		});
	}
	
	/**
	 * Use this init button to create an "add" row
	 */
	public void init(){
		init(new GUserObjectTemplate());
		view.setMode(EditableTableMode.ADD);
	}
	public void init(GUserObjectTemplate template){
		this.template = template;
		view.displayNameText().setText(template.getName());
		view.urlPrefixText().setText(template.getValue());
		
		imageUploadPresenter = injector.getImageUploadPresenter();
		imageUploadPresenter.setSubmitButtonVisable(false);
		imageUploadPresenter.setImageUploadObserver(this);
		view.imageUploadWidget().clear();
		view.imageUploadWidget().add(imageUploadPresenter.getWidget());
		
		if(template.getImage() != null){
			ImagePresenter imagePresenter = injector.getImagePresenter();
			//imagePresenter.setImage(template.getImage());
			imagePresenter.setImage(template.getImage(), 16, 16);
			view.staticImageWidget().clear();
			view.staticImageWidget().add(imagePresenter.getWidget());
		}
		
		view.setMode(EditableTableMode.VIEW);
	}
	
	Widget getImageWidget(){
		return view.getImageWidget();
	}
	Widget getUrlPrefixWidget(){
		return view.getUrlPrefixWidget();
	}
	Widget getDisplayNameWidget(){
		return view.getDisplayNameWidget();
	}
	Widget getControlsWidget(){
		return view.getControlsWidget();
	}

	@Override
	public void notifyImageActionCompletWithStatus(String imageId) {
		if(imageId != null){
			UserObjectTemplateCommand cmd = new UserObjectTemplateCommand();
			cmd.setAction(action);
			cmd.setTemplateType(UserObjectTemplateConstants.TEMPLATE_WEBPAGE);
			cmd.setDisplayName(view.displayNameText().getText());
			cmd.setImageId(imageId);
			cmd.setTemplateId(template.getTemplateId());
			cmd.setTemplateValue(view.urlPrefixText().getText());
			
			RpcServiceAsync service = injector.getService();
			service.execute(cmd, buildTemplateCallback());
		}
	}
	
	private void processDeleteRequest() {
		UserObjectTemplateCommand cmd = new UserObjectTemplateCommand();
		cmd.setAction(ActionType.DELETE);
		cmd.setTemplateId(template.getTemplateId());
		RpcServiceAsync service = injector.getService();
		service.execute(cmd, buildTemplateCallbackForDelete());
	}

	private AsyncCallback<GenericCommandResult<GUserObjectTemplate>> buildTemplateCallbackForDelete() {
		return new CommandResultCallback<GenericCommandResult<GUserObjectTemplate>>(){
			@Override
			public void onSuccess(GenericCommandResult<GUserObjectTemplate> result) {
				view.delete();
			}
		};
	}

	private CommandResultCallback<GenericCommandResult<GUserObjectTemplate>> buildTemplateCallback() {
		return new CommandResultCallback<GenericCommandResult<GUserObjectTemplate>>(){
			@Override
			public void onSuccess(GenericCommandResult<GUserObjectTemplate> result) {
				init(result.getObject());
			}
		};
	}
}
