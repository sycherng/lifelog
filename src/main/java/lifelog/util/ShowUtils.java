package lifelog.util;
import lifelog.*;
import lifelog.domain.*;
import java.io.Console;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShowUtils {
	public static void helpText() {
		System.out.println("quit\nlog\nshow all\nshow categories\nshow topics\nshow questions\nmove id\nreassign parent id\ncreate\ndelete\ndelete id\nview date category ids");
	}
	public static void showAll() {
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, 2);
		System.out.println(sb.toString());
	}
	
	public static void showCategories() {
		StringBuilder sb = new StringBuilder();
		appendCategories(sb, 1);
		System.out.println(sb.toString());
	}
	
	public static void showTopics() {
		StringBuilder sb = new StringBuilder();
		for (String category_id: Main.category_hierarchy) {
			appendTopics(sb, category_id, 1, 0);
		}
		System.out.println(sb.toString());		
	}

	public static void showQuestions() {
		StringBuilder sb = new StringBuilder();
		for (LinkedList<String> topic_ll: Main.topic_hierarchy.values()) {
			for (String topic_id: topic_ll) {
				appendQuestions(sb, topic_id, 0);
			}
		}
		System.out.println(sb.toString());
	}

	public static void appendCategories(StringBuilder sb, int layers_to_show) { // layers = 0 means show only this layer
		for (String category_id: Main.category_hierarchy) {
			sb.append(String.format("%1$s | %2$s\n", 
					category_id, 
					Main.categories.get(category_id).prompt
					));
			if (layers_to_show > 0) {
				appendTopics(sb, category_id, (layers_to_show - 1), 1);

			}
		}
	}
	
	public static void appendTopics(StringBuilder sb, String category_id, int layers_to_show, int tabs) {
		if (Main.topic_hierarchy.containsKey(category_id)) {
			for (String topic_id: Main.topic_hierarchy.get(category_id)) {
				sb.append((String.format("%1$s> %2$s (%3$s)\n",
						String.join("", Collections.nCopies(tabs, "\t")), //repeat \t tabs times
						Main.topics.get(topic_id).prompt,
						topic_id
						)));
				if (layers_to_show > 0) {
					appendQuestions(sb, topic_id, (tabs + 1));
				}
			}
		}
	}
	
	public static void appendQuestions(StringBuilder sb, String topic_id, int tabs) {
		if (Main.question_hierarchy.containsKey(topic_id)) {
			for (String question_id: Main.question_hierarchy.get(topic_id)) {
				sb.append((String.format("%1$sL %2$s (%3$s)\n",
						String.join("", Collections.nCopies(tabs, "\t")), //repeat \t tabs times
						Main.questions.get(question_id).prompt,
						question_id
						)));
			}
		}
	}
	
	public static void viewDialogue(Console c, String command) {
		String[] message = command.split(" ");
		List<String> valid_ids = getValidIds(message);
		List<LocalDate> date_list = getValidDates(message); 
		StringBuilder sb = new StringBuilder();
		makeViewHeading(sb, date_list);
		HashMap<String, ArrayList<String>> id_map = splitByQuestionType(valid_ids);
		buildScaleQuestionsView(sb, id_map.get("Scale"), date_list);
		buildChoiceQuestionsView(sb, id_map.get("Choice"), date_list);
		buildFreeQuestionsView(sb, id_map.get("Free"), date_list);
		System.out.println(sb.toString());
	}

	private static List<String> getValidIds(String[] message) {
		ArrayList<String> result = new ArrayList<String>();
		for (String id: Arrays.copyOfRange(message, 3, message.length - 1)) {
			if (Utils.idExists(id) && id.startsWith("c")) {
				result.add(id);
			} else {
				System.out.printf("%s is an invalid category id.", id);
			}
		}
		return result;
	}

	private static List<LocalDate> getValidDates(String[] message) {
		LocalDate from_date = Utils.parseDate(message[1]);
		LocalDate to_date = Utils.parseDate(message[2]);
		if (from_date == null || to_date == null) {
			System.out.println("Invalid dates");
			return null;
		} if (from_date.isAfter(to_date)) {
			System.out.println("From date must be before to date.");
			return null;
		}
		final int DAYS = (int) from_date.until(to_date, ChronoUnit.DAYS);

		List<LocalDate> date_list = Stream.iterate(from_date, d -> d.plusDays(1))
		  .limit(DAYS + 1)
		  .collect(Collectors.toList());	
		
		return date_list;
	}
	
	private static void makeViewHeading(StringBuilder sb, List<LocalDate> date_list) {
		sb.append(String.join("", Collections.nCopies(10, " "))); // append 10 spaces
		for (LocalDate date: date_list) {
			sb.append(String.format("| %s ", date));
		}
		sb.append("| Comment");
		
	}
	
	private static HashMap<String, ArrayList<String>> splitByQuestionType(List<String> valid_ids) {
		ArrayList<String> scale_ids = new ArrayList<>();
		ArrayList<String> choice_ids = new ArrayList<>();
		ArrayList<String> free_ids = new ArrayList<>();
		for (String id: valid_ids) {
			String type = Main.questions.get(id).type;
			if (type.equals("Scale")) {
				scale_ids.add(id);
			} else if (type.equals("Choice")) {
				choice_ids.add(id);
			} else if (type.equals("Free")) {
				free_ids.add(id);
			}
		}
		HashMap<String, ArrayList<String>> id_map = new HashMap<String, ArrayList<String>>();
		id_map.put("Scale", scale_ids);
		id_map.put("Choice", scale_ids);
		id_map.put("Free", scale_ids);
		return id_map;
	}
	
	private static void buildScaleQuestionsView(StringBuilder sb, ArrayList<String> ids, List<LocalDate> date_list) {
		for (String id: ids) {
			ScaleQuestion scale_question = (ScaleQuestion)Main.questions.get(id);
			String prompt = scale_question.prompt.substring(0, 11); // 10 char limit
			List<Integer> answers = null;
			for (LocalDate date: date_list) {
				answers = Main.answers.get(date)
						.get(id).answer
						.stream()
							.map(Integer::parseInt)
							.collect(Collectors.toList());
			} String comment = getComment(answers, scale_question);
			sb.append(prompt);
			for (Integer answer: answers) {
				sb.append(answer.toString());
			} sb.append(comment);
		} 
	}
	
	private static void buildChoiceQuestionsView(StringBuilder sb, ArrayList<String> question_ids, List<LocalDate> date_list) {
		for (String question_id: question_ids) {
			ChoiceQuestion choice_question = (ChoiceQuestion)Main.questions.get(question_id);
			String prompt = choice_question.prompt.substring(0, 11);
			ArrayList<Integer> answer_weights_across_dates = new ArrayList<>();
			ArrayList<String> answer_abbreviations_across_dates = new ArrayList<>();
			for (LocalDate date: date_list) {
				List<String> answers_string_for_date = Main.answers.get(date).get(question_id).answer;
				StringBuilder abbreviations_builder_for_date = new StringBuilder();
				for (String abbreviation_string: answers_string_for_date) {
					char abbreviation = abbreviation_string.charAt(0);
					int weight = findWeight(abbreviation, choice_question.options);
					answer_weights_across_dates.add(weight);
					abbreviations_builder_for_date.append(abbreviation_string);
					abbreviations_builder_for_date.append(" ");
				}
			answer_abbreviations_across_dates.add(abbreviations_builder_for_date.toString());
			abbreviations_builder_for_date.setLength(0);
			}
			String comment = getComment(answer_weights_across_dates, choice_question);
			sb.append(prompt);
			for (String abbreviations_builder_for_date: answer_abbreviations_across_dates) {
				sb.append(abbreviations_builder_for_date);
			} sb.append(comment);
		}
	}
	
	private static int findWeight(char goal, ArrayList<Option> options) {
		return options.stream().filter(x -> x.abbreviation == goal).collect(Collectors.toList()).get(0).weight;
	}
	
	private static void buildFreeQuestionsView(StringBuilder sb, ArrayList<String> ids,	List<LocalDate> date_list) {
		for (String id: ids) {
			FreeQuestion free_question = (FreeQuestion)Main.questions.get(id);
			String prompt = free_question.prompt.substring(0, 11);
			ArrayList<ArrayList<String>> answers_across_dates= new ArrayList<>();
			for (LocalDate date: date_list) {
				answers_across_dates.add(Main.answers.get(date).get(id).answer);
			}
			sb.append(prompt);
			sb.append("\n");
			for (ArrayList<String> answers_for_date: answers_across_dates) {
				for (String answer: answers_for_date) {
					sb.append(String.join("", Collections.nCopies(10, " "))); // 10 spaces
					sb.append(answer);
					sb.append("\n");
				}
			}
		}
	}
	
	private static String getComment(List<Integer> answers, CriteriaQuestion question_object) {
		int days = answers.size();
		int critical_high_count = 0;
		int critical_low_count = 0;

		ArrayList<Integer> critical_high_streaks = new ArrayList<>();
		ArrayList<Integer> critical_low_streaks = new ArrayList<>();
		ArrayList<Integer> increase_streak = new ArrayList<>();
		ArrayList<Integer> decrease_streak = new ArrayList<>();
		
		countStreaks(answers, question_object, 
				critical_high_count, critical_low_count,
				critical_high_streaks, critical_low_streaks, increase_streak, decrease_streak);

		int critical_high_percentage = critical_high_count / days * 100;
		int critical_low_percentage = critical_low_count / days * 100;
		
		int duration = question_object.critical_duration;

		if (critical_high_percentage >= 80) {
			return "crit. high";
		} else if (critical_low_percentage >= 80) {
			return "crit. low";
		} else if (
				(critical_high_count > 0) && 
				(critical_low_count > 0) &&
				satisfiesDuration(duration, critical_high_streaks) &&
				satisfiesDuration(duration, critical_low_streaks) 
				) {
				return "crit. unstable";
		} else if (critical_high_count == 0 && critical_low_count == 0) {
			boolean increase = satisfiesDuration(duration, increase_streak);
			boolean decrease = satisfiesDuration(duration, decrease_streak);
			if (increase && decrease) {
				return "variable";
			} else if (increase && !decrease) {
				return "increase";
			} else {
				return "decrease";
			}
		}
		return "";
	}
	
	private static boolean satisfiesDuration(int critical_duration, ArrayList<Integer> streak_arr) {
		return streak_arr.stream().filter(d -> d >= critical_duration).findAny().isPresent();
	}
	private static void countStreaks(List<Integer> answers, CriteriaQuestion question_object, int critical_high_count, int critical_low_count, ArrayList<Integer> critical_high_streaks, ArrayList<Integer> critical_low_streaks, ArrayList<Integer> increase_streak, ArrayList<Integer> decrease_streak) {
		int last_num = 0;
		for (Integer num: answers) {
			if (num >= question_object.critical_high) {
				critical_high_count += 1;
				updateStreak(critical_high_streaks, critical_low_streaks);
			} else if (num <= question_object.critical_low) {
				critical_low_count += 1;
				updateStreak(critical_low_streaks, critical_high_streaks);
			} else {
				critical_high_streaks.add(0);
				critical_low_streaks.add(0);
			} 
			
			if (num - last_num >= question_object.critical_variance) {
				updateStreak(increase_streak, decrease_streak);
			} else if (last_num - num >= question_object.critical_variance) {
				updateStreak(decrease_streak, increase_streak);
			}
			last_num = num;
		}
	}
	private static void updateStreak(ArrayList<Integer> this_streak, ArrayList<Integer> opposing_streak) {
		int last_index = this_streak.size() - 1;
		int last_value = this_streak.get(last_index);
		this_streak.set(last_index, last_value + 1);
		opposing_streak.add(0);
	}
}
