package lifelog;
import org.json.simple.JSONObject;
import java.util.*;
import static lifelog.Tools.printTypeAndContent;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import javax.management.BadStringOperationException;

public class MockMain {
        public static void main(String[] args) throws IOException, ParseException, BadStringOperationException {
                String json_string = QuestionsDecoder.getJSONString();
                JSONObject json_object = QuestionsDecoder.stringToJSONObject(json_string);
                HashMap<String, Category> categories = QuestionsDecoder.decodeCategory(json_object);
                HashMap<String, Topic> topics = QuestionsDecoder.decodeTopic(json_object);
                HashMap<String, AbstractQuestion> questions = QuestionsDecoder.decodeQuestions(json_object);
                Tools.print("from mockmain");
                printTypeAndContent(categories, "categories");
                printTypeAndContent(topics, "topics");
                printTypeAndContent(questions, "questions");
        }
}
