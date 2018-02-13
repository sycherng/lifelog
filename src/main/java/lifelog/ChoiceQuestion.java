package lifelog;
import java.util.*;
import java.lang.String;
import java.time.LocalDate;
import org.json.simple.JSONObject;
import static java.lang.Math.toIntExact;

public class ChoiceQuestion extends CriteriaQuestion{
        public List<Option> options;
        public HashMap<String, Option> options_map = null;

        public ChoiceQuestion(String id, int ordinal, String prompt, String topic_id, int critical_low, int critical_high, int critical_variance, int critical_duration, HashMap<String, Option> options_map) {
                super(id, ordinal, prompt, topic_id,  "Choice", critical_low, critical_high, critical_variance, critical_duration);
                this.options = makeOptions(options_map);
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
        public <T> List<Option> makeOptions(HashMap<String, T> options_map) {
                List<Option> results = new ArrayList<Option>();
                for (Map.Entry<String, T> e: options_map.entrySet()) {
                        HashMap<String, T> option_attributes = (HashMap<String, T>)e.getValue();
                        String abbrev = (String)option_attributes.get("abbreviation");
                        char abbreviation = abbrev.charAt(0);
                        String full = (String)option_attributes.get("full");
                        int weight = toIntExact((Long)option_attributes.get("weight"));
                        Option new_option = new Option(abbreviation, full, weight);
                        results.add(new_option);
                }
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
        public String answeredToJsonString() {
                //Unimplemented
                return null;
        }

        /** Returns the JSON string that this instance should serialize to.
         * For answered instances only.
         */
        public String templateToJsonString() {
                //Unimplemented
                return null;
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

