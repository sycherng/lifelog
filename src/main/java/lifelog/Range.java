package lifelog;

public class Range{
    		public int start;
    		public int stop;
			public int step;

    public Range(int start, int stop, int step) {
            this.start = start;
            this.stop = stop;
            this.step = step;
    }

	public boolean contains(int number) {
            return (number >= start && number < stop);
    }

	public String showRange() {
		return String.format("%1$s ~ %2$s (%3$s increments)", 
				this.start, 
				this.stop,
				this.step);
	}
}