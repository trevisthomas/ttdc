package org.ttdc.gwt.client.uibinder.calendar;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.post.PostCollectionPresenter;
import org.ttdc.gwt.client.presenters.post.PostPresenter.Mode;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Hour;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class DayPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, DayPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField(provided = true) Grid tableElement =  new Grid(24,2);
        
    @Inject
    public DayPanel(Injector injector) { 
    	this.injector = injector;
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	generateSidebar();
	}
    
    public void setDay(Day day){
		if(day.isContent()){
			for(Hour h : day.getHours()){
				PostCollectionPresenter postCollection = injector.getPostCollectionPresenter();
				postCollection.setPostList(h.getPosts(), Mode.FLAT);
				
				insertHourWidget(h.getHourOfDay(),postCollection.getWidget());
			}
		}
	}
    
    private void generateSidebar(){
		String html;
		for(int h = 0; h < 24 ; h++){
			if(h < 12){
				html = (h!=0?h:12)+"a";
			}
			else if(h == 12){
				html = "N";
			}
			else {
				html = h-12+"p";
			}
			HTMLPanel htmlPanel = new HTMLPanel(html);
			htmlPanel.setStyleName("tt-calendar-hour-label tt-text-huge tt-color-calendar-hour-label");
			tableElement.setWidget(h, 0, htmlPanel);
			tableElement.getCellFormatter().setStyleName(h, 0, "tt-fill-height tt-special-border-bottom");
//			if(h%2 == 0 ){
//				//tableElement.getCellFormatter().setStyleName(h, 1, "tt-graybar tt-special-border-bottom");
//				tableElement.getCellFormatter().setStyleName(h, 1, "tt-color-contrast2-border tt-border-top-bottom");
//			}
			
			tableElement.getCellFormatter().setStyleName(h, 1, "tt-color-calendar-day-hour tt-border-bottom");
			
		}
	}
    
    private void insertHourWidget(int hour, Widget widget) {
    	tableElement.setWidget(hour,1,widget);
	}
    
    @Override
    public Widget getWidget() {
    	return this;
    }

}
