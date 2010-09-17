package org.ttdc.gwt.client.uibinder.post;

import java.util.ArrayList;
import java.util.List;

public class MagicNestedSpacer {
	static final String BLANK = "tt-nested-spacer-blank";
	static final String LEAF = "tt-nested-spacer-leaf";
	static final String NODE = "tt-nested-spacer-node";
	static final String CONTINUE = "tt-nested-spacer-continue";
	
	List<String> decisionEngine(boolean lastIsEnd, int [] maxSegmentValues, int [] mySegmentValues){
		List<String> styles = new ArrayList<String>();
		
		for(int i=0; i < mySegmentValues.length ; i++ ){
			boolean endOfBranch = maxSegmentValues[i] <= mySegmentValues[i];
			if(i == mySegmentValues.length-1){
				//End of segment
				//if(endOfBranch){
				if(lastIsEnd || endOfBranch){
					styles.add(LEAF);
				}
				else{
					styles.add(NODE);
				}
			}
			else{
				if(endOfBranch){
					styles.add(BLANK);
				}
				else{
					styles.add(CONTINUE);
				}
			}
		}
		
		return styles;
	}
	
	public String getBackgroundRepeaterForStyle(String style){
		if(LEAF.equals(style)){
			return BLANK;
		}
		else if(BLANK.equals(style)){
			return BLANK;
		}
		else if(NODE.equals(style)){
			return CONTINUE;
		}
		else if(CONTINUE.equals(style)){
			return CONTINUE;
		}
		else{
			throw new RuntimeException("Epic failure during tree branc construction. No clue what you're asking for.");
		}
		
	}
}
