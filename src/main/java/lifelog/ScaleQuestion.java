package lifelog;
import org.json.simple.JSONObject;
import java.util.*;
import java.time.LocalDate;
import static java.lang.Math.toIntExact;

public class ScaleQuestion extends CriteriaQuestion{
        public Range range;
        public String legend;

        public ScaleQuestion(String id, int ordinal, String prompt, String topic_id, int critical_low, int critical_high, int critical_variance, int critical_duration, int range_start, int range_stop, int range_step, String legend) {
                super(id, ordinal, prompt, topic_id, "Scale", critical_low, critical_high, critical_variance, critical_duration);
                this.range = new Range(range_start, range_stop, range_step);
                this.legend = legend;
        }

    @SuppressWarnings("unchecked") //Unchecked cast from map.get("range") to HashMap<String, Integer> for JSON parsing of Range objects.
	public static ScaleQuestion makeFromMap(String id, JSONObject map) {
		int ordinal = toIntExact((Long)map.get("ordinal"));
		String prompt = (String) map.get("prompt");
		Tools.print(prompt);
		String topic_id = (String) map.get("topic_id");
		int critical_variance = toIntExact((Long)map.get("critical_variance"));
		int critical_low = toIntExact((Long)map.get("critical_low"));
		int critical_high = toIntExact((Long)map.get("critical_high"));
		int critical_duration = toIntExact((Long)map.get("critical_duration"));
		HashMap<String, Long> range = (HashMap<String, Long>) map.get("range");
		Tools.printTypeAndContent(range.get("range_start"), "range_start");
		int range_start = toIntExact((Long)range.get("range_start"));
		int range_stop = toIntExact((Long)range.get("range_stop"));
		int range_step = toIntExact((Long)range.get("range_step"));
		String legend = (String) map.get("legend");
		return new ScaleQuestion(id, ordinal, prompt, topic_id, critical_low, critical_high, critical_variance, critical_duration, range_start, range_stop, range_step, legend);
        }

        /** Called when user submits a fully answered response to a template ScaleQuestion
         * instantiates a copy of an ScaleQuestion with date and answer fields.
         */
        public AbstractQuestion createAnswerInstance(LocalDate date, String[] args) {
			return null;
                //Unimplemented
        }

        /** Returns the JSON string that this instance should serialize to.
         * For answered instances only.
         */
        public String answeredToJsonString() {
			return null;
                //Unimplemented
        }
        public String templateToJsonString() {
			return null;
            //Unimplemented
    }
        @SuppressWarnings("unused")
        private class Range{
        		public int start;
        		public int stop;
				public int step;

	    public Range(int start, int stop, int step) {
	            this.start = start;
	            this.stop = stop;
	            this.step = step;
	    }

		private boolean contains(int number) {
	            return (number >= start && number < stop);
	    }
    }
}
