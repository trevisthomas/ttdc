package com.thetransactioncompany.jsonrpc2.client;

import java.util.HashMap;
import java.util.List;
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
	public Map<String, Object> getResultFromList(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Photos", (List<Map<String, Object>>)responseMap.get("result"));
		return map;
	}
	public String getError(){
		return (String) responseMap.get("error");
	}
}	
