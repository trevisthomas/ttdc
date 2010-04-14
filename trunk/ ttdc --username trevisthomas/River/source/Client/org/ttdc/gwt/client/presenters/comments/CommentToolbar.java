package org.ttdc.gwt.client.presenters.comments;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.client.presenters.util.MyListBox;


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
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

public class CommentToolbar extends Composite implements EmbedContentPopupSource, LinkDialogSource, HasHTML{
	private static final String HTTP_STATIC_ICONS_GIF = "http://blog.elitecoderz.net/wp-includes/js/tinymce/themes/advanced/img/icons.gif";
	private final static String HTTP_STATIC_ICONS_2_GIF = "/images/";
	private final static String TEMP_SPOILER_BACKGROUND_URL = "http://localhost:8888/images/admin_MotherOfGodsnapshot20090122112439_stn.jpg";
	
	public static final String TEMP_SPOILER_MARKUP = "style=\"background-image: url('http://localhost:8888/images/admin_MotherOfGodsnapshot20090122112439_stn.jpg');\"";
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
	
	private ToggleButton italic;
	private ToggleButton bold;
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
	
	
	
	private static final Map<String, RichTextArea.FontSize> fontSizeList = new LinkedHashMap<String, RichTextArea.FontSize>();
	static{
		fontSizeList.put("Size",null);
		fontSizeList.put("xx-small",RichTextArea.FontSize.XX_SMALL);
		fontSizeList.put("x-small",RichTextArea.FontSize.X_SMALL);
		fontSizeList.put("small",RichTextArea.FontSize.SMALL);
		fontSizeList.put("medium",RichTextArea.FontSize.MEDIUM);
		fontSizeList.put("large",RichTextArea.FontSize.LARGE);
		fontSizeList.put("x-large",RichTextArea.FontSize.X_LARGE);
		fontSizeList.put("xx-large",RichTextArea.FontSize.XX_LARGE);
	}
	
