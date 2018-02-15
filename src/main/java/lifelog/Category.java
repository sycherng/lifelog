package lifelog;
import org.json.simple.JSONObject;
import static java.lang.Math.toIntExact;

public class Category {
        public String id;
        public int ordinal;
        public String name;
        public String ordinal_signature;

        public Category(String id, int ordinal, String name, String ordinal_signature) {
                this.id = id;
                this.ordinal = ordinal;
                this.name = name;
                this.ordinal_signature = ordinal_signature;
        }

        public static Category makeFromMap(String ordinal_signature, JSONObject map) {
                String id = (String)map.get("id");
        		long extracted_ordinal = (Long)map.get("ordinal");
                int ordinal = toIntExact(extracted_ordinal);
                String name =  (String)map.get("name");
                return new Category(id, ordinal, name, ordinal_signature);
        }
        
        @SuppressWarnings("unchecked") //JSONObject is not generic, cannot be parameterized
		public JSONObject toJSONObject() {
				JSONObject category_details = new JSONObject();
				category_details.put("id", this.id);
				category_details.put("ordinal", this.ordinal);
				category_details.put("name", this.name);
				return category_details;
        }
}