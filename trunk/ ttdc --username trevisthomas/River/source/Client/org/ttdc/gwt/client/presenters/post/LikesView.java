package org.ttdc.gwt.client.presenters.post;

import java.util.List;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;


public class LikesView implements LikesPresenter.View{
	private HTMLPanel main;
		
	public LikesView() {
		
	}
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasWidgets getLikersTaget() {
		return main;
	}

	@Override
	public void addLike(List<Widget> likers){
		String html = "Liked by: ";
		boolean first = true;
		for(Widget w : likers){
			if(first){
				first = false;
			}
			else{
				html += ", ";
			}
			html += w.getElement().getInnerHTML();
		}
		html+=".";
		
		main = new HTMLPanel("<div>"+html+"</div>");
		
	}
}
