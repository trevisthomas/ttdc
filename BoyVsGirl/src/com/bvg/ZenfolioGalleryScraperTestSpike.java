package com.bvg;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.ConnectionConfigurator;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import com.thetransactioncompany.jsonrpc2.client.JsonSimpleResponse;
import com.thetransactioncompany.jsonrpc2.client.JsonSimpleSession;

public class ZenfolioGalleryScraperTestSpike {

	public static void main(String[] args) {
		ZenfolioGalleryScraperTestSpike test = new ZenfolioGalleryScraperTestSpike();

		test.test();
	}

	public void test() {
		// The JSON-RPC 2.0 server URL
		URL serverURL = null;

		try {
			serverURL = new URL("http://www.zenfolio.com/api/1.6/zfapi.asmx");

		} catch (MalformedURLException e) {
			// handle exception...
		}

		// Create new JSON-RPC 2.0 client session
		JsonSimpleSession mySession = new JsonSimpleSession(serverURL);

		// Construct new request
//		String method = "LoadPhotoSet";
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("photosetId", "p616294023");
//		params.put("level", "Level1");
//		params.put("includePhotos", "true");
		
		
//		String method = "LoadPhoto";
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("photoId", "e17d8726d");
//		
//		
//		params.put("level", "Level1");
		
		
		
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("photoId", "e17d8726d");
//		
//		
//		params.put("level", "Level1");
		
		
		List<String> params = new ArrayList<String>();
		
//		String method = "LoadPhoto";
//		//params.add("327977501");
//		params.add("143527856");
//		params.add("Level1");
//		
		
		String method = "LoadPhotoSet";
		params.add("854996360");
		params.add("1");
		params.add("true");
		
		
//		String method = "GetChallenge";
//		params.add("davidandmeisphotography");
		
		
		
		
		mySession.getOptions().ignoreVersion(true);
		
		
		int requestID = 1;
		JSONRPC2Request request = new JSONRPC2Request(method, params, requestID);

//		mySession.setConnectionConfigurator(new MyConnectionConfiguratorDude());
		
		//mySession.getOptions().setRequestContentType("text/xml; charset=utf-8");
		//mySession.getOptions().setRequestContentType("application/json");
		
		

		// Send request
		JsonSimpleResponse response = null;

		try {
			response = mySession.send(request);
			
			Object photos = response.getResult().get("Photos");
			System.err.println(photos);
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class MyConnectionConfiguratorDude implements ConnectionConfigurator {
		@Override
		public void configure(HttpURLConnection connection) {
			try {
				connection.setRequestMethod("POST");
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connection.addRequestProperty("X-Zenfolio-User-Agent",
					"Acme PhotoEdit plugin for Zenfolio v1.0");
			
			//connection.addRequestProperty("Cookie", "zf_ua=Acme%20PhotoEdit%20plugin%20for%20Zenfolio%20v1.0;");
					
					
			
			//Cookie: zf_ua=Acme%20PhotoEdit%20plugin%20for%20Zenfolio%20v1.0;
					
					connection.addRequestProperty("User-Agent", "Acme PhotoEdit plugin for Zenfolio v1.0");
			//connection.addRequestProperty("User-Agent", "Boy vs Girl is rocking the house, v1");
			
//			connection.setRequestProperty("Host", "www.zenfolio.com");
//System.err.println(connection.getContentLength());
			
		}
	}
}
