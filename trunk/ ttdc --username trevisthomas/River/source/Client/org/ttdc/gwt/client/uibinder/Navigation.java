package org.ttdc.gwt.client.uibinder;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.ListItemWidget;
import org.ttdc.gwt.client.presenters.util.UnorderedListWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class Navigation extends Composite implements PersonEventListener{
	interface MyUiBinder extends UiBinder<Widget, Navigation> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private HyperlinkPresenter homeLinkPresenter;
	private HyperlinkPresenter searchLinkPresenter;
	private HyperlinkPresenter movieLinkPresenter;
	private HyperlinkPresenter usersLinkPresenter;
	private HyperlinkPresenter dashboardLinkPresenter;
	private HyperlinkPresenter adminLinkPresenter;
	private HyperlinkPresenter calendarLinkPresenter;
	
	private Injector injector;
	
	private final Hyperlink homeElement;
	private final Hyperlink searchElement;
	private final Hyperlink movieElement;
	private final Hyperlink usersElement;
	private final Hyperlink dashboardElement;
	private final Hyperlink adminElement;
	private final Hyperlink calendarElement;
	
	@UiField(provided = true) UnorderedListWidget navList = new UnorderedListWidget();
	
	@Inject
	public Navigation(Injector injector) {
		this.injector = injector;
		navList.setStyleName("tt-navigation");
		
		homeLinkPresenter = injector.getHyperlinkPresenter();
		searchLinkPresenter = injector.getHyperlinkPresenter();
		movieLinkPresenter = injector.getHyperlinkPresenter();
		usersLinkPresenter = injector.getHyperlinkPresenter();
		dashboardLinkPresenter = injector.getHyperlinkPresenter();
		adminLinkPresenter = injector.getHyperlinkPresenter();
		calendarLinkPresenter = injector.getHyperlinkPresenter();
		
		homeLinkPresenter.setView("Home", HistoryConstants.VIEW_HOME);
		searchLinkPresenter.setView("Search", HistoryConstants.VIEW_SEARCH);
		movieLinkPresenter.setView("Movie", HistoryConstants.VIEW_MOVIE_LIST);
		usersLinkPresenter.setView("Users", HistoryConstants.VIEW_USER_LIST);
		dashboardLinkPresenter.setView("Dashboard", HistoryConstants.VIEW_DASHBOARD);
		calendarLinkPresenter.setView("Calendar", HistoryConstants.VIEW_CALENDAR);
		adminLinkPresenter.setView("Admin", HistoryConstants.VIEW_ADMIN_TOOLS);
		homeLinkPresenter.setView("Home", HistoryConstants.VIEW_HOME);
		searchLinkPresenter.setView("Search", HistoryConstants.VIEW_SEARCH);
		
		homeElement = homeLinkPresenter.getHyperlink();
		searchElement = searchLinkPresenter.getHyperlink();
		movieElement = movieLinkPresenter.getHyperlink();
		usersElement = usersLinkPresenter.getHyperlink();
		dashboardElement = dashboardLinkPresenter.getHyperlink();
		calendarElement = calendarLinkPresenter.getHyperlink();
		adminElement = adminLinkPresenter.getHyperlink();
				
		applyUserSpecificSettings();
		
		initWidget(binder.createAndBindUi(this));
		
		EventBus.getInstance().addListener(this);
	}
	
	

	private void applyUserSpecificSettings() {
		List<Hyperlink> links = new ArrayList<Hyperlink>();
		links.add(homeElement);
		links.add(searchElement);
		links.add(movieElement);
		if(!ConnectionId.isAnonymous()){
			links.add(usersElement);
			links.add(dashboardElement);
		}
		links.add(calendarElement);
		if(ConnectionId.isAdministrator()){
			links.add(adminElement);		
		}
		
		//setMenu(links);
		navList.loadHyperlinks(links);
	}
	
	@Override
	public void onPersonEvent(PersonEvent event) {
		if(event.is(PersonEventType.USER_CHANGED) || event.is(PersonEventType.USER_PROFILE_UPDATED)){
			applyUserSpecificSettings();
		}
	}
	
//	public void setMenu(List<Hyperlink> links)
//	{
//		navList.clear();
//		for (int i=0; i<links.size(); i++)
//		{
//			Hyperlink item = links.get(i);
//			if (i>0)
//			{
//				navList.add(new ListItemWidget("|"));
//			}
//			navList.add(new ListItemWidget(item));
//		}
//	}
}
