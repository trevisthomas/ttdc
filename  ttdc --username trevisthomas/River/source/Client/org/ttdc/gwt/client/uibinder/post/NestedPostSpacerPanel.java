package org.ttdc.gwt.client.uibinder.post;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.presenters.post.PostPresenterCommon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class NestedPostSpacerPanel extends Composite implements PostPresenterCommon{
	interface MyUiBinder extends UiBinder<Widget, NestedPostSpacerPanel> {}
	private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
	private PostSummaryPanel postSummaryDelegate;
	private Injector injector;
	
	@UiField FlexTable nestedSpacerTableElement;
	
	@Inject
	public NestedPostSpacerPanel(Injector injector) {
		this.injector = injector;
		initWidget(binder.createAndBindUi(this));
		nestedSpacerTableElement.setStyleName("tt-fill");
	}
	
	public void initialize(GPost post, PostSummaryPanel summaryWidget){
		postSummaryDelegate = summaryWidget;
		int spacers = post.getPath().split("\\.").length - 2;
		
		
		for(int i = 0; i < spacers ; i++){
			nestedSpacerTableElement.setWidget(0, i, new Label(" "));
			nestedSpacerTableElement.getFlexCellFormatter().addStyleName(0, i, "tt-nested-spacer tt-nested-spacer-blank");
		}
		nestedSpacerTableElement.setWidget(0, spacers, summaryWidget);
		
		//buildFancySpacer(post,summaryWidget);
	}
	
	private void buildFancySpacer(GPost post, PostSummaryPanel summaryWidget) {
		postSummaryDelegate = summaryWidget;
    	GPost threadPost = post.getThread();
    	MagicNestedSpacer magic = new MagicNestedSpacer();
    	List<String> styles = magic.decisionEngine(post.isEndOfBranch(), threadPost.getPathSegmentMax(), post.getPathSegmentArray());
			
		int col = -1;
		
    	for(String style : styles){
    		Label label = new Label();
    		label.setStyleName("tt-nested-spacer");
    		label.addStyleName(style);
    		
    		String background =  magic.getBackgroundRepeaterForStyle(style);
    		
    		if(col == -1){
    			col++;
    		}
    		else{
    			nestedSpacerTableElement.setWidget(0, col, label);
    			nestedSpacerTableElement.getFlexCellFormatter().addStyleName(0, col++, "tt-nested-spacer "+background);
    		}
    	}
    	
    	nestedSpacerTableElement.setWidget(0, col, summaryWidget);
	}

	@Override
	public void contractPost() {
		postSummaryDelegate.contractPost();		
	}

	@Override
	public String getPostId() {
		return postSummaryDelegate.getPostId();
	}

	@Override
	public void expandPost() {
		postSummaryDelegate.expandPost();		
	}

	@Override
	public GPost getPost() {
		return postSummaryDelegate.getPost();
	}
	
	@Override
	public Widget getWidget() {
		return this;
	}
}
