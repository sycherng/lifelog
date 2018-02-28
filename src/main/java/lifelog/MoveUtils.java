package lifelog;


import static lifelog.Utils.print;
import java.io.Console;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/** Static utilities associated with rearranging display sequence of survey items.
 */
public class MoveUtils {
	public static void move(Console c, String message) {
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
		if ((initial == 'c') && (Main.category_hierarchy.contains(id))) {
			return Main.category_hierarchy;
		} else if (initial == 't') {
			id_list = Main.topic_hierarchy;
		} else if (initial == 'q') {
			id_list = Main.question_hierarchy;
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
		return null;
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
}
