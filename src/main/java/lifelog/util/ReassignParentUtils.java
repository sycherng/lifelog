package lifelog.util;
import lifelog.*;

import java.io.Console;
import java.util.*;

public class ReassignParentUtils {

	public static void reassignParentDialogue(Console c, String message) {
		String target_id = message.split(" ")[2];
		HashMap<String, LinkedList<String>> target_hierarchy_map = Utils.findHierarchyMap(c, target_id);
		if (target_hierarchy_map == null) {
			System.out.System.out.printlnln("Invalid id supplied.");
			return;
		} String parent_id = Utils.findParentId(target_id);
		if (parent_id == null) {
			System.out.System.out.printlnln("Invalid id supplied.");
			return;
		}
		System.out.printf("%1$s currently belongs under %2$s.\nProvide the id of its new parent, or respond with \"(e)\" to exit parent reassigning mode.", 
				target_id,
				parent_id
				);
		while (true) {
			String response = c.readLine();
			if (response.equals("e")) {
				System.out.println("Exiting parent reassigning mode...");
				return;
			} else if ((!(Utils.idExists(response))) || (response.charAt(0) != parent_id.charAt(0))) {
				System.out.println("Invalid parent id supplied.");
			} else {
				String new_parent_id = response;
				reassignParent(target_id, parent_id, new_parent_id, target_hierarchy_map);
				return;		
			}
		} 	
	}

	public static void reassignParent(String child_to_move, String parent_id, String new_parent_id, HashMap<String, LinkedList<String>> hierarchy_map) {
		/* pluck child from current ll
		 * add child to end of parent ll
		 */
		if (parent_id.equals(new_parent_id)) {
			System.out.printf("%1$s already belongs to parent %2$s. Nothing to do.\nExiting parent reassigning mode...",
				child_to_move,
				parent_id);
			return;
		} else {
			hierarchy_map.get(parent_id).remove(child_to_move);
			if (hierarchy_map.containsKey(new_parent_id)) {
				hierarchy_map.get(new_parent_id).add(child_to_move);
			} else {
				hierarchy_map.put(new_parent_id, new LinkedList<String>(Collections.singleton(child_to_move)));
			}
			// change the object's Topic.category_id or Question.topic_id
			if ((child_to_move.startsWith("t") && (Main.topics.containsKey(child_to_move)))) {
				Main.topics.get(child_to_move).category_id = new_parent_id;
			} else if ((child_to_move.startsWith("q") && (Main.questions.containsKey(child_to_move)))) {
				Main.questions.get(child_to_move).topic_id = new_parent_id;
			}
		} System.out.printf("%1$s moved from parent %2$s to %3$s",
			child_to_move,
			parent_id,
			new_parent_id
			);
	}
}
