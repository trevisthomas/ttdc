package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.messaging.EventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

public class ImageUploadView implements ImageUploadPresenter.View{
	private final HorizontalPanel formContainerPanel = new HorizontalPanel();
	private final VerticalPanel formWidgetPanel = new VerticalPanel(); //Internal panel for form elements
	private final FileUpload fileUpload = new FileUpload();
	private final FormPanel form = new FormPanel();
	private final Button submitButton = new Button("Upload Image");
	
	public ImageUploadView() {
		formContainerPanel.add(form);
		formContainerPanel.add(submitButton);
		
		
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);
	    form.setAction(GWT.getHostPageBaseURL() + "imageupload");
	    fileUpload.setName("uploadFormElement");//This is critical so that the servlet can know wtf.
	    
	    form.setWidget(formWidgetPanel);
	    //form.addSubmitCompleteHandler(getImageUploadSubmitCompleteHandler());
	    form.addSubmitHandler(getImageUploadSubmitFormHandler(fileUpload));
	    
	    formWidgetPanel.add(fileUpload);
	    
	    submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
	}
	
	@Override
	public Widget getWidget() {
		return formContainerPanel;
	}
	
	FormPanel.SubmitHandler getImageUploadSubmitFormHandler(final FileUpload fileUploadObject){
		return new FormPanel.SubmitHandler(){
			@Override
			public void onSubmit(SubmitEvent event) {
				if (fileUploadObject.getFilename().length() == 0) {
					EventBus.fireErrorMessage("You must select a file!");
					event.cancel();
		        }
			}
		}; 
	}
	
	@Override
	public void addImageUploadSubmitHandler(FormPanel.SubmitHandler handler){
		form.addSubmitHandler(handler);
	}
	@Override
	public void addImageUploadSubmitCompleteHandler(FormPanel.SubmitCompleteHandler handler){
		 form.addSubmitCompleteHandler(handler);
	}
	
	@Override
	public void setSubmitButtonVisable(boolean visible) {
		submitButton.setVisible(visible);
	}

	@Override
	public void submit() {
		form.submit();
	}

	/** 
	 * I created this method when implementing the template editor.  
	 * It's purpose is to allow a user to know if the image has been 
	 * set.  It was of use for allowing the image to be optional during 
	 * an update. 
	 */
	@Override
	public boolean isReadyForSubmit() {
		return !(fileUpload.getFilename().length() == 0);
	}
}
