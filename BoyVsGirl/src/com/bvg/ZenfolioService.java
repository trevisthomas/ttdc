package com.bvg;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.thetransactioncompany.jsonrpc2.client.*;
import com.thetransactioncompany.jsonrpc2.*;


public class ZenfolioService {
	private static int requestID = 0;

	public PhotoSet loadPhotoSet(String photoSetId) {
		URL serverURL = null;

		try {
			serverURL = new URL("http://www.zenfolio.com/api/1.6/zfapi.asmx");
	
			JsonSimpleSession mySession = new JsonSimpleSession(serverURL);
			mySession.setConnectionConfigurator(new MyConnectionConfiguratorDude());
			
			List<String> params = new ArrayList<String>();
			
			String method = "LoadPhotoSet";
			params.add(photoSetId);
			params.add("1");
			params.add("true");
	
			JSONRPC2Request request = new JSONRPC2Request(method, params, requestID++);
			
			
			JsonSimpleResponse response = null;

			response = mySession.send(request);
			
			return new PhotoSet(response.getResult());
			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public class MyConnectionConfiguratorDude implements ConnectionConfigurator {
		@Override
		public void configure(HttpURLConnection connection) {
			try {
				connection.setRequestMethod("POST");
			} catch (ProtocolException e) {
				throw new RuntimeException(e);
			}
			connection.addRequestProperty("X-Zenfolio-User-Agent", "Boy vs Girl Photography v1");
			connection.addRequestProperty("User-Agent", "Boy vs Girl Photography v1");
		}
	}
	
}
