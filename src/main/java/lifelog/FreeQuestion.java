package lifelog;
import org.json.simple.JSONObject;

import java.time.LocalDate;
import static java.lang.Math.toIntExact;

public class FreeQuestion extends AbstractQuestion {
        public int num_of_answers;

        public FreeQuestion(String id, int ordinal, String prompt, String topic_id, int num_of_answers, String ordinal_signature) {
                super(id, ordinal, prompt, topic_id, "Free", ordinal_signature);
                this.num_of_answers = num_of_answers;
        }

        public static FreeQuestion makeFromMap(String ordinal_signature, JSONObject map) {
                String id = (String)map.get("id");
        		int ordinal = toIntExact((Long)map.get("ordinal"));
                String prompt = (String)map.get("prompt");
                String topic_id = (String)map.get("topic_id");
                int num_of_answers = toIntExact((Long)map.get("num_of_answers"));
                return new FreeQuestion(id, ordinal, prompt, topic_id, num_of_answers, ordinal_signature);
        }

        /** Called when user submits a fully answered response to a template FreeQuestion
         * instantiates a copy of an FreeQuestion with date and answer fields.
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
                question_details.put("num_of_answers", this.num_of_answers);
                return question_details;           
        }
}

