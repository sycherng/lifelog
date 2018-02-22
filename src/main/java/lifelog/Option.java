package lifelog;

public class Option {
        public char abbreviation;
        public String full;
        public int weight;

        public Option(char abbreviation, String full, int weight) {
                this.abbreviation = abbreviation;
                this.full = full;
                this.weight = weight;
        }
}