package org.ttdc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlTools {
	public static Document loadDocumentFromFile(String filename) {
		File file = new File(filename);
		return loadDocumentFromFile(file);
	}
	
	public static Document loadDocumentFromFile(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        Document xmlDoc = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			InputSource source = new InputSource(reader);
			xmlDoc = builder.parse(source);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return xmlDoc;
	}
	
	
	public static Document createDocumentFromString(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        StringReader reader = new StringReader(xml);
        InputSource source = new InputSource(reader);
        Document xmlDoc = builder.parse(source);
        return xmlDoc;
    }	
}
