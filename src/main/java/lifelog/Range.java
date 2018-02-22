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

	@SuppressWarnings("unused")
	private boolean contains(int number) {
            return (number >= start && number < stop);
    }
}