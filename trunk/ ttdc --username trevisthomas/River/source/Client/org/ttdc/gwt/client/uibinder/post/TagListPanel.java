package org.ttdc.gwt.client.uibinder.post;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SugestionOracle;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.presenters.comments.RemovableTagPresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TagListPanel extends Composite {
	interface MyUiBinder extends UiBinder<Widget, TagListPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	private List<GAssociationPostTag> tagList = new ArrayList<GAssociationPostTag>();
	private List<RemovableTagPresenter> tagPresenterList = new ArrayList<RemovableTagPresenter>();
	private Mode mode;
	private SugestionOracle tagSuggestionOracle;
	private SuggestBox tagSuggestionBox;
	
	@UiField HTMLPanel tagListElement;
	@UiField HTMLPanel editableTagListElement;
	@UiField FlowPanel tagsElement;
	@UiField FlowPanel editableTagsElement;
	@UiField SimplePanel tagSelectorElement;
	@UiField Button addButtonElement;
	@UiField Button cancelButtonElement;
	@UiField Anchor editTagsLinkElement;
	
	public enum Mode{
		STATIC,
		EDITABLE,
		EDIT_ONLY,
		EDIT
	}
	
	@Inject
	public TagListPanel(Injector injector) {
		this.injector = injector;
		
		//
		
		initWidget(binder.createAndBindUi(this));
		
		cancelButtonElement.setText("Close");
		addButtonElement.setText("Add");
		
		editableTagListElement.setVisible(false);
		tagListElement.setVisible(false);
		
		tagSuggestionOracle = injector.getTagSugestionOracle();
		tagSuggestionBox = tagSuggestionOracle.createSuggestBoxForPostView();
		tagSelectorElement.add(tagSuggestionBox);

	}
	
	public void init(List<GAssociationPostTag> asses, Mode mode){
		this.tagList.addAll(asses);
		this.mode = mode;
		
		changeToMode(mode);
		
		for(GAssociationPostTag ass : asses){
			HyperlinkPresenter tagLink = injector.getHyperlinkPresenter();
			tagLink.setTag(ass.getTag());
			tagLink.init();//TODO: refactor this call out
			tagsElement.insert(tagLink.getHyperlink(),0);
			
			createRemovableTag(ass.getTag());
		}
		
		editTagsLinkElement.setText("edit");
	}

	private void changeToMode(Mode mode) {
		if(Mode.EDIT_ONLY.equals(mode) || Mode.EDIT.equals(mode)){
			editableTagListElement.setVisible(true);
			tagListElement.setVisible(false);
		}
		else{ //Mode.STATIC.equals(mode) || Mode.EDITABLE.equals(mode)
			editableTagListElement.setVisible(false);
			tagListElement.setVisible(true);
		}
	}
	
	private void createRemovableTag(GTag tag) {
		RemovableTagPresenter tagPresenter = injector.getRemovableTagPresenter();
		tagPresenterList.add(tagPresenter);
		editableTagsElement.add(tagPresenter.getWidget());
		tagPresenter.init(tag, new RemoveTagClickHandler(tagPresenter));
	}
	
	private class RemoveTagClickHandler implements ClickHandler{
		private RemovableTagPresenter presenter;
		public RemoveTagClickHandler(RemovableTagPresenter presenter) {
			this.presenter = presenter;
		}
		@Override
		public void onClick(ClickEvent event) {
			tagPresenterList.remove(presenter);
			editableTagsElement.remove(presenter.getWidget());
		}
	
	} 
	
	@UiHandler("addButtonElement")
	public void onClickAdd(ClickEvent event){
		SuggestionObject suggestion = tagSuggestionOracle.getCurrentSuggestion();
		if(suggestion != null){
			createRemovableTag(suggestion.getTag());
		}
		else{
			GTag tag = new GTag();
			tag.setValue(tagSuggestionBox.getValue());
			createRemovableTag(tag);
		}
		tagSuggestionOracle.clear();
	}
	
	@UiHandler("cancelButtonElement")
	public void onClickCancelEdit(ClickEvent event){
		changeToMode(Mode.STATIC);
	}
	
	@UiHandler("editTagsLinkElement")
	public void onClickEdit(ClickEvent event){
		//Toggle
//		if(Mode.STATIC.equals(mode)){
//			editTagsLinkElement.setText("edit");
//			mode = Mode.EDITABLE;
//		}
//		else{
//			editTagsLinkElement.setText("edit");
//			mode = Mode.STATIC;
//		}
		changeToMode(Mode.EDIT);
	}
	
	
	
	
}
