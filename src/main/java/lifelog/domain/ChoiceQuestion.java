package lifelog.domain;
import java.util.*;
import java.lang.String;
import org.json.simple.JSONObject;
import static java.lang.Math.toIntExact;

public class ChoiceQuestion extends CriteriaQuestion{
        public ArrayList<Option> options;

        public ChoiceQuestion(String id, int ordinal, String prompt, String topic_id, int critical_low, int critical_high, int critical_variance, int critical_duration, HashMap<String, Option> options_map) {
                super(id, ordinal, prompt, topic_id,  "Choice", critical_low, critical_high, critical_variance, critical_duration);
                this.options = makeOptions(options_map);
        }

		public ChoiceQuestion(String id, int ordinal, String prompt, String topic_id, int critical_low, int critical_high, int critical_variance, int critical_duration, ArrayList<Option> option_list) {
			super(id, ordinal, prompt, topic_id, "Choice", 	critical_low, critical_high, critical_variance, critical_duration);
			this.options = option_list;
		}

        /** Given a JSON map of a ChoiceQuestion, deserialize it
         * and return it as a ChoiceQuestion object
         */
        @SuppressWarnings("unchecked") //Unchecked cast from map.get("options") to HashMap<String, Option> for JSON parsing of Option objects HashMap.
        public static ChoiceQuestion makeFromMap(String id, JSONObject map) {
                int ordinal = toIntExact((Long)map.get("ordinal"));
                String prompt = (String)map.get("prompt");
                String topic_id = (String)map.get("topic_id");
                int critical_low = toIntExact((Long)map.get("critical_low"));
                int critical_high = toIntExact((Long)map.get("critical_high"));
                int critical_variance = toIntExact((Long)map.get("critical_variance"));
                int critical_duration = toIntExact((Long)map.get("critical_duration"));
                HashMap<String, Option> options_map = (HashMap<String, Option>)map.get("options");
                return new ChoiceQuestion(id, ordinal, prompt, topic_id, critical_low, critical_high, critical_variance, critical_duration, options_map);
       }

        /** Given a HashMap(int, HashMap(String, T)) with the following format:
         * 1: {"abbreviation": "e", "full": "entertainment", "weight": 1}
         * return List of Option
         */
        @SuppressWarnings("unchecked") //Unchecked cast from e.getValue() to HashMap<String, T> for JSON parsing of individual Option objects attributes.
        public <T> ArrayList<Option> makeOptions(HashMap<String, T> options_map) {
                ArrayList<Option> results = new ArrayList<Option>();
                for (Map.Entry<String, T> e: options_map.entrySet()) {
                        HashMap<String, T> option_attributes = (HashMap<String, T>)e.getValue();
                        String abbrev = (String)e.getKey();
                        char abbreviation = abbrev.charAt(0);
                        String full = (String)option_attributes.get("full");
                        int weight = toIntExact((Long)option_attributes.get("weight"));
                        Option new_option = new Option(abbreviation, full, weight);
                        results.add(new_option);
                }
                return results;
        }

        /** Returns the JSON string that this instance should serialize to.
         * For answered instances only.
         */
        @SuppressWarnings("unchecked")
		public JSONObject templateToJSONObject() {
                JSONObject question_details = new JSONObject();
                question_details.put("ordinal", this.ordinal);
                question_details.put("prompt", this.prompt);
                question_details.put("topic_id", this.topic_id);
                question_details.put("type", this.type);
                question_details.put("critical_low", this.critical_low);
                question_details.put("critical_high", this.critical_high);
                question_details.put("critical_variance", this.critical_variance);
                question_details.put("critical_duration", this.critical_duration);
                ArrayList<Option> option_object = this.options;
                JSONObject all_options_details = new JSONObject();
                for (Option o: option_object) {
                		JSONObject single_option_details = new JSONObject();
                		single_option_details.put("full", o.full);
                		single_option_details.put("weight", o.weight);
                		all_options_details.put(Character.toString(o.abbreviation), single_option_details);
                }
                question_details.put("options", all_options_details);
                return question_details;           
        }
}
