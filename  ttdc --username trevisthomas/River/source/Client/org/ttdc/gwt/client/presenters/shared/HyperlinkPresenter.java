package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.post.SearchTagResultsPresenter;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.inject.Inject;

public class HyperlinkPresenter extends BasePresenter<HyperlinkPresenter.View> {
	private GTag tag;
	private GPerson person;
	private GPost post;
	private String url;
	
	public enum StyleType{
		DEFAULT,
		PAGINATOR
	}
	
	@Inject
	public HyperlinkPresenter(Injector injector) {
		super(injector,injector.getHyperlinkView());
	}
	
	/**
	 * 
	 * The View interface
	 *
	 */
	public static interface View extends BaseView{
		public HistoryToken getHistoryToken();
		public HasText getDisplayName();
		public HasClickHandlers getLinkHandlers();
		public void setHighlighted(boolean enabled);
		public boolean isHighlighted(); 
		public void setCloudRank(int cloudRank);
		public void setStyleType(StyleType styleType);
		public void setUrl(String url);
		public Hyperlink getHyperlink();
		public void setStyleName(String style);
	}
	
	public void setPerson(GPerson person){
		this.person = person;
		view.getHistoryToken().setParameter(HistoryConstants.VIEW,HistoryConstants.VIEW_USER_PROFILE); 
		view.getHistoryToken().setParameter(HistoryConstants.PERSON_ID,person.getPersonId());
		view.getDisplayName().setText(person.getLogin());
		//No need to listen for the click, it's handled internally by the underlying Hyperlink
		init();
	}
	
	public void setTag(GTag tag){
		this.tag = tag;
		view.getHistoryToken().setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_SEARCH_TAG_RESULTS); 
		view.getHistoryToken().setParameter(HistoryConstants.SEARCH_TAG_ID_KEY,tag.getTagId());
		
		view.getDisplayName().setText(tag.getValue());
		view.setCloudRank(tag.getCloudRank());
		
		//Finish setting up history for having a clickable tag
	}
	
	public void setToken(HistoryToken token, String value){
		view.getHistoryToken().load(token);
		view.getDisplayName().setText(value);
		init();//This must be called before the link will render.  
	}
	
	public void setView(String name, String viewConstant){
		view.getHistoryToken().setParameter(HistoryConstants.VIEW, viewConstant);
		view.getDisplayName().setText(name);
		init();//This must be called before the link will render.  
			   //Since this setView doesn't expect any other arguments, i do it here so that the users don't have to
		 	   //but it's not very intuitive.
	}
	
	public void setPost(GPost post) {
		try{
			setPost(post,post.getTitle());
		}
		catch (Exception e) {
			setPost(post,"Exception getting name");
		}
	}
	
	public void setPost(GPost post, String title) {
		this.post = post;
		view.getHistoryToken().setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC); 
		view.getHistoryToken().setParameter(HistoryConstants.POST_ID_KEY,post.getPostId());
		view.getDisplayName().setText(title);
	}
	
	public void setUrl(String url){
		this.url = url;
		view.setUrl(url);
	}
	
	/*
	 *   This isnt used as of Nov 5.  I think that it is a relic of how i thought that this would work
	 *   i think that i left it because it might be useful. The important part though is that 
	 *   these links mostly do their own work.  No need to handle the click.
	 */
	public void addClickHandler(ClickHandler clickHandler) {
		view.getLinkHandlers().addClickHandler(clickHandler);	
	}
	
	public void setText(String value){
		view.getDisplayName().setText(value);
	}

	/**
	 * Initially this is for testing purposes only
	 * 
	 * @return
	 */
	public GTag getTag() {
		return tag;
	}
	
	public void setStyleName(String style){
		view.setStyleName(style);
	}

	/**
	 * Initially this is for testing purposes only
	 * 
	 * @return
	 */
	public GPerson getPerson() {
		return person;
	}

	/**
	 * Highlighted allows an alternate rendering view for chosen hyperlinks.  What being 
	 * highlighted means would be defined by the view implementation.
	 * 
	 * @return
	 */
	public boolean isHighlighted() {
		return view.isHighlighted();
	}

	public void setHighlighted(boolean enabled) {
		view.setHighlighted(enabled);
	}
	
	public void setStyleType(StyleType styleType){
		view.setStyleType(styleType);
	}

	public Hyperlink getHyperlink(){
		return view.getHyperlink();
	}
	
	public void init(){
		view.getWidget();
	}
	
}
