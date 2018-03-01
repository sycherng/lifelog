package lifelog.domain;
import org.json.simple.JSONObject;
import static java.lang.Math.toIntExact;

public class Topic extends SurveyItem {
        public String id;
        public int ordinal;
        public String prompt;
        public String category_id;

        public Topic(String id, int ordinal, String prompt, String category_id) {
                this.id = id;
                this.ordinal = ordinal;
                this.prompt = prompt;
                this.category_id = category_id;
        }

        public static Topic makeFromMap(String id, JSONObject topic_map) {
                long extracted_ordinal = (Long)topic_map.get("ordinal");
                int ordinal = toIntExact(extracted_ordinal);
                String prompt = (String) topic_map.get("prompt");
                String category_id = (String) topic_map.get("category_id");
                return new Topic(id, ordinal, prompt, category_id);
        }
        
        @SuppressWarnings("unchecked") //JSONObject is not generic, cannot be parameterized
		public JSONObject toJSONObject() {
		        JSONObject topic_details = new JSONObject();
		        topic_details.put("ordinal", this.ordinal);
		        topic_details.put("prompt", this.prompt);
		        topic_details.put("category_id", this.category_id);
		        return topic_details;
        }
}

