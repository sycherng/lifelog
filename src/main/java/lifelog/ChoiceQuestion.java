package lifelog;
import java.util.*;
import java.lang.String;
import java.time.LocalDate;
import org.json.simple.JSONObject;
import static java.lang.Math.toIntExact;

public class ChoiceQuestion extends CriteriaQuestion{
        public ArrayList<Option> options;

        public ChoiceQuestion(String id, int ordinal, String prompt, String topic_id, int critical_low, int critical_high, int critical_variance, int critical_duration, HashMap<String, Option> options_map, String ordinal_signature) {
                super(id, ordinal, prompt, topic_id,  "Choice", critical_low, critical_high, critical_variance, critical_duration, ordinal_signature);
                this.options = makeOptions(options_map);
        }

        /** Given a JSON map of a ChoiceQuestion, deserialize it
         * and return it as a ChoiceQuestion object
         */
        @SuppressWarnings("unchecked") //Unchecked cast from map.get("options") to HashMap<String, Option> for JSON parsing of Option objects HashMap.
        public static ChoiceQuestion makeFromMap(String ordinal_signature, JSONObject map) {
        		String id = (String)map.get("id");
        		int ordinal = toIntExact((Long)map.get("ordinal"));
                String prompt = (String)map.get("prompt");
                String topic_id = (String)map.get("topic_id");
                int critical_low = toIntExact((Long)map.get("critical_low"));
                int critical_high = toIntExact((Long)map.get("critical_high"));
                int critical_variance = toIntExact((Long)map.get("critical_variance"));
                int critical_duration = toIntExact((Long)map.get("critical_duration"));
                HashMap<String, Option> options_map = (HashMap<String, Option>)map.get("options");
                return new ChoiceQuestion(id, ordinal, prompt, topic_id, critical_low, critical_high, critical_variance, critical_duration, options_map, ordinal_signature);
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
                        //Tools.printTypeAndContent(new_option.full, "new option");
                }
                //Tools.printTypeAndContent(results, "results here");
                return results;
        }

        /** Called when user submits a fully answered response to a template ChoiceQuestion
         * instantiates a copy of an ChoiceQuestion with date and answer fields.
         */
        public AbstractQuestion createAnswerInstance(LocalDate date, String[] args) {
                //Unimplemented
                return null;
        }

        /** Returns the JSON string that this instance should serialize to.
         * For answered instances only.
         */
        @SuppressWarnings("unchecked")
		public JSONObject templateToJSONObject() {
                JSONObject question_details = new JSONObject();
                question_details.put("id", this.id);
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

        public class Option {
                public char abbreviation;
                public String full;
                public int weight;

                public Option(char abbreviation, String full, int weight) {
                        this.abbreviation = abbreviation;
                        this.full = full;
                        this.weight = weight;
                }
        }
}

