package org.ttdc.gwt.client.uibinder.comment;

import org.ttdc.gwt.client.presenters.comments.PopupRelative;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;

public class InsertTrevTagPopup extends PopupRelative{
	private final Grid main = new Grid(2,2);
	private final TextBox contentSmall = new TextBox();
	private final TextArea contentBig = new TextArea();
	private final Label contentLabel = new Label("Text");
	private TextBoxBase content;
	
	private final Button okButton = new Button("Ok");
	private final Button cancelButton = new Button("Cancel");
	private final FlowPanel buttonPanel = new FlowPanel();
	
	private final String open;
	private final String close;
	
	public static interface InsertTrevTagPopupSource{
		void performInsert(String text);
	}
	
	private final InsertTrevTagPopupSource source;
	private boolean useTextArea = false; 
	public InsertTrevTagPopup(InsertTrevTagPopupSource source, final String open, final String close, boolean useTextArea) {
		add(main);
		this.source = source;
		
		this.open = open;
		this.close = close;
		
		main.setWidget(0, 0, contentLabel);
		if(useTextArea){
			content = contentBig;
		}
		else{
			content = contentSmall;
		}
		this.useTextArea = useTextArea;
		main.setWidget(0, 1, content);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		main.setWidget(1, 1, buttonPanel);
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				InsertTrevTagPopup.this.hide();
			}
		});
		
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				notifySource();
			}
		});
	}
	public InsertTrevTagPopup(InsertTrevTagPopupSource source, final String open, final String close) {
		this(source, open, close, false);
	}
	private void notifySource(){
		String insert = open + content.getText() + close;
		if(useTextArea){
			insert = "\n" + insert  + "\n";
		}
		
		InsertTrevTagPopup.this.source.performInsert(insert);
		InsertTrevTagPopup.this.hide();
	}

}
