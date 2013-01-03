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
	private static int PAGE_SIZE = 24;

	public PhotoSet loadPhotoSet(String photoSetId) {
		URL serverURL = null;

		try {
			serverURL = new URL("http://www.zenfolio.com/api/1.6/zfapi.asmx");
	
			JsonSimpleSession mySession = new JsonSimpleSession(serverURL);
			mySession.setConnectionConfigurator(new MyConnectionConfiguratorDude());
			
			List<String> params = new ArrayList<String>();
			
			String method = "LoadPhotoSet";
			params.add(photoSetId);
			params.add("2"); //http://secure.zenfolio.com/zf/help/api/ref/methods/loadphotoset //Dosent seem to work.
			params.add("true");
	
			JSONRPC2Request request = new JSONRPC2Request(method, params, requestID++);
			
			
			JsonSimpleResponse response = null;

			response = mySession.send(request);
			
			return new PhotoSet(response.getResult(), null);
			
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
	
	public PhotoSet loadPhotoSetPhotos(String photoSetId, int pageNumber) {
		URL serverURL = null;

		try {
			serverURL = new URL("http://www.zenfolio.com/api/1.6/zfapi.asmx");
	
			JsonSimpleSession mySession = new JsonSimpleSession(serverURL);
			mySession.setConnectionConfigurator(new MyConnectionConfiguratorDude());
			
			List<String> params = new ArrayList<String>();
			
			String method = "LoadPhotoSetPhotos";
			params.add(photoSetId);
			
			if(pageNumber < 1){
				params.add("1");
			}
			else{
				params.add( ""+ (pageNumber-1) * PAGE_SIZE);
			}
			//params.add("" + pageNumber * PAGE_SIZE);
			
			params.add(""+PAGE_SIZE);
	
			JSONRPC2Request request = new JSONRPC2Request(method, params, requestID++);
			
			
			JsonSimpleResponse response = null;

			response = mySession.send(request);
			
			return new PhotoSet(response.getResultFromList(), photoSetId);
			
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
	
	/**
	 * This version is a hack.  It expects that the id is a collection id and not a gallery id.  
	 * This is only important because for some reason the zenfolio api returns the gallery id
	 * from the webservice and not the collection id.  This makes the pageUrl links take you to the gallery 
	 * which is not what we wanted. So i hacked some code together to swap the values.  If this 
	 * isnt working for an image it's probably because the first segment of the path after photos.bvg.com/ isnt the letter p.
	 * 
	 *   I expect that they look like this : http://photos.boyvsgirlphotography.com/p356653574/e2733BD78
	 * 
	 * @param collectionId
	 * @return
	 */
	public PhotoSet loadPhotoSetAsCollection(String collectionId) {
		URL serverURL = null;

		try {
			serverURL = new URL("http://www.zenfolio.com/api/1.6/zfapi.asmx");
	
			JsonSimpleSession mySession = new JsonSimpleSession(serverURL);
			mySession.setConnectionConfigurator(new MyConnectionConfiguratorDude());
			
			List<String> params = new ArrayList<String>();
			
			String method = "LoadPhotoSet";
			params.add(collectionId);
			params.add("1");
			params.add("true");
	
			JSONRPC2Request request = new JSONRPC2Request(method, params, requestID++);
			
			
			JsonSimpleResponse response = null;

			response = mySession.send(request);
			
			return new PhotoSet(response.getResult(), collectionId);
			
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

	public Photo loadPhoto(String photoId) {
		URL serverURL = null;

		try {
			serverURL = new URL("http://www.zenfolio.com/api/1.6/zfapi.asmx");
	
			JsonSimpleSession mySession = new JsonSimpleSession(serverURL);
			mySession.setConnectionConfigurator(new MyConnectionConfiguratorDude());
			
			List<String> params = new ArrayList<String>();
			
			String method = "LoadPhoto";
			params.add(photoId);
			params.add("2"); 
			
	
			JSONRPC2Request request = new JSONRPC2Request(method, params, requestID++);
			JsonSimpleResponse response = null;
			response = mySession.send(request);

			return new Photo(response.getResult());
			
			
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
	
	
}
