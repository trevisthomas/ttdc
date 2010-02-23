package org.ttdc.gwt.client.presenters.admin;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.ImageActivityObserver;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.ImageCrudCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ImageRowPresenter extends BasePresenter<ImageRowPresenter.View>{
	public interface View extends BaseView{
		Widget getControlsWidget();
		Widget getNameWidget();
		 
		HasClickHandlers updateButton();
		HasClickHandlers deleteButton();
		
		HasText nameTextBox();
		
	}
	
	private String imageId;
	
	@Inject
	public ImageRowPresenter(Injector injector) {
		super(injector, injector.getImageRowView());
	}

	public Widget getControlsWidget(){
		return view.getControlsWidget();
	}
	
	public Widget getNameWidget(){
		return view.getNameWidget();
	}
	
	public void init(final ImageActivityObserver observer, GImage image){
		this.imageId = image.getImageId();
		
		view.nameTextBox().setText(image.getName());
		
		view.updateButton().addClickHandler(createUpdateButtonClickHandler(observer));
		
		view.deleteButton().addClickHandler(createDeleteButtonClickHandler(observer));
	}

	private ClickHandler createDeleteButtonClickHandler(final ImageActivityObserver observer) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ImageCrudCommand cmd = new ImageCrudCommand(ActionType.DELETE);
				cmd.setName(view.nameTextBox().getText());
				cmd.setImageId(imageId);
				CommandResultCallback<GenericCommandResult<GImage>> callback = deleteImageCallback(observer);
				EventBus.fireMessage("Deleting...");
				RpcServiceAsync service = injector.getService();
				service.execute(cmd, callback);
			}
		};
	}

	private ClickHandler createUpdateButtonClickHandler(final ImageActivityObserver observer) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ImageCrudCommand cmd = new ImageCrudCommand(ActionType.UPDATE);
				cmd.setName(view.nameTextBox().getText());
				cmd.setImageId(imageId);
				CommandResultCallback<GenericCommandResult<GImage>> callback = updateImageCallback(observer);
				EventBus.fireMessage("Updating...");
				RpcServiceAsync service = injector.getService();
				service.execute(cmd, callback);
			}
			
		};
	}
	
	protected CommandResultCallback<GenericCommandResult<GImage>> deleteImageCallback(final ImageActivityObserver observer) {
		return new CommandResultCallback<GenericCommandResult<GImage>>(){
			@Override
			public void onSuccess(GenericCommandResult<GImage> result) {
				EventBus.fireMessage(result.getMessage());
				observer.notifyImageActionCompletWithStatus(null);//Just want to tell the thing to refresh.
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.fireErrorMessage(caught.getMessage());
			}
		};
	}

	private CommandResultCallback<GenericCommandResult<GImage>> updateImageCallback(final ImageActivityObserver observer) {
		return new CommandResultCallback<GenericCommandResult<GImage>>(){
			@Override
			public void onSuccess(GenericCommandResult<GImage> result) {
				view.nameTextBox().setText(result.getObject().getName());
				EventBus.fireMessage(result.getMessage());
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.fireErrorMessage(caught.getMessage());
			}
		};
	}
	
}
