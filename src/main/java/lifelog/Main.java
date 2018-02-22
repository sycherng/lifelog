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

import lifelog.ChoiceQuestion.Option;

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
		/*Console c = System.console();
		if (c == null) {
			System.err.println("No console. Shutting down...");
			System.exit(1);
		} else {
			System.out.println("Welcome back to your lifelog.");
			while (true) {
				String command = c.readLine();
				if (command.equals("quit")) {
					saveState();
					System.out.println("Shutting down...");
					System.exit(1);
				}
				else if (command.equals("show all")) {
					showAll();
				}
				else if (command.equals("show categories") || command.equals("show c")) {
					showCategories();
				}
				else if (command.equals("show topics") || command.equals("show t")) {
					showTopics();
				}
				else if (command.equals("show questions") || command.equals("show q")) {
					showQuestions();
				}
				else if (command.startsWith("move") && (command.length() == 10)) {
					move(c, command);
				}
				else if (command.equals("create")) {
					createDialogue(c);
				else if (command.equals("help")) {
					helpText();
				} System.out.println("-------------------------");
			}
		}*/
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
		        return a.ordinal - b.ordinal;
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
					return a.ordinal - b.ordinal;
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
					return a.ordinal - a.ordinal;
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
		System.out.println("I may have been successful!");
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
		appendCategories(sb, 2);
		System.out.println(sb.toString());
	}
	
	private static void showCategories() {
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, 1);
		System.out.println(sb.toString());
	}
	
	private static void showTopics() {
		StringBuilder sb = new StringBuilder();
		for (String category_id: category_hierarchy) {
			appendTopics(sb, category_id, 1, 0);
		}
		System.out.println(sb.toString());		
	}

	private static void showQuestions() {
		StringBuilder sb = new StringBuilder();
		for (LinkedList<String> topic_ll: topic_hierarchy.values()) {
			for (String topic_id: topic_ll) {
				appendQuestions(sb, topic_id, 0);
			}
		}
		System.out.println(sb.toString());
	}

	private static void appendCategories(StringBuilder sb, int layers_to_show) { // layers = 0 means show only this layer
		for (String category_id: category_hierarchy) {
			sb.append(String.format("%1$s | %2$s\n", 
					category_id, 
					categories.get(category_id).prompt
					));
			if (layers_to_show > 0) {
				appendTopics(sb, category_id, (layers_to_show - 1), 1);

			}
		}
	}
	
	private static void appendTopics(StringBuilder sb, String category_id, int layers_to_show, int tabs) {
		if (topic_hierarchy.containsKey(category_id)) {
			for (String topic_id: topic_hierarchy.get(category_id)) {
				sb.append((String.format("%1$s> %2$s (%3$s)\n",
						String.join("", Collections.nCopies(tabs, "\t")), //repeat \t tabs times
						topics.get(topic_id).prompt,
						topic_id
						)));
				if (layers_to_show > 0) {
					appendQuestions(sb, topic_id, (tabs + 1));
				}
			}
		}
	}
	
	private static void appendQuestions(StringBuilder sb, String topic_id, int tabs) {
		if (question_hierarchy.containsKey(topic_id)) {
			for (String question_id: question_hierarchy.get(topic_id)) {
				sb.append((String.format("%1$sL %2$s (%3$s)\n",
						String.join("", Collections.nCopies(tabs, "\t")), //repeat \t tabs times
						questions.get(question_id).prompt,
						question_id
						)));
			}
		}
	}
	
	private static void move(Console c, String message) {
		Boolean contains = false;
		String target = message.split(" ")[1];
		HashMap<String, LinkedList<String>> target_list = null;
		char initial = target.charAt(0);
		if ((initial == 'c') && (category_hierarchy.contains(target))) {
				contains = true;
				moveMode(c, target, category_hierarchy);
		} else if (initial == 't') {
			target_list = topic_hierarchy;
		} else if (initial == 'q') {
			target_list = question_hierarchy; 
		}
		
		if (target_list != null) {
			for (Map.Entry<String, LinkedList<String>> e: target_list.entrySet()) {
				LinkedList<String> ll = e.getValue();
				if (ll.contains(target)) {
					contains = true;
					moveMode(c, target, ll);
				}
			} 
		}
		
		if (contains == false) {
			System.out.println("Invalid id provided.");
		}
	}
	
	private static void moveMode(Console c, String target, LinkedList<String> ll) {
		String response = "";
		StringBuilder sb = new StringBuilder();
		String finish_string = "(f) finish and exit move mode";
		int current_index = ll.indexOf(target);

		while (true) {
			printTempOrder(ll, target);
			
			sb.setLength(0);
			if (current_index != 0) {
				sb.append("(u) move up\\n");
			} if (current_index != (ll.size() - 1)) {
				sb.append("(d) move down\\n");
			} System.out.println(String.format("%1$s%2$s", sb.toString(), finish_string));

			response = c.readLine();

			if (response.equals("f")) {
				System.out.println(String.format("%1$s moved.", target));
				return;
			} else {
				int diff = 0;
				
				if (response.equals("u") && (current_index != 0)) {
					diff = -1;
					swapOrdinal(current_index, diff, ll);
					current_index += diff;
				} else if (response.equals("d") && (current_index != ll.size() - 1)) {
					diff = 1;
					swapOrdinal(current_index, diff, ll);
					current_index += diff;
				}
			}
		}
	}
	
	private static void swapOrdinal(int current_index, int diff, LinkedList<String> ll) {
		String target = ll.get(current_index);
		String replacement = ll.get(current_index + diff);
		ll.set(current_index, replacement);
		ll.set(current_index + diff, target);
	}
	
	private static void printTempOrder(LinkedList<String> ll, String target) {
		StringBuilder sb = new StringBuilder();
		for (String s: ll) {
			sb.append(s);
			if (s.equals(target)) {
				sb.append(" <-");
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
	
	private static void createDialogue(Console c) {
		System.out.println("(c) create a new category\n(t) create a new topic\n(q) create a new question\n\n(e) exit this menu");
		String response = c.readLine();
		while (true) {
			if (response.equals("c")) {
				createCategoryDialogue(c);
			} else if (response.equals("t")) {
				createTopicDialogue(c);
			} else if (response.equals("q")) {
				createQuestionDialogue(c);
			} else if (response.equals("e")) {
				System.out.println("Exiting create mode...");
				return;
			}
		}
	}

	private static void createCategoryDialogue(Console c) {
		System.out.println("What will be the category's name?");
		String prompt = c.readLine();
		System.out.println(String.format("Make a new category with name \"%1$s\"?\n\n(y) yes\n(s) start over\n(e) exit create mode", prompt));
		while (true) {
			String response = c.readLine();
			if (response.equals("e")) {
				System.out.println("Exiting create mode...");
				return;
			} else if (response.equals("s")) {
				createDialogue(c);
			} else if (response.equals("y")) {
				createCategory(prompt);
			}
		}
	}

	private static void createCategory(String prompt) {
		String id = getNextId(Collections.singleton(category_hierarchy));
		if (id != null) {
			int ordinal = category_hierarchy.size() + 1;
			Category new_category = new Category(id, ordinal, prompt);
			categories.put(id, new_category);
			category_hierarchy.add(id);
			System.out.println(String.format("Category %1$s (%2$s) created.", prompt, id)); 
		}
	}

	private static void createTopicDialogue(Console c) {
		System.out.println("What will be the topic's name?");
		String prompt = c.readLine();
		System.out.println("What category will this topic fall under?");
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, 0);
		System.out.println(sb.toString());
		String category_id = null;
		while (true) {
			String response = c.readLine();
			if (category_hierarchy.contains(response)) {
				category_id = response;
				break;
			} else {
				System.out.println("Invalid id provided");
			}
		}
		System.out.println(String.format("Make a new topic with name \"%1$s\" under category \"%2$s\"?\n\n(y) yes\n(s) start over\n(e) exit create mode", prompt, category_id));
		while (true) {
			String response = c.readLine();
			if (response.equals("e")) {
				System.out.println("Exiting create mode...");
				return;
			} else if (response.equals("s")) {
				createDialogue(c);
			} else if (response.equals("y")) {
				createTopic(prompt, category_id);
			}
		}
	}

	private static void createTopic(String prompt, String category_id) {
		String id = getNextId(topic_hierarchy.values());
		if (id != null) {
			int ordinal = topic_hierarchy.size() + 1;
			Topic new_topic = new Topic(id, ordinal, prompt, category_id);
			topics.put(id, new_topic);
			topic_hierarchy.get(category_id).add(id);
			System.out.println(String.format("Topic %1$s (%2$s) created.", prompt, id)); 
		}
	}

	private static void createQuestionDialogue(Console c) {
		String response;
		System.out.println("What type of question will it be?\n\n(f) free answer\n(c) multiple choice\n(s) on a numerical scale");
		String type;
		while (true) {
			response = c.readLine();
			if (response.equals("f")) {
				type = "f";
				break;
			} else if (response.equals("c")) {
				type = "c";
				break;
			} else if (response.equals("s")) {
				type = "s";
				break;
			}
		}
		System.out.println("What will be the prompt?");
		String prompt = c.readLine();

		String topic_id;
		System.out.println("Which topic will this question fall under?");
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, 0);
		System.out.println(sb.toString());
		while (true) {
			response = c.readLine();
			LinkedList<String> ll = question_hierarchy.get(response);
			if (ll == null) {
				System.out.println("Invalid id provided");
			} else {
				topic_id = response;
				break;
			}
		}
		
		String id = getNextId(question_hierarchy.values());
		if (id != null) {
			int ordinal = question_hierarchy.get(topic_id).size() + 1;
			
			if (type.equals("f")) {
				createFreeQuestionDialogue(c, type, id, ordinal, prompt, topic_id);
			} else if (type.equals("c")) {
				createChoiceQuestionDialogue(c, type, id, ordinal, prompt, topic_id);
			} else {
				createScaleQuestionDialogue(c, type, id, ordinal, prompt, topic_id);
			}
		} 
	}

	private static void createFreeQuestionDialogue(Console c, String type, String prompt, int ordinal, String topic_id, String id) {
		System.out.println("How many are allowed with each entry?");
		String response;
		int num_of_answers = 0;
		boolean success = false;
		while (true) {
			response = c.readLine();
			try {
				num_of_answers = Integer.parseInt(response);
				success = true;
			} catch(Exception NumberFormatException) {
				System.out.println("Please enter a number only.");
			} finally {
				if (success) {
					break;
				}
			}
		}
		boolean confirm_question = confirmQuestion(c, String.format("id = %1$s\nprompt= %2$s\nnumber of answers = %3$s\nunder topic:%4$s", id, prompt, num_of_answers, topic_id));
		if (confirm_question) {
			FreeQuestion new_object = new FreeQuestion(id, ordinal, prompt, topic_id, num_of_answers);
			questions.put(topic_id, new_object);
			question_hierarchy.get(topic_id).add(id);
		}		
	}

	private static boolean confirmQuestion(Console c, String details) {
		String response;
		while (true) {
			response = c.readLine("Create a new question with\n%1$s?\n\n(y) yes\n(s) start over\n(e) exit", details);
			if (response.equals("y")) {
				return true;
			} else if (response.equals("e")) {
				System.out.println("Exiting create mode...");
				return false;
			} else if (response.equals("s")) {
				createDialogue(c);
			}
		}
	}
	
	private static void createChoiceQuestionDialogue(Console c, String type, String prompt, int ordinal, String topic_id, String id) {
		//ChoiceQuestion(String id, int ordinal, String prompt, String topic_id, int critical_low, int critical_high, int critical_variance, int critical_duration, HashMap<String, Option> options_map)
		HashMap<String, Option> options_map = createAllOptions(c);
		
		int critical_low = getIntResponse(c);
		int critical_high = getIntResponse(c);
		int critical_variance = getIntResponse(c);
		int critical_duration = getIntResponse(c);
		
		StringBuilder sb = new StringBuilder();
		for (Option option: options_map.values()) {
			sb.append(String.format("%1$s - %2$s (%3$s)\n", option.abbreviation, option.full, option.weight));
		}
		boolean confirm_question = confirmQuestion(c, String.format(
				"id = %1$s\nprompt= %2$s\nlow = %3$s high = %4$s variance = %5$s duration = %6$s\n\nWith options %7$s\nunder topic:%8$s", 
				id,	prompt, critical_low, critical_high, critical_variance, critical_duration, sb.toString(), topic_id
				));
		if (confirm_question) {
			ChoiceQuestion new_object = new ChoiceQuestion(id, ordinal, prompt, topic_id, critical_low, critical_high, critical_variance, critical_duration, options_map);
			questions.put(topic_id, new_object);
			question_hierarchy.get(topic_id).add(id);
		}
	}
	
	private static HashMap<String, Option> createAllOptions(Console c) {
		HashMap<String, Option> result = new HashMap<>();
		while (true) {
			Option option = createOneOption(c);
			if (option != null) {
				result.put(Character.toString(option.abbreviation), option);
			}
			//confirm if add more
			System.out.println("Add more options?\n\n(y) yes\n(n) no");
			String response = c.readLine();
			if (response.equals("y")) {
				continue;
			} else if (response.equals("n")) {
				return result;
			} else {
				return result;
			}
		}
	}
	private static Option createOneOption(Console c) {
		String response;
		while (true) {
			//Option(char abbreviation, String full, int weight)
			System.out.println("Creating new option...\nWhat is this option's prompt?");
			String option_prompt = c.readLine();
			System.out.println("What is the option's abbreviation? (Please enter a single letter that is not x).");
			String option_abbreviation = "";
			while (true) {
				response = c.readLine();
				if ((response != "x") && response.length() == 1) {
					option_abbreviation = response;
					break;
				}
			}
			System.out.println("What is the option's weight?");
			int option_weight = getIntResponse(c);
			 
			System.out.println(String.format("Create the following option?\nprompt = \"%1$s\"\nabbreviation = %2$s\nweight=%3$s\n\n(y) yes\n(s) start over\n(e) exit", option_prompt, option_abbreviation, option_weight));
			while (true) {
				response = c.readLine();
				if (response.equals("y")) {
					return new Option(option_abbreviation.charAt(0), option_prompt, option_weight);
				} else if (response.equals("s")) {
					createDialogue(c);
					break;
				} else if (response.equals("e")) {
					System.out.println("Exiting create mode...");
					break;
				}
			} 
			return null;
		}
	}
	
	
	private static int getIntResponse(Console c) {
		int answer;
		while (true) {
			String response = c.readLine();
			try {
				answer = Integer.parseInt(response);
				return answer;
			} catch(Exception NumberFormatException) {
				System.out.println("Please enter a number.");
			}
		}
	}

	private static void createScaleQuestionDialogue(Console c, String type, String prompt, int ordinal, String topic_id, String id) {
		// TODO Auto-generated method stub
		
	}

	/** @return appropriate id for a new SurveyItem
	 * @param collection of linked lists containing all ids of that SurveyItem type
	 */
	private static String getNextId(Collection<LinkedList<String>> ll_collection) {
		String max_id = "";
		for (LinkedList<String> ll: ll_collection) {
			for (String id: ll) {
				if (max_id.compareTo(id) < 0) {
					max_id = id;
				}
			}
		} Integer new_number = Integer.parseInt(max_id.substring(1));
		int length = (int)(Math.log(new_number) + 1);
		if (length > 4) {
			System.out.println("Maximum number of this element reached.");
			return null;
		} else {
			String new_id = max_id.substring(0, (5 - length)) + new_number.toString();
			return new_id;
		}
	}
	
	private static void helpText() {
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
