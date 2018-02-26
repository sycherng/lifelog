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
		print("Delete which id? Enter id or (f) to delete.");
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
			print("Onvalid id provided");
		}
		
	}

	private static void deleteCategory(String id_to_delete) { //TODO redo based on deleteTopic()
		/* cannot delete ccccc, ttttt unless empty
		 * To delete a category
		 * make a new category ccccc "Unassigned" if not exists (Main.categories AND Main.category_hierarchy)
		 * dump all content topics in there, reassign all content Topics' category_id to ccccc
		 * remove category from Main.categories and category_hierarchy
		 * 
		 */
		if (id_to_delete.equals(CATEGORY_UNASSIGNED_ID) && !(Main.category_hierarchy.isEmpty())) {
			print("Cannot delete category \"Unassigned Topics\". It is automatically deleted when it becomes empty.");
		} else {
			Category unassigned_category = new Category(CATEGORY_UNASSIGNED_ID, UNASSIGNED_ORDINAL, CATEGORY_UNASSIGNED_NAME);
			Main.categories.put(unassigned_category.id, unassigned_category);
			Main.categories.remove(id_to_delete);
			LinkedList<String> unassigned_topic_ids = Main.topic_hierarchy.get(id_to_delete);
			Main.topic_hierarchy.remove(id_to_delete);
			Main.topic_hierarchy.put(unassigned_category.id, unassigned_topic_ids);
			for (String topic_id: unassigned_topic_ids) {
				Main.topics.get(topic_id).category_id = unassigned_category.id;
			}
		}
	}

	private static void deleteTopic(String id_to_delete) { //DONE
		/* ttttt may only be deleted if it is empty
		 * if TopicUnassigned doesnt exist yet, create it
		 * update main.topics, main.topics_hierarchy, main.questions_hierarchy
		 * update all child Question's topic_id to unassigned topic id
		 * Delete ccccc if now empty
		 */
		if (id_to_delete.equals(TOPIC_UNASSIGNED_ID) && !(Utils.idExists(CATEGORY_UNASSIGNED_ID))) {
			print(String.format("%1$s auto-deletes when empty", TOPIC_UNASSIGNED_NAME));
		} else {
			if (Utils.idExists(TOPIC_UNASSIGNED_ID) == false) {
				spawnTopicUnassigned();
			} String category_id = Main.topics.get(id_to_delete).category_id;
			Main.topics.remove(id_to_delete);
			Main.topic_hierarchy.remove(category_id);
			LinkedList<String> question_ids = Main.question_hierarchy.get(id_to_delete);
			Main.question_hierarchy.remove(id_to_delete);
			Main.question_hierarchy.get(TOPIC_UNASSIGNED_ID).addAll(question_ids);
			for (String question_id: question_ids) {
				Main.questions.get(question_id).topic_id = TOPIC_UNASSIGNED_ID;
			} print(String.format("%1$s deleted.", id_to_delete));
			
			if ((Utils.idExists(CATEGORY_UNASSIGNED_ID)) && (Utils.findChildIds(CATEGORY_UNASSIGNED_ID) == null)) {
				deleteCategory(CATEGORY_UNASSIGNED_ID);
			}
		}		
	}

	private static void deleteQuestion(String id_to_delete) { //TODO
		// if parent is ttttt and this makes its parent empty, delete ttttt
		
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
