package lifelog;
import java.io.Console;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.management.BadStringOperationException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.time.LocalDate;

public class Main {
	public static HashMap<String, Category> categories;
	public static HashMap<String, Topic> topics;
	public static HashMap<String, AbstractQuestion> questions;
	public static HashMap<LocalDate, HashMap<String, Answer>> answers;
	public static LinkedList<String> category_hierarchy = new LinkedList<String>();
	public static HashMap<String, LinkedList<String>> topic_hierarchy= new HashMap<>();
	public static HashMap<String, LinkedList<String>> question_hierarchy = new HashMap<>();
	
	public static void main (String[] args) throws IOException, ParseException, BadStringOperationException {
		loadState();
		showQuestions();
		//Tools.printTypeAndContent(category_hierarchy, "cat-h");
		//Tools.printTypeAndContent(topic_hierarchy, "top-h");
		//Tools.printTypeAndContjent(question_hierarchy, "que-h");
		/*Console c = System.console();
		if (c == null) {
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
					System.out.println("Did you mean \"show all\"?");
				}
				else if (command.equals("show all")) {
					showAll();
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
		System.out.println("Program closed.");
	}
	
	private static void loadState() throws IOException, ParseException, BadStringOperationException {
		loadQuestions();
		loadAnswers();
		buildMemberStructure();
	}
		
	private static void loadQuestions() throws IOException, ParseException, BadStringOperationException {
		String json_string = QuestionsDecoder.getJSONString();
        JSONObject json_object = QuestionsDecoder.stringToJSONObject(json_string);
        categories = QuestionsDecoder.decodeCategory(json_object);
        topics = QuestionsDecoder.decodeTopic(json_object);
        questions = QuestionsDecoder.decodeQuestions(json_object);
	}
	
	private static void loadAnswers() throws IOException, ParseException, BadStringOperationException {
		String json_string = AnswersDecoder.getJSONString();
		JSONObject json_object = AnswersDecoder.stringToJSONObject(json_string);
		answers = AnswersDecoder.decodeAllAnswers(json_object);
	}

	private static void buildMemberStructure() {
		buildCategoryHierarchy();
		buildTopicHierarchy();
		buildQuestionHierarchy();
	}
	private static void buildCategoryHierarchy() {
		LinkedList<Category> temp_category_hierarchy = (LinkedList<Category>)categories.values().stream()
				.collect(Collectors.toCollection(LinkedList::new));
		Collections.sort(temp_category_hierarchy, new Comparator<Category>() {
		    @Override
		    public int compare(Category a, Category b) {
		        return b.ordinal - a.ordinal;
		    }
		});
		for (Category e: temp_category_hierarchy) {
			//Tools.printTypeAndContent(category_hierarchy, "cat hier");
			category_hierarchy.addLast(e.id);
		}		
	}
	
	private static void buildTopicHierarchy() {
		//HashMap<String, LinkedList<String>> topic_hierarchy
		//HashMap<cat_id, LinkedList<Top_id>>
		HashMap<String, ArrayList<Topic>> temp = new HashMap<>(); //category id: member Topics
		for (Map.Entry<String, Topic> e: topics.entrySet()) {
			Topic topic = e.getValue();
			String category_id = topic.category_id;
			if (temp.containsKey(category_id)) {
				ArrayList<Topic> old_list = temp.get(category_id);
				old_list.add(topic);		
			} else {
				ArrayList<Topic> new_list = new ArrayList<>();
				new_list.add(topic);
				temp.put(category_id, new_list);
			}
		} for (Map.Entry<String, ArrayList<Topic>> te: temp.entrySet()) {
			String category_id = te.getKey();
			ArrayList<Topic> members = te.getValue();
			Collections.sort(members, new Comparator<Topic>() {
				@Override
				public int compare(Topic a, Topic b) {
					return b.ordinal - a.ordinal;
				}
			});
			LinkedList<String> member_list = new LinkedList<>();
			for (Topic member: members) {
				member_list.add(member.id);
			}
			topic_hierarchy.put(category_id, member_list);
		}
	}
	
	private static void buildQuestionHierarchy() {
		//HashMap<String, LinkedList<String>> question_hierarchy
		//HashMap<top_id, LinkedList<que_id>>
		HashMap<String, ArrayList<AbstractQuestion>> temp = new HashMap<>(); //topic id: member AbstractQuestions
		for (Map.Entry<String, AbstractQuestion> e: questions.entrySet()) {
			AbstractQuestion question = e.getValue();
			String topic_id = question.topic_id;
			if (temp.containsKey(topic_id)) {
				ArrayList<AbstractQuestion> old_list = temp.get(topic_id);
				old_list.add(question);		
			} else {
				ArrayList<AbstractQuestion> new_list = new ArrayList<>();
				new_list.add(question);
				temp.put(topic_id, new_list);
			}
		} for (Map.Entry<String, ArrayList<AbstractQuestion>> te: temp.entrySet()) {
			String topic_id = te.getKey();
			ArrayList<AbstractQuestion> members = te.getValue();
			Collections.sort(members, new Comparator<AbstractQuestion>() {
				@Override
				public int compare(AbstractQuestion a, AbstractQuestion b) {
					return b.ordinal - a.ordinal;
				}
			});
			LinkedList<String> member_list = new LinkedList<>();
			for (AbstractQuestion member: members) {
				member_list.add(member.id);
			}
			question_hierarchy.put(topic_id, member_list);
		}
	}

	private static void saveState() throws IOException {
		updateOrdinals();
		QuestionsEncoder.encodeAll(); //encodes all categories, topics, questions
		AnswersEncoder.encodeAnswers(); //encodes all answers
	}

	private static void updateOrdinals() {
		//LinkedList<String> category_hierarchy (category ids in their own sequence)
		int c = 1;
		for (String category_id: category_hierarchy) {
			Category category = categories.get(category_id);
			category.ordinal = c;
			c += 1;
		} //HashMap<String, LinkedList<String>> topic_hierarchy; (category_ids to topics in their own sequence)
		for (Map.Entry<String, LinkedList<String>> th_entry: topic_hierarchy.entrySet()) {
			LinkedList<String> topic_ll = th_entry.getValue();
			c = 1;
			for (String topic_id: topic_ll) {
				Topic topic = topics.get(topic_id);
				topic.ordinal = c;
				c += 1;
			}
		} //HashMap<String, LinkedList<String>> question_hierarchy; (topic_ids to questions in their own sequence)
		for (Map.Entry<String, LinkedList<String>> qh_entry: question_hierarchy.entrySet()) {
			LinkedList<String> question_ll = qh_entry.getValue();
			c = 1;
			for (String question_id: question_ll) {
				AbstractQuestion question = questions.get(question_id);
				question.ordinal = c;
				c += 1;
			}
		}
	}

	private static void showAll() {
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, true, true);
		System.out.println(sb.toString());
	}
	
	private static void appendCategories(StringBuilder sb, Boolean append_topics, Boolean append_questions) {
		for (String category_id: category_hierarchy) {
			sb.append(String.format("%1$s | %2$s\n", 
					category_id, 
					categories.get(category_id).prompt
					));
			if (append_topics) {
				appendTopics(category_id, sb, append_questions);

			}
		}
	}
	
	private static void appendTopics(String category_id, StringBuilder sb, Boolean append_questions) {
		if (topic_hierarchy.containsKey(category_id)) {
			for (String topic_id: topic_hierarchy.get(category_id)) {
				sb.append((String.format("\t> %1$s (%2$s)\n",
						topics.get(topic_id).prompt,
						topic_id
						)));
				if (append_questions) {
					appendQuestions(topic_id, sb);
				}
			}
		}
	}
	
	private static void appendQuestions(String topic_id, StringBuilder sb) {
		if (question_hierarchy.containsKey(topic_id)) {
			for (String question_id: question_hierarchy.get(topic_id)) {
				sb.append((String.format("\t\tL %1$s (%2$s)\n",
						questions.get(question_id).prompt,
						question_id
						)));
			}
		}
	}
	
	private static void showCategories() {
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, true, false);
		System.out.println(sb.toString());
	}
	
	private static void showTopics() {
		StringBuilder sb = new StringBuilder();
		for (String category_id: category_hierarchy) {
			appendTopics(category_id, sb, true);
		}
		System.out.println(sb.toString());		
	}

	private static void showQuestions() {
		StringBuilder sb = new StringBuilder();
		for (LinkedList<String> topic_ll: topic_hierarchy.values()) {
			for (String topic_id: topic_ll) {
				appendQuestions(topic_id, sb);
			}
		}
		System.out.println(sb.toString());
	}

	private static void helpText(Console c) {
		// TODO Auto-generated method stub
		
	}
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
//	}
//}
