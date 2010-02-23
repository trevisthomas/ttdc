package org.ttdc.gwt.client.components.views;

import org.ttdc.gwt.client.components.widgets.HeaderWidget;
import org.ttdc.gwt.client.components.widgets.HierarchyFlatTabPanelWidget;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FrontPageView extends Composite implements PersonEventListener{
	private final VerticalPanel root = new VerticalPanel();
	private final HierarchyFlatTabPanelWidget hierarchyFlatTabPanelWidget;
	private final HeaderWidget headerWidget;
	private FrontPageView(){
		headerWidget = HeaderWidget.createInstance();
		//EventBus.getInstance().addListener(this);
		root.add(headerWidget);
		hierarchyFlatTabPanelWidget = HierarchyFlatTabPanelWidget.buildHierarchyFlatTabPanel();
		root.add(hierarchyFlatTabPanelWidget);
		
		initWidget(root);
	}
	public static FrontPageView createInstance(){
		FrontPageView view = new FrontPageView();
		return view;
	}
	
	public void onPersonEvent(PersonEvent event) {
		if(event.getType().equals(PersonEventType.TRAFFIC))
			Window.alert("Hey, "+event.getSource().getName()+" Just logged in.");
		//hierarchyFlatTabPanelWidget.reloadContent();
		//TODO: reposition/cretae widgets for a different user
	}
}
