package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GImage;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.inject.Inject;

public class ImagePresenter extends BasePresenter<ImagePresenter.View>{
	public interface View extends BaseView{
		void setImageTitle(String title);
		void setImageWidth(int width);
		void setImageHeight(int height);
		void setImage(GImage image);
		void setLinkToFull(boolean enable);
		void setShowAsThumbnail(boolean asThumbnail);
		HasClickHandlers imageClickHandler();
		void setClickableCursor(boolean clickable);
		void setLinkUrl(String url);
	}
	
	@Inject
	public ImagePresenter(Injector injector) {
		super(injector,injector.getImageView());
	}

	public void setImage(GImage image){
		view.setImage(image);
	}
	
	public void setImage(GImage image, String title){
		view.setImage(image);
		view.setImageTitle(title);
	}
	public void setImage(GImage image, String title, int width, int height){
		view.setImage(image);
		view.setImageTitle(title);
		maybeSetHightAndWidth(image, width, height);
		
	}
	private void maybeSetHightAndWidth(GImage image, int width, int height) {
		if(image.getWidth() > width || image.getHeight() > height){
			view.setImageWidth(width);
			view.setImageHeight(height);
		}
	}

	public void setImage(GImage image, int width, int height){
		setImage(image,image.getName(),width,height);
	}
	
	public void useThumbnail(boolean thumbnail){
		view.setShowAsThumbnail(thumbnail);
	} 
	
	public void linkToFullImage(boolean enable){
		view.setLinkToFull(enable);
	}

	/**
	 * Warning. If you call this method twice you'll probably get two actions on click.
	 * So if you want to use it that way, you're gonna want to clear the handlers before
	 * adding a new one. 
	 * 
	 * @param linkToken
	 */
	public void setLinkToken(final HistoryToken linkToken) {
		if(linkToken != null){
			view.setClickableCursor(true);
			view.imageClickHandler().addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					EventBus.fireHistoryToken(linkToken);
				}
			});
		}
	}
	
	public void setLinkUrl(String url){
		view.setLinkUrl(url);
	}
}
