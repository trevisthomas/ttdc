package org.ttdc.gwt.client.presenters.comments;


/*
 * This software is published under the Apchae 2.0 licenses.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Author: Erik Scholtz 
 * Web: http://blog.elitecoderz.net
 */


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;

public class RichTextToolbar extends Composite{
	private static final String HTTP_STATIC_ICONS_GIF = "http://blog.elitecoderz.net/wp-includes/js/tinymce/themes/advanced/img/icons.gif";

	/** Private Variables **/
	//The main (Vertical)-Panel and the two inner (Horizontal)-Panels
	private VerticalPanel outer;
	private HorizontalPanel topPanel;
	private HorizontalPanel bottomPanel;

	//The RichTextArea this Toolbar referes to and the Interfaces to access the RichTextArea
	private RichTextArea styleText;
	private Formatter styleTextFormatter;

	//We use an internal class of the ClickHandler and the KeyUpHandler to be private to others with these events
	private EventHandler evHandler;
	
	private String embedTarget;

	//The Buttons of the Menubar
	private ToggleButton bold;
	private ToggleButton italic;
	private ToggleButton underline;
	private ToggleButton stroke;
	private ToggleButton subscript;
	private ToggleButton superscript;
	private PushButton alignleft;
	private PushButton alignmiddle;
	private PushButton alignright;
	private PushButton orderlist;
	private PushButton unorderlist;
	private PushButton indentleft;
	private PushButton indentright;
	private PushButton generatelink;
	private PushButton breaklink;
	private PushButton insertline;
	private PushButton insertimage;
	private PushButton removeformatting;
	private PushButton youtube;
	private PushButton embed;
	private ToggleButton texthtml;
	
	private ListBox fontlist;
	private ListBox colorlist;

	/** Constructor of the Toolbar **/
	public RichTextToolbar(RichTextArea richtext, String embedTarget) {
		this.embedTarget = embedTarget;
		
		//Initialize the main-panel
		outer = new VerticalPanel();

		//Initialize the two inner panels
		topPanel = new HorizontalPanel();
		bottomPanel = new HorizontalPanel();
		topPanel.setStyleName("RichTextToolbar");
		bottomPanel.setStyleName("RichTextToolbar");

		//Save the reference to the RichText area we refer to and get the interfaces to the stylings

		styleText = richtext;
		styleTextFormatter = styleText.getFormatter();

		//Set some graphical options, so this toolbar looks how we like it.
		topPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		bottomPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);

		//Add the two inner panels to the main panel
		outer.add(topPanel);
		outer.add(bottomPanel);

		//Some graphical stuff to the main panel and the initialisation of the new widget
		outer.setWidth("100%");
		outer.setStyleName("RichTextToolbar");
		initWidget(outer);

		//
		evHandler = new EventHandler();

		//Add KeyUp and Click-Handler to the RichText, so that we can actualize the toolbar if neccessary
		styleText.addKeyUpHandler(evHandler);
		styleText.addClickHandler(evHandler);

