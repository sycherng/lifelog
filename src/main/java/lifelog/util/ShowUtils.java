package lifelog.util;
import lifelog.*;
import java.io.Console;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

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
		LocalDate specified_date = Utils.parseDate(message[1]);
		if (specified_date == null) {
			return;
		} for (String id: Arrays.copyOfRange(message, 2, message.length -1)) {
			if (Utils.idExists(id) == false) {
				System.out.printf(); //TODO
				continue;
			}
		} 
	}
}
