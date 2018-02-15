package lifelog;

public abstract class CriteriaQuestion extends AbstractQuestion {
        public int critical_low;
        public int critical_high;
        public int critical_variance;
        public int critical_duration;

        public CriteriaQuestion(String id, int ordinal, String prompt, String topic_id, String type, int critical_low, int critical_high, int critical_variance, int critical_duration, String ordinal_signature) {
                super(id, ordinal, prompt, topic_id, type, ordinal_signature);
                this.critical_low = critical_low;
                this.critical_high = critical_high;
                this.critical_variance = critical_variance;
                this.critical_duration = critical_duration;
        }
}
