package lifelog;
import java.io.IOException;
import java.util.HashMap;
import javax.management.BadStringOperationException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.time.LocalDate;

public class Main {
		public static HashMap<String, Category> categories;
		public static HashMap<String, Topic> topics;
		public static HashMap<String, AbstractQuestion> questions;
		public static HashMap<LocalDate, HashMap<String, Answer>> answers;
	
		public static void main (String[] args) throws IOException, ParseException, BadStringOperationException {
				Tools.print("from main");
				loadState();
		}
	
		public static void loadState() throws IOException, ParseException, BadStringOperationException {
				loadQuestions();
				loadAnswers();
		}
		
		public static void loadQuestions() throws IOException, ParseException, BadStringOperationException {
				String json_string = QuestionsDecoder.getJSONString();
		        JSONObject json_object = QuestionsDecoder.stringToJSONObject(json_string);
		        categories = QuestionsDecoder.decodeCategory(json_object);
		        topics = QuestionsDecoder.decodeTopic(json_object);
		        questions = QuestionsDecoder.decodeQuestions(json_object);
		}
	
		public static void loadAnswers() throws IOException, ParseException, BadStringOperationException {
				String json_string = AnswersDecoder.getJSONString();
				JSONObject json_object = AnswersDecoder.stringToJSONObject(json_string);
				answers = AnswersDecoder.decodeAllAnswers(json_object);
				
				/*
				Tools.printTypeAndContent(answers, "answers");
				HashMap feb10 = answers.get(LocalDate.parse("2018-02-10"));
				Tools.printTypeAndContent(feb10, "feb 10 answers");
				Answer feb10q3 = (Answer) feb10.get("q0003");
				Tools.printTypeAndContent(feb10q3.answer, "q0003 answer");
				*/
		}
}

//		//some kind of while program_is_on loop
//		//if command -> do this function
//		//if exit command -> end program
//	}
///* Inserting a new answer
// * 1. insertion (Date, question_id, String[]) -> AbstractQuestion
// * 2. autosave: take the AbstractQuestion we just made, serialize it, and add it to the answers json
// */
//	/** Uses question_id as a key to all_questions to get the AbstractQuestion,
//	 * unrolls args and processes them,
//	 * calls AbstractQuestion.createAnswerInstance() to make a new answered question,
//	 * adds the fully answered question to the all_answers hashmap
//	 */
//	public void createAnswer(String question_id, Date date, String[] args) {
//		//unimplemented
//		//answer = new question_template.createAnswerInstance()
//	}
//
///* Adding a new question
// * 1. createNewQuestion literally all strings (question_id, class_type, other_params...) -> AbstractQuestion
// * 2. serialize the AbstractQuestion and add to the questions json
// */
//	/** Checks for the largest question_id in all_questions,
//	 * string-builds and returns the next logical question_id.
//	 */
//	public String makeNextQuestionId() {
//		//unimplemented
//		//this.all_questions
//	}
//
//	/** call constructor for a leaf Question object based on question_type,
//	 * use getNextQuestionId() to create the new Question object's id,
//	 * unroll args and processes them to fulfill required attributes for constructor,
//	 * adds the newly created Question object to all_questions hashmap
//	 */
//	public void createQuestion() {
//		//unimplemented
//		//this.all_questions
//	}
//}
