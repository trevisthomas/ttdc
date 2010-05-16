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
    @UiField (provided=true) Button createButtonElement = new Button("Create");
    @UiField (provided=true) Button cancelButtonElement = new Button("Cancel");
    @UiField (provided=true) Button editButtonElement = new Button("Edit");
    
    private String originalImageName = "";
    
    @Inject
    public NewMoviePanel(Injector injector) { 
    	this.injector = injector;
    	
    	titleElement = new TextBox();
    	releaseYearElement = new TextBox();
    	imdbUrlElement = new TextBox();
    	posterUrlElement = new TextBox();
//    	createButtonElement = 
//    	cancelButtonElement = new Button("Cancel");
//    	editButtonElement = new Button("Edit");
    	
    	initWidget(binder.createAndBindUi(this)); 
	}
    
    public void init(){
    	
    }
    
    public void init(GPost post){
    	//Init for edit mode
    	originalImageName = post.getImage().getName();
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
    	
    }
    
    private void createPost() {
		PostCrudCommand cmd = new PostCrudCommand();
		cmd.setAction(PostActionType.CREATE);
		cmd.setConnectionId(ConnectionId.getInstance().getConnectionId());
		
		cmd.setMovie(true);
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

	private CommandResultCallback<PostCommandResult> buildCreatePostCallback() {
		CommandResultCallback<PostCommandResult> callback = new CommandResultCallback<PostCommandResult>(){
			@Override
			public void onSuccess(PostCommandResult result) {
				//Window.alert("Created");
				close();
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

}
