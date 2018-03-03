package lifelog.domain;
import org.json.simple.JSONObject;
import java.util.*;
import static java.lang.Math.toIntExact;

public class ScaleQuestion extends CriteriaQuestion{
        public Range range;
        public String legend;

        public ScaleQuestion(String id, int ordinal, String prompt, String topic_id, int critical_low, int critical_high, int critical_variance, int critical_duration, int range_start, int range_stop, int range_step, String legend) {
	        super(id, ordinal, prompt, topic_id, "Scale", critical_low, critical_high, critical_variance, critical_duration);
                this.range = new Range(range_start, range_stop, range_step);
                this.legend = legend;
        }
        
        public ScaleQuestion(String id, int ordinal, String prompt, String topic_id, int critical_low, int critical_high, int critical_variance, int critical_duration, Range range, String legend) {
        	super(id, ordinal, prompt, topic_id, "Scale", critical_low, critical_high, critical_variance, critical_duration);
		this.range = range;
		this.legend = legend;
	}

	@SuppressWarnings("unchecked") //Unchecked cast from map.get("range") to HashMap<String, Integer> for JSON parsing of Range objects.
	public static ScaleQuestion makeFromMap(String id, JSONObject map) {
		int ordinal = toIntExact((Long)map.get("ordinal"));
		String prompt = (String) map.get("prompt");
		String topic_id = (String) map.get("topic_id");
		int critical_variance = toIntExact((Long)map.get("critical_variance"));
		int critical_low = toIntExact((Long)map.get("critical_low"));
		int critical_high = toIntExact((Long)map.get("critical_high"));
		int critical_duration = toIntExact((Long)map.get("critical_duration"));
		HashMap<String, Long> range = (HashMap<String, Long>) map.get("range");
		int range_start = toIntExact((Long)range.get("range_start"));
		int range_stop = toIntExact((Long)range.get("range_stop"));
		int range_step = toIntExact((Long)range.get("range_step"));
		String legend = (String) map.get("legend");
		return new ScaleQuestion(id, ordinal, prompt, topic_id, critical_low, critical_high, critical_variance, critical_duration, range_start, range_stop, range_step, legend);
	}

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
                question_details.put("legend", this.legend);
                JSONObject range_details = new JSONObject();
                Range range_object = this.range;
                range_details.put("range_start", range_object.start);
                range_details.put("range_stop", range_object.stop);
                range_details.put("range_step", range_object.step);
                question_details.put("range", range_details);
                return question_details;
        }
}
