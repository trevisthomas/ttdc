package org.ttdc.gwt.client.presenters.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

/**
 * The real one for some reason doesnt do some things that are pretty fundamental so, i'm extending it
 * 
 *
 */
public class MyListBox extends ListBox{
	public MyListBox() {
		
	}
	
	public MyListBox(boolean isMultiSelect){
		super(isMultiSelect);
	}
	
	public String getSelectedValue(){
		int index = this.getSelectedIndex();
		if(index >= 0)
			return this.getValue(index);
		else
			return "";
	}
	
	public String getSelectedText(){
		int index = this.getSelectedIndex();
		if(index >= 0)
			return this.getItemText(index);
		else
			return "";
	}
	
	public void setSelectedValue(String value){
		int index = 0;
		for(int i = 0 ; i < this.getItemCount() ; i++){
			if(this.getValue(i).equals(value)){
				index = i;
			}
		}
		this.setSelectedIndex(index);
	}
	
	public List<String> getSelectedValues(){
		List<String> selectedItems = new ArrayList<String>();
		for(int i = 0 ; i < this.getItemCount() ; i++){
			if(this.isItemSelected(i)){
				selectedItems.add(this.getValue(i));
			}
		}
		return selectedItems;
	}
}
