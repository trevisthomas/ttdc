package org.ttdc.gwt.client.presenters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class MockHasWidgets implements HasWidgets{
	private List<Widget> widgets = new ArrayList<Widget>();
	
	public void add(Widget w) {
		widgets.add(w);
	}

	public void clear() {
		widgets.clear();
	}

	public Iterator<Widget> iterator() {
		return widgets.iterator();
	}

	public boolean remove(Widget w) {
		return widgets.remove(w);
	}
	
	public int size(){
		return widgets.size();
	}
	
}
