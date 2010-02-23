package org.ttdc.gwt.client.presenters.shared;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class GenericTabularFlowView implements GenericTabularFlowPresenter.View{
	private final FlexTable flowizer = new FlexTable();
	
	private static final int DEFAULT_MAX_COLUMNS = 5;
	private int maxColumns = DEFAULT_MAX_COLUMNS;
	private int currentCol = 0;
	private int currentRow = 0;
	private int currentIndex = 0;
	@Override
	public Widget getWidget() {
		return flowizer;
	}

	@Override
	public void addWidgetToFlow(Widget w) {
		if(w == null)
			throw new RuntimeException("Dont make me angry with a null widget!");
		
		flowizer.setWidget(currentRow, currentCol, w);
		//flowizer.setWidget(currentRow, currentCol, w);
		
		currentIndex++;
		if(currentIndex % maxColumns == 0 && currentIndex > 0){
			currentRow++;
		}
		
		if(currentCol < maxColumns-1)
			currentCol++;
		else
			currentCol = 0;
	}

	@Override
	public void setMaxColumns(int cols) {
		maxColumns = cols;
	}
}
