package org.ttdc.gwt.client.uibinder;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class Navigation extends Composite {
	interface MyUiBinder extends UiBinder<Widget, Navigation> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private HyperlinkPresenter homeLinkPresenter;
	private HyperlinkPresenter searchLinkPresenter;
	private HyperlinkPresenter movieLinkPresenter;
	private HyperlinkPresenter historyLinkPresenter;
	private HyperlinkPresenter usersLinkPresenter;
	private HyperlinkPresenter dashboardLinkPresenter;
	private HyperlinkPresenter adminLinkPresenter;
	private HyperlinkPresenter calendarLinkPresenter;
	
	private Injector injector;
	
	@UiField(provided = true) Hyperlink homeElement;
	@UiField(provided = true) Hyperlink searchElement;
	@UiField(provided = true) Hyperlink movieElement;
	@UiField(provided = true) Hyperlink historyElement;
	@UiField(provided = true) Hyperlink usersElement;
	@UiField(provided = true) Hyperlink dashboardElement;
	@UiField(provided = true) Hyperlink adminElement;
	@UiField(provided = true) Hyperlink calendarElement;
	
	@Inject
	public Navigation(Injector injector) {
		this.injector = injector;
		
		homeLinkPresenter = injector.getHyperlinkPresenter();
		searchLinkPresenter = injector.getHyperlinkPresenter();
		movieLinkPresenter = injector.getHyperlinkPresenter();
		historyLinkPresenter = injector.getHyperlinkPresenter();
		usersLinkPresenter = injector.getHyperlinkPresenter();
		dashboardLinkPresenter = injector.getHyperlinkPresenter();
		adminLinkPresenter = injector.getHyperlinkPresenter();
		calendarLinkPresenter = injector.getHyperlinkPresenter();
		
		homeLinkPresenter.setView("Home", HistoryConstants.VIEW_HOME);
		homeElement = homeLinkPresenter.getHyperlink();
		
		searchLinkPresenter.setView("Search", HistoryConstants.VIEW_SEARCH);
		searchElement = searchLinkPresenter.getHyperlink();
		
		movieLinkPresenter.setView("Movie", HistoryConstants.VIEW_MOVIE_LIST);
		movieElement = movieLinkPresenter.getHyperlink();
		
		historyLinkPresenter.setView("History", HistoryConstants.VIEW_CALENDAR);
		historyElement = historyLinkPresenter.getHyperlink();
		
		usersLinkPresenter.setView("Users", HistoryConstants.VIEW_USER_LIST);
		usersElement = usersLinkPresenter.getHyperlink();
		
		dashboardLinkPresenter.setView("Dashboard", HistoryConstants.VIEW_DASHBOARD);
		dashboardElement = dashboardLinkPresenter.getHyperlink();
		
		calendarLinkPresenter.setView("Calendar", HistoryConstants.VIEW_CALENDAR);
		calendarElement = calendarLinkPresenter.getHyperlink();
		
		
		if(ConnectionId.isAnonymous()){
			dashboardElement.setVisible(false);
			usersElement.setVisible(false);
		}
		
		
		adminLinkPresenter.setView("Admin", HistoryConstants.VIEW_ADMIN_TOOLS);
		adminElement = adminLinkPresenter.getHyperlink();
		
		if(!ConnectionId.isAdministrator()){
			adminElement.setVisible(false);		
		}
		
		
		initWidget(binder.createAndBindUi(this));
	}
	
}
