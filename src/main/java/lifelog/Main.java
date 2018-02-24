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

import static lifelog.Tools.print; //helper function that turns System.out.println() to print()

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
			print("Welcome back to your lifelog.");
			while (true) {
				String command = c.readLine();
				if (command.equals("quit")) {
					saveState();
					print("Shutting down...");
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
				else if (command.startsWith("reassign parent") && (command.length() == 21)) {
					reassignParentDialogue(c, command);
				}
				else if (command.equals("create")) {
					createDialogue(c);
				else if (command.equals("help")) {
					helpText();
				} print("-------------------------");
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
		print("I may have been successful!");
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

	private static boolean idExists(String id) {
		if (id.startsWith("c")) {
			return categories.containsKey(id);
		} else if (id.startsWith("t")) {
			return topics.containsKey(id);
		} else if (id.startsWith("q")) {
			return questions.containsKey(id);
		}
	}
	
	private static boolean isParent(String parent_id, String child_id) {
		if (child_id.startsWith("c")) {
			return false;
		} else if (child_id.startsWith("t")) {
			return parent_id.equals(topics.get(child_id).category_id);
		} else if (child_id.startsWith("q")) {
			return parent_id.equals(questions.get(child_id).topic_id);
		} else {
			return false;
		}
	}
	
	private static boolean isChild(String child_id, String parent_id) {
		return isParent(parent_id, child_id);
	}
	
	private static String findParentId(String child_id) {
		if (child_id.startsWith("c")) {
			return null;
		} else if (child_id.startsWith("t")) {
			return topics.get(child_id).category_id;
		} else if (child_id.startsWith("q")) {
			return questions.get(child_id).topic_id;
		}
	}
	
	private static LinkedList<String> findChildIds(String parent_id) {
		if (parent_id.startsWith("c")) {
			return topic_hierarchy.get(parent_id);
		} else if (parent_id.startsWith("t")) {
			return question_hierarchy.get(parent_id);
		} else {
			return null;
		}
	}
	
	private static HashMap<String, LinkedList<String>> findHierarchyMap(Console c, String target) {
		if (target.startsWith("t")) {
			return topic_hierarchy;
		} else if (target.startsWith("q")) {
			return question_hierarchy;
		} else {
			return null;
		}
	}
	
	private static void showAll() {
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, 2);
		print(sb.toString());
	}
	
	private static void showCategories() {
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, 1);
		print(sb.toString());
	}
	
	private static void showTopics() {
		StringBuilder sb = new StringBuilder();
		for (String category_id: category_hierarchy) {
			appendTopics(sb, category_id, 1, 0);
		}
		print(sb.toString());		
	}

	private static void showQuestions() {
		StringBuilder sb = new StringBuilder();
		for (LinkedList<String> topic_ll: topic_hierarchy.values()) {
			for (String topic_id: topic_ll) {
				appendQuestions(sb, topic_id, 0);
			}
		}
		print(sb.toString());
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
		String target = message.split(" ")[1];
		//validate the id provided
		LinkedList<String> peers = returnPeerLinkedList(target);
		//feed the id to actual action area
		if (peers != null) {
			moveMode(c, target, peers);
		}
	}
	
	private static LinkedList<String> returnPeerLinkedList(String id) {
		HashMap<String, LinkedList<String>> id_list = null;
		char initial = id.charAt(0);
		if ((initial == 'c') && (category_hierarchy.contains(id))) {
			return category_hierarchy;
		} else if (initial == 't') {
			id_list = topic_hierarchy;
		} else if (initial == 'q') {
			id_list = question_hierarchy;
		} if (id_list != null) { 
			  for (Map.Entry<String, LinkedList<String>> e: id_list.entrySet()) {
				LinkedList<String> ll = e.getValue();
				if (ll.contains(id)) {
					return ll;
				}
			  } 
		} else {
			print("Invalid id provided.");
			return null;
		}
	}
	
	private static void moveMode(Console c, String target, LinkedList<String> ll) {
		String response = "";
		StringBuilder sb = new StringBuilder();
		String finish_string = "(e) finish and exit move mode";
		int current_index = ll.indexOf(target);

		while (true) {
			printTempOrder(ll, target);
			
			sb.setLength(0);
			if (current_index != 0) {
				sb.append("(u) move up\\n");
			} if (current_index != (ll.size() - 1)) {
				sb.append("(d) move down\\n");
			} print(String.format("%1$s%2$s", sb.toString(), finish_string));

			response = c.readLine();

			if (response.equals("e")) {
				print(String.format("Exiting move mode...", target));
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
	
	private static void printTempOrder(LinkedList<String> ll, String target) {
		StringBuilder sb = new StringBuilder();
		for (String s: ll) {
			sb.append(s);
			if (s.equals(target)) {
				sb.append(" <-");
			}
			sb.append("\n");
		}
		print(sb.toString());
	}
	
	private static void swapOrdinal(int current_index, int diff, LinkedList<String> ll) {
		String target = ll.get(current_index);
		String replacement = ll.get(current_index + diff);
		ll.set(current_index, replacement);
		ll.set(current_index + diff, target);
	}
	
	private static void reassignParentDialogue(Console c, String message) {
		String target_id = message.split(" ")[2];
		HashMap<String, LinkedList<String>> target_hierarchy_map = findHierarchyMap(c, target_id);
		if (target_hierarchy_map == null) {
			System.out.println("Invalid id supplied.");
			return;
		} String parent_id = findParentId(target_id);
		if (parent_id == null) {
			System.out.println("Invalid id supplied.");
			return;
		}
		String type_initial = target_id.substring(0, 1);
		String response = c.readLine(
				"%1$s currently belongs under %2$s.\nProvide the id of its new parent, or respond with \"(e)\" to exit parent reassigning mode.", 
				target_id,
				parent_id);
		while (true) {
			if (response.equals("e")) {
				print("Exiting parent reassigning mode...");
				return;
			} else if (idExists(response)) {
				String new_parent_id = response;
				reassignParent(target_id, parent_id, new_parent_id, target_hierarchy_map);
			} else {
				print("Invalid parent id supplied.");
			}
		} 	
	}

	private static void reassignParent(String child_to_move, String desired_parent, String new_parent_id, HashMap<String, LinkedList<String>> hierarchy_map) {
		hierarchy_map.get()
	}

	private static void reassignParent(Console c, String target, HashMap<String<LinkedList<String>>>)
	


	private static void createDialogue(Console c) {
		print("(c) create a new category\n(t) create a new topic\n(q) create a new question\n\n(e) exit this menu");
		String response = c.readLine();
		while (true) {
			if (response.equals("c")) {
				createCategoryDialogue(c);
			} else if (response.equals("t")) {
				createTopicDialogue(c);
			} else if (response.equals("q")) {
				createQuestionDialogue(c);
			} else if (response.equals("e")) {
				print("Exiting create mode...");
				return;
			}
		}
	}

	private static void createCategoryDialogue(Console c) {
		print("What will be the category's name?");
		String prompt = c.readLine();
		print(String.format("Make a new category with name \"%1$s\"?\n\n(y) yes\n(s) start over\n(e) exit create mode", prompt));
		while (true) {
			String response = c.readLine();
			if (response.equals("e")) {
				print("Exiting create mode...");
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
			topic_hierarchy.put(id, new LinkedList<String>());
			print(String.format("Category %1$s (%2$s) created.", prompt, id)); 
		}
	}

	private static void createTopicDialogue(Console c) {
		print("What will be the topic's name?");
		String prompt = c.readLine();
		print("What category will this topic fall under?");
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, 0);
		print(sb.toString());
		String category_id = null;
		while (true) {
			String response = c.readLine();
			if (category_hierarchy.contains(response)) {
				category_id = response;
				break;
			} else {
				print("Invalid id provided");
			}
		}
		print(String.format("Make a new topic with name \"%1$s\" under category \"%2$s\"?\n\n(y) yes\n(s) start over\n(e) exit create mode", prompt, category_id));
		while (true) {
			String response = c.readLine();
			if (response.equals("e")) {
				print("Exiting create mode...");
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
			question_hierarchy.put(id, new LinkedList<String>());
			print(String.format("Topic %1$s (%2$s) created.", prompt, id)); 
		}
	}

	private static void createQuestionDialogue(Console c) {
		String response;
		print("What type of question will it be?\n\n(f) free answer\n(c) multiple choice\n(s) on a numerical scale");
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
		print("What will be the prompt?");
		String prompt = c.readLine();

		String topic_id;
		print("Which topic will this question fall under?");
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, 0);
		print(sb.toString());
		while (true) {
			response = c.readLine();
			LinkedList<String> ll = question_hierarchy.get(response);
			if (ll == null) {
				print("Invalid id provided");
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
		print("How many are allowed with each entry?");
		String response;
		int num_of_answers = 0;
		boolean success = false;
		while (true) {
			response = c.readLine();
			try {
				num_of_answers = Integer.parseInt(response);
				success = true;
			} catch(Exception NumberFormatException) {
				print("Please enter a number only.");
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
				print("Exiting create mode...");
				return false;
			} else if (response.equals("s")) {
				createDialogue(c);
			}
		}
	}
	
	private static void createChoiceQuestionDialogue(Console c, String type, String prompt, int ordinal, String topic_id, String id) {
		//ChoiceQuestion(String id, int ordinal, String prompt, String topic_id, 
		//int critical_low, int critical_high, int critical_variance, int critical_duration, 
		//HashMap<String, Option> options_map)
		
		HashMap<String, Option> options_map = createAllOptions(c);
		
		print("You have assigned weights for each option. ");
		ArrayList<Integer> criteria = getCriteria(c);
		
		String options_string = showOptions(options_map);
		boolean confirm_question = confirmQuestion(c, String.format(
				"id = %1$s\nprompt= %2$s\nlow = %3$s high = %4$s variance = %5$s duration = %6$s\n\nWith options %7$s\nunder topic:%8$s", 
				id,	
				prompt, 
				criteria.get(0), criteria.get(1), criteria.get(2), criteria.get(3), 
				options_string, 
				topic_id
				));
		if (confirm_question) {
			ChoiceQuestion choice_question = new ChoiceQuestion(
					id, ordinal, prompt, topic_id, 
					criteria.get(0), criteria.get(1), criteria.get(2), criteria.get(3), 
					options_map);
			questions.put(topic_id, choice_question);
			question_hierarchy.get(topic_id).add(id);
		} //else is handled by confirm_question()
	}
	
	private static String showOptions(HashMap<String, Option> options_map) {
		StringBuilder sb = new StringBuilder();
		for (Option option: options_map.values()) {
			sb.append(String.format("%1$s - %2$s (%3$s)\n", option.abbreviation, option.full, option.weight));
		} return sb.toString();
	}
	
	private static HashMap<String, Option> createAllOptions(Console c) {
		HashMap<String, Option> result = new HashMap<>();
		while (true) {
			Option option = createOneOption(c);
			if (option != null) {
				result.put(Character.toString(option.abbreviation), option);
			}
			//confirm if add more
			print("Add more options?\n\n(y) yes\n(n) no");
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
			print("Creating new option...\nWhat is this option's prompt?");
			String option_prompt = c.readLine();
			print("What is the option's abbreviation? (Please enter a single letter that is not x).");
			String option_abbreviation = "";
			while (true) {
				response = c.readLine();
				if ((response != "x") && response.length() == 1) {
					option_abbreviation = response;
					break;
				}
			}
			print("What is the option's weight?");
			int option_weight = getIntResponse(c);
			 
			print(String.format("Create the following option?\nprompt = \"%1$s\"\nabbreviation = %2$s\nweight=%3$s\n\n(y) yes\n(s) start over\n(e) exit", option_prompt, option_abbreviation, option_weight));
			while (true) {
				response = c.readLine();
				if (response.equals("y")) {
					return new Option(option_abbreviation.charAt(0), option_prompt, option_weight);
				} else if (response.equals("s")) {
					createDialogue(c);
					break;
				} else if (response.equals("e")) {
					print("Exiting create mode...");
					break;
				}
			} 
			return null;
		}
	}
	
	private static ArrayList<Integer> getCriteria(Console c) {
		print("What total score would be considered low? Answer -99999 if not applicable.");
		int critical_low = getIntResponse(c);
		print("What total score would be considered high? Answer -99999 if not applicable.");
		int critical_high = getIntResponse(c);
		print("How many points variance would be considered significant? Answer -99999 if not applicable.");
		int critical_variance = getIntResponse(c);
		print("How many days of sustained low or high score should be considered significant? Answer -99999 if not applicable.");
		int critical_duration = getIntResponse(c);
		ArrayList<Integer> results = new ArrayList<Integer>(
			    Arrays.asList(critical_low, critical_high, critical_variance, critical_duration));
		return results;
	}
	
	private static void createScaleQuestionDialogue(Console c, String type, String prompt, int ordinal, String topic_id, String id) {
		//(String id, int ordinal, String prompt, String topic_id, 
		//int critical_low, int critical_high, int critical_variance, int critical_duration, 
		//Range range, String legend)
		
		print("What is the lowest integer on the scale?");
		int range_start = getIntResponse(c);
		print("What is the highest integer on the scale?");
		int range_stop = getIntResponse(c);
		print("How far apart is each increment on the scale?");
		int range_step = getIntResponse(c);
		Range range = new Range(range_start, range_stop, range_step);
		print("What is the legend for this scale? Response with \"NA\" to skip this.");
		String response = c.readLine();
		String legend;
		if (response.equals("NA")) {
			legend = null;
		} else {
			legend = response;
		} ArrayList<Integer> criteria = getCriteria(c);
		
		boolean confirm_question = confirmQuestion(c, String.format(
				"id = %1$s\nprompt= %2$s\nlow = %3$s high = %4$s variance = %5$s duration = %6$s\n\nWith range %7$s\nunder topic:%8$s", 
				id,	
				prompt, 
				criteria.get(0), criteria.get(1), criteria.get(2), criteria.get(3), 
				showRange(range), 
				topic_id
				));
		if (confirm_question) {
			ScaleQuestion scale_question = new ScaleQuestion(
					id, ordinal, prompt, topic_id,
					criteria.get(0), criteria.get(1), criteria.get(2), criteria.get(3),
					range,
					legend);
			questions.put(topic_id, scale_question);
			question_hierarchy.get(topic_id).add(id);
		} //else is handled by confirm_question()
	}
	
	private static String showRange(Range range) {
		return String.format("%1$s ~ %2$s (%3$s increments)", 
				range.start, 
				range.stop,
				range.step);
	}
	
	private static int getIntResponse(Console c) {
		int answer;
		while (true) {
			String response = c.readLine();
			try {
				answer = Integer.parseInt(response);
				return answer;
			} catch(Exception NumberFormatException) {
				print("Please enter an integer.");
			}
		}
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
			print("Maximum number of this element reached.");
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
