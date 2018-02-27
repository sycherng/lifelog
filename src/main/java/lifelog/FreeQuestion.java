package lifelog;
import org.json.simple.JSONObject;

import java.time.LocalDate;
import static java.lang.Math.toIntExact;

public class FreeQuestion extends AbstractQuestion {
        public int num_of_answers;

        public FreeQuestion(String id, int ordinal, String prompt, String topic_id, int num_of_answers) {
                super(id, ordinal, prompt, topic_id, "Free");
                this.num_of_answers = num_of_answers;
        }

        public static FreeQuestion makeFromMap(String id, JSONObject map) {
                int ordinal = toIntExact((Long)map.get("ordinal"));
                String prompt = (String)map.get("prompt");
                String topic_id = (String)map.get("topic_id");
                int num_of_answers = toIntExact((Long)map.get("num_of_answers"));
                return new FreeQuestion(id, ordinal, prompt, topic_id, num_of_answers);
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
                question_details.put("num_of_answers", this.num_of_answers);
                return question_details;           
        }
}

