package org.ttdc.gwt.client.presenters.shared;

import java.util.Set;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.inject.Inject;

public class ImageUploadPresenter extends BasePresenter<ImageUploadPresenter.View>{
	private ImageActivityObserver observer;
	public interface View extends BaseView{
		void setSubmitButtonVisable(boolean visible);
		void submit();
		void addImageUploadSubmitHandler(SubmitHandler handler);
		void addImageUploadSubmitCompleteHandler(SubmitCompleteHandler handler);
		boolean isReadyForSubmit();
	}
	
	@Inject
	public ImageUploadPresenter(Injector injector) {
		super(injector,injector.getImageUploadView());
		view.addImageUploadSubmitCompleteHandler(getImageUploadSubmitCompleteHandler());
	}
	
	public void setImageUploadObserver(ImageActivityObserver observer){
		this.observer = observer; 
	}
	
	public void setSubmitButtonVisable(boolean visible){
		view.setSubmitButtonVisable(visible);
	}
	
	public void submit(){
		if(view.isReadyForSubmit())
			view.submit();
		else{
			observer.notifyImageActionCompletWithStatus("");//Hm.
		}
	}
	
	public boolean isReadyForSubmit(){
		return view.isReadyForSubmit();
	}
	
	FormPanel.SubmitCompleteHandler getImageUploadSubmitCompleteHandler(){
		return new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				//String jsonResult = removePreTags(event.getResults());
				String jsonResult = event.getResults();
				JSONObject jso = null;
				JSONValue jsv = null;
				jsv = JSONParser.parse(jsonResult);
				jso = jsv.isObject();

				if (jso != null) {
					Set<String> keySet = jso.keySet();
					for(String key : keySet){
						String message = translateJsonString(jso, key);
						if("success".equals(key)){
							EventBus.fireMessage(message);
						}
						else if("guid".equals(key)){
							observer.notifyImageActionCompletWithStatus(message);
						}
						else if("error".equals(key)){
							EventBus.fireErrorMessage(message);
						}
						else{
							EventBus.fireErrorMessage("Image Upload Complete handler got garbage from json");
						}
					}
				}
				else
					EventBus.fireErrorMessage("JSO object is null.");
				
				//observer.notifyImageActionCompletWithStatus(null);
			}

			private String translateJsonString(JSONObject jso, String status) {
				JSONValue v = jso.get(status);
				JSONString message = v.isString();
				if(message != null){
					return message.stringValue();
				}
				else{
					return "JSON was null";
				}
			}
		};
	}
		
	// This method strips the <pre> tag out of the response.
    // That <pre> tag in the response because of a
    // GWT 1.3.3 bug. (still in GWT2) Grabbed this from the Google Web Tollkit Solutions book
	
	//Issue resolved by setting content type to text/html on the servlet
	
//	private static String removePreTags(String response) {
//		if (response.startsWith("<")) { //IE8 was making them upper case!!? and Chrome put attributes in it!
//			int index1;
//			int index2;
//			index1 = response.indexOf('>')+1;
//			index2 = response.lastIndexOf('<');
//			return response.substring(index1, index2);
//			
//		} else {
//			return response;
//		}
//	}
}
