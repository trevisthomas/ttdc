package org.ttdc.gwt.client.components.widgets;

import java.util.List;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.services.RpcService;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.GetLatestFlatCommand;
import org.ttdc.gwt.shared.commands.GetLatestHierarchyCommand;
import org.ttdc.gwt.shared.commands.results.PostListCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HierarchyFlatTabPanelWidget extends Composite implements PersonEventListener{
	private final DecoratedTabPanel root = new DecoratedTabPanel();
	private final Panel postPanelFlat = new VerticalPanel();
	private final Panel postPanelHierarchy = new VerticalPanel();
	private final Label loadingFlatLabel = new Label("Loading...");
	private final Label loadingHierarchMessageLabel = new Label("Loading Hierarchy...");
	private final RpcServiceAsync service = GWT.create(RpcService.class);
	
	public static HierarchyFlatTabPanelWidget buildHierarchyFlatTabPanel(){
		HierarchyFlatTabPanelWidget newInstance = new HierarchyFlatTabPanelWidget();
		return newInstance;
	}
	private HierarchyFlatTabPanelWidget() {
		root.add(initializePostFlatPanel(), "Flat");
		root.add(initializePostHierarchyPanel(), "Hierarchy");
		root.selectTab(0);
		
		configureHierarchySelectedHandler();
		EventBus.getInstance().addListener(this);
		
		initWidget(root);
	}

	private void configureHierarchySelectedHandler() {
		root.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				if (event.getItem().intValue() == 1) {
					if (loadingHierarchMessageLabel.isAttached()) 
						loadHierarchyPosts();
				}
				if (event.getItem().intValue() == 0) {
					if (loadingFlatLabel.isAttached())
						loadFlatPosts();
				}
			}
		});
	}	
	
	private void loadFlatPosts(){
		GetLatestFlatCommand getLatestFlatCommand = new GetLatestFlatCommand();
		CommandResultCallback<PostListCommandResult> gotLatestFlat = new CommandResultCallback<PostListCommandResult>(){
			public void onSuccess(PostListCommandResult result) {
				renderPosts(result.getPosts(),postPanelFlat,true);
			}
		};
		service.execute(getLatestFlatCommand, gotLatestFlat);
	}
	
	private void loadHierarchyPosts(){
		GetLatestHierarchyCommand getLatestHierarchyCommand = new GetLatestHierarchyCommand();
		CommandResultCallback<PostListCommandResult> gotLatestFlat = new CommandResultCallback<PostListCommandResult>(){
			public void onSuccess(PostListCommandResult result) {
				renderPosts(result.getPosts(),postPanelHierarchy,false);
			}
		};
		service.execute(getLatestHierarchyCommand, gotLatestFlat);
	}
	
	private void renderPosts(List<GPost> posts,Panel target, boolean flat) {
		target.clear();	
		for(GPost post : posts){
			PostPanelWidget widget = new PostPanelWidget();
			widget.setPost(post);
			target.add(widget);
			if(post.hasChildren() && !flat){
				Panel newTarget = renderIndention(target);
				renderPosts(post.getPosts(),newTarget,flat);
			}
		}
	}
	private Panel renderIndention(Panel target) {
		HorizontalPanel hpanel = new HorizontalPanel();
		target.add(hpanel);
		VerticalPanel vpanel = new VerticalPanel();
		hpanel.add(createSpacer());
		hpanel.add(vpanel);
		return vpanel;
	}
	
	private Panel createSpacer(){
		return new HTMLPanel("&nbsp;&nbsp;&nbsp;&nbsp;");
	}
	
	public void onPersonEvent(PersonEvent event) {
		//TODO: trevis, this is probably a spot where you should decide which tab to show, changing users could change the default tab
		if(event.getType() == PersonEventType.USER_CHANGED)
			reloadContent();
	}
	private void reloadContent(){
		initializePostFlatPanel();
		initializePostHierarchyPanel();
		if(postPanelFlat.isVisible()){
			loadFlatPosts();
		}
		else if(postPanelHierarchy.isVisible()){
			loadHierarchyPosts();
		}
	}
	private Panel initializePostFlatPanel(){
		postPanelFlat.clear();
		postPanelFlat.add(loadingFlatLabel);
		return postPanelFlat;
	}
	private Panel initializePostHierarchyPanel(){
		postPanelHierarchy.clear();
		postPanelHierarchy.add(loadingHierarchMessageLabel);
		return postPanelHierarchy;
	}
}
