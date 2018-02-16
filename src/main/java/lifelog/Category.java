package lifelog;
import org.json.simple.JSONObject;
import static java.lang.Math.toIntExact;

public class Category {
        public String id;
        public int ordinal;
        public String name;

        public Category(String id, int ordinal, String name) {
                this.id = id;
                this.ordinal = ordinal;
                this.name = name;
        }

        public static Category makeFromMap(String id, JSONObject map) {
                long extracted_ordinal = (Long)map.get("ordinal");
                int ordinal = toIntExact(extracted_ordinal);
                String name =  (String)map.get("name");
                return new Category(id, ordinal, name);
        }
        
        @SuppressWarnings("unchecked") //JSONObject is not generic, cannot be parameterized
		public JSONObject toJSONObject() {
				JSONObject category_details = new JSONObject();
				category_details.put("ordinal", this.ordinal);
				category_details.put("name", this.name);
				return category_details;
        }
}