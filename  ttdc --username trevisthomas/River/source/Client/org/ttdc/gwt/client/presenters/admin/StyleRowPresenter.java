package org.ttdc.gwt.client.presenters.admin;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.StyleCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class StyleRowPresenter extends BasePresenter<StyleRowPresenter.View>{
	public interface View extends BaseView{
		Widget getStyleNameWidget();
		Widget getCssFileNameWidget();
		Widget getDescriptonWidget();
		Widget getActionsWidget();
		Widget getDefaultStyleWidget();
		
		HasClickHandlers updateButton();
		HasClickHandlers deleteButton();
		HasClickHandlers cancelButton();
		HasClickHandlers editButton();
		HasClickHandlers addButton();
		HasClickHandlers defaultStyleCheckBox();
		
		HasText styleNameText();
		HasText cssFileNameText();
		HasText descriptionText();
		HasValue<Boolean> defaultStyle();
		
		void delete();//Trevis, UserObjectTemplateRowPresenter/View also does this and at the moment the implementation is a bit of a hack
		void setMode(EditableTableMode editableTableMode);
	}
	
	private GStyle style = null;
	
	@Inject
	public StyleRowPresenter(Injector injector) {
		super(injector, injector.getStyleRowView());
		
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
		
		view.addButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processAddOrUpdateRequest(ActionType.CREATE);
			}
		});
		
		view.updateButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processAddOrUpdateRequest(ActionType.UPDATE);
			}
		});
		
		view.deleteButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processDeleteRequest();
			}
		});
		
		view.defaultStyleCheckBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				processDefaultStyleRequest();
			}
		});
	}
	
	

	public void init(){
		init(new GStyle());
		view.setMode(EditableTableMode.ADD);
	}
	public void init(GStyle style){
		this.style = style;
		
		view.descriptionText().setText(style.getDescription());
		view.styleNameText().setText(style.getName());
		view.cssFileNameText().setText(style.getCss());
		view.defaultStyle().setValue(style.isDefaultStyle());
		view.setMode(EditableTableMode.VIEW);
	}
	
	Widget getStyleNameWidget(){
		return view.getStyleNameWidget();
	}
	Widget getCssFileNameWidget(){
		return view.getCssFileNameWidget();
	}
	Widget getDescriptonWidget(){
		return view.getDescriptonWidget();
	}
	Widget getActionsWidget(){
		return view.getActionsWidget();
	}
	Widget getDefaultStyleWidget(){
		return view.getDefaultStyleWidget();
	}
	
	private void processDeleteRequest() {
		StyleCommand cmd = new StyleCommand();
		cmd.setAction(ActionType.DELETE);
		cmd.setStyleId(style.getStyleId());
		RpcServiceAsync service = injector.getService();
		service.execute(cmd, buildCallbackForDelete());
	}
	
	private void processAddOrUpdateRequest(ActionType action){
		StyleCommand cmd = new StyleCommand();
		cmd.setAction(action);
		if(style != null)
			cmd.setStyleId(style.getStyleId());
		cmd.setDisplayName(view.styleNameText().getText());
		cmd.setCssFileName(view.cssFileNameText().getText());
		cmd.setDescription(view.descriptionText().getText());
		
		RpcServiceAsync service = injector.getService();
		service.execute(cmd, buildCallback());
	}
	
	protected void processDefaultStyleRequest() {
		if(style.isDefaultStyle()){
			view.defaultStyle().setValue(true);
			return;//dont reset it
		}
		
		StyleCommand cmd = new StyleCommand();
		cmd.setAction(ActionType.UPDATE);
		cmd.setStyleId(style.getStyleId());
		cmd.setDefaultStyle(true);
		RpcServiceAsync service = injector.getService();
		service.execute(cmd, buildStyleUpdateCallback());
	}


	private CommandResultCallback<GenericCommandResult<GStyle>> buildStyleUpdateCallback() {
		return new CommandResultCallback<GenericCommandResult<GStyle>>(){
			@Override
			public void onSuccess(GenericCommandResult<GStyle> result) {
				EventBus.getInstance().reload();//NUKE!
			}
		};
	}

	private AsyncCallback<GenericCommandResult<GStyle>> buildCallbackForDelete() {
		return new CommandResultCallback<GenericCommandResult<GStyle>>(){
			@Override
			public void onSuccess(GenericCommandResult<GStyle> result) {
				view.delete();
			}
		};
	}

	private CommandResultCallback<GenericCommandResult<GStyle>> buildCallback() {
		return new CommandResultCallback<GenericCommandResult<GStyle>>(){
			@Override
			public void onSuccess(GenericCommandResult<GStyle> result) {
				init(result.getObject());
			}
		};
	}

}
