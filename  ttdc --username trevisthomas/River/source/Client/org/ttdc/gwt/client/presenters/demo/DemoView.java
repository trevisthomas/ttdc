package org.ttdc.gwt.client.presenters.demo;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class DemoView implements DemoPresenter.View{
	private final RichTextArea rta = new RichTextArea();
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel show = new SimplePanel();
	private final SimplePanel messages = new SimplePanel();
	private final SimplePanel navigationPanel = new SimplePanel();
	
	public DemoView() {
		main.add(navigationPanel);
		main.add(messages);
		rta.setWidth("600px");
		rta.setHeight("500px");
		main.add(rta);
		Button linkButton = new Button("link"); 
		Button showButton = new Button("Show");
		
		main.add(linkButton);
		main.add(showButton);
		
		linkButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//Dont work on IE8	
				//String s = "<script language='JavaScript'> function tggle_1262232501650() { var s=document.getElementById(\"tggle_1262232501650\"); if ( s.innerHTML.length==0 ) s.innerHTML='<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"Http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"Http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>'; else s.innerHTML=\"\"; } </script><a href=\"javascript:tggle_1262232501650();\">s[embedded]s</a><br><span id=\"tggle_1262232501650\"></span>";
				//String s = "<script language='JavaScript'> function tggle_1262232501650() { var s=document.getElementById(\"tggle_1262232501650\"); if ( s.innerHTML.length==0 ) s.innerHTML='<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"Http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"Http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>'; else s.innerHTML=\"\"; } </script><br><span id=\"tggle_1262232501650\"></span>";
				
				
				//String s = "<script language='JavaScript'> function tggle_1262232501650() { var s=document.getElementById(\"tggle_1262232501650\"); if ( s.innerHTML.length==0 ) s.innerHTML='<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"Http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"Http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>'; else s.innerHTML=\"\"; } </script><a href=\"javascript:tggle_1262232501650();\">s[embedded]s</a><br><span id=\"tggle_1262232501650\"></span>";
				//Pass youtube url string to function
				
				
				//Didnt work at all i dont think
				//String embedYouTube = "<object width=\"425\" height=\"344\"><param name=\"movie\" value=\"Http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"Http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>";
				
				//toggletest
				//String s = "<a href=\"javascript:toggle_visibility('ToggleTarget_1262232501650');\">s[embedded]s</a><br><span class=\"hidden\" id=\"ToggleTarget_1262232501650\">"+embedYouTube+"</span>";
				
				
//				String s = "<a href=\"javascript:tggle_embed('VidTarget_1262232501650','"+embed+"');\">s[embedded]s</a><br>"; +
//						"<span id=\"VidTarget_1262232501650\"></span>";
				
				
				String embedTarget = "VidTarget_1262232501650";
				String embedSource = "http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&";
				String directSource = "http://www.youtube.com/watch?v=SDbQ5xvsrIU";
				
				String text = crazyGetSelectedText(rta);
				
				String s = "<a target=\"_blank\" href=\""+directSource+"\">"+text+"</a><a href=\"javascript:tggle_video('"+embedTarget+"','"+embedSource+"');\">[view]</a>";
				rta.getFormatter().insertHTML(s);
				
				
			}

			private String crazyGetSelectedText(RichTextArea rta) {
				final String MARKER = "http://trevsmarker.com"; 
				rta.getFormatter().createLink(MARKER);
				String withMarker = rta.getHTML();
				
				int markerIndex = withMarker.indexOf(MARKER);
				int beginIndex = withMarker.indexOf('>', markerIndex)+1;
				int endIndex = withMarker.indexOf('<', beginIndex);
				String selected = withMarker.substring(beginIndex, endIndex);
				
				rta.getFormatter().removeLink();
				return selected;
			}
		});

		showButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				show.clear();
				show.add(new HTML(rta.getHTML()));
				System.out.println(rta.getHTML());
			}
		});
		
		main.add(show);
		main.add(new HTML("<center><span id=\"VidTarget_1262232501650\"></span></center>"));
	}
	
	@Override
	public HasWidgets navigationPanel() {
		return navigationPanel;
	}
	
	@Override
	public HasWidgets messagePanel() {
		return messages;
	}

	@Override
	public void show() {
		RootPanel.get("content").clear();
		RootPanel.get("content").add(getWidget());
	}

	@Override
	public Widget getWidget() {
		return main;
	}

}
