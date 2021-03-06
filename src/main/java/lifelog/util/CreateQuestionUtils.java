package lifelog.util;
import lifelog.*;
import lifelog.domain.*;
import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class CreateQuestionUtils{
	public static void createDialogue(Console c) {
		System.out.println("(c) create a new category\n(t) create a new topic\n(q) create a new question\n\n(e) exit this menu");
		String response = c.readLine();
		while (true) {
			if (response.equals("c")) {
				createCategoryDialogue(c);
				return;
			} else if (response.equals("t")) {
				createTopicDialogue(c);
				return;
			} else if (response.equals("q")) {
				createQuestionDialogue(c);
				return;
			} else if (response.equals("e")) {
				System.out.println("Exiting create mode...");
				return;
			}
		}
	}

	private static void createCategoryDialogue(Console c) {
		System.out.println("What will be the category's name?");
		String prompt = c.readLine();
		System.out.printf("Make a new category with name \"%1$s\"?\n\n(y) yes\n(s) start over\n(e) exit create mode", prompt);
		while (true) {
			String response = c.readLine();
			if (response.equals("e")) {
				System.out.println("Exiting create mode...");
				return;
			} else if (response.equals("s")) {
				createDialogue(c);
			} else if (response.equals("y")) {
				createCategory(prompt);
				return;
			}
		}
	}

	private static void createCategory(String prompt) {
		String id = Utils.getNextId(Collections.singleton(Main.category_hierarchy));
		if (id != null) {
			int ordinal = Main.category_hierarchy.size() + 1;
			Category new_category = new Category(id, ordinal, prompt);
			Main.categories.put(id, new_category);
			Main.category_hierarchy.add(id);
			Main.topic_hierarchy.put(id, new LinkedList<String>());
			System.out.printf("Category %1$s (%2$s) created.", prompt, id);
			return;
		}
	}

	private static void createTopicDialogue(Console c) {
		System.out.println("What will be the topic's name?");
		String prompt = c.readLine();
		System.out.println("What category will this topic fall under?");
		StringBuilder sb = new StringBuilder();
		ShowUtils.appendCategories(sb, 0);
		System.out.println(sb.toString());
		String category_id = null;
		while (true) {
			String response = c.readLine();
			if (Main.category_hierarchy.contains(response)) {
				category_id = response;
				break;
			} else {
				System.out.println("Invalid id provided");
			}
		}
		System.out.printf("Make a new topic with name \"%1$s\" under category \"%2$s\"?\n\n(y) yes\n(s) start over\n(e) exit create mode", prompt, category_id);
		while (true) {
			String response = c.readLine();
			if (response.equals("e")) {
				System.out.println("Exiting create mode...");
				return;
			} else if (response.equals("s")) {
				createDialogue(c);
			} else if (response.equals("y")) {
				createTopic(prompt, category_id);
				return;
			}
		}
	}

	private static void createTopic(String prompt, String category_id) {
		String id = Utils.getNextId(Main.topic_hierarchy.values());
		if (id != null) {
			int ordinal = Main.topic_hierarchy.size() + 1;
			Topic new_topic = new Topic(id, ordinal, prompt, category_id);
			Main.topics.put(id, new_topic);
			Main.topic_hierarchy.get(category_id).add(id);
			Main.question_hierarchy.put(id, new LinkedList<String>());
			System.out.printf("Topic %1$s (%2$s) created.", prompt, id); 
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
		ShowUtils.showTopics();
		while (true) {
			response = c.readLine();
			if (Utils.idExists(response) && response.startsWith("t")) {
				topic_id = response;
				break;
			} else {
				System.out.println("Invalid id provided");
			}
		}
		
		String id = Utils.getNextId(Main.question_hierarchy.values());
		if (id != null) {
			int ordinal;
			if (Main.question_hierarchy.containsKey(topic_id)) {
				ordinal = Main.question_hierarchy.get(topic_id).size() + 1;
			} else {
				ordinal = 1;
			}
			
			if (type.equals("f")) {
				createFreeQuestionDialogue(
				c, type, prompt, ordinal, topic_id, id);
				return;
			} else if (type.equals("c")) {
				createChoiceQuestionDialogue(
				c, type, prompt, ordinal, topic_id, id);
				return;
			} else {
				createScaleQuestionDialogue(
				c, type, prompt, ordinal, topic_id, id);
				return;
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
		boolean confirm_question = confirmQuestion(c, String.format("id = %1$s\nprompt = %2$s\nnumber of answers = %3$s\nunder topic: %4$s", id, prompt, num_of_answers, topic_id));
		if (confirm_question) {
			FreeQuestion new_object = new FreeQuestion(id, ordinal, prompt, topic_id, num_of_answers);
			Main.questions.put(id, new_object);
			if (Main.question_hierarchy.containsKey(topic_id) == false) {
				LinkedList<String> ll = new LinkedList<>();
				ll.add(id);
				Main.question_hierarchy.put(topic_id, ll);
			} else {
				Main.question_hierarchy.get(topic_id).add(id);
			}
			System.out.printf("Question %1$s created.", id);
		}
	}

	private static boolean confirmQuestion(Console c, String details) {
		while (true) {
			System.out.printf("Create a new question with\n%1$s?\n\n(y) yes\n(s) start over\n(e) exit",	details);
			String response = c.readLine();
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
		//ChoiceQuestion(String id, int ordinal, String prompt, String topic_id, 
		//int critical_low, int critical_high, int critical_variance, int critical_duration, 
		//HashMap<String, Option> options_map)
		
		ArrayList<Option> options_array = createAllOptions(c);

		System.out.println("You have assigned weights for each option. ");
		ArrayList<Integer> criteria = getCriteria(c);
		
		String options_string = Option.makeOptionsString(options_array);
		boolean confirm_question = confirmQuestion(c, String.format(
				"id = %1$s\nprompt = %2$s\nlow = %3$s high = %4$s variance = %5$s duration = %6$s\noptions =\n%7$s\nunder topic: %8$s\n", 
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
					options_array);
			Main.questions.put(id, choice_question);
			if (Main.question_hierarchy.containsKey(topic_id) == false) {
				LinkedList<String> ll = new LinkedList<>();
				ll.add(id);
				Main.question_hierarchy.put(topic_id, ll);
			} else {
				Main.question_hierarchy.get(topic_id).add(id);
			}
			System.out.printf("Question %1$s created", id);
		}
	}
	
	private static ArrayList<Option> createAllOptions(Console c) {
		HashMap<String, Option> result = new HashMap<>();
		while (true) {
			Option option = createOneOption(c);
			if (option != null) {
				result.put(Character.toString(option.abbreviation), option);
			}
			//confirm if add more
			System.out.println("Add more options?\n\n(y) yes\n(n) no");
			while (true) {
				String response = c.readLine();
				if (response.equals("y")) {
					break;
				} else if (response.equals("n")) {
					ArrayList<Option> options_array = new ArrayList<>();
					options_array.addAll(result.values());
					return options_array;
				}
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
			int option_weight = Utils.getIntResponse(c);
			 
			System.out.printf("Create the following option?\nprompt = \"%1$s\"\nabbreviation = %2$s\nweight=%3$s\n\n(y) yes\n(s) start over\n(e) exit", option_prompt, option_abbreviation, option_weight);
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
	
	private static ArrayList<Integer> getCriteria(Console c) {
		System.out.println("What total score would be considered low? Answer -99999 if not applicable.");
		int critical_low = Utils.getIntResponse(c);
		System.out.println("What total score would be considered high? Answer -99999 if not applicable.");
		int critical_high = Utils.getIntResponse(c);
		System.out.println("How many points variance would be considered significant? Answer -99999 if not applicable.");
		int critical_variance = Utils.getIntResponse(c);
		System.out.println("How many days of sustained low or high score should be considered significant? Answer -99999 if not applicable.");
		int critical_duration = Utils.getIntResponse(c);
		ArrayList<Integer> results = new ArrayList<Integer>(
			    Arrays.asList(critical_low, critical_high, critical_variance, critical_duration));
		return results;
	}
	
	private static void createScaleQuestionDialogue(Console c, String type, String prompt, int ordinal, String topic_id, String id) {
		//(String id, int ordinal, String prompt, String topic_id, 
		//int critical_low, int critical_high, int critical_variance, int critical_duration, 
		//Range range, String legend)
		System.out.println("What is the lowest integer on the scale?");
		int range_start = Utils.getIntResponse(c);
		System.out.println("What is the highest integer on the scale?");
		int range_stop = Utils.getIntResponse(c);
		System.out.println("How far apart is each increment on the scale?");
		int range_step = Utils.getIntResponse(c);
		Range range = new Range(range_start, range_stop, range_step);
		System.out.println("What is the legend for this scale? Response with \"NA\" to skip this.");
		String response = c.readLine();
		String legend;
		if (response.equals("NA")) {
			legend = null;
		} else {
			legend = response;
		} ArrayList<Integer> criteria = getCriteria(c);
		
		boolean confirm_question = confirmQuestion(c, String.format(
				"id = %1$s\nprompt= %2$s\nlow = %3$s high = %4$s variance = %5$s duration = %6$s\n\nWith range %7$s\nunder topic: %8$s", 
				id,	
				prompt, 
				criteria.get(0), criteria.get(1), criteria.get(2), criteria.get(3), 
				range.showRange(),
				topic_id
				));
		if (confirm_question) {
			ScaleQuestion scale_question = new ScaleQuestion(
					id, ordinal, prompt, topic_id,
					criteria.get(0), criteria.get(1), criteria.get(2), criteria.get(3),
					range,
					legend);
			System.out.println(scale_question.prompt);
			Main.questions.put(id, scale_question);
			if (Main.question_hierarchy.containsKey(topic_id) == false) {
				LinkedList<String> ll = new LinkedList<>();
				ll.add(id);
				Main.question_hierarchy.put(topic_id, ll);
			} else {
				Main.question_hierarchy.get(topic_id).add(id);
			}
			System.out.printf("Question %1$s created", id);
			}
	}
}
