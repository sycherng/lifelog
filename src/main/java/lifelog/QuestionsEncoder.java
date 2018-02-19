package lifelog;

import java.util.Map;

import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

public class QuestionsEncoder {
			/* "categories": cid: attribute: value
			 * "topics": tid: attribute: value
			 * "questions": qid: question-specific encoding
			 */
		@SuppressWarnings("unchecked") //JSONObject is not generic
		public static void encodeAll() throws IOException {
				JSONObject categories_jsonobject = categoriesToJSONString();
				JSONObject topics_jsonobject = topicsToJSONString();
				JSONObject questions_jsonobject = questionsToJSONString();
				//put all into one object
				JSONObject combined_jsonobject = new JSONObject();
				combined_jsonobject.put("categories", categories_jsonobject);
				combined_jsonobject.put("topics", topics_jsonobject);
				combined_jsonobject.put("questions", questions_jsonobject);
				//convert to string
				String json_string = combined_jsonobject.toJSONString();
				//write that string to a file
				writeToFile(json_string);
				
		}
		private static void writeToFile(String json_string) throws IOException {
				FileWriter writer = new FileWriter("C:\\Users\\latte\\git\\maven-lifelog\\lifelog\\src\\test\\resources\\categories_topics_questions.json");
				try {
						writer.write(json_string);
				} finally {
						writer.close();
				}
		}

		@SuppressWarnings("unchecked") //JSONObject is not generic
		private static JSONObject categoriesToJSONString() {
				JSONObject categories_map = new JSONObject();
				for (Map.Entry<String, Category> e: Main.categories.entrySet()) {
			        	String category_id = (String) e.getKey();
						Category category_object = (Category) e.getValue();
						categories_map.put(category_id, category_object.toJSONObject());
				}
				return categories_map;
		}
		
		@SuppressWarnings("unchecked") //JSONObject is not generic
		private static JSONObject topicsToJSONString() {
				JSONObject topics_map = new JSONObject();
				for (Map.Entry<String, Topic> e: Main.topics.entrySet()) {
						String topic_id = (String) e.getKey();
						Topic topic_object = (Topic) e.getValue();
						topics_map.put(topic_id, topic_object.toJSONObject());
				}
				return topics_map;
		}

		@SuppressWarnings("unchecked") //JSONObject is not generic
		private static JSONObject questionsToJSONString() {
				JSONObject questions_map = new JSONObject();
				for (Map.Entry<String, AbstractQuestion> e: Main.questions.entrySet()) {
						String question_id = (String) e.getKey();
						AbstractQuestion question_object = (AbstractQuestion) e.getValue();
						if (question_object.type.equals("Free")) {
								questions_map.put(question_id, ((FreeQuestion)question_object).templateToJSONObject());
						} else if (question_object.type.equals("Scale")) {
								questions_map.put(question_id, ((ScaleQuestion)question_object).templateToJSONObject());
						} else if (question_object.type.equals("Choice")) {
								questions_map.put(question_id, ((ChoiceQuestion)question_object).templateToJSONObject());
						}
				}
				return questions_map;
		}
}