package org.ttdc.gwt.client.presenters.comments;

import java.util.ArrayList;
import java.util.List;


import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

public class CommentToolbar extends Composite implements EmbedContentPopupSource, LinkDialogSource{
	private static final String HTTP_STATIC_ICONS_GIF = "http://blog.elitecoderz.net/wp-includes/js/tinymce/themes/advanced/img/icons.gif";
	private RichTextArea styleText;
	private Formatter styleTextFormatter;
	private VerticalPanel outer = new VerticalPanel();
	private final HorizontalPanel topPanel = new HorizontalPanel();
	private final HorizontalPanel subPanel = new HorizontalPanel();
	//private JsArrayString tx;
	
	//We use an internal class of the ClickHandler and the KeyUpHandler to be private to others with these events
	private EventHandler evHandler;
	private String embedTarget;
	
	private PushButton embed;
	private PushButton colorSelect;
	private ToggleButton texthtml;
	
	private PushButton blue;
	private PushButton red;
	private PushButton orange;
	private PushButton green;
	
	private PushButton italic;
	private PushButton bold;
	private PushButton big;
	private PushButton small;
	private PushButton spoiler;
	private PushButton strike;
	private PushButton underline;
	private PushButton quote;
	private PushButton offsite;
	
	private PushButton alignleft;
	private PushButton alignmiddle;
	private PushButton alignright;
	private PushButton orderlist;
	private PushButton unorderlist;
	private PushButton indentleft;
	private PushButton indentright;
	private PushButton insertline;
	private PushButton insertimage;
	private PushButton removeformatting;
//	private ToggleButton subscript;
//	private ToggleButton superscript;
	private PushButton generatelink;
	private PushButton breaklink;
	
//	private PushButton generatelink;
//	private PushButton breaklink;
	
	
	
	
	
	private List<RichStyleElement> colorStyleList = new ArrayList<RichStyleElement>();
	
	private RichStyleElement styleItalic = new RichStyleElement("Italic","span","shackTag_i","font-style:italic;");
	private RichStyleElement styleBold = new RichStyleElement("Bold","span","shackTag_b","font-weight:bolder;");
	private RichStyleElement styleBig = new RichStyleElement("Huge","span","shackTag_BIG","font-family:Arial, Verdana;line-height: 100%;font-size: 18pt;font-weight:700;");
	private RichStyleElement styleSmall = new RichStyleElement("Small","span","shackTag_s","font-size: 75%;	font-weight:lighter;");
	private RichStyleElement styleSpoiler = new RichStyleElement("Spoiler","span","shackTag_i","font-style:italic;");
	private RichStyleElement styleStrikethrough = new RichStyleElement("Strike","span","shackTag_strike","text-decoration:line-through;");
	private RichStyleElement styleUnderline = new RichStyleElement("Underline","span","shackTag_u","text-decoration: underline;");
	private RichStyleElement styleQuote = new RichStyleElement("Quote","div","shackTag_o","border:solid; border-width:1px;border-left-width:0px;border-right-width:0px;padding: 4px;margin:10px;font-size:100%;font-style:italic;");
	private RichStyleElement styleOffsiteQuote = new RichStyleElement("Offsite","div","shackTag_o","font-family:verdana;border:dashed;border-width:1px;border-left-width:0px;border-right-width:0px;padding: 4px;margin:10px;font-size:100%;font-style:normal;");
	//Beaware that code didnt exist pre v7
	private RichStyleElement styleCode = new RichStyleElement("Code","pre","shackTag_code","font-family:'Courier New',Courier,monospace;font-size: 80%;");
	//private RichStyleElement styleLink = new RichStyleElement("Link","div","shackTag_o","border:solid; border-width:1px;border-left-width:0px;border-right-width:0px;padding: 4px;margin:10px;font-size:100%;font-style:italic;");
	
	private RichStyleElement styleBlue = new RichStyleElement("Blue", "span", "shackTag_blue","color:blue");
	private RichStyleElement styleRed = new RichStyleElement("Red", "span", "shackTag_red","color:red");
	private RichStyleElement styleOrange = new RichStyleElement("Orange", "span", "shackTag_orange","color:orange");
	private RichStyleElement styleGreen = new RichStyleElement("Green", "span", "shackTag_gren","color:green");
	
