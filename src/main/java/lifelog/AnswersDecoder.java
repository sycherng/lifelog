package lifelog;
import org.json.simple.parser.*;
import org.json.simple.JSONObject;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.BadStringOperationException;

public class AnswersDecoder {
		public static JSONParser parser = new JSONParser();

		public static String getJSONString() throws IOException {
				FileReader json_file = null;
				String json_string = "";
				
				try {
					json_file = new FileReader("C:\\Users\\latte\\git\\maven-lifelog\\lifelog\\src\\test\\resources\\answers.json");

					int c;
					while ((c = json_file.read()) != -1) {
						json_string += (char)c;						
					} 
				} finally {
						if (json_file != null) {
							json_file.close();
						}
				} if (json_string != "") {
					return json_string;
				} else {
					throw new IOException("answers json is empty.");
				}
		}
		public static JSONObject stringToJSONObject(String json_string) throws ParseException, BadStringOperationException {
				if (json_string.equals("")) {
					throw new BadStringOperationException("answers string is empty");
				} else {
					return (JSONObject)AnswersDecoder.parser.parse(json_string); 
				}
		}
        /** Given a JSONObject representing all answers,
         * return a HashMap of (date: (id: answer))
         */
        @SuppressWarnings("unchecked") //Unchecked cast for json_object.get("answers")
        public static HashMap<LocalDate, HashMap<String, Answer>> decodeAllAnswers(JSONObject json_object) {
        		HashMap<LocalDate, HashMap<String, Answer>> results = new HashMap<LocalDate, HashMap<String, Answer>>();
                HashMap<String, HashMap<String, ArrayList<String>>> date_map = (HashMap<String, HashMap<String, ArrayList<String>>>)json_object.get("answers");
                for (Map.Entry<String, HashMap<String, ArrayList<String>>> e: (Collection<Map.Entry<String, HashMap<String, ArrayList<String>>>>)date_map.entrySet()) {
                		LocalDate date = LocalDate.parse(e.getKey());
                        HashMap<String, ArrayList<String>> question_to_answer_map = e.getValue();
                        HashMap<String, Answer> answers_for_date = decodeAnswer(date, question_to_answer_map);
                        results.put(date, answers_for_date);
                }
                return results;
        }

        public static HashMap<String, Answer> decodeAnswer(LocalDate date, HashMap<String, ArrayList<String>> questionid_to_answer) {
                HashMap<String, Answer> result = new HashMap<String, Answer>();
                for (Entry<String, ArrayList<String>> e: (Collection<Map.Entry<String, ArrayList<String>>>)questionid_to_answer.entrySet()) {
                		String question_id = (String)e.getKey();
                        ArrayList<String> answer = (ArrayList<String>)e.getValue();
                        Answer new_answer = new Answer(answer);
                        result.put(question_id, new_answer);
                }
                return result;
        }

		

}
