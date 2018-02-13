package lifelog;
import java.time.LocalDate;
import java.util.ArrayList;

public class Answer {
		String question_id;
		LocalDate date;
		ArrayList<String> answer;
		
		public Answer(String question_id, LocalDate date, ArrayList<String> answer) {
				this.question_id = question_id;
				this.date = date;
				this.answer = answer;
		}
}