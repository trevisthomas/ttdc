package org.ttdc.gwt.client.messaging.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ttdc.gwt.shared.util.EqualsUtil;
import org.ttdc.gwt.shared.util.HashCodeUtil;


import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * My site views are driven from these tokens.  They tie into the GWT History system to allow
 * users to save bookmarks and to navigate the site via hyperlinks.  It's basically an 
 * url query string.  
 * 
 * @author Trevis
 *
 */
public class HistoryToken implements IsSerializable{
	private Map<String,String> map = new LinkedHashMap<String,String>();
	
	public HistoryToken(){}
	
	public HistoryToken(String queryString){
		if(queryString != null && queryString.trim().length() > 0){
			init(queryString);
		}
	}
	
	public void load(HistoryToken that){
		map.clear();
		map.putAll(that.map);
	}

	private void init(String queryString) {
		String[] nvpairs = queryString.split("&");
		for (int x=0; x<nvpairs.length; x++){
			String[] pair = nvpairs[x].split("=");
			if(pair.length < 2) throw new RuntimeException("Invalid Query String");
			map.put(pair[0],pair[1]);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Set<String> keys = map.keySet();
		for(String key : keys){
			if(sb.length() > 0) sb.append("&");
			sb.append(key);
			sb.append("=");
			sb.append(map.get(key));
		}
		return sb.toString();
	}
	
	public String getParameter(String name){
		return map.get(name);
	}
	
	public String getParameter(String name, String defaultValue){
		if(hasParameter(name))
			return getParameter(name);
		else
			return defaultValue;
	}
	
	public Integer getParameterAsInt(String name){
		return Integer.parseInt(getParameter(name));
	}
	
	public Integer getParameterAsInt(String name, int i) {
		if(hasParameter(name)){
			return getParameterAsInt(name);
		}
		else{
			return i;
		}
	}
	
	public Long getParameterAsLong(String name){
		return Long.parseLong(getParameter(name));
	}
	
	public Long getParameterAsLong(String name, long i) {
		if(hasParameter(name)){
			return getParameterAsLong(name);
		}
		else{
			return i;
		}
	}
	
	public void  setParameter(String name, int value){
		setParameter(name, ""+value);
	}
	
	public void  setParameter(String name, long value){
		setParameter(name, ""+value);
	}
	
	public void setParameter(String name, String value){
		if(name == null || value == null) 
			throw new RuntimeException("HistoryToken.setParameter(): Dont give me crap!");
		map.put(name, value);
	}
	
	public void addParameter(String name, String value){
		if(isParameterEq(name, value)) return; //Dont add duplicates
		if(name == null || value == null) 
			throw new RuntimeException("HistoryToken.setParameter(): Dont give me crap!");
		//HACK!! Fix this! Make it so that this thing can really handle multiple better
		if(map.containsKey(name)){
			String tmp = map.get(name);
			map.put(name, tmp + "," +value);
		}
		else
			map.put(name, value);
	}
	
	
	
	public void removeParameter(String name){
		map.remove(name);
	}
	
	public List<String> getParameterList(String name){
		String val = getParameter(name);
		if(val == null) return new ArrayList<String>();
		String values [] = val.split(",");
		List<String> list = new ArrayList<String>(Arrays.asList(values));
		return list;
	}
	
	public boolean hasParameter(String name){
		return map.containsKey(name);
	}
	
	public boolean isParameterEq(String name, String target){
		if(!hasParameter(name)) return false;
		return getParameter(name).equals(target);
	}

	
	@Override
	public boolean equals(Object obj) {
		if( this == obj) return true;
		if( !(obj instanceof HistoryToken) ) return false;
		HistoryToken that = (HistoryToken) obj;
		if(this.map.size() != that.map.size()) return false;
		for(String key : this.map.keySet()){
			if(!EqualsUtil.areEqual(this.map.get(key), that.map.get(key))) return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, this.toString());
		return result;
	}
}
