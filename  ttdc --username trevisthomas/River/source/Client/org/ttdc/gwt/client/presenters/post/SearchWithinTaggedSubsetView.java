package org.ttdc.gwt.client.presenters.post;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * Trevis, this class is almost identical to SiteSearchView... perhaps they could share?
 * The initial reason that i'm not making them share is because i think that the other 
 * will eventually do auto completion which may make less sense here.
 *
 */
public class SearchWithinTaggedSubsetView implements SearchWithinTaggedSubsetPresenter.View{
	private final TextBox phraseField = new TextBox();
	private final Button searchButton = new Button("Search Within Results");
	private final FlowPanel buttons = new FlowPanel();
	private final Grid mainWidget = new Grid(1,2);
	private final Button browseButton = new Button("Browse");
	
	public TextBox getPhraseField() {
		return phraseField;
	}

	@Override
	public Button getSearchButton() {
		return searchButton;
	}
	
	@Override
	public HasClickHandlers getBrowseButton() {
		return browseButton;
	}

	public SearchWithinTaggedSubsetView() {
		mainWidget.setWidget(0, 0, phraseField);
		mainWidget.setWidget(0, 1, buttons);
		buttons.add(searchButton);
		
		//Very similar code is now in the other SearchWithin presenter.  Look for a way to refactor
		phraseField.addFocusHandler(new FocusHandler(){
			@Override
				public void onFocus(FocusEvent event) {
					phraseField.setSelectionRange(0, phraseField.getText().length());					
				}	
		});
		
		phraseField.addKeyUpHandler(new KeyUpHandler(){
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					searchButton.click();
				}
			}
		});
	}
	
	@Override
	public void showBrowseButton(boolean show){
		if(show && !browseButton.isAttached()){
			buttons.add(browseButton);
		}
		else if(browseButton.isAttached()){
			buttons.remove(browseButton);
		}
		else{
			/*nothing to do*/
		}
	}
	
	
	@Override
	public Widget getWidget() {
		return mainWidget;
	}

	

}
