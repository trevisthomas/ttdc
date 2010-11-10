package org.ttdc.gwt.client.uibinder.post;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.PostCrudCommand;
import org.ttdc.gwt.shared.commands.results.PostCommandResult;
import org.ttdc.gwt.shared.commands.types.PostActionType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class NewMoviePanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, NewMoviePanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    private Injector injector;
    
    @UiField (provided=true) TextBox titleElement;
    @UiField (provided=true) TextBox releaseYearElement;
    @UiField (provided=true) TextBox imdbUrlElement;
    @UiField (provided=true) TextBox posterUrlElement;
    @UiField (provided=true) Button createButtonElement;
    @UiField (provided=true) Button cancelButtonElement;
    @UiField (provided=true) Button editButtonElement;
    
    private String originalImageName = "";
    private GPost post;
    private PostActionType mode;
    
    @Inject
    public NewMoviePanel(Injector injector) { 
    	this.injector = injector;
    	
    	titleElement = new TextBox();
    	releaseYearElement = new TextBox();
    	imdbUrlElement = new TextBox();
    	posterUrlElement = new TextBox();
    	createButtonElement = new Button("Create");
    	cancelButtonElement = new Button("Cancel");
    	editButtonElement = new Button("Edit");
    	
    	initWidget(binder.createAndBindUi(this)); 
	}
    
    public void init(){
    	mode = PostActionType.CREATE;
    	editButtonElement.setVisible(false);
    	createButtonElement.setVisible(true);
    }
    
    public void init(GPost post){
    	//Init for edit mode
    	mode = PostActionType.UPDATE;
    	editButtonElement.setVisible(true);
    	createButtonElement.setVisible(false);
    	originalImageName = post.getImage().getName();
    	
    	titleElement.setText(post.getTitleTag().getValue()); //Raw title value without the date
    	releaseYearElement.setText(post.getPublishYear().toString());
    	imdbUrlElement.setText(post.getUrl());
    	if(post.getImage() != null)
    		posterUrlElement.setText(post.getImage().getName());
    	
    	this.post = post;
    }
    
    @UiHandler("createButtonElement")
    void createClickHandler(ClickEvent handler){
    	createPost();
    }
    
    @UiHandler("cancelButtonElement")
    void cancelClickHandler(ClickEvent handler){
    	close();
    }
    
    @UiHandler("editButtonElement")
    void editClickHandler(ClickEvent handler){
    	createPost();
    }
    
    private void createPost() {
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(mode);
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		
		cmd.setMovie(true);
		
		if(post != null){
			cmd.setPostId(post.getPostId());
		}
		if(!originalImageName.equals(posterUrlElement.getText())){
			cmd.setImageUrl(posterUrlElement.getText());
		}
		else{
			cmd.setImageUrl(null);
		}
		cmd.setUrl(imdbUrlElement.getText());
		cmd.setYear(releaseYearElement.getText());
		cmd.setTitle(titleElement.getText());
		
//		cmd.setLogin(login)
//		cmd.setPassword(password)
		
		
		CommandResultCallback<PostCommandResult> callback = buildCreatePostCallback();
		
		injector.getService().execute(cmd,callback);
		
	}
    
//    private void editPost(){
//    	PostCrudCommand cmd = new PostCrudCommand();
//		cmd.setAction(PostActionType.UPDATE);
//		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
//		cmd.setMovie(true);
//		if(!originalImageName.equals(posterUrlElement.getText())){
//			cmd.setImageUrl(posterUrlElement.getText());
//		}
//		else{
//			cmd.setImageUrl(null);
//		}
//		cmd.setUrl(imdbUrlElement.getText());
//		cmd.setYear(releaseYearElement.getText());
//		cmd.setTitle(titleElement.getText());
//		
////		cmd.setLogin(login)
////		cmd.setPassword(password)
//		
//		
//		CommandResultCallback<PostCommandResult> callback = buildCreatePostCallback();
//		
//		injector.getService().execute(cmd,callback);
//		
//    }

	private CommandResultCallback<PostCommandResult> buildCreatePostCallback() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				//Window.alert("Created");
				cancelButtonElement.click();
				//Window.Location.reload();
				PostEvent event = new PostEvent(PostEventType.NEW_FORCE_REFRESH, result.getPost());
				EventBus.fireEvent(event);
			}
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				super.onFailure(caught);
			}
		};
		return callback;
	}
	
	private void close(){
		setVisible(false);
	}
	
	 
	public void addCancelClickHandler(ClickHandler handler){
		cancelButtonElement.addClickHandler(handler);
	}
}
