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
}

