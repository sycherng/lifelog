package lifelog;

import java.io.Console;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class Utils {
        /** Given an Object, print the var name passed in, its type and contents.
         */
        public static void printTypeAndContent(Object obj, String name) {
                print(String.format("INSPECTING %1$s | TYPE %2$s | CONTENTS %3$s", name, obj.getClass().getName(),obj.toString()));
        }

        /** Takes a string and prints it. Simplifies the command for QoL while debugging. */
        public static void print(String str) {
                System.out.println(str);
        }
        

    	public static boolean idExists(String id) {
    		if (id.length() != 5) {
    			return false;
    		} else if (id.startsWith("c")) {
    			return Main.categories.containsKey(id);
    		} else if (id.startsWith("t")) {
    			return Main.topics.containsKey(id);
    		} else if (id.startsWith("q")) {
    			return Main.questions.containsKey(id);
    		}
			return false;
    	}
    	
    	public static boolean isParent(String parent_id, String child_id) {
    		if (child_id.startsWith("c")) {
    			return false;
    		} else if (child_id.startsWith("t")) {
    			return parent_id.equals(Main.topics.get(child_id).category_id);
    		} else if (child_id.startsWith("q")) {
    			return parent_id.equals(Main.questions.get(child_id).topic_id);
    		} else {
    			return false;
    		}
    	}
    	
    	public static boolean isChild(String child_id, String parent_id) {
    		return isParent(parent_id, child_id);
    	}
    	
    	public static String findParentId(String child_id) {
    		if (child_id.startsWith("t")) {
    			return Main.topics.get(child_id).category_id;
    		} else if (child_id.startsWith("q")) {
    			return Main.questions.get(child_id).topic_id;
    		}
			return null;
    	}
    	
    	public static LinkedList<String> findChildIds(String parent_id) {
    		if (parent_id.startsWith("c")) {
    			return Main.topic_hierarchy.get(parent_id);
    		} else if (parent_id.startsWith("t")) {
    			return Main.question_hierarchy.get(parent_id);
    		} else {
    			return null;
    		}
    	}
    	
    	public static HashMap<String, LinkedList<String>> findHierarchyMap(Console c, String target) {
    		if (target.startsWith("t")) {
    			return Main.topic_hierarchy;
    		} else if (target.startsWith("q")) {
    			return Main.question_hierarchy;
    		} else {
    			return null;
    		}
    	}
    	
    	/** @return appropriate id for a new SurveyItem
    	 * @param collection of linked lists containing all ids of that SurveyItem type
    	 */
    	public static String getNextId(Collection<LinkedList<String>> ll_collection) {
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
    	
    	public static int getIntResponse(Console c) {
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
}
