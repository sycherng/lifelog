package lifelog;
import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.management.BadStringOperationException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.time.LocalDate;

public class Main {
		public static TreeMap<String, Category> categories;
		public static TreeMap<String, Topic> topics;
		public static TreeMap<String, AbstractQuestion> questions;
		public static HashMap<LocalDate, HashMap<String, Answer>> answers;
	
		public static void main (String[] args) throws IOException, ParseException, BadStringOperationException {
				loadState();
				Console c = System.console();
				showCategories(c);
				/*if (c == null) {
						System.err.println("No console.");
						System.exit(1);
				} else {
						while (true) {
								System.out.println("Welcome back to your lifelog.");
								String command = c.readLine();
								if (command.equals("quit")) {
										saveState();
										System.exit(1);
								}
								else if (command.equals("show")) {
										showMenu(c);
								}
								else if (command.equals("show categories") || command.equals("show c")) {
										showCategories(c);
								}
								else if (command.equals("show topics") || command.equals("show t")) {
										showTopics(c);
								}
								else if (command.equals("show questions") || command.equals("show q")) {
										showQuestions(c);
								}
								else if (command.equals("help")) {
										helpText(c);
								}
								//if (command.equals()) {}
						}
				}*/
				saveState();
		}

		public static void loadState() throws IOException, ParseException, BadStringOperationException {
				loadQuestions();
				loadAnswers();
		}
		
		public static void loadQuestions() throws IOException, ParseException, BadStringOperationException {
				String json_string = QuestionsDecoder.getJSONString();
		        JSONObject json_object = QuestionsDecoder.stringToJSONObject(json_string);
		        categories = QuestionsDecoder.decodeCategory(json_object);
		        topics = QuestionsDecoder.decodeTopic(json_object);
		        questions = QuestionsDecoder.decodeQuestions(json_object);
		}
	
		public static void loadAnswers() throws IOException, ParseException, BadStringOperationException {
				String json_string = AnswersDecoder.getJSONString();
				JSONObject json_object = AnswersDecoder.stringToJSONObject(json_string);
				answers = AnswersDecoder.decodeAllAnswers(json_object);
		}
		
		public static void saveState() throws IOException {
				QuestionsEncoder.encodeAll(); //encodes all categories, topics, questions
				AnswersEncoder.encodeAnswers(); //encodes all answers
		}
		
		public static void showMenu(Console c) {
				String response = c.readLine("Choose one of the following:\n(c) categories\n(t) topics\n(q) questions");
				if (response.equals("c")) {
						showCategories(c);
				}
				if (response.equals("t")) {
						showTopics(c);
				}
				if (response.equals("q")) {
						showQuestions(c);
				}				
		}
		private static String formatShow(String parent_id, String parent_name, ArrayList<String> member_names) {
				StringBuilder result = new StringBuilder();
				result.append(String.format(" %1$s | %2$s\n", parent_id, parent_name));
				for (String name: member_names) {
						result.append(new String(new char[8]).replace("\0", " ")); //add 8 spaces
						result.append("> ");
						result.append(name);
						result.append("\n");
				}
				return result.toString();
		}
		
		private static HashMap<String, ArrayList<String>> getCategoryMemberNames() {
				//iterate over all topics to grab their category_ids
				//place in a hashmap of category_id : array of names of member topics
				HashMap<String, ArrayList<String>> category_to_topic_names = new HashMap<>();
				for (Map.Entry<String, Topic> t_entry: Main.topics.entrySet()) { //for all topics
						Topic topic = t_entry.getValue(); //topic
						String category_id = topic.category_id; //topic's category id
						String topic_name = topic.name; //topic's name
						if (category_to_topic_names.containsKey(category_id)) { //if already in the map
								ArrayList<String> child_topic_names = category_to_topic_names.get(category_id); //grab the array
								child_topic_names.add(topic_name); //add name to the array
						} else { //if not already in the map
								ArrayList<String> child_topic_names = new ArrayList<>();  //make a new arr
								child_topic_names.add(topic_name); //add the topic name to it
								category_to_topic_names.put(category_id, child_topic_names); //add as new entry
						}
				}
				return category_to_topic_names;
		}
				
		private static void showCategories(Console c) {
			HashMap<String, ArrayList<String>> category_to_topic_names = getCategoryMemberNames();
				for (Map.Entry<String, Category> c_entry: Main.categories.entrySet()) {
						//for each category, list the prompt, ordinal, and members
						//ensure it is sorted by ordinal on testing
						String ordinal_signature = c_entry.getKey();
						Category current_category = c_entry.getValue();
						ArrayList<String> child_topic_names = category_to_topic_names.get(current_category.id);
						String result = formatShow(current_category.id, current_category.name, child_topic_names);
						System.out.println(result);
				}
		}

		private static void showTopics(Console c) {
			// TODO Auto-generated method stub
			
		}

		private static void showQuestions(Console c) {
			// TODO Auto-generated method stub
			
		}

		public static void helpText(Console c) {
				System.out.println("quit | show | ");
		}

///* Inserting a new answer
// * 1. insertion (Date, question_id, String[]) -> AbstractQuestion
// * 2. autosave: take the AbstractQuestion we just made, serialize it, and add it to the answers json
// */
//	/** Uses question_id as a key to all_questions to get the AbstractQuestion,
//	 * unrolls args and processes them,
//	 * calls AbstractQuestion.createAnswerInstance() to make a new answered question,
//	 * adds the fully answered question to the all_answers hashmap
//	 */
//	public void createAnswer(String question_id, Date date, String[] args) {
//		//unimplemented
//		//answer = new question_template.createAnswerInstance()
//	}
//
///* Adding a new question
// * 1. createNewQuestion literally all strings (question_id, class_type, other_params...) -> AbstractQuestion
// * 2. serialize the AbstractQuestion and add to the questions json
// */
//	/** Checks for the largest question_id in all_questions,
//	 * string-builds and returns the next logical question_id.
//	 */
//	public String makeNextQuestionId() {
//		//unimplemented
//		//this.all_questions
//	}
//
//	/** call constructor for a leaf Question object based on question_type,
//	 * use getNextQuestionId() to create the new Question object's id,
//	 * unroll args and processes them to fulfill required attributes for constructor,
//	 * adds the newly created Question object to all_questions hashmap
//	 */
//	public void createQuestion() {
//		//unimplemented
//		//this.all_questions
}