	private List<RichStyleElement> colorStyleList = new ArrayList<RichStyleElement>();
	
//	private RichStyleElement styleItalic = new RichStyleElement("Italic","span","shackTag_i","font-style:italic;");
//	private RichStyleElement styleBold = new RichStyleElement("Bold","span","shackTag_b","font-weight:bolder;");
//	private RichStyleElement styleBig = new RichStyleElement("Huge","span","shackTag_BIG","font-family:Arial, Verdana;line-height: 100%;font-size: 18pt;font-weight:700;");
//	private RichStyleElement styleSmall = new RichStyleElement("Small","span","shackTag_s","font-size: 75%;	font-weight:lighter;");
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
				
			}else if (event.getSource().equals(bold)) {
					styleTextFormatter.toggleBold();
			}else if (event.getSource().equals(italic)) {
					styleTextFormatter.toggleItalic();
			}else if (event.getSource().equals(texthtml)) {
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
		if (styleTextFormatter != null) {
			bold.setDown(styleTextFormatter.isBold());
			italic.setDown(styleTextFormatter.isItalic());
//			underline.setDown(styleTextFormatter.isUnderlined());
//			subscript.setDown(styleTextFormatter.isSubscript());
//			superscript.setDown(styleTextFormatter.isSuperscript());
//			stroke.setDown(styleTextFormatter.isStrikethrough());
		}
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
		topPanel.add(texthtml = createToggleButton(HTTP_STATIC_ICONS_GIF,20,520,20,20,"Show as HTML"));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(embed = createPushButton(HTTP_STATIC_ICONS_GIF,0,840,20,20,"Embed"));
		topPanel.add(new HTML("&nbsp;"));
		
		
		topPanel.add(blue = createPushButton("icon_blue",styleBlue));
		topPanel.add(red = createPushButton("icon_red",styleRed));
		topPanel.add(orange = createPushButton("icon_orange",styleOrange));
		topPanel.add(green = createPushButton("icon_green",styleGreen));
		
		topPanel.add(new HTML("&nbsp;"));
//		topPanel.add(italic = createPushButton(HTTP_STATIC_ICONS_GIF,0,60,20,20,styleItalic));
//		topPanel.add(bold = createPushButton(HTTP_STATIC_ICONS_GIF,0,0,20,20,styleBold));
		topPanel.add(bold = createToggleButton(HTTP_STATIC_ICONS_GIF,0,0,20,20,"Bold"));
		topPanel.add(italic = createToggleButton(HTTP_STATIC_ICONS_GIF,0,60,20,20,"Italic"));
//		topPanel.add(big = createPushButton(10,10,styleBig));
//		topPanel.add(small = createPushButton(10,10,styleSmall));
		topPanel.add(spoiler = createPushButton(HTTP_STATIC_ICONS_GIF,0,320,20,20,styleSpoiler, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String html = "<span "+TEMP_SPOILER_MARKUP+" class=\"spoiler\" onmouseover=\"this.className='reveal';\" onmouseout=\"this.className='spoiler';\">"
					+getSelectedText()+"</span>";
				styleTextFormatter.insertHTML(html);
			}
		}));
		topPanel.add(strike = createPushButton(HTTP_STATIC_ICONS_GIF,0,120,20,20,styleStrikethrough));
		topPanel.add(underline = createPushButton(HTTP_STATIC_ICONS_GIF,0,140,20,20,styleUnderline));
		topPanel.add(createPushButton(HTTP_STATIC_ICONS_GIF,20,100,20,20,styleCode,new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				styleTextFormatter.setFontName("monospace");
				styleTextFormatter.setFontSize(RichTextArea.FontSize.SMALL);
			}
		}));
		topPanel.add(quote = createPushButton(HTTP_STATIC_ICONS_GIF,0,220,20,20,styleQuote));
		topPanel.add(offsite = createPushButton(HTTP_STATIC_ICONS_GIF,20,920,20,20,styleOffsiteQuote));
		
