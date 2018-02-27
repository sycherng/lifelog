package lifelog;

import static lifelog.Utils.print;
import java.io.Console;
import java.time.LocalDate;
import java.util.*;
import java.lang.String;


//public static HashMap<LocalDate, HashMap<String, Answer>> Main.answers;

public class CreateAnswerUtils {
	public static void log(Console c) {
		print("Starting log mode...\n Respond \"f\" at any time to quit, \"s\" to skip.");
		LocalDate today = LocalDate.now();
		HashMap<String, Answer> log_for_today = Main.answers.get(today);
		//look in order of everything in hierarchy lists
		//if no answer yet, ask the question, make the answer, attach to Main.answers
		for (String category_id: Main.category_hierarchy) {
			String category_prompt = Main.categories.get(category_id).prompt;
			for (String topic_id: Main.topic_hierarchy.get(category_id)) {
				String topic_prompt = Main.topics.get(topic_id).prompt;
				for (String question_id: Main.question_hierarchy.get(topic_id)) {
					if (log_for_today.containsKey(question_id) == false) {
						AbstractQuestion question = Main.questions.get(question_id);
						Answer answer_object = createAnswer(c, question, topic_prompt, category_prompt);
						if (answer_object == null) {
							print("Exiting log mode...");
							return;
						} else if (answer_object.answer.contains("skip")) {
							print (String.format("Skipping %1$s...", question.prompt));
						} else {
							log_for_today.put(question_id, answer_object);
						}
					}
				}
			}
		}
	}
	
	private static Answer createAnswer(Console c, AbstractQuestion question, String topic_prompt, String category_prompt) {
		print(String.format("%1$s > %2$s", category_prompt, topic_prompt));
		if (question.type == "free") {
			return createFreeQuestion(c, question);
		} else if (question.type == "scale") {
			return createScaleQuestion(c, question);
		} else if (question.type == "choice") {
			return createChoiceQuestion(c, question);
		}
		return null;
	}

	private static Answer createFreeQuestion(Console c, AbstractQuestion question) {
		ArrayList<String> answers = new ArrayList<>();
		FreeQuestion free_question = (FreeQuestion)question;
		StringBuilder sb = new StringBuilder();
		sb.append(free_question.prompt);
		int num_answers_left = free_question.num_of_answers;
		if (num_answers_left > 1) {
			sb.append(String.format("(%1$s answers)", num_answers_left));
		} print(sb.toString());
		while (true) {
			Answer answer_object;
			String response;
			if (num_answers_left == 0 || answers.contains("skip")) {
				answer_object = new Answer(answers);
				return answer_object;
			}
			response = c.readLine();
			if (response.equals("f")) {
				return null;
			} else if (response.equals("s")) {
				answers.add("skip");
			} else {
				answers.add(response);
				num_answers_left -= 1;
			}
		}
	}

	private static Answer createScaleQuestion(Console c, AbstractQuestion question) {
		ArrayList<String> answers = new ArrayList<>();
		ScaleQuestion scale_question = (ScaleQuestion)question;
		print(String.format("%1$s (%2$s-%3$s)\n%4$s", 
				scale_question.prompt, 
				scale_question.range.start, 
				scale_question.range.stop,
				scale_question.legend
				));
		 
		while (true) {
			String response = c.readLine();
			
			if (response.equals("f")) {
				return null;
			} else if (response.equals("s")) {
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
					print("Please submit an integer.");
				}
			}
		}
	}

	private static Answer createChoiceQuestion(Console c, AbstractQuestion question) {
		ArrayList<String> answers = new ArrayList<>();
		ChoiceQuestion choice_question = (ChoiceQuestion)question;
		print(String.format("%1%s\n%2$s",
				choice_question.prompt,
				Option.makeOptionsString(choice_question.options)
				));
		
		String response = c.readLine();
		if (response.equals("f")) {
			return null;
		} else if (response.equals("s")) {
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

	//TODO "redo question" for the day option
}