package org.ttdc.gwt.client.presenters.util;

import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public class UnorderedListWidget extends ComplexPanel
{
	public UnorderedListWidget()
	{
		setElement(Document.get().createULElement());
	}

	public void setId(String id)
	{
		// Set an attribute common to all tags
		getElement().setId(id);
	}

	public void setDir(String dir)
	{
		// Set an attribute specific to this tag
		((UListElement) getElement().cast()).setDir(dir);
	}

	public void add(Widget w)
	{
		// ComplexPanel requires the two-arg add() method
		super.add(w, getElement());
	}
	
	public void addItem(Widget widget){
		add(new ListItemWidget(widget));
	}
	public void loadHyperlinks(List<Hyperlink> links)
	{
		clear();
		for (int i=0; i<links.size(); i++)
		{
			Hyperlink item = links.get(i);
			if (i>0)
			{
				add(new ListItemWidget("|"));
			}
			add(new ListItemWidget(item));
		}
	}
	
	public void loadAnchors(List<Anchor> links)
	{
		clear();
		for (int i=0; i<links.size(); i++)
		{
			Anchor item = links.get(i);
			if (i>0)
			{
				add(new ListItemWidget("|"));
			}
			add(new ListItemWidget(item));
		}
	}
}
