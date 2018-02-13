package tools;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import lifelog.Tools;

import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.io.FileReader;
import java.util.*;


public class MockQuestionsDecoder {

	public static void main(String[] args) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		FileReader json_file = null;
		String json_string = "";

		try {
			json_file = new FileReader("mock_questions.json");

			int c;
			while ((c = json_file.read()) != -1) {
				char chara = (char) c;
				json_string += chara;
			}
		} finally {
			if (json_file != null) {
				json_file.close();
			}
		}

		if (json_string != "") {
			//Use JSONParser.parse() to convert JSON String to JSONObject
			JSONObject json_object = (JSONObject)parser.parse(json_string);
			extractValue(json_object, "q0002");
		}
	}

	/** Given a HashMap and a desired key, try to find a corresponding Entry in this HashMap and
	 * return its value.
	 */
	@SuppressWarnings("unchecked")
	public static <T> JSONObject extractValue(JSONObject json_object, String target_key) {
		Iterator<?> it = json_object.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, T> entry = (Map.Entry<String, T>)it.next();
			if (entry.getKey().equals(target_key)) {
				Tools.printTypeAndContent(entry.getKey(), "entry key");
				Tools.printTypeAndContent(entry.getValue(), "entry value");
				return (JSONObject)entry.getValue();
			}
			it.remove(); //avoids a ConcurrentModificationExeption
		}
		System.out.println("Failed to extract entry, returned null.");
		return null;
	}	

	/** Takes a string and prints it. Simplifies the command for QoL while debugging. */
	public static void print(String str) {
		System.out.println(str);
	}
}
