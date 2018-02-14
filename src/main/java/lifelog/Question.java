package lifelog;

import org.json.simple.JSONObject;

public interface Question {
	    /** Returns the JSON string that this instance should serialize to.
	     * For answered instances only.
	     */    
		public JSONObject templateToJSONObject();
}
