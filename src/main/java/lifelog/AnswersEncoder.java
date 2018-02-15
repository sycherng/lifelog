package lifelog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;

/**given a HashMap<LocalDate, HashMap<String, Answer>> answers
 * object -> jsonobject ---parser---> string
 * save into a file answers.json 
 */
public class AnswersEncoder {
		//inner = hashmap of questionid to answer contents
		//middle = hashmap of date to innermap
		//outer = hashmap of "answers" to middlemap

		@SuppressWarnings("unchecked")
		public static void encodeAnswers() throws IOException {
				JSONObject outermap = new JSONObject();
				JSONObject middlemap = new JSONObject();
				JSONObject innermap = new JSONObject();
				LocalDate date;
				String question_id;
				ArrayList<String> answer_content;
				HashMap<LocalDate, HashMap<String, Answer>> answers_map = Main.answers;
				for (Map.Entry<LocalDate, HashMap<String, Answer>> main_answers_entry: (Collection<Map.Entry<LocalDate, HashMap<String, Answer>>>)answers_map.entrySet()) {
						date = main_answers_entry.getKey();
						Collection<Map.Entry<String, Answer>> it = main_answers_entry.getValue().entrySet();
						for (Map.Entry<String, Answer> date_entry: it) {
								question_id = date_entry.getKey();
								Answer answer_object = date_entry.getValue();
								answer_content = answer_object.answer;
								innermap.put(question_id, answer_content);
						}
						middlemap.put(date, innermap);
						innermap = new JSONObject();						
				}	
				outermap.put("answers", middlemap);
				String json_string = outermap.toJSONString();
				saveJSONStringToFile(json_string);
		}
		
		public static void saveJSONStringToFile(String json_string) throws IOException {
				FileWriter writer = new FileWriter("C:\\Users\\latte\\git\\maven-lifelog\\lifelog\\src\\test\\resources\\answers.json");
				try {
						writer.write(json_string);
				} finally {
						writer.close();
				}
		}
}
