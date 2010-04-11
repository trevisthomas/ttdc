package org.ttdc.gwt.client.uibinder;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Demo extends Composite{
	 @UiTemplate("Demo.ui.xml")
	    interface MyUiBinder extends UiBinder<Widget, Demo> {}
	 	@UiField TextBox loginBox;
	 	
	 	@UiField TabLayoutPanel tabPanel;
	 
	    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	    public Demo() { 
	    	initWidget(binder.createAndBindUi(this)); 
	    	
	    	setBoobs("I guess it can <b>see</b> this? ");
	    	tabPanel.selectTab(0);
	    }
	    
	    @UiHandler("buttonSubmit")
	    void doClickSubmit(ClickEvent event) {
	    	Window.alert(loginBox.getValue());
	    }
	    @UiField SpanElement boobs;
	    //String boobs = "yeah, it knows.";
	    
	    public void setBoobs(String userName) {
	    	boobs.setInnerHTML(userName);
	     }

}
