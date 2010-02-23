package org.ttdc.gwt.client.presenters.admin;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GStyle;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.StyleListCommand;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class StyleManagementPresenter extends BasePresenter<StyleManagementPresenter.View>{
	
	public interface View extends BaseView{
		void addStyle(Widget defaultStyle, Widget cssFileName, Widget displayName, Widget description, Widget controls );
		HasClickHandlers showAddRowClickHandler();
		HasWidgets pageMessagesPanel();
		
	}
	
	@Inject
	public StyleManagementPresenter(Injector injector) {
		super(injector,injector.getStyleManagementView());
		
		view.showAddRowClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createNewAddRow();
			}
		});
	}
	
	protected void createNewAddRow() {
		//This is to get the "add" row
		StyleRowPresenter rowPresenter = injector.getStyleRowPresenter();
		rowPresenter.init();
		view.addStyle(rowPresenter.getDefaultStyleWidget(),
					  rowPresenter.getStyleNameWidget(), 
					  rowPresenter.getCssFileNameWidget(),
					  rowPresenter.getDescriptonWidget(), 
					  rowPresenter.getActionsWidget());
	}

	public void init(HistoryToken token) {
		StyleListCommand cmd = new StyleListCommand();
		RpcServiceAsync service = injector.getService();
		service.execute(cmd, buildListCallback());
	}
	
	private CommandResultCallback<GenericListCommandResult<GStyle>> buildListCallback() {
		return new CommandResultCallback<GenericListCommandResult<GStyle>>(){
			@Override
			public void onSuccess(GenericListCommandResult<GStyle> result) {
				for(GStyle style : result.getList()){
					StyleRowPresenter rowPresenter = injector.getStyleRowPresenter();
					rowPresenter.init(style);
					view.addStyle(rowPresenter.getDefaultStyleWidget(), 
								  rowPresenter.getStyleNameWidget(), 
								  rowPresenter.getCssFileNameWidget(),
								  rowPresenter.getDescriptonWidget(), 
								  rowPresenter.getActionsWidget());
				}
				EventBus.fireMessage(result.getMessage());
			}
		};
	}
	
}