	public CommentToolbar(RichTextArea richtext, String embedTarget) {
		this.styleText = richtext;
		this.embedTarget = embedTarget;
		
		styleTextFormatter = styleText.getFormatter();
		topPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		subPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		outer.add(topPanel);
		outer.setWidth("100%");
		
		outer.add(topPanel);
		outer.add(subPanel);
		
//		outer.setStyleName("RichTextToolbar");
		initWidget(outer);
		

		evHandler = new EventHandler();

		//styleText.addKeyUpHandler(evHandler);
		//styleText.addClickHandler(evHandler);

		buildTools();
		
//		colorStyleList.add(new RichStyleElement("Blue", "<span class=\"shackTag_blue\">","<span style=\"color: blue\">", "</span>"));
//		colorStyleList.add(new RichStyleElement("Red", "<span class=\"shackTag_red\">", "<span style=\"color: red\">", "</span>"));
//		colorStyleList.add(new RichStyleElement("Orange", "<span class=\"shackTag_orange\">","<span style=\"color: orange\">", "</span>"));
//		colorStyleList.add(new RichStyleElement("Green", "<span class=\"shackTag_green\">", "<span style=\"color: green\">","</span>"));
	}

	//So sloppy...
	private String tmpSelectedText;
	private int tmpStartPos;
	private class EventHandler implements ClickHandler,KeyUpHandler, ChangeHandler {
		public void onClick(ClickEvent event) {
			if(event.getSource().equals(embed)){
				styleTextFormatter.removeFormat();
				tmpSelectedText = getSelectedText();
				tmpStartPos = startPositionInHtml();
				EmbedContentPopup popup = new EmbedContentPopup(CommentToolbar.this, tmpSelectedText);
				popup.setGlassEnabled(true);
				popup.setAnimationEnabled(true);
				popup.center();
				popup.show();
			}else if(event.getSource().equals(generatelink)){
				styleTextFormatter.removeFormat();
				tmpSelectedText = getSelectedText();
				tmpStartPos = startPositionInHtml();
				LinkDialog popup = new LinkDialog(CommentToolbar.this, tmpSelectedText);
				popup.setGlassEnabled(true);
				popup.setAnimationEnabled(true);
				popup.center();
				popup.show();	
				
			} else if (event.getSource().equals(texthtml)) {
				if (texthtml.isDown()) {
					styleText.setText(styleText.getHTML());
				} else {
					styleText.setHTML(styleText.getText());
				}
			}
			updateStatus();
		}
		@Override
		public void onChange(ChangeEvent event) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onKeyUp(KeyUpEvent event) {
			updateStatus();
		}
	}
	private String getSelectedText(){
		JsArrayString tx = getSelection(styleText.getElement());
		String selectedText = tx.get(0);
		return selectedText;
	}
	
	/** Native JavaScript that returns the selected text and position of the start **/
	public static native JsArrayString getSelection(Element elem) /*-{
		var txt = "";
		var pos = 0;
		var range;
    	var parentElement;
    	var container;

        if (elem.contentWindow.getSelection) {
        	txt = elem.contentWindow.getSelection();
        	pos = elem.contentWindow.getSelection().getRangeAt(0).startOffset;
        } else if (elem.contentWindow.document.getSelection) {
        	txt = elem.contentWindow.document.getSelection();
        	pos = elem.contentWindow.document.getSelection().getRangeAt(0).startOffset;
  		} else if (elem.contentWindow.document.selection) {
  			range = elem.contentWindow.document.selection.createRange();
        	txt = range.text;
        	parentElement = range.parentElement();
        	container = range.duplicate();
        	container.moveToElementText(parentElement);
        	container.setEndPoint('EndToEnd', range);
        	pos = container.text.length - range.text.length;
        }
  		return [""+txt,""+pos];
	}-*/;

	

//	/** Method called to toggle the style in HTML-Mode **/
//	private void changeHtmlStyle(String startTag, String stopTag) {
//		JsArrayString tx = getSelection(styleText.getElement());
//		String txbuffer = styleText.getText();
//		Integer startpos = Integer.parseInt(tx.get(1));
//		String selectedText = tx.get(0);
//		styleText.setText(txbuffer.substring(0, startpos)+startTag+selectedText+stopTag+txbuffer.substring(startpos+selectedText.length()));
//	}

	/** Private method with a more understandable name to get if HTML mode is on or not **/
	private Boolean isHTMLMode() {
		return  texthtml.isDown();
	}

	/** Private method to set the toggle buttons and disable/enable buttons which do not work in html-mode **/
	private void updateStatus() {
//		if (styleTextFormatter != null) {
//			bold.setDown(styleTextFormatter.isBold());
//			italic.setDown(styleTextFormatter.isItalic());
//			underline.setDown(styleTextFormatter.isUnderlined());
//			subscript.setDown(styleTextFormatter.isSubscript());
//			superscript.setDown(styleTextFormatter.isSuperscript());
//			stroke.setDown(styleTextFormatter.isStrikethrough());
//		}
//		
		if (isHTMLMode()) {
			embed.setEnabled(false);
		} else {
			embed.setEnabled(true);
		}
	}

