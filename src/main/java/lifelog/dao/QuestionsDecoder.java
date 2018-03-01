package lifelog.dao;
import lifelog.domain.*;
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
                        json_file = new FileReader("/home/ec2-user/ll/src/test/resources/categories_topics_questions.json");

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
        /** Given a JSON object representing all categories, topics, and questions,
         * return a HashMap of id : Category objects
         */
        @SuppressWarnings("unchecked") //Unchecked conversion from cmap.entrySet() to Map.Entry
		public static <T> HashMap<String, Category> decodeCategory(JSONObject json_object) {
                HashMap<String, Category> results = new HashMap<>();
                JSONObject cmap = (JSONObject)json_object.get("categories");
                for (Map.Entry<String, T> e: (Collection<Map.Entry<String, T>>)cmap.entrySet()) {
                        String key = e.getKey();
                        Category new_category = Category.makeFromMap(e.getKey(), (JSONObject)e.getValue());
                        results.put(key, new_category);
                }
                return results;
        }
        
        /** Given a JSON representing all categories, topics, and questions,
         * return a HashMap of id : Topic objects
         */
		@SuppressWarnings("unchecked") //Unchecked conversion from tmap.entrySet() to Map.Entry
		public static <T> HashMap<String, Topic> decodeTopic(JSONObject json_object) {
	                HashMap<String, Topic> results = new HashMap<>();
	                JSONObject tmap = (JSONObject)json_object.get("topics");
	                for (Map.Entry<String, T> e: (Collection<Map.Entry<String, T>>)tmap.entrySet()) {
	                        results.put(e.getKey(), Topic.makeFromMap(e.getKey(), (JSONObject)e.getValue()));
	                }
	                return results;
	        }
	    /** Given the JSONObject representing categories_topics_questions.json,
	     * deserialize, initialize and return as a map of unique ids to AbstractQuestions
	     */
	    @SuppressWarnings("unchecked") //Unchecked conversion from qmap.entrySet() to Map.Entry
		public static <T> HashMap<String, AbstractQuestion> decodeQuestions(JSONObject json_object) {
		HashMap<String, AbstractQuestion> results = new HashMap<>();
	            JSONObject qmap = (JSONObject)json_object.get("questions");
	            for (Map.Entry<String, T> e: (Collection<Map.Entry<String, T>>)qmap.entrySet()) {
	                    String question_id = (String)e.getKey();
	                    JSONObject question_attributes = (JSONObject)e.getValue();
	                    String type = (String)question_attributes.get("type");
			AbstractQuestion question = null;
	                    if (type.equals("Free")) {
	                            question = FreeQuestion.makeFromMap(question_id, question_attributes);
	                    } else if (type.equals("Scale")) {
	                            question = ScaleQuestion.makeFromMap(question_id, question_attributes);
	                    } else if (type.equals("Choice")) {
	                            question = ChoiceQuestion.makeFromMap(question_id, question_attributes);
	                    }
	                    results.put(question_id, question);
	            }
	            return results;
	    }
}