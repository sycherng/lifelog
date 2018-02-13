package lifelog;
import java.time.LocalDate;

public abstract class AbstractQuestion implements Question {
        public String id;
        public int ordinal;
        public String prompt;
        public String topic_id;
        public String type;

        public AbstractQuestion(String id, int ordinal, String prompt, String topic_id, String type) {
                this.id = id;
                this.ordinal = ordinal;
                this.prompt = prompt;
                this.topic_id = topic_id;
                this.type = type;
        }

        /** Called when user submits a fully answered response to a template question
         * instantiates a copy of an AbstractQuestion with date and answer fields.
         */
        public abstract AbstractQuestion createAnswerInstance(LocalDate date, String[] args);

        /** Returns the JSON string that this instance should serialize to.
         * For answered instances only.
         */
        public abstract String answeredToJsonString();

        /** Returns the JSON string that this instance should serialize to.
         * For answered instances only.
         */
        public abstract String templateToJsonString();
}       
