package org.ttdc.gwt.client.presenters.admin;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.shared.ImageActivityObserver;
import org.ttdc.gwt.client.presenters.shared.ImagePresenter;
import org.ttdc.gwt.client.presenters.shared.ImageUploadPresenter;
import org.ttdc.gwt.client.presenters.shared.PaginationPresenter;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.ImageCrudCommand;
import org.ttdc.gwt.shared.commands.ImageListCommand;
import org.ttdc.gwt.shared.commands.results.GenericCommandResult;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.ActionType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ImageManagementPresenter extends BasePresenter<ImageManagementPresenter.View> implements ImageActivityObserver{
	public interface View extends BaseView{
		void addImage(Widget imageWidget, Widget nameWidget, Widget personWidget, String size, Widget controlsWidget);
		HasWidgets paginatorTarget();
		HasWidgets imageUploadTarget();
		HasClickHandlers scrapeImageButton();
		HasText urlTextBox();
		HasText nameTextBox();
		void clear();
		// TODO: use the PageMessagePresenter/View		
	}
	
	private final ImageActivityObserver observer = this; //This self reference is so that the callback class can access it.
	private HistoryToken previousToken;
		
	@Inject
	public ImageManagementPresenter(Injector injector) {
		super(injector,injector.getImageManagementView());
		
		ImageUploadPresenter presenter = injector.getImageUploadPresenter();
		presenter.setImageUploadObserver(this);
		view.imageUploadTarget().add(presenter.getWidget());
		
		view.scrapeImageButton().addClickHandler(createWebGrabButtonClickHandler(this));
	}
	
	private ClickHandler createWebGrabButtonClickHandler(final ImageActivityObserver observer) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ImageCrudCommand cmd = new ImageCrudCommand(ActionType.CREATE);
				cmd.setName(view.nameTextBox().getText());
				cmd.setUrl(view.urlTextBox().getText());
				CommandResultCallback<GenericCommandResult<GImage>> callback = webGrabImageCallback(observer);
				EventBus.fireMessage("Scraping image from URL...");
				RpcServiceAsync service = injector.getService();
				service.execute(cmd, callback);
			}
		};
	}

	
	
	public void init(HistoryToken token){
		previousToken = new HistoryToken();
		previousToken.load(token);
		CommandResultCallback<PaginatedListCommandResult<GImage>> callback = buildImageListCallback(token);
		
		ImageListCommand cmd = new ImageListCommand();
		if(token.hasParameter(HistoryConstants.PAGE_NUMBER_KEY))
			cmd.setCurrentPage(token.getParameterAsInt(HistoryConstants.PAGE_NUMBER_KEY));
		
		RpcServiceAsync service = injector.getService();
		service.execute(cmd, callback);
	}
	
	private CommandResultCallback<GenericCommandResult<GImage>> webGrabImageCallback(final ImageActivityObserver observer) {
		return new CommandResultCallback<GenericCommandResult<GImage>>(){
			@Override
			public void onSuccess(GenericCommandResult<GImage> result) {
				view.clear();
				EventBus.fireMessage(result.getMessage());
				observer.notifyImageActionCompletWithStatus(result.getObject().getImageId());//Just want to tell the thing to refresh.
			}
			@Override
			public void onFailure(Throwable caught) {
				EventBus.fireErrorMessage(caught.getMessage());
			}
		};
	}
	
	private CommandResultCallback<PaginatedListCommandResult<GImage>> buildImageListCallback(final HistoryToken token) {
		CommandResultCallback<PaginatedListCommandResult<GImage>> callback = new CommandResultCallback<PaginatedListCommandResult<GImage>>(){
			@Override
			public void onSuccess(PaginatedListCommandResult<GImage> result) {
				view.clear();
				for(GImage image : result.getResults().getList()){
					HyperlinkPresenter personLink = injector.getHyperlinkPresenter();
					personLink.setPerson(image.getOwner());
					
					ImagePresenter imagePresenter = injector.getImagePresenter();
					imagePresenter.setImage(image, 60, -1);
					imagePresenter.linkToFullImage(true);
										
					ImageRowPresenter imageRowPresenter = injector.getImageRowPresenter();
					imageRowPresenter.init(observer, image);
					view.addImage(imagePresenter.getWidget(), 
								  imageRowPresenter.getNameWidget(), 
								  personLink.getWidget(), 
								  image.getWidth() + "x" +image.getHeight(),
								  imageRowPresenter.getControlsWidget());
				}
				PaginationPresenter paginator = injector.getPaginationPresenter();
				paginator.initialize(token, result.getResults());
				view.paginatorTarget().add(paginator.getWidget());
			}
		};
		return callback;
	}

	@Override
	public void notifyImageActionCompletWithStatus(String imageId) {
		init(previousToken);
	}
}
