package org.ttdc.gwt.client.presenters.tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.autocomplete.SuggestionObject;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.constants.TagConstants;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventListener;
import org.ttdc.gwt.client.messaging.tag.TagEventType;
import org.ttdc.gwt.client.presenters.post.TagRemovePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.AssociationPostTagCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.AssociationPostTagResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.inject.Inject;

public class PostTagListPresenter extends BasePresenter<TagListPresenterView> implements TagEventListener{
	private final Map<String, HyperlinkPresenter> tagLinkPresenterMap;
	private final Map<String, TagRemovePresenter> tagRemovePresenterMap;
	private boolean editMode = false;
	private String postId = null;
	
	@Inject
	public PostTagListPresenter(Injector injector) {
		super(injector, injector.getCommonTagListView());
		tagLinkPresenterMap = new LinkedHashMap<String, HyperlinkPresenter>();
		tagRemovePresenterMap = new LinkedHashMap<String, TagRemovePresenter>();
	}
	
	@Override
	public void onTagEvent(TagEvent event) {
		if(postId == null) return;
		if(!postId.equals(event.getSource().getPost().getPostId())) return;
		if(TagEventType.NEW.equals(event.getType()))
			addTagAssociationToList(event.getSource());
		else if(TagEventType.REMOVED.equals(event.getType())){
			removeTagAssociationFromList(event.getSource());
		}
		else
			throw new RuntimeException("Unknown TagEvent type.  Not sure what to do.");
		
		notifyViewOfNewPresenterList();
	}
	
	/**
	 * Configures TagListPresenter presenter for post mode.
	 * 
	 * @param asses
	 */
	public void setTagAssociationList(List<GAssociationPostTag> asses){
		for(GAssociationPostTag ass : asses){
			if(postId == null){
				postId = ass.getPost().getPostId();
				EventBus.getInstance().addListener(this);
			}
			if(isValidTagTypeForPost(ass)){
				addTagAssociationToList(ass);
			}
		}
		//Configure the add button for adding a post to a tag
		view.getAddClickHandler().addClickHandler(new AddTagToPostClickHandler());

		notifyViewOfNewPresenterList();
	}


	private void addTagAssociationToList(GAssociationPostTag ass) {
		HyperlinkPresenter browseLinkPresenter = injector.getHyperlinkPresenter();
		browseLinkPresenter.setTag(ass.getTag());
		tagLinkPresenterMap.put(ass.getTag().getTagId(), browseLinkPresenter);
		
		TagRemovePresenter removeLinkPresenter = injector.getTagRemovePresenter();
		removeLinkPresenter.setTag(ass.getTag());
		removeLinkPresenter.addClickHandler(new RemoveTagFromPostClickHandler(ass));
		tagRemovePresenterMap.put(ass.getTag().getTagId(), removeLinkPresenter);
	}
	
	private void removeTagAssociationFromList(GAssociationPostTag ass) {
		tagLinkPresenterMap.remove(ass.getTag().getTagId());
		tagRemovePresenterMap.remove(ass.getTag().getTagId());
	}
	
	private boolean isValidTagTypeForPost(GAssociationPostTag ass) {
		return TagConstants.TYPE_TOPIC.equals(ass.getTag().getType()) && !ass.isTitle();
	}

	public boolean isEditMode() {
		return editMode;
	}
	
	public void toggleEditMode(){
		setEditMode(!isEditMode());
	}

	public void setEditMode(boolean editMode) {
		if(this.editMode != editMode){
			this.editMode = editMode;
			notifyViewOfNewPresenterList();
		}
	}

	private void notifyViewOfNewPresenterList() {
		if(isEditMode())
			view.showPresenters(getTagIdList(), new ArrayList<BasePresenter<?>>(tagRemovePresenterMap.values()));
		else
			view.showPresenters(getTagIdList(), new ArrayList<BasePresenter<?>>(tagLinkPresenterMap.values()));
	}

	Map<String, HyperlinkPresenter> getTagLinkPresenterMap() {
		return tagLinkPresenterMap;
	}

	Map<String, TagRemovePresenter> getTagRemovePresenterMap() {
		return tagRemovePresenterMap;
	}
	
	public List<String> getTagIdList(){
		List<String> tagIds = new ArrayList<String>();
		tagIds.addAll(tagRemovePresenterMap.keySet());
		return tagIds;
	} 
	
	
	private class AssociationPostTagCallback extends CommandResultCallback<AssociationPostTagResult>{
		@Override
		public void onSuccess(AssociationPostTagResult result) {
			if(result.isCreate()){
				addTagAssociationToList(result.getAssociationPostTag());
			}
			else if(result.isRemove()){
				//nothing to do
			}
			else{
				MessageEvent event = new MessageEvent(MessageEventType.SYSTEM_ERROR,result.getMessage());
				EventBus.getInstance().fireEvent(event);
			}
		}
	}
	
	public void setHighlightedTagIdList(List<String> highlightedTagIdList){
		for(HyperlinkPresenter linkPresenter : tagLinkPresenterMap.values()){
			if(highlightedTagIdList.contains(linkPresenter.getTag().getTagId())){
				linkPresenter.setHighlighted(true);
			}
			else{
				linkPresenter.setHighlighted(false);
			}
		}
	}
	
	/**
	 * 
	 * Click Handler for removing a tag from a post
	 *
	 */
	private class RemoveTagFromPostClickHandler implements ClickHandler{
		private final AssociationPostTagCommand command;
		private final String tagId;
		
		public RemoveTagFromPostClickHandler(GAssociationPostTag ass) {
			command = new AssociationPostTagCommand();
			command.setAssociationId(ass.getGuid());
			command.setConnectionId(ConnectionId.getInstance().getConnectionId());
			tagId = ass.getTag().getTagId();
		}
		
		public void onClick(ClickEvent event) {
			RpcServiceAsync service = injector.getService(); 
			service.execute(command, new AssociationPostTagCallback());
			tagLinkPresenterMap.remove(tagId);
			tagRemovePresenterMap.remove(tagId);
			notifyViewOfNewPresenterList();
		}
	}
	
	//I dont think that this code is really used anywhere
	/**
	 * Click Handler for adding a tag to a post. Be aware that this is assuming 
	 * that the server will send out an event in the event bus about the new association.
	 * 
	 * Trevis, you may have a problem since removes happen immediately but adds require a round trip
	 * 
	 */
	private class AddTagToPostClickHandler implements ClickHandler{
		public void onClick(ClickEvent event) {
			SuggestionObject tagSuggestion = view.getTagSuggestion();
			final AssociationPostTagCommand addTagCommand = 
				new AssociationPostTagCommand();
			addTagCommand.setTag(tagSuggestion.getTag());
			addTagCommand.setPostId(postId);
			RpcServiceAsync service = injector.getService(); 
			service.execute(addTagCommand, new AssociationPostTagCallback());
		}
	}
	
}
