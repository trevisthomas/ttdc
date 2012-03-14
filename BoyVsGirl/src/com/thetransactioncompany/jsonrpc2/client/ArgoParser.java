package com.thetransactioncompany.jsonrpc2.client;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import argo.jdom.JdomParser;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import argo.saj.JsonListener;
import argo.saj.SajParser;

public class ArgoParser {
	private static final JdomParser JDOM_PARSER = new JdomParser();
	public static void parse(String txt){
		//parseSax(txt);
		parseJackson(txt);
//		try {
//			JsonRootNode json = JDOM_PARSER.parse(txt);
//			
//			//Map map = json.getObjectNode("PhotoSet",1);
//			
//			Map map = json.getObjectNode("result");
//			
//			
//			
////			Map map = json.getFields();
////			
////			Set keys = map.keySet();
////			Object o = map.get("result");
////			
////			for(Object k : keys){
////				System.err.println(k);
////			}
//			System.err.println(json);
//		} catch (InvalidSyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private static final SajParser SAJ_PARSER = new SajParser();
	public static void parseSax(String text){
			
			final Set<String> fieldNames = new HashSet<String>();
			Reader jsonReader = new StringReader(text);
			try {
				SAJ_PARSER.parse(jsonReader, new JsonListener() {
				    public void startField(String name) {
				    	System.err.println("Start Field: "+ name);	
				        fieldNames.add(name);
				    }
				    public void startDocument() { System.err.println("Start Doc"); }
				    public void endDocument() { System.err.println("End Doc"); }
					@Override
					public void startArray() {
						 System.err.println("startArray");
					}
					@Override
					public void endArray() {
						System.err.println("endArray");
						
					}
					@Override
					public void startObject() {
						System.err.println("startObject");
						
					}
					@Override
					public void endObject() {
						System.err.println("endObject");
						
					}
					@Override
					public void endField() {
						System.err.println("endObject");
						
					}
					@Override
					public void stringValue(String value) {
						System.err.println("stringValue");
						
					}
					@Override
					public void numberValue(String value) {
						System.err.println("numberValue");
						
					}
					@Override
					public void trueValue() {
						System.err.println("trueValue");
						
					}
					@Override
					public void falseValue() {
						System.err.println("falseValue");
						
					}
					@Override
					public void nullValue() {
						System.err.println("nullValue");
						
					}
				    
				
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	private static final ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
	
	public static void parseJackson(String text) {
		Reader jsonReader = new StringReader(text);
		try {
			Map<String, Object> map = mapper.readValue(jsonReader, Map.class);
			
			Map o = (Map)map.get("result");
			Set keys = o.keySet();
			map.get("result");
			for (Object k : keys) {
				System.err.println(k);
			}
			
			//map.get("result");

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//public static class Result
}
