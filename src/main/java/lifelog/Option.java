package lifelog;

import static lifelog.Utils.print;

import java.util.*;

public class Option {
        public char abbreviation;
        public String full;
        public int weight;

        public Option(char abbreviation, String full, int weight) {
                this.abbreviation = abbreviation;
                this.full = full;
                this.weight = weight;
        }
        
        public void showOptionWithWeight() {
        	print(String.format("(%1$s) %2$s (weight: %3$s)", abbreviation, full, weight));
        }
        
        public void showOption() {
        	print(String.format("(%1$s) %2$s", abbreviation, full));
        }
        
    	public static String makeOptionsStringWithWeight(Collection<Option> options) {
    		StringBuilder sb = new StringBuilder();
    		for (Option option: options) {
    			sb.append(String.format("%1$s - %2$s (%3$s)\n", 
    					option.abbreviation, 
    					option.full, 
    					option.weight));
    		} return sb.toString();
    	}
    	public static String makeOptionsString(Collection<Option> options) {
    		StringBuilder sb = new StringBuilder();
    		for (Option option: options) {
    			sb.append(String.format("%1$s - %2$s\n", 
    					option.abbreviation, 
    					option.full
    					));
    		} return sb.toString();
    	}
}