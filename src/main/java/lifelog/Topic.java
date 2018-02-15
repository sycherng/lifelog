package lifelog;
import org.json.simple.JSONObject;
import static java.lang.Math.toIntExact;

public class Topic {
        public String id;
        public int ordinal;
        public String name;
        public String category_id;
        public String ordinal_signature;

        public Topic(String id, int ordinal, String name, String category_id, String ordinal_signature) {
                this.id = id;
                this.ordinal = ordinal;
                this.name = name;
                this.category_id = category_id;
                this.ordinal_signature = ordinal_signature;
        }

        public static Topic makeFromMap(String ordinal_signature, JSONObject topic_map) {
            	String id = (String)topic_map.get("id");
            	long extracted_ordinal = (Long)topic_map.get("ordinal");
                int ordinal = toIntExact(extracted_ordinal);
                String name = (String) topic_map.get("name");
                String category_id = (String) topic_map.get("category_id");
                return new Topic(id, ordinal, name, category_id, ordinal_signature);
        }
        
        @SuppressWarnings("unchecked") //JSONObject is not generic, cannot be parameterized
		public JSONObject toJSONObject() {
		        JSONObject topic_details = new JSONObject();
		        topic_details.put("id", this.id);
		        topic_details.put("ordinal", this.ordinal);
		        topic_details.put("name", this.name);
		        topic_details.put("category_id", this.category_id);
		        return topic_details;
        }
}

