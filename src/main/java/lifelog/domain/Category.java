package lifelog.domain;
import org.json.simple.JSONObject;
import static java.lang.Math.toIntExact;

public class Category extends SurveyItem {
        public String id;
        public int ordinal;
        public String prompt;

        public Category(String id, int ordinal, String prompt) {
                this.id = id;
                this.ordinal = ordinal;
                this.prompt = prompt;
        }

        public static Category makeFromMap(String id, JSONObject map) {
                long extracted_ordinal = (Long)map.get("ordinal");
                int ordinal = toIntExact(extracted_ordinal);
                String prompt =  (String)map.get("prompt");
                return new Category(id, ordinal, prompt);
        }
        
        @SuppressWarnings("unchecked") //JSONObject is not generic, cannot be parameterized
		public JSONObject toJSONObject() {
				JSONObject category_details = new JSONObject();
				category_details.put("ordinal", this.ordinal);
				category_details.put("prompt", this.prompt);
				return category_details;
        }
        
        public int getOrdinal() {
        	return this.ordinal;
        }
}