package org.ttdc.gwt.client.presenters.search;

import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.client.presenters.util.MyListBox;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.util.StringUtil;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchBoxView implements SearchBoxPresenter.View{
	private final MyListBox activeUserList = new MyListBox(false);
	private final Grid main = new Grid(1,3);
	private final TextBox searchBox = new TextBox();
	private final ClickableIconPanel expandClicker = new ClickableIconPanel("tt-clickable-icon-down");
	private final static PopupPanel controlsPopup = new PopupPanel(false);
	private final HorizontalPanel upperControls = new HorizontalPanel();
	private final HorizontalPanel calendarPanel = new HorizontalPanel(); // This was done quickly so please feel free to change it when adding headers to teh calenders
	private final VerticalPanel controlsPanel = new VerticalPanel();
	private final HorizontalPanel lowerControls = new HorizontalPanel();
	
	private final SimplePanel startDayPanel = new SimplePanel();
	private final SimplePanel endDayPanel = new SimplePanel();
	
	private final Button searchButton = new Button("Search");
	private final Label refineSearchLabel = new Label();
	
	private Day startDay = null;
	private Day endDay = null;
	private String personIdFilter = null;
	
	private String searchPhrase;
	private String defaultMessage;
	
	@Override
	public void hidePopup() {
		if(controlsPopup.isShowing()){
			controlsPopup.hide();
		}	
	}
	
	public SearchBoxView() {
		searchBox.setStyleName("tt-textbox-search");
		controlsPopup.setStyleName("tt-search-panel-adv");
		//activeUserList.addItem("-- TTDC'ers --","-1");
		//activeUserList.addItem(" ","-1");
		activeUserList.setVisibleItemCount(5);
		
		setupEventHandlers();
		main.setStyleName("tt-search-panel");
		main.setWidget(0, 0, expandClicker);
		main.setWidget(0, 1, searchBox);
		main.setWidget(0, 2, searchButton);
				
		
		controlsPopup.clear();
		controlsPopup.add(controlsPanel);
		controlsPanel.add(refineSearchLabel);
		upperControls.add(activeUserList);
		upperControls.add(calendarPanel);
		controlsPanel.add(lowerControls);
		controlsPanel.add(upperControls);
		
		lowerControls.add(startDayPanel);
		lowerControls.add(endDayPanel);
		
				
	}

	
	private void setupEventHandlers() {
		searchBox.addFocusHandler(new FocusHandler(){
			@Override
			public void onFocus(FocusEvent event) {
				searchBox.removeStyleName("tt-textbox-disabled");
				if(searchBox.getText().equals(defaultMessage)){
					if(StringUtil.notEmpty(searchPhrase)){
						searchBox.setText(searchPhrase);
						highlightSearchBoxText();
					}
					else{
						searchBox.setText("");
					}
				}
				else{
					highlightSearchBoxText();
				}
			}
		});
		
		searchBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(StringUtil.empty(searchBox.getText())){
					searchPhrase = "";
				}
			}
		});
		searchBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if(searchBox.getText().trim().equals("") || searchBox.getText().equals(searchPhrase)){
					loadDefaultTextIntoSearchBox();
				}
			}
		});
		
		searchBox.addKeyUpHandler(new KeyUpHandler(){
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					searchButton.click();
				}
			}
		});
		
		expandClicker.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(controlsPopup.isShowing()){
					controlsPopup.hide();
				}
				else{
					// Reposition the popup relative to the button
		            //Widget source = (Widget) event.getSource();
		            
		            int left = main.getAbsoluteLeft();
		            int top = main.getAbsoluteTop() + main.getOffsetHeight() - 1;
		            controlsPopup.setPopupPosition(left, top);

		            // Show the popup
					controlsPopup.show();	
				}
			}
		});
	}
	
	private void highlightSearchBoxText() {
		searchBox.setSelectionRange(0, searchBox.getText().length());
	}
	
	@Override
	public Widget getWidget() {
		if(StringUtil.empty(searchBox.getText())){
			loadDefaultTextIntoSearchBox();
		}
		return main;
	}

	private void loadDefaultTextIntoSearchBox() {
		searchBox.setText(defaultMessage);
		searchBox.addStyleName("tt-textbox-disabled");
	}
	
	@Override
	public void addPerson(String personId, String login) {
		activeUserList.addItem(login,personId);
		
	}
	
	@Override
	public void setSelectedCreatorId(String personId) {
		activeUserList.setSelectedValue(personId);
	}

	@Override
	public String getSelectedCreatorId(){
		return activeUserList.getSelectedValue();
	}

	@Override
	public HasClickHandlers searchClickHandler() {
		return searchButton;
	}

	@Override
	public String prsonIdFilter() {
		return personIdFilter;
	}


	@Override
	public HasText searchBox() {
		return searchBox;
	}

	@Override
	public void setDefaultMessage(String message) {
		defaultMessage = message;
		loadDefaultTextIntoSearchBox();
	}
	@Override
	public void setSearchPhrase(String phrase) {
		searchPhrase = phrase;
	}

	
	@Override
	public String getSearchPhrase() {
		if(searchBox.getText().equals(defaultMessage)){
			if(StringUtil.notEmpty(searchPhrase))
				return searchPhrase;
			else
				return "";
		}
		else{
			return searchBox.getText();
		}
	}
	
	private void updateRefineSearchLabelMessage(){
		String msg = "";
		if(startDay != null)
			msg = DateFormatUtil.formatLongDay(startDay.toDate());
		if(endDay != null)
			msg += " " + DateFormatUtil.formatLongDay(endDay.toDate());
		
		//TODO add people.
		refineSearchLabel.setText(msg);
	}

	@Override
	public HasWidgets calendarPanel() {
		return calendarPanel;
	}

	@Override
	public HasWidgets endDatePanel() {
		return endDayPanel;
	}

	@Override
	public HasWidgets startDatePanel() {
		return startDayPanel;
	}


	@Override
	public void setFromDate(Day day) {
		startDay = day;
		updateRefineSearchLabelMessage();
	}

	@Override
	public void setToDate(Day day) {
		endDay = day;
		updateRefineSearchLabelMessage();
		
	}

	
}
