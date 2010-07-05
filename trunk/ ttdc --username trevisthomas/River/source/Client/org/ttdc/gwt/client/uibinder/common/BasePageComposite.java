package org.ttdc.gwt.client.uibinder.common;

import org.ttdc.gwt.client.common.TopLevelViewBase;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

abstract public class BasePageComposite extends Composite implements TopLevelViewBase{
	@Override
	public void show(HistoryToken token) {
		onShow(token);
		RootPanel.get("content").clear();
		RootPanel.get("content").add(this);
	}	
	
	abstract protected void onShow(HistoryToken token);
}
