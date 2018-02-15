package lifelog;
import java.io.IOException;
import java.io.FileReader;
import javax.management.BadStringOperationException;
import java.util.*;
import org.json.simple.parser.*;
import org.json.simple.JSONObject;

public class QuestionsDecoder {
        public static JSONParser parser = new JSONParser();

        /** Given a JSON file, returns it as a String
         */
        public static String getJSONString() throws IOException {
                FileReader json_file = null;
                String json_string = "";

                try {
                        json_file = new FileReader("C:\\Users\\latte\\git\\maven-lifelog\\lifelog\\src\\test\\resources\\full_categories_topics_questions_mock.json");

                        int c;
                        while ((c = json_file.read()) != -1) {
    						json_string += (char)c;
                        }
                } finally {
                        if (json_file != null) {
                                json_file.close();
                        }
                }
                if (json_string != ""){
                        return json_string;
                }
				else {
					throw new IOException("questions json is empty.");
				}
        }

        /** Given a String, returns a JSONObject if successfully parsed
         */
        public static JSONObject stringToJSONObject(String json_string) throws ParseException, BadStringOperationException {
                if (json_string.equals("")) {
                		throw new BadStringOperationException("questions string is empty.");	
                } else {
                		return (JSONObject)QuestionsDecoder.parser.parse(json_string);
                }
        }
	    /** Given the JSONObject representing categories_topics_questions.json,
	     * deserialize, initialize and return as a map of ordinal signatures to Category objects
	     */
        @SuppressWarnings("unchecked") //Unchecked conversion from cmap.entrySet() to Map.Entry
		public static <T> TreeMap<String, Category> decodeCategory(JSONObject json_object) {
        		TreeMap<String, Category> results = new TreeMap<>(); //TreeMap<ordinal_signature, Category>
        		JSONObject cmap = (JSONObject)json_object.get("categories");
                for (Map.Entry<String, T> e: (Collection<Map.Entry<String, T>>)cmap.entrySet()) {
                        String ordinal_signature = e.getKey();
                        Category new_category = Category.makeFromMap(ordinal_signature, (JSONObject)e.getValue());
                        results.put(ordinal_signature, new_category);
                }
                return results;
        }
	    /** Given the JSONObject representing categories_topics_questions.json,
	     * deserialize, initialize and return as a map of ordinal signatures to Topic objects
	     */
		@SuppressWarnings("unchecked") //Unchecked conversion from tmap.entrySet() to Map.Entry
		public static <T> TreeMap<String, Topic> decodeTopic(JSONObject json_object) {
	                TreeMap<String, Topic> results = new TreeMap<>(); //TreeMap<ordinal_signature, Topic>
	                JSONObject tmap = (JSONObject)json_object.get("topics");
	                for (Map.Entry<String, T> e: (Collection<Map.Entry<String, T>>)tmap.entrySet()) {
	                		String ordinal_signature = e.getKey();
	                		Topic new_topic = Topic.makeFromMap(ordinal_signature, (JSONObject)e.getValue());
	                        results.put(ordinal_signature, new_topic);
	                }
	                return results;
	        }
	    /** Given the JSONObject representing categories_topics_questions.json,
	     * deserialize, initialize and return as a map of ordinal signatures to AbstractQuestions
	     */
	    @SuppressWarnings("unchecked") //Unchecked conversion from qmap.entrySet() to Map.Entry
		public static <T> TreeMap<String, AbstractQuestion> decodeQuestions(JSONObject json_object) {
		TreeMap<String, AbstractQuestion> results = new TreeMap<>(); //TreeMap<ordinal_signature, AbstractQuestion>
	            JSONObject qmap = (JSONObject)json_object.get("questions");
	            for (Map.Entry<String, T> e: (Collection<Map.Entry<String, T>>)qmap.entrySet()) {
	                    String ordinal_signature = (String)e.getKey();
	                    JSONObject question_attributes = (JSONObject)e.getValue();
	                    String type = (String)question_attributes.get("type");
	                    AbstractQuestion new_question = null;
	                    if (type.equals("Free")) {
	                            new_question = FreeQuestion.makeFromMap(ordinal_signature, question_attributes);
	                    } else if (type.equals("Scale")) {
	                    		new_question = ScaleQuestion.makeFromMap(ordinal_signature, question_attributes);
	                    } else if (type.equals("Choice")) {
	                    		new_question = ChoiceQuestion.makeFromMap(ordinal_signature, question_attributes);
	                    }
	                    results.put(ordinal_signature, new_question);
	            }
	            return results;
	    }
}

