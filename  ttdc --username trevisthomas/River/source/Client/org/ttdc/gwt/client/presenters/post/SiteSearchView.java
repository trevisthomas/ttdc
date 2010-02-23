package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.presenters.util.ViewHelpers;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SiteSearchView extends Composite implements SiteSearchPresenter.View{
	private final TextBox phraseField = new TextBox();
	private final Button searchButton = new Button("Go, Man!");
	private final Grid mainWidget = new Grid(1,2);
	
	public TextBox getPhraseField() {
		return phraseField;
	}

	public Button getSearchButton() {
		return searchButton;
	}

	public SiteSearchView() {
		mainWidget.setWidget(0, 0, phraseField);
		mainWidget.setWidget(0, 1, searchButton);
		
		ViewHelpers.configureSearchTextBox(phraseField,searchButton);
		
//		phraseField.addFocusHandler(new FocusHandler(){
//			@Override
//				public void onFocus(FocusEvent event) {
//					phraseField.setSelectionRange(0, phraseField.getText().length());					
//				}	
//		});
//		
//		phraseField.addKeyUpHandler(new KeyUpHandler(){
//			@Override
//			public void onKeyUp(KeyUpEvent event) {
//				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
//					searchButton.click();
//				}
//			}
//		});
		
	}
	
	
	@Override
	public Widget getWidget() {
		return mainWidget;
	}

	@Override
	public HasText searchButtonText() {
		return searchButton;
	}
}