		//Now lets fill the new toolbar with life
		buildTools();
	}

	/** Click Handler of the Toolbar **/
	private class EventHandler implements ClickHandler,KeyUpHandler, ChangeHandler {
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(bold)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"font-weight: bold;\">","</span>");
				} else {
					styleTextFormatter.toggleBold();
				}
			} else if (event.getSource().equals(italic)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"font-style: italic;\">","</span>");
				} else {
					styleTextFormatter.toggleItalic();
				}
			} else if (event.getSource().equals(underline)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"text-decoration: underline;\">","</span>");
				} else {
					styleTextFormatter.toggleUnderline();
				}
			} else if (event.getSource().equals(stroke)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"text-decoration: line-through;\">","</span>");
				} else {
					styleTextFormatter.toggleStrikethrough();
				}
			} else if (event.getSource().equals(subscript)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<sub>","</sub>");
				} else {
					styleTextFormatter.toggleSubscript();
				}
			} else if (event.getSource().equals(superscript)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<sup>","</sup>");
				} else {
					styleTextFormatter.toggleSuperscript();
				}
			} else if (event.getSource().equals(alignleft)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<div style=\"text-align: left;\">","</div>");
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.LEFT);
				}
			} else if (event.getSource().equals(alignmiddle)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<div style=\"text-align: center;\">","</div>");
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.CENTER);
				}
			} else if (event.getSource().equals(alignright)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<div style=\"text-align: right;\">","</div>");
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.RIGHT);
				}
			} else if (event.getSource().equals(orderlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<ol><li>","</li></ol>");
				} else {
					styleTextFormatter.insertOrderedList();
				}
			} else if (event.getSource().equals(unorderlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<ul><li>","</li></ul>");
				} else {
					styleTextFormatter.insertUnorderedList();
				}
			} else if (event.getSource().equals(indentright)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<div style=\"margin-left: 40px;\">","</div>");
				} else {
					styleTextFormatter.rightIndent();
				}
			} else if (event.getSource().equals(indentleft)) {
				if (isHTMLMode()) {
					//TODO nothing can be done here at the moment
				} else {
					styleTextFormatter.leftIndent();
				}
			} else if (event.getSource().equals(generatelink)) {
				String url = Window.prompt("Enter a link URL:", "http://");
				if (url != null) {
					if (isHTMLMode()) {
						changeHtmlStyle("<a href=\""+url+"\">","</a>");
					} else {
						styleTextFormatter.createLink(url);
					}
				}
			} else if (event.getSource().equals(breaklink)) {
				if (isHTMLMode()) {
					//TODO nothing can be done here at the moment
				} else {
					styleTextFormatter.removeLink();
				}
			} else if (event.getSource().equals(insertimage)) {
				String url = Window.prompt("Enter an image URL:", "http://");
				if (url != null) {
					if (isHTMLMode()) {
						changeHtmlStyle("<img src=\""+url+"\">","");
					} else {
						styleTextFormatter.insertImage(url);
					}
				}
			}  else if (event.getSource().equals(insertline)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<hr style=\"width: 100%; height: 2px;\">","");
				} else {
					styleTextFormatter.insertHorizontalRule();
				}
			} else if (event.getSource().equals(removeformatting)) {
				if (isHTMLMode()) {
					//TODO nothing can be done here at the moment
				} else {
					styleTextFormatter.removeFormat();
				}
			} else if (event.getSource().equals(texthtml)) {
				if (texthtml.isDown()) {
					styleText.setText(styleText.getHTML());
				} else {
					styleText.setHTML(styleText.getText());
				}
			} else if (event.getSource().equals(styleText)) {
				//Change invoked by the richtextArea
			} else if(event.getSource().equals(youtube))	{
				String embedSource = "http://www.youtube.com/v/SDbQ5xvsrIU&hl=en_US&fs=1&";
				String directSource = "http://www.youtube.com/watch?v=SDbQ5xvsrIU";
				
				//String text = crazyGetSelectedText(styleText);
				String text = getSelectedText();
				
				String s = "<a target=\"_blank\" href=\""+directSource+"\">"+text+"</a><a href=\"javascript:tggle_video('"+embedTarget+"','"+embedSource+"');\">[view]</a>";
				styleText.getFormatter().insertHTML(s);
				
				//changeHtmlStyle("<a target=\"_blank\" href=\""+directSource+"\">","</a><a href=\"javascript:tggle_video('"+embedTarget+"','"+embedSource+"');\">[view]</a>");
			}
			else if(event.getSource().equals(embed)){
				String selectedText = getSimpleSelection();
				EmbedContentPopup popup = new EmbedContentPopup(RichTextToolbar.this, selectedText);
				popup.setGlassEnabled(true);
				popup.setAnimationEnabled(true);
				popup.center();
				popup.show();
			}
			
			updateStatus();
		}

		public void onKeyUp(KeyUpEvent event) {
			updateStatus();
		}

		public void onChange(ChangeEvent event) {
			System.out.println("fire");
			if (event.getSource().equals(fontlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"font-family: "+fontlist.getValue(fontlist.getSelectedIndex())+";\">","</span>");
				} else {
					styleTextFormatter.setFontName(fontlist.getValue(fontlist.getSelectedIndex()));
				}
			} else if (event.getSource().equals(colorlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"color: "+colorlist.getValue(colorlist.getSelectedIndex())+";\">","</span>");
				} else {
					styleTextFormatter.setForeColor(colorlist.getValue(colorlist.getSelectedIndex()));
				}
			}
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
  		if (elem.contentWindow.getSelection) {
        	txt = elem.contentWindow.getSelection();
        	pos = elem.contentWindow.getSelection().getRangeAt(0).startOffset;
        } else if (elem.contentWindow.document.getSelection) {
        	txt = elem.contentWindow.document.getSelection();
        	pos = elem.contentWindow.document.getSelection().getRangeAt(0).startOffset;
  		} else if (elem.contentWindow.document.selection) {
        	txt = elem.contentWindow.document.selection.createRange().text;
        	pos = elem.contentWindow.document.selection.getRangeAt(0).startOffset;
        }
  		return [""+txt,""+pos];
	}-*/;

	/** Method called to toggle the style in HTML-Mode **/
	private void changeHtmlStyle(String startTag, String stopTag) {
		JsArrayString tx = getSelection(styleText.getElement());
		String txbuffer = styleText.getText();
		Integer startpos = Integer.parseInt(tx.get(1));
		String selectedText = tx.get(0);
		styleText.setText(txbuffer.substring(0, startpos)+startTag+selectedText+stopTag+txbuffer.substring(startpos+selectedText.length()));
	}

	/** Private method with a more understandable name to get if HTML mode is on or not **/
	private Boolean isHTMLMode() {
		return  texthtml.isDown();
	}

	/** Private method to set the toggle buttons and disable/enable buttons which do not work in html-mode **/
	private void updateStatus() {
		if (styleTextFormatter != null) {
			bold.setDown(styleTextFormatter.isBold());
			italic.setDown(styleTextFormatter.isItalic());
			underline.setDown(styleTextFormatter.isUnderlined());
			subscript.setDown(styleTextFormatter.isSubscript());
			superscript.setDown(styleTextFormatter.isSuperscript());
			stroke.setDown(styleTextFormatter.isStrikethrough());
		}
		
		if (isHTMLMode()) {
			removeformatting.setEnabled(false);
			indentleft.setEnabled(false);
			breaklink.setEnabled(false);
		} else {
			removeformatting.setEnabled(true);
			indentleft.setEnabled(true);
			breaklink.setEnabled(true);
		}
	}

	/** Initialize the options on the toolbar **/
	private void buildTools() {
		//Init the TOP Panel forst
		topPanel.add(bold = createToggleButton(HTTP_STATIC_ICONS_GIF,0,0,20,20,"Bold"));
		topPanel.add(italic = createToggleButton(HTTP_STATIC_ICONS_GIF,0,60,20,20,"Italic"));
		topPanel.add(underline = createToggleButton(HTTP_STATIC_ICONS_GIF,0,140,20,20,"Underline"));
		topPanel.add(stroke = createToggleButton(HTTP_STATIC_ICONS_GIF,0,120,20,20,"Stroke"));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(subscript = createToggleButton(HTTP_STATIC_ICONS_GIF,0,600,20,20,"Subscript"));
		topPanel.add(superscript = createToggleButton(HTTP_STATIC_ICONS_GIF,0,620,20,20,"Superscript"));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(alignleft = createPushButton(HTTP_STATIC_ICONS_GIF,0,460,20,20,"Align Left"));
		topPanel.add(alignmiddle = createPushButton(HTTP_STATIC_ICONS_GIF,0,420,20,20,"Align Center"));
		topPanel.add(alignright = createPushButton(HTTP_STATIC_ICONS_GIF,0,480,20,20,"Align Right"));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(orderlist = createPushButton(HTTP_STATIC_ICONS_GIF,0,80,20,20,"Ordered List"));
		topPanel.add(unorderlist = createPushButton(HTTP_STATIC_ICONS_GIF,0,20,20,20,"Unordered List"));
		topPanel.add(indentright = createPushButton(HTTP_STATIC_ICONS_GIF,0,400,20,20,"Ident Right"));
		topPanel.add(indentleft = createPushButton(HTTP_STATIC_ICONS_GIF,0,540,20,20,"Ident Left"));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(generatelink = createPushButton(HTTP_STATIC_ICONS_GIF,0,500,20,20,"Generate Link"));
		topPanel.add(breaklink = createPushButton(HTTP_STATIC_ICONS_GIF,0,640,20,20,"Break Link"));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(insertline = createPushButton(HTTP_STATIC_ICONS_GIF,0,360,20,20,"Insert Horizontal Line"));
		topPanel.add(insertimage = createPushButton(HTTP_STATIC_ICONS_GIF,0,380,20,20,"Insert Image"));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(removeformatting = createPushButton(HTTP_STATIC_ICONS_GIF,20,460,20,20,"Remove Formatting"));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(texthtml = createToggleButton(HTTP_STATIC_ICONS_GIF,0,260,20,20,"Show as HTML"));

		//Init the BOTTOM Panel
		bottomPanel.add(fontlist = createFontList());
		bottomPanel.add(new HTML("&nbsp;"));
		bottomPanel.add(colorlist = createColorList());
		bottomPanel.add(new HTML("&nbsp;"));
		bottomPanel.add(youtube = createPushButton(HTTP_STATIC_ICONS_GIF,5,80,25,20,"Youtube"));
		bottomPanel.add(embed = createPushButton(HTTP_STATIC_ICONS_GIF,5,80,25,20,"Embed"));
		
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
	
	/** Method to create the fontlist for the toolbar **/
	private ListBox createFontList() {
	    ListBox mylistBox = new ListBox();
	    mylistBox.addChangeHandler(evHandler);
	    mylistBox.setVisibleItemCount(1);
	
	    mylistBox.addItem("Fonts", "");
	    mylistBox.addItem("Times New Roman", "Times New Roman");
	    mylistBox.addItem("Arial", "Arial");
	    mylistBox.addItem("Courier New", "Courier New");
	    mylistBox.addItem("Georgia", "Georgia");
	    mylistBox.addItem("Trebuchet", "Trebuchet");
	    mylistBox.addItem("Verdana", "Verdana");
	    return mylistBox;
	}
	
	/** Method to create the colorlist for the toolbar **/
	private ListBox createColorList() {
	    ListBox mylistBox = new ListBox();
	    mylistBox.addChangeHandler(evHandler);
	    mylistBox.setVisibleItemCount(1);
	
	    mylistBox.addItem("Colors");
	    mylistBox.addItem("white", "white");
	    mylistBox.addItem("black", "black");
	    mylistBox.addItem("red", "red");
	    mylistBox.addItem("green", "green");
	    mylistBox.addItem("yellow", "yellow");
	    mylistBox.addItem("blue", "blue");
	    return mylistBox;
	}
	
	public void performLinkEmbed(String selectedText, String directSource, String embedSource) {
		embedSource = embedSource.replaceAll("\"", ""); //Hack because inserting the htmp performs some html encoding that complety messes up when there are qutation marks
		String s = "<a target=\"_blank\" href=\""+directSource+"\">"+selectedText+"</a><a href=\"javascript:tggle_embed('"+embedTarget+"','"+embedSource+"');\">[view]</a>";
		styleText.getFormatter().insertHTML(s);
	}

	public String getSimpleSelection(){
		return getSimpleSelection(styleText.getElement()).get(0).toString();
	}

	public static native JsArrayString getSimpleSelection(Element elem) /*-{
		var txt = "";
		if (elem.contentWindow.getSelection != undefined) {
			txt = elem.contentWindow.getSelection();
		} else if (elem.contentWindow.document.getSelection != undefined) {
			txt = elem.contentWindow.document.getSelection();
		} else if (elem.contentWindow.document.selection  != undefined) {
			txt = elem.contentWindow.document.selection.createRange().text;
		}
		return [""+txt];
	}-*/;
	
}


