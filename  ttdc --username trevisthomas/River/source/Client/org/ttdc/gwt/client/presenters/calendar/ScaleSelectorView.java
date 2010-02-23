package org.ttdc.gwt.client.presenters.calendar;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ScaleSelectorView implements ScaleSelectorPresenter.View{
	private final FlowPanel mainPanel = new FlowPanel();
	private final SimplePanel dayTarget = new SimplePanel();
	private final SimplePanel weekTarget = new SimplePanel();
	private final SimplePanel monthTarget = new SimplePanel();
	private final SimplePanel yearTarget = new SimplePanel();

	public ScaleSelectorView() {
		dayTarget.addStyleName("tt-calendar-scale-selector");
		weekTarget.addStyleName("tt-calendar-scale-selector");
		monthTarget.addStyleName("tt-calendar-scale-selector");
		yearTarget.addStyleName("tt-calendar-scale-selector");
		
		mainPanel.add(dayTarget);
		mainPanel.add(weekTarget);
		mainPanel.add(monthTarget);
		mainPanel.add(yearTarget);
	}
	
	@Override
	public HasWidgets dayTarget() {
		return dayTarget;
	}

	@Override
	public HasWidgets weekTarget() {
		return weekTarget;
	}
	
	@Override
	public HasWidgets monthTarget() {
		return monthTarget;
	}
	
	@Override
	public HasWidgets yearTarget() {
		return yearTarget;
	}

	@Override
	public void setSelectionDay() {
		clearSelected();
		dayTarget.addStyleName("tt-selected");
	}

	@Override
	public void setSelectionWeek() {
		clearSelected();
		weekTarget.addStyleName("tt-selected");
	}
	
	@Override
	public void setSelectionMonth() {
		clearSelected();
		monthTarget.addStyleName("tt-selected");
	}

	@Override
	public void setSelectionYear() {
		clearSelected();
		yearTarget.addStyleName("tt-selected");
	}
	
	private void clearSelected(){
		dayTarget.removeStyleName("tt-selected");
		weekTarget.removeStyleName("tt-selected");;
		monthTarget.removeStyleName("tt-selected");
		yearTarget.removeStyleName("tt-selected");
	}

	@Override
	public Widget getWidget() {
		return mainPanel;
	}

}
