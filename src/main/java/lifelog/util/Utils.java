package lifelog.util;
import lifelog.*;

import java.io.Console;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class Utils {
        /** For debugging. Given an Object, print the var name passed in, its type and contents.
         */
        public static void printTypeAndContent(Object obj, String name) {
            System.out.printf("INSPECTING %1$s | TYPE %2$s | CONTENTS %3$s", 
            		name, 
            		obj.getClass().getName(),
            		obj.toString()
            		);
        }
        
        /** For debugging. Given an Object, print the var name passed in, its type and contents.
         */
        public static void printTypeAndContent(Object obj) {
            System.out.printf("INSPECTING ... | TYPE %1$s | CONTENTS %2$s", 
            		obj.getClass().
            		getName(), 
            		obj.toString()
            		);
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
		if (idExists(child_id) == false) {
			return null;
		}
    		if (child_id.startsWith("t")) {
    			return Main.topics.get(child_id).category_id;
    		} else if (child_id.startsWith("q")) {
    			return Main.questions.get(child_id).topic_id;
    		}
			return null;
    	}
    	
    	public static LinkedList<String> findChildIds(String parent_id) {
		if (idExists(parent_id) == false) {
			return null;
		}
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
    		} Integer new_number = Integer.parseInt(max_id.substring(1)) + 1;
    		int length = (int)(Math.log10(new_number) + 1);
		System.out.printf("%1$s, %2$s, %3$s", new_number, length, 5 - length);
    		if (length > 4) {
    			System.out.println("Maximum number of this element reached.");
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
    				System.out.println("Please enter an integer.");
    			}
    		}
    	}
    	
    	public static LocalDate parseDate(String date_string) {
    		try {
    			LocalDate date = LocalDate.parse(date_string);
    			return date;
    		} catch(DateTimeParseException e) {
    			System.out.printf("\"%1$s\" is not a valid date.\n", date_string);
    			return null;
    		}
    	}
}
