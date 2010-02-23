package org.ttdc.gwt.client.presenters.admin;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageManagementView implements ImageManagementPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel imageUploadTarget = new SimplePanel();
	private final Grid scrapForm = new Grid(3,2);
	
	private final FlexTable imageTable = new FlexTable();
	private final SimplePanel paginator = new SimplePanel();
	
	private final Grid imageForms = new Grid(1,2);
	private final TextBox urlTextBox = new TextBox();
	private final TextBox nameTextBox = new TextBox();
	private final Button scrapImageButton = new Button("Grab Url");
	
	
	
	private int row=0;

	public ImageManagementView() {
		main.add(imageForms);
		imageForms.setWidget(0, 0, imageUploadTarget);
		imageForms.setWidget(0, 1, scrapForm);
				
		scrapForm.setWidget(0, 0, new Label("URL"));
		scrapForm.setWidget(0, 1, urlTextBox);
		scrapForm.setWidget(1, 0, new Label("Save As (Optional)"));
		scrapForm.setWidget(1, 1, nameTextBox);
		scrapForm.setWidget(2, 1, scrapImageButton);
		
		main.add(imageTable);
		main.add(paginator);
		
		resetTableHeader();
		
	}

	private void resetTableHeader() {
		imageTable.setWidget(0, 0, new Label("Image"));
		imageTable.setWidget(0, 1, new Label("Name"));
		imageTable.setWidget(0, 2, new Label("Owner"));
		imageTable.setWidget(0, 3, new Label("Size"));
		imageTable.setWidget(0, 4, new Label("Action"));
	}
	
	@Override
	public HasWidgets paginatorTarget() {
		return paginator;
	}

	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets imageUploadTarget() {
		return imageUploadTarget;
	}

	@Override
	public void addImage(Widget imageWidget, Widget nameWidget, Widget personWidget, String size, Widget controlsWidget) {
		row++;
		
		imageTable.setWidget(row, 0, imageWidget);
		imageTable.setWidget(row, 1, nameWidget);
		imageTable.setWidget(row, 2, personWidget);
		imageTable.setWidget(row, 3, new Label(size));
		imageTable.setWidget(row, 4, controlsWidget);
	}

	@Override
	public void clear() {
		imageTable.clear();
		paginator.clear();
		urlTextBox.setText("");
		nameTextBox.setText("");
		resetTableHeader();
	}

	@Override
	public HasText nameTextBox() {
		return nameTextBox;
	}

	@Override
	public HasClickHandlers scrapeImageButton() {
		return scrapImageButton;
	}

	@Override
	public HasText urlTextBox() {
		return urlTextBox;
	}

}
