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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

public class CommentToolbar extends Composite implements EmbedContentPopupSource, LinkDialogSource, HasHTML{
	private static final String HTTP_STATIC_ICONS_GIF = "http://blog.elitecoderz.net/wp-includes/js/tinymce/themes/advanced/img/icons.gif";
	private RichTextArea richTextArea;
	private Formatter styleTextFormatter;
	private VerticalPanel outer = new VerticalPanel();
	private final HorizontalPanel topPanel = new HorizontalPanel();
	private final HorizontalPanel subPanel = new HorizontalPanel();
	
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
	
	public PushButton alignleft;
	private PushButton alignmiddle;
	private PushButton alignright;
	private PushButton orderlist;
	private PushButton unorderlist;
	private PushButton indentleft;
	private PushButton indentright;
	private PushButton insertline;
	private PushButton insertimage;
	private PushButton removeformatting;
	private PushButton subscript;
	private PushButton superscript;
	private PushButton generatelink;
	private PushButton breaklink;
	
	private PushButton monospacedcode;
	
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
	
	private RichStyleElement styleStrikethrough = new RichStyleElement("Strike","span","shackTag_strike");
	private RichStyleElement styleUnderline = new RichStyleElement("Underline","span","shackTag_u");
	private RichStyleElement styleQuote = new RichStyleElement("Quote","div","shackTag_q");
	private RichStyleElement styleOffsiteQuote = new RichStyleElement("Offsite","div","shackTag_o");
	//Beaware that code didnt exist pre v7
	private RichStyleElement styleCode = new RichStyleElement("Code","div","shackTag_code");
	private RichStyleElement styleBlue = new RichStyleElement("Blue", "span", "shackTag_blue");
	private RichStyleElement styleRed = new RichStyleElement("Red", "span", "shackTag_red");
	private RichStyleElement styleOrange = new RichStyleElement("Orange", "span", "shackTag_orange");
	private RichStyleElement styleGreen = new RichStyleElement("Green", "span", "shackTag_green");
	
	public CommentToolbar(RichTextArea richtext) {
		this.richTextArea = richtext;
		
		styleTextFormatter = richTextArea.getFormatter();
		topPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		subPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		outer.add(topPanel);
		outer.setWidth("100%");
		
		outer.add(topPanel);
		outer.add(subPanel);
		
		initWidget(outer);
		

		evHandler = new EventHandler();

		buildTools();
		
	}

	//So sloppy...
	private String tmpSelectedText;
	private int tmpStartPos;
	
	private final static PopupPanel popup = new PopupPanel(false);
	