	/** Initialize the options on the toolbar **/
	private void buildTools() {
		//Init the TOP Panel forst
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(texthtml = createToggleButton(HTTP_STATIC_ICONS_GIF,0,260,20,20,"Show as HTML"));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(embed = createPushButton(HTTP_STATIC_ICONS_GIF,5,80,25,20,"Embed"));
		topPanel.add(new HTML("&nbsp;"));
		
		
		topPanel.add(blue = createPushButton(10,10,styleBlue));
		topPanel.add(red = createPushButton(10,10,styleRed));
		topPanel.add(orange = createPushButton(10,10,styleOrange));
		topPanel.add(green = createPushButton(10,10,styleGreen));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(italic = createPushButton(10,10,styleItalic));
		topPanel.add(bold = createPushButton(10,10,styleBold));
		topPanel.add(big = createPushButton(10,10,styleBig));
		topPanel.add(small = createPushButton(10,10,styleSmall));
		topPanel.add(spoiler = createPushButton(10,10,styleSpoiler));
		topPanel.add(strike = createPushButton(10,10,styleStrikethrough));
		topPanel.add(underline = createPushButton(10,10,styleUnderline));
		topPanel.add(createPushButton(10,10,styleCode,new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				styleTextFormatter.setFontName("monospace");
				styleTextFormatter.setFontSize(RichTextArea.FontSize.SMALL);
			}
		}));
		topPanel.add(quote = createPushButton(10,10,styleQuote));
		topPanel.add(offsite = createPushButton(10,10,styleOffsiteQuote));
		
//		subPanel.add(subscript = createToggleButton(HTTP_STATIC_ICONS_GIF,0,600,20,20,"Subscript"));
//		subPanel.add(superscript = createToggleButton(HTTP_STATIC_ICONS_GIF,0,620,20,20,"Superscript"));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(alignleft = createPushButton(HTTP_STATIC_ICONS_GIF,0,460,10,10,"Align Left", new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.setJustification(RichTextArea.Justification.LEFT);}
			}));
		subPanel.add(alignmiddle = createPushButton(HTTP_STATIC_ICONS_GIF,0,420,10,10,"Align Center",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.setJustification(RichTextArea.Justification.CENTER);}
			}));
		subPanel.add(alignright = createPushButton(HTTP_STATIC_ICONS_GIF,0,480,10,10,"Align Right",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.setJustification(RichTextArea.Justification.RIGHT);}
			}));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(orderlist = createPushButton(HTTP_STATIC_ICONS_GIF,0,80,10,10,"Ordered List",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.insertOrderedList();}
			}));
		subPanel.add(unorderlist = createPushButton(HTTP_STATIC_ICONS_GIF,0,10,10,10,"Unordered List",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.insertUnorderedList();}
			}));
		subPanel.add(indentright = createPushButton(HTTP_STATIC_ICONS_GIF,0,400,10,10,"Ident Right",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.rightIndent();}
			}));
		subPanel.add(indentleft = createPushButton(HTTP_STATIC_ICONS_GIF,0,540,10,10,"Ident Left",new ClickHandler() {
				@Override public void onClick(ClickEvent event) { styleTextFormatter.leftIndent();}
			}));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(generatelink = createPushButton(HTTP_STATIC_ICONS_GIF,0,500,10,10,"Generate Link"));
		
		subPanel.add(breaklink = createPushButton(HTTP_STATIC_ICONS_GIF,0,640,10,10,"Break Link",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {	styleTextFormatter.removeLink(); }
			}));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(insertline = createPushButton(HTTP_STATIC_ICONS_GIF,0,360,10,10,"Insert Horizontal Line",new ClickHandler() {
				@Override public void onClick(ClickEvent event) { styleTextFormatter.insertHorizontalRule();}
			}));
		
		subPanel.add(removeformatting = createPushButton(HTTP_STATIC_ICONS_GIF,20,460,10,10,"Remove Formatting",new ClickHandler() {
				@Override public void onClick(ClickEvent event) { styleTextFormatter.removeFormat(); }
			}));
		subPanel.add(new HTML("&nbsp;"));
		
		
	}

	/** Method to create a Toggle button for the toolbar **/
	private ToggleButton createToggleButton(String url, Integer top, Integer left, Integer width, Integer height, String tip) {
		Image extract = new Image(url, left, top, width, height);
		ToggleButton tb = new ToggleButton(extract);
		tb.setHeight(height+"px");
		tb.setWidth(width+"px");
		tb.addClickHandler(evHandler);
		if (tip != null) {
			tb.setTitle(tip);
		}
		return tb;
	}

	/** Method to create a Push button for the toolbar **/
	private PushButton createPushButton(String url, Integer top, Integer left, Integer width, Integer height, String tip) {
		Image extract = new Image(url, left, top, width, height);
		PushButton tb = new PushButton(extract);
		tb.setHeight(height+"px");
		tb.setWidth(width+"px");
		tb.addClickHandler(evHandler);
		if (tip != null) {
			tb.setTitle(tip);
		}
		return tb;
	}
	
	/** Method to create a Push button for the toolbar **/
	private PushButton createPushButton(String url, Integer top, Integer left, Integer width, Integer height, String tip, ClickHandler handler) {
		Image extract = new Image(url, left, top, width, height);
		PushButton tb = new PushButton(extract);
		tb.setHeight(height+"px");
		tb.setWidth(width+"px");
		tb.addClickHandler(handler);
		if (tip != null) {
			tb.setTitle(tip);
		}
		return tb;
	}
	
	private PushButton createPushButton(Integer width, Integer height, final RichStyleElement style, ClickHandler handler) {
		PushButton tb = new PushButton(style.getName().substring(0,3));
		tb.setHeight(height+"px");
		tb.setWidth(width+"px");
		tb.addClickHandler(handler);
		tb.setTitle(style.getName());
		return tb;
	}
	
	
	private PushButton createPushButton(Integer width, Integer height, final RichStyleElement style) {
		PushButton tb = new PushButton(style.getName().substring(0,3));
		tb.setHeight(height+"px");
		tb.setWidth(width+"px");
		tb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				styleTextFormatter.insertHTML(style.wrap(getSelectedText()));
			}
		});
		tb.setTitle(style.getName());
		return tb;
	}
	
	@Override
	public void performLinkEmbed(String selectedText, String directSource, String embedSource) {
		embedSource = embedSource.replaceAll("\"", ""); //Hack because inserting the htmp performs some html encoding that complety messes up when there are qutation marks
//		String s = "<a target=\"_blank\" href=\""+directSource+"\">"+selectedText+"</a><a href=\"javascript:tggle_embed('"+embedTarget+"','"+embedSource+"');\">[view]</a>";
		//styleText.getFormatter().insertHTML(s);
		
		insertHtmlAt(selectedText,tmpSelectedText, tmpStartPos, "<a target=\"_blank\" href=\""+directSource+"\">",
			"</a><a href=\"javascript:tggle_embed('"+embedTarget+"','"+embedSource+"');\">[view]</a>");
		
//		insertHtmlAt(tx, "<a target=\"_blank\" href=\""+directSource+"\">",
//			"</a><a href=\"javascript:tggle_embed('"+embedTarget+"','"+embedSource+"');\">[view]</a>");
	}
	
	@Override
	public void performLink(String selectedText, String directSource) {
		insertHtmlAt(selectedText,tmpSelectedText, tmpStartPos, "<a target=\"_blank\" href=\""+directSource+"\">","</a>");
	}
	
