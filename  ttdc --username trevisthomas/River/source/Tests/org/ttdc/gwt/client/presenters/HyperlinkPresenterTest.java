package org.ttdc.gwt.client.presenters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.PersonBeanMother;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;

public class HyperlinkPresenterTest {
	//private final static Logger log = Logger.getLogger(HyperlinkPresenterTest.class);
	Injector injector;
	
	@Before 
	public void setup(){
		injector = new MockInjector();
	}
	
	@Test 
	public void testPersonClicking(){
		HistoryToken historyToken = new HistoryToken();
		MockHasText nameHasText = new MockHasText();
		
		HyperlinkPresenter.View v = mock(HyperlinkPresenter.View.class);
		when(v.getHistoryToken()).thenReturn(historyToken); //"view=personDetails&personId=1234"
		when(v.getDisplayName()).thenReturn(nameHasText);//"trevTest"
		when(v.getLinkHandlers()).thenReturn(new MockHasClickHandlers());

		((MockInjector)injector).setHyperlinkView(v);
		
		GPerson person = PersonBeanMother.createPerson1();
		HyperlinkPresenter presenter = new HyperlinkPresenter(injector);
		
		presenter.setPerson(person);
		assertEquals(person.getPersonId(), historyToken.getParameter("personId"));
		assertEquals(person.getLogin(), nameHasText.getText());
		
		
		//Verify that history is working
		/*
		final String personId = person.getPersonId();
		
		EventBus.getInstance().addListener(
				new HistoryEventListener(){
					public void onHistoryEvent(HistoryEvent event) {
						assertEquals(HistoryEventType.VIEW_CHANGE,event.getType());
						assertEquals(personId,event.getSource().getParameter("personId"));
						log.debug(event.getSource());
					}
				}	
			);
		
		MockHasClickHandlers.clickMockButton(presenter.getView().getLinkHandlers());
		*/
	}
	
	
}
