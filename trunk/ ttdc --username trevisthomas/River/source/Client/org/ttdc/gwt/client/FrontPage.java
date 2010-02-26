package org.ttdc.gwt.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ttdc.gwt.client.autocomplete.TagSugestionOracle;
import org.ttdc.gwt.client.components.views.FrontPageView;
import org.ttdc.gwt.client.components.widgets.UserProfileWidget;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.HistoryMonitor;
import org.ttdc.gwt.client.messaging.ServerEventMonitor;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.history.HistoryEvent;
import org.ttdc.gwt.client.messaging.history.HistoryEventListener;
import org.ttdc.gwt.client.presenters.HistoryEventPresenterManager;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.GetPersonDetailsCommand;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;

public class FrontPage implements EntryPoint, HistoryEventListener{
	private final static String VIEW_HOME = "view=main";
	//private final RpcServiceAsync service = GWT.create(RpcService.class);
	
	private final Injector injector = GWT.create(Injector.class);

	public void onModuleLoad() {
		
		EventBus.getInstance().addListener(new MessageEventListener(){
			public void onMessageEvent(MessageEvent event) {
				if(event.getType().equals(MessageEventType.SYSTEM_ERROR))
					Window.alert(event.getSource());
			}
		});
		
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler(){
			public void onUncaughtException(Throwable e) {
				EventBus.getInstance().fireEvent(new MessageEvent(MessageEventType.SYSTEM_ERROR,e.getMessage()));				
			}
		});
		
		/*
		 * Trevis. Think about why this had to happen first. 
		 * What i saw is that if this came after the fireCurrentHistoryState call
		 * that my search page showed nothing.  It seems that requesting a new url
		 * from the bar at the top reloads the EntryPoint! Ok, yeah that now makes 
		 * perfect sense.  Thanks for figureing it out!
		*/ 
		HistoryMonitor.initInstance(); //To listen to GWT for browser history changes and generate HistoryEvents
		HistoryEventPresenterManager.initInstance(injector); //To listen to EventBus for HistoryEvents
		
		EventBus.getInstance().addListener(this);
		String historyToken = History.getToken();
        if (historyToken.length() == 0) {
        	History.newItem(VIEW_HOME);
                
        } else {
        	History.newItem(historyToken);
        }
		
        
	    History.fireCurrentHistoryState();
	    //new ServerEventMonitor();
	    
	}
	
	
	public void renderViewHome(){
		RootPanel.get("content").clear();
		
		//Make this oracle GIN'jected
		//TagSugestionOracle oracle = new TagSugestionOracle(injector);
		HorizontalPanel hPanel = new HorizontalPanel();
		TagSugestionOracle oracle = injector.getTagSugestionOracle();
		SuggestBox box = oracle.createSuggestBoxPostTitle();
		hPanel.add(new Label("Title"));
		hPanel.add(box);
		
		oracle = injector.getTagSugestionOracle();
		box = oracle.createSuggestBoxForPostView(Arrays.asList("3FE5F7A3-F91D-41E3-9225-E2538D59E5C3"));
		hPanel.add(new Label("Exclude Corporate Goodness"));
		hPanel.add(box);

		
		oracle = injector.getTagSugestionOracle();
		box = oracle.createSuggestBoxForSearch(Arrays.asList("3FE5F7A3-F91D-41E3-9225-E2538D59E5C3"));
		hPanel.add(new Label("Union Corporate Goodness"));
		hPanel.add(box);

		oracle = injector.getTagSugestionOracle();
		box = oracle.createSuggestBoxForTopics();
		hPanel.add(new Label("Topics (root threads)"));
		hPanel.add(box);

		RootPanel.get("content").add(hPanel);
		
		//
		
		RootPanel.get("content").add(FrontPageView.createInstance());
	}

	//TODO remove this stuff completly this class has no need to listen for history events
	// or at least it wont once i get everything using MVP
	
	// Nov 2 2009, yeah the parts of this that are still necessary should ove to HistoryEventPresneter
	
	public void onHistoryEvent(HistoryEvent event) {
		String view = event.getSource().getParameter("view");
		if("main".equals(view)){
			renderViewHome();
		}
		else if("personDetails".equals(view)){
			String personId = event.getSource().getParameter("personId");
			//Window.alert(personId);
			GetPersonDetailsCommand cmd = new GetPersonDetailsCommand(personId);
			
			RpcServiceAsync service = injector.getService();
			service.execute(cmd, new CommandResultCallback<PersonCommandResult>(){
				public void onSuccess(PersonCommandResult result) {
					RootPanel.get("content").clear();
					RootPanel.get("content").add(UserProfileWidget.createInstance(result.getPerson()));
				}
			});
		}
		else if("".equals(view)){
			renderViewHome();
		}
		
	}
}