	private class EventHandler implements ClickHandler,KeyUpHandler, ChangeHandler {
		public void onClick(ClickEvent event) {
			if(event.getSource().equals(embed)){
				styleTextFormatter.removeFormat();
				tmpSelectedText = getSelectedText();
				tmpStartPos = startPositionInHtml();
				EmbedContentPopup popup = new EmbedContentPopup(CommentToolbar.this, tmpSelectedText);
				popup.showPositionRelativeTo(embed);
				//popup.show();
			}else if(event.getSource().equals(generatelink)){
				styleTextFormatter.removeFormat();
				tmpSelectedText = getSelectedText();
				tmpStartPos = startPositionInHtml();
				LinkDialog popup = new LinkDialog(CommentToolbar.this, tmpSelectedText);
				popup.showPositionRelativeTo(generatelink);
				//popup.show();	
				
			}else if (event.getSource().equals(bold)) {
					styleTextFormatter.toggleBold();
			}else if (event.getSource().equals(italic)) {
					styleTextFormatter.toggleItalic();
			}else if (event.getSource().equals(texthtml)) {
				if (texthtml.isDown()) {
					richTextArea.setText(richTextArea.getHTML());
				} else {
					richTextArea.setHTML(richTextArea.getText());
				}
			}
			else if (event.getSource().equals(orderlist)) {
				styleTextFormatter.insertOrderedList();
			}
			else if (event.getSource().equals(alignleft)) {
				styleTextFormatter.setJustification(RichTextArea.Justification.LEFT);
			}
			else if (event.getSource().equals(alignmiddle)) {
				styleTextFormatter.setJustification(RichTextArea.Justification.CENTER);
			}
			else if (event.getSource().equals(alignright)) {
				styleTextFormatter.setJustification(RichTextArea.Justification.RIGHT);
			}
			else if (event.getSource().equals(unorderlist)) {
				styleTextFormatter.insertUnorderedList();
			}
			else if (event.getSource().equals(indentleft)) {
				styleTextFormatter.leftIndent();
			}
			else if (event.getSource().equals(indentright)) {
				styleTextFormatter.rightIndent();
			}
			else if (event.getSource().equals(breaklink)) {
				styleTextFormatter.removeLink();
				styleTextFormatter.removeFormat();
			}
			else if(event.getSource().equals(insertline)){
				styleTextFormatter.insertHorizontalRule();
			}
			else if(event.getSource().equals(subscript)){
				styleTextFormatter.insertHTML("<sub>"+getSelectedText()+"</sub>");
				styleTextFormatter.removeFormat();
			}
			else if(event.getSource().equals(superscript)){
				styleTextFormatter.insertHTML("<sup>"+getSelectedText()+"</sup>");
				styleTextFormatter.removeFormat();
			}
			else if(event.getSource().equals(removeformatting)){
				styleTextFormatter.removeFormat();
			}
			else if(event.getSource().equals(spoiler)){
				String html = "<span class=\"spoiler\" onmouseover=\"this.className='reveal';\" onmouseout=\"this.className='spoiler';\">"
					+getSelectedText()+"</span>";
				styleTextFormatter.insertHTML(html);
				styleTextFormatter.removeFormat();
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
		JsArrayString tx = getSelection(richTextArea.getElement());
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
////			underline.setDown(styleTextFormatter.isUnderlined());
////			subscript.setDown(styleTextFormatter.isSubscript());
////			superscript.setDown(styleTextFormatter.isSuperscript());
////			stroke.setDown(styleTextFormatter.isStrikethrough());
//		}
////		
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
		topPanel.add(italic = createPushButton(HTTP_STATIC_ICONS_GIF,0,60,20,20,"Italic"));
		topPanel.add(bold = createPushButton(HTTP_STATIC_ICONS_GIF,0,0,20,20,"Bold"));
//		topPanel.add(bold = createToggleButton(HTTP_STATIC_ICONS_GIF,0,0,20,20,"Bold"));
//		topPanel.add(italic = createToggleButton(HTTP_STATIC_ICONS_GIF,0,60,20,20,"Italic"));
		

		topPanel.add(spoiler = createPushButton(HTTP_STATIC_ICONS_GIF,0,320,20,20, "Spoiler"));
		topPanel.add(strike = createPushButton(HTTP_STATIC_ICONS_GIF,0,120,20,20,styleStrikethrough));
		topPanel.add(underline = createPushButton(HTTP_STATIC_ICONS_GIF,0,140,20,20,styleUnderline));
		topPanel.add(createPushButton(HTTP_STATIC_ICONS_GIF,20,100,20,20, styleCode));
		
		
		topPanel.add(quote = createPushButton(HTTP_STATIC_ICONS_GIF,0,220,20,20,styleQuote));
		topPanel.add(offsite = createPushButton(HTTP_STATIC_ICONS_GIF,20,920,20,20,styleOffsiteQuote));
		
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(alignleft = createPushButton(HTTP_STATIC_ICONS_GIF,0,460,20,20,"Align Left"));
		subPanel.add(alignmiddle = createPushButton(HTTP_STATIC_ICONS_GIF,0,420,20,20,"Align Center"));
		subPanel.add(alignright = createPushButton(HTTP_STATIC_ICONS_GIF,0,480,20,20,"Align Right"));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(orderlist = createPushButton(HTTP_STATIC_ICONS_GIF,0,80,20,20,"Ordered List"));
		subPanel.add(unorderlist = createPushButton(HTTP_STATIC_ICONS_GIF,0,20,20,20,"Unordered List"));
		
		subPanel.add(indentright = createPushButton(HTTP_STATIC_ICONS_GIF,0,400,20,20,"Ident Right"));
		subPanel.add(indentleft = createPushButton(HTTP_STATIC_ICONS_GIF,0,540,20,20,"Ident Left"));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(generatelink = createPushButton(HTTP_STATIC_ICONS_GIF,0,500,20,20,"Generate Link"));
		
		subPanel.add(breaklink = createPushButton(HTTP_STATIC_ICONS_GIF,0,640,20,20,"Break Link"));
		subPanel.add(new HTML("&nbsp;"));
		subPanel.add(insertline = createPushButton(HTTP_STATIC_ICONS_GIF,0,360,20,20,"Insert Horizontal Line"));
		
		subPanel.add(subscript = createPushButton(HTTP_STATIC_ICONS_GIF,0,600,20,20,"Sub script"));
		
		subPanel.add(superscript = createPushButton(HTTP_STATIC_ICONS_GIF,0,620,20,20,"Super script"));
		
		
		subPanel.add(removeformatting = createPushButton(HTTP_STATIC_ICONS_GIF,0,160,20,20,"Remove Formatting"));
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
				styleTextFormatter.removeFormat();
			}
		});
		tb.setTitle(style.getName());
		return tb;
	}
	
	private PushButton createPushButton(String styleName, final RichStyleElement style) {
		PushButton tb = new PushButton();
		tb.setStyleName("icon_toolbar_button");
		tb.addStyleName(styleName);
		tb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(getSelectedText().trim().equals(""))
					return;
				styleTextFormatter.insertHTML(style.wrap(getSelectedText()));
				styleTextFormatter.removeFormat();
			}
		});
		tb.setTitle(style.getName());
		return tb;
	}
	
	
	
	
	@Override
	public void performLinkEmbed(String selectedText, String directSource, String embedSource) {
		embedSource = embedSource.replaceAll("\"", ""); //Hack because inserting the htmp performs some html encoding that complety messes up when there are qutation marks
		
		insertHtmlAt(selectedText,tmpSelectedText, tmpStartPos, "<a target=\"_blank\" href=\""+directSource+"\">",
			"</a><a href=\"javascript:tggle_embed7('"+embedSource+"');\">[view]</a>");
		
	}
	
	@Override
	public void performLink(String selectedText, String directSource) {
		insertHtmlAt(selectedText,tmpSelectedText, tmpStartPos, "<a target=\"_blank\" href=\""+directSource+"\">","</a>");
	}
	
	private int startPositionInHtml(){
		String marker = "http://123TTDC";
		richTextArea.getFormatter().createLink(marker);
		int startpos = richTextArea.getHTML().indexOf(marker) - 9; //"<a href="
		richTextArea.getFormatter().removeLink();
		if(startpos < 0) //Added Sept 9 because all of a sudden nothing fucking worked.  Adding the marker link doesnt do anything
			startpos = 0;
		return startpos;
	}
	
	
	private void insertHtmlAt(String newText, String selectedText, int startpos,  String startTag, String stopTag) {
		String txbuffer = richTextArea.getHTML();
		richTextArea.setHTML(txbuffer.substring(0, startpos)+startTag+newText+stopTag+txbuffer.substring(startpos+selectedText.length()));
	}
	
	@Override
	public String getHTML() {
		//return styleText.getHTML().replaceAll(TEMP_SPOILER_BACKGROUND_URL, "");
		return richTextArea.getHTML();
	}

	@Override
	public void setHTML(String html) {
		richTextArea.setHTML(html);
	}

	@Override
	public String getText() {
		return richTextArea.getText();
	}

	@Override
	public void setText(String text) {
		richTextArea.setText(text);
		
	}

}
