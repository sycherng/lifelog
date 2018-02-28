package lifelog;

import static lifelog.Utils.print;

import java.io.Console;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class DeleteUtils {
	//for ease of later modification
	public static final String CATEGORY_UNASSIGNED_NAME = "Unassigned Topics";
	public static final String TOPIC_UNASSIGNED_NAME = "Unassigned Questions";		
	public static final String CATEGORY_UNASSIGNED_ID = "ccccc"; 
	public static final String TOPIC_UNASSIGNED_ID = "ttttt";
	public static final int UNASSIGNED_ORDINAL = 99999;
	
	public static void deleteDialogue(Console c) {
		print("Delete which id? Enter id or (f) to exit.");
		ShowUtils.showAll();
		while (true) {
			String response = c.readLine();
			if (response.equals("f")) {
				print("Exiting delete mode...");
				return;
			} else {
				deleteIdDialogue(response);
			}
		}
	}

	/* unvalidated id of any type
	 */
	public static void deleteIdDialogue(String id) {
		if (Utils.idExists(id)) {
			if (id.startsWith("c")) {
				deleteCategory(id);
			} else if (id.startsWith("t")) {
				deleteTopic(id);
			} else if (id.startsWith("q")) {
				deleteQuestion(id);
			} 
		} else {
			print("Invalid id provided.");
		}	
	}
	
	private static void deleteCategory(String id_to_delete) {
		/* ccccc may only be deleted if it is empty
		 * if CategoryUnassigned doesnt exist yet, create it
		 * update main.categories, main.category_hierarchy, main.topics_hierarchy
		 * update all child Topics' category_id to category_unassigned_id
		 */
		if (id_to_delete.equals(CATEGORY_UNASSIGNED_ID) && !(Main.category_hierarchy.isEmpty())) {
			print(String.format("%1$s auto-deletes when empty.", CATEGORY_UNASSIGNED_NAME));
		} else {
			if (Utils.idExists(CATEGORY_UNASSIGNED_ID) == false) {
				spawnCategoryUnassigned();
			}
			Main.categories.remove(id_to_delete);
			Main.category_hierarchy.remove(id_to_delete);
			LinkedList<String> topic_ids = Main.topic_hierarchy.get(id_to_delete);
			Main.topic_hierarchy.remove(id_to_delete);
			if (topic_ids != null) {
				Main.topic_hierarchy.get(CATEGORY_UNASSIGNED_ID).addAll(topic_ids);
				for (String topic_id: topic_ids) {
					Main.topics.get(topic_id).category_id = CATEGORY_UNASSIGNED_ID;
				}
			 } print(String.format("%1$s deleted.", id_to_delete));
		}
	}

	private static void deleteTopic(String id_to_delete) {
		/* ttttt may only be deleted if it is empty
		 * if TopicUnassigned doesnt exist yet, create it
		 * update main.topics, main.topics_hierarchy, main.questions_hierarchy
		 * update all child Questions' topic_id to topic_unassigned_id
		 * Delete ccccc if now empty
		 */
		if (id_to_delete.equals(TOPIC_UNASSIGNED_ID) && !(Utils.idExists(CATEGORY_UNASSIGNED_ID))) {
			print(String.format("%1$s auto-deletes when empty.", TOPIC_UNASSIGNED_NAME));
		} else {
			if (Utils.idExists(TOPIC_UNASSIGNED_ID) == false) {
				spawnTopicUnassigned();
			} 
			String category_id = Main.topics.get(id_to_delete).category_id;
			Main.topics.remove(id_to_delete);
			Main.topic_hierarchy.get(category_id).remove(id_to_delete);

			LinkedList<String> question_ids = Main.question_hierarchy.get(id_to_delete);
			Main.question_hierarchy.remove(id_to_delete);
			
			if (question_ids != null) {
				Main.question_hierarchy.get(TOPIC_UNASSIGNED_ID).addAll(question_ids);
				for (String question_id: question_ids) {
					Main.questions.get(question_id).topic_id = TOPIC_UNASSIGNED_ID;
				}
			}
			 print(String.format("%1$s deleted.", id_to_delete));
			
			if ((Utils.idExists(CATEGORY_UNASSIGNED_ID)) && (Utils.findChildIds(CATEGORY_UNASSIGNED_ID) == null)) {
				deleteCategory(CATEGORY_UNASSIGNED_ID);
			}		
		}
	}

	private static void deleteQuestion(String id_to_delete) {
		/* update main.questions, main.questions_hierarchy
		 * Delete ttttt if now empty
		 */
		String topic_id = Main.questions.get(id_to_delete).topic_id;
		Main.questions.remove(id_to_delete);
		Main.question_hierarchy.get(topic_id).remove(id_to_delete);
		
		print(String.format("%1$s deleted.", id_to_delete));
		
		if ((Utils.idExists(TOPIC_UNASSIGNED_ID)) && (Utils.findChildIds(TOPIC_UNASSIGNED_ID) == null)) {
			deleteCategory(TOPIC_UNASSIGNED_ID);
		}		
	}
	
	private static void spawnCategoryUnassigned() {
		/* make the category
		 * update main.categories, main.category_hierarchy, main.topic_hierarchy
		 */
		Category category_unassigned = new Category(CATEGORY_UNASSIGNED_ID, UNASSIGNED_ORDINAL, CATEGORY_UNASSIGNED_NAME);
		Main.categories.put(category_unassigned.id, category_unassigned);
		Main.category_hierarchy.add(category_unassigned.id);
		Main.topic_hierarchy.put(category_unassigned.id, new LinkedList<String>());
	}
	
	private static void spawnTopicUnassigned() {
		/* if the category doesnt exist yet, make the category
		 * make the new topic
		 * update main.topics, main.topic_hierarchy, main.question_hierarchy
		 */
		if (Main.categories.containsKey(CATEGORY_UNASSIGNED_ID) == false) {
			spawnCategoryUnassigned();
		} 
		Topic topic_unassigned = new Topic(TOPIC_UNASSIGNED_ID, UNASSIGNED_ORDINAL, TOPIC_UNASSIGNED_NAME, CATEGORY_UNASSIGNED_ID);
		Main.topics.put(topic_unassigned.id, topic_unassigned);
		Main.topic_hierarchy.get(topic_unassigned.category_id).add(topic_unassigned.id);
		Main.question_hierarchy.put(topic_unassigned.id, new LinkedList<String>());
	}
}
