package org.ttdc.gwt.client.presenters.shared;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * 
 * Top level page views should extend this. Trevis you added this very late in the game so 
 * refactor my fat friend, refactor.
 *
 */
public interface BasePageView extends BaseView{
	void show();
	HasWidgets messagePanel();
	HasWidgets navigationPanel();
	
}
