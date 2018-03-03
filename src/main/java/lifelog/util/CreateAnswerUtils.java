package lifelog.util;
import lifelog.*;
import lifelog.domain.*;
import java.io.Console;
import java.time.LocalDate;
import java.util.*;
import java.lang.String;


//public static HashMap<LocalDate, HashMap<String, Answer>> Main.answers;

public class CreateAnswerUtils {	
	public static void log(Console c) {
		boolean first_question = true;
		LocalDate today = LocalDate.now();
		HashMap<String, Answer> log_for_today;

		if (Main.answers.containsKey(today) == false) {
			log_for_today = new HashMap<String, Answer>();
			Main.answers.put(today, log_for_today);
		}
		log_for_today = Main.answers.get(today);		
		if (Main.questions.keySet().equals(log_for_today.keySet())) {
			System.out.println("Everything has been answered today! Nothing to do. Exiting log mode...");
			return;
		}
		//look in order of everything in hierarchy lists
		//if no answer yet, ask the question, make the answer, attach to Main.answers
		for (String category_id: Main.category_hierarchy) {
			String category_prompt = Main.categories.get(category_id).prompt;
			for (String topic_id: Main.topic_hierarchy.get(category_id)) {
				String topic_prompt = Main.topics.get(topic_id).prompt;
				for (String question_id: Main.question_hierarchy.get(topic_id)) {
					if (log_for_today.containsKey(question_id) == false) {
						if (first_question) {
							System.out.println("\nStarting log mode...\nRespond \"fin\" at any time to quit, \"skip\" to skip.\n\n");
							first_question = false;
						}
						AbstractQuestion question = Main.questions.get(question_id);
						Answer answer_object = createAnswer(c, question, topic_prompt, category_prompt);
						if (answer_object == null) {
							System.out.println("Exiting log mode...");
							return;
						} else if (answer_object.answer.contains("skip")) {
							System.out.printf("Skipping \"%1$s\"...", question.prompt);
						} else {
							log_for_today.put(question_id, answer_object);
						}
					}
				}
			}
		}
	}
	
	private static Answer createAnswer(Console c, AbstractQuestion question, String topic_prompt, String category_prompt) {
		System.out.printf("\n%1$s > %2$s\n", category_prompt, topic_prompt);
		if (question.type == "Free") {
			return createFreeAnswer(c, question);
		} else if (question.type == "Scale") {
			return createScaleAnswer(c, question);
		} else if (question.type == "Choice") {
			return createChoiceAnswer(c, question);
		}
		return null;
	}

	private static Answer createFreeAnswer(Console c, AbstractQuestion question) {
		ArrayList<String> answers = new ArrayList<>();
		FreeQuestion free_question = (FreeQuestion)question;
		StringBuilder sb = new StringBuilder();
		sb.append(free_question.prompt);
		int num_answers_left = free_question.num_of_answers;
		if (num_answers_left > 1) {
			sb.append(String.format(" (%1$s answers)", num_answers_left));
		} System.out.println(sb.toString());
		while (true) {
			Answer answer_object;
			String response;
			if (num_answers_left == 0 || answers.contains("skip")) {
				answer_object = new Answer(answers);
				return answer_object;
			}
			response = c.readLine();
			if (response.equals("fin")) {
				return null;
			} else if (response.equals("skip")) {
				answers.add("skip");
			} else {
				answers.add(response);
				num_answers_left -= 1;
			}
		}
	}

	private static Answer createScaleAnswer(Console c, AbstractQuestion question) {
		ArrayList<String> answers = new ArrayList<>();
		ScaleQuestion scale_question = (ScaleQuestion)question;
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%1$s (%2$s-%3$s)", 
				scale_question.prompt,
				scale_question.range.start, 
				scale_question.range.stop));
		if (scale_question.legend != null) {
			sb.append(String.format("\n%1$s", scale_question.legend));
		}
		System.out.println(sb.toString()); 
		while (true) {
			String response = c.readLine();
			
			if (response.equals("fin")) {
				return null;
			} else if (response.equals("skip")) {
				answers.add("skip");
				return new Answer(answers);
			} else {
				try {
					int answer = Integer.parseInt(response);
					if (scale_question.range.contains(answer)) {
						answers.add(Integer.toString(answer));
						return new Answer(answers);
					} 
				} catch(NumberFormatException e) {
					System.out.println("Please submit an integer.");
				}
			}
		}
	}

	private static Answer createChoiceAnswer(Console c, AbstractQuestion question) {
		ArrayList<String> answers = new ArrayList<>();
		ChoiceQuestion choice_question = (ChoiceQuestion)question;
		System.out.printf("%1$s\n%2$s",
				choice_question.prompt,
				Option.makeOptionsString(choice_question.options)
				);
		
		String response = c.readLine();
		if (response.equals("fin")) {
			return null;
		} else if (response.equals("skip")) {
			answers.add("skip");
			return new Answer(answers);
		} else {
			char[] response_list = response.toCharArray();
			for (char r: response_list) {
				for (Option option: choice_question.options) {
					if (option.abbreviation == r) {
						answers.add(Character.toString(r));
					}
				}
			}
		} if (answers.isEmpty()) {
			answers.add("x");
		} return new Answer(answers);
	}
}