//		subPanel.add(subscript = createToggleButton(HTTP_STATIC_ICONS_GIF,0,600,20,20,"Subscript"));
//		subPanel.add(superscript = createToggleButton(HTTP_STATIC_ICONS_GIF,0,620,20,20,"Superscript"));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(alignleft = createPushButton(HTTP_STATIC_ICONS_GIF,0,460,20,20,"Align Left", new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.setJustification(RichTextArea.Justification.LEFT);}
			}));
		subPanel.add(alignmiddle = createPushButton(HTTP_STATIC_ICONS_GIF,0,420,20,20,"Align Center",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.setJustification(RichTextArea.Justification.CENTER);}
			}));
		subPanel.add(alignright = createPushButton(HTTP_STATIC_ICONS_GIF,0,480,20,20,"Align Right",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.setJustification(RichTextArea.Justification.RIGHT);}
			}));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(orderlist = createPushButton(HTTP_STATIC_ICONS_GIF,0,80,20,20,"Ordered List",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.insertOrderedList();}
			}));
		subPanel.add(unorderlist = createPushButton(HTTP_STATIC_ICONS_GIF,0,20,20,20,"Unordered List",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.insertUnorderedList();}
			}));
		subPanel.add(indentright = createPushButton(HTTP_STATIC_ICONS_GIF,0,400,20,20,"Ident Right",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {styleTextFormatter.rightIndent();}
			}));
		subPanel.add(indentleft = createPushButton(HTTP_STATIC_ICONS_GIF,0,540,20,20,"Ident Left",new ClickHandler() {
				@Override public void onClick(ClickEvent event) { styleTextFormatter.leftIndent();}
			}));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(generatelink = createPushButton(HTTP_STATIC_ICONS_GIF,0,500,20,20,"Generate Link"));
		
		subPanel.add(breaklink = createPushButton(HTTP_STATIC_ICONS_GIF,0,640,20,20,"Break Link",new ClickHandler() {
				@Override public void onClick(ClickEvent event) {	styleTextFormatter.removeLink(); }
			}));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(insertline = createPushButton(HTTP_STATIC_ICONS_GIF,0,360,20,20,"Insert Horizontal Line",new ClickHandler() {
				@Override public void onClick(ClickEvent event) { styleTextFormatter.insertHorizontalRule();}
			}));
		
		subPanel.add(createPushButton(HTTP_STATIC_ICONS_GIF,0,600,20,20,"Subscript",new ClickHandler() {
			@Override public void onClick(ClickEvent event) { styleTextFormatter.insertHTML("<sub>"+getSelectedText()+"</sub>"); }
		}));
		
		subPanel.add(createPushButton(HTTP_STATIC_ICONS_GIF,0,620,20,20,"Superscript",new ClickHandler() {
			@Override public void onClick(ClickEvent event) { styleTextFormatter.insertHTML("<sup>"+getSelectedText()+"</sup>"); }
		}));
		
		
		subPanel.add(removeformatting = createPushButton(HTTP_STATIC_ICONS_GIF,0,160,20,20,"Remove Formatting",new ClickHandler() {
				@Override public void onClick(ClickEvent event) { styleTextFormatter.removeFormat(); }
			}));
		subPanel.add(new HTML("&nbsp;"));
		
		subPanel.add(createFontListDropdown());
		
	}
	
	private Widget createFontListDropdown() {
		MyListBox mylistBox = new MyListBox();
	    mylistBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				MyListBox listbox = (MyListBox)event.getSource();
				String selected = listbox.getSelectedValue();
				if(fontSizeList.get(selected) != null){
					styleTextFormatter.setFontSize(fontSizeList.get(selected));
					listbox.setSelectedIndex(0);
				}
			}
		});
	    mylistBox.setVisibleItemCount(1);
	    for(String key : fontSizeList.keySet()){
	    	mylistBox.addItem(key);
	    }
	    return mylistBox;
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
	
	private PushButton createPushButton(String url, Integer top, Integer left,Integer width, Integer height, final RichStyleElement style, ClickHandler handler) {
		Image icon = new Image(url, left, top, width, height);
		PushButton tb = new PushButton(icon);
		tb.setHeight(height+"px");
		tb.setWidth(width+"px");
		tb.addClickHandler(handler);
		tb.setTitle(style.getName());
		return tb;
	}
	
	
	private PushButton createPushButton(String url, Integer top, Integer left, Integer width, Integer height, final RichStyleElement style) {
		//PushButton tb = new PushButton(style.getName().substring(0,3));
		Image icon = new Image(url, left, top, width, height);
		PushButton tb = new PushButton(icon);
		tb.setHeight(height+"px");
		tb.setWidth(width+"px");
		tb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(getSelectedText().trim().equals(""))
					return;
				styleTextFormatter.insertHTML(style.wrap(getSelectedText()));
			}
		});
		tb.setTitle(style.getName());
		return tb;
	}
	
	private PushButton createPushButton(String styleName, final RichStyleElement style) {
		//PushButton tb = new PushButton(style.getName().substring(0,3));
		PushButton tb = new PushButton();
//		tb.setHeight("20px");
//		tb.setWidth("20px");
		//tb.addStyleName("icon_toolbar_button");
		tb.setStyleName("icon_toolbar_button");
		tb.addStyleName(styleName);
		tb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(getSelectedText().trim().equals(""))
					return;
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
	
	@Override
	public String getHTML() {
		return styleText.getHTML().replaceAll(TEMP_SPOILER_BACKGROUND_URL, "");
	}

	@Override
	public void setHTML(String html) {
		styleText.setHTML(html);
	}

	@Override
	public String getText() {
		return styleText.getText();
	}

	@Override
	public void setText(String text) {
		styleText.setText(text);
		
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
