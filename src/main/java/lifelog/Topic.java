package lifelog;
import org.json.simple.JSONObject;
import static java.lang.Math.toIntExact;

public class Topic {
        public String id;
        public int ordinal;
        public String name;
        public String category_id;

        public Topic(String id, int ordinal, String name, String category_id) {
                this.id = id;
                this.ordinal = ordinal;
                this.name = name;
                this.category_id = category_id;
        }

        public static Topic makeFromMap(String id, JSONObject topic_map) {
                long extracted_ordinal = (Long)topic_map.get("ordinal");
                int ordinal = toIntExact(extracted_ordinal);
                String name = (String) topic_map.get("name");
                String category_id = (String) topic_map.get("category_id");
                return new Topic(id, ordinal, name, category_id);
        }
        
        @SuppressWarnings("unchecked") //JSONObject is not generic, cannot be parameterized
		public JSONObject toJSONObject() {
		        JSONObject topic_details = new JSONObject();
		        topic_details.put("ordinal", this.ordinal);
		        topic_details.put("name", this.name);
		        topic_details.put("category_id", this.category_id);
		        return topic_details;
        }
}

