package lifelog.domain;

public abstract class AbstractQuestion extends SurveyItem implements Question {
    public String id;
    public int ordinal;
    public String prompt;
    public String topic_id;
    public String type;

    public AbstractQuestion(String id, int ordinal, String prompt, String topic_id, String type) {
	    this.id = id;
	    this.ordinal = ordinal;
	    this.prompt = prompt;
	    this.topic_id = topic_id;
	    this.type = type;
    }
}       
