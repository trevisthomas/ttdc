package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter.StyleType;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public class HyperlinkView implements HyperlinkPresenter.View {
	private final HistoryToken historyToken = new HistoryToken();
	private String url; 
	
	private final Hyperlink link = new Hyperlink();
	private boolean selected = false;
	private StyleType styleType = HyperlinkPresenter.StyleType.DEFAULT;
	
	public HyperlinkView() {
		link.setStylePrimaryName("tt-hyperLinkView");
	}
	
	@Override
	public HasText getDisplayName() {
		return link;
	}

	@Override
	public HistoryToken getHistoryToken() {
		return historyToken;
	}

	@Override
	public HasClickHandlers getLinkHandlers() {
		return link;
	}
	
	@Override
	public Hyperlink getHyperlink() {
		return link;
	}
	
	@Override
	public Widget getWidget() {
		if(styleType == StyleType.PAGINATOR){
			link.addStyleName("tt-paddedbox");
			if(isHighlighted()){
				link.addStyleName("tt-selected");
			}
		}
		if(historyToken != null)
			link.setTargetHistoryToken(historyToken.toString());
		else if(StringUtil.notEmpty(url))
			link.setTargetHistoryToken(url);
		
			
		return link;
	}

	@Override
	public boolean isHighlighted() {
		return selected;
	}

	@Override
	public void setHighlighted(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setCloudRank(int cloudRank) {
		if(cloudRank >= 0){// if -1 just ignore the field
			link.addStyleName("tt-cloud-"+cloudRank);
			link.addStyleName("tt-cloud-tag");
		}
	}

	@Override
	public void setStyleType(StyleType styleType) {
		this.styleType = styleType;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
