package org.ttdc.gwt.client.presenters.tag;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.autocomplete.SuggestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

/*
 * TODO: !! Please refactor this so that the class is called TagListView
 */
public class CommonTagListView implements TagListPresenterView{
	private final FlowPanel mainPanel = new FlowPanel();
	private final Button addButton = new Button();
	private SuggestionOracle oracle;
	
	//private Injector injector;
	
	private final List<String> unionTagIdList = new ArrayList<String>();
	
	private SuggestBox box;
	
	public CommonTagListView() {
		
	}
	
 	public HasClickHandlers getAddClickHandler() {
		return addButton;
	}

	public SuggestionObject getTagSuggestion() {
		return oracle.getCurrentSuggestion();
	}

	public void setExcludeTagIdList(List<String> excludeTagIdList) {
		// TODO Auto-generated method stub
		
	}

	public void setUnionTagIdList(List<String> unionTagIdList) {
		this.unionTagIdList.addAll(unionTagIdList);
	}
	
	@Override
	public void setTagSuggestionOracle(SuggestionOracle oracle) {
		this.oracle = oracle;
	}
	
	public void showPresenters(List<String> tagIdList, List<BasePresenter<?>> presenters) {
		mainPanel.clear();
		
		if(oracle == null){
			throw new RuntimeException("Oracle is null");
		}
		
		//box = oracle.createSuggestBoxForSearch(unionTagIdList);
		box = oracle.createSuggestBoxForSearch(tagIdList);
		
		for(BasePresenter p : presenters){
			mainPanel.add(p.getWidget());
		}
		
		box.addStyleName("tt-hyperLinkView");
		mainPanel.add(box);
		mainPanel.add(addButton);
	}

	public Widget getWidget() {
		return mainPanel;
	}

}