//	private void insertHtmlAt(JsArrayString tx, String startTag, String stopTag) {
//		String txbuffer = styleText.getHTML();
//		Integer startpos = Integer.parseInt(tx.get(1));
//		String selectedText = tx.get(0);
//		styleText.setHTML(txbuffer.substring(0, startpos)+startTag+selectedText+stopTag+txbuffer.substring(startpos+selectedText.length()));
//	}
//	
//	private int startPositionInHtml(String text){
//		//If the text is duplicated, this wont work.
//		int startpos = styleText.getHTML().indexOf(text);
//		return startpos;
//	}
	
	private int startPositionInHtml(){
		String marker = "http://123TTDC";
		styleText.getFormatter().createLink(marker);
		int startpos = styleText.getHTML().indexOf(marker) - 9; //"<a href="
		styleText.getFormatter().removeLink();
		return startpos;
	}
	
	
	private void insertHtmlAt(String newText, String selectedText, int startpos,  String startTag, String stopTag) {
		String txbuffer = styleText.getHTML();
		styleText.setHTML(txbuffer.substring(0, startpos)+startTag+newText+stopTag+txbuffer.substring(startpos+selectedText.length()));
	}

//	private class RichStyleElementPopup extends PopupPanel{
//		private VerticalPanel clickableItems = new VerticalPanel();
//		public RichStyleElementPopup(List<RichStyleElement> list) {
//			super(true);
//			setAnimationEnabled(true);
//			setWidget(clickableItems);
//			JsArrayString tx = getSelection(styleText.getElement());
//			for(RichStyleElement styleElement : list){
//				HTML item = new HTML(styleElement.getName());
//				item.addClickHandler(new RichStyleClickHandler(tx,styleElement));
//				clickableItems.add(item);
//			}
//		}
//	}
//	
//	private class RichStyleClickHandler implements ClickHandler{
//		private JsArrayString tx;
//		private final RichStyleElement element;
//		public RichStyleClickHandler(JsArrayString tx, RichStyleElement element) {
//			this.element = element;
//			this.tx = tx;
//		}
//		
//		@Override
//		public void onClick(ClickEvent event) {
//			insertHtmlAt(tmpSelectedText, tmpStartPos, element.getOpenTag(),element.getCloseTag());
//			updateStatus();
//		}
//	}
}
