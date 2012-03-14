package com.thetransactioncompany.jsonrpc2.client;

import java.util.Map;

public class JsonSimpleResponse {
	private final Map<String, Object> responseMap;
	public JsonSimpleResponse(Map<String, Object> responseMap) {
		this.responseMap = responseMap;
	}
	public int getID(){
		return (Integer) responseMap.get("id");
	}
	public Map<String, Object> getResult(){
		return (Map<String, Object>) responseMap.get("result");
	}
	public String getError(){
		return (String) responseMap.get("error");
	}
}	
