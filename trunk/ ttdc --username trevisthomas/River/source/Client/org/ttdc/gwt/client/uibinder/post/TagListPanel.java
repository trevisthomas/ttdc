package org.ttdc.gwt.client.uibinder.post;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TagListPanel extends Composite {
	interface MyUiBinder extends UiBinder<Widget, TagListPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	
	private Injector injector;
	private List<GAssociationPostTag> tagList = new ArrayList<GAssociationPostTag>();
	private Mode mode;
	
	@UiField HTMLPanel tagListElement;
	@UiField HTMLPanel editableTagListElement;
	@UiField FlowPanel tagsElement;
	@UiField FlowPanel editableTagsElement;
	@UiField SimplePanel tagSelectorElement;
	@UiField Button addButtonElement;
	
	public enum Mode{
		STATIC,
		EDITABLE,
		EDIT_ONLY
	}
	
	@Inject
	public TagListPanel(Injector injector) {
		this.injector = injector;
		
		//
		
		initWidget(binder.createAndBindUi(this));
		
		editableTagListElement.setVisible(false);
		tagListElement.setVisible(false);
	}
	
	public void init(List<GAssociationPostTag> asses, Mode mode){
		this.tagList.addAll(asses);
		this.mode = mode;
		
		if(Mode.EDIT_ONLY.equals(mode)){
			editableTagListElement.setVisible(true);
			tagListElement.setVisible(false);
		}
		else{ //Mode.STATIC.equals(mode) || Mode.EDITABLE.equals(mode)
			editableTagListElement.setVisible(false);
			tagListElement.setVisible(true);
		}
		
		for(GAssociationPostTag ass : asses){
			HyperlinkPresenter tagLink = injector.getHyperlinkPresenter();
			tagLink.setTag(ass.getTag());
			tagLink.init();//TODO: refactor this call out
			tagsElement.add(tagLink.getHyperlink());
		}
		
		
	}
	
//	@UiHandler("unMuteButton")
//	void onClickUnMute(ClickEvent event){
//		UserObjectCrudCommand cmd = new UserObjectCrudCommand();
//		cmd.setType(UserObjectConstants.TYPE_FILTER_THREAD);
//		cmd.setAction(ActionType.DELETE);
//		cmd.setValue(post.getRoot().getPostId());
//		
//		injector.getService().execute(cmd, createPostUnMuteCallback());		
//	}
//	
//	public void init(GPost post){
//		threadLinkPresenter.setPost(post);
//		threadLinkPresenter.init();
//		this.post = post;
//	}
//	
//	private CommandResultCallback<GenericCommandResult<GUserObject>> createPostUnMuteCallback() {
//		return new CommandResultCallback<GenericCommandResult<GUserObject>>(){
//				@Override
//				public void onSuccess(GenericCommandResult<GUserObject> result) {
//					removeFromParent();
//				}
//			};
//	}
}
