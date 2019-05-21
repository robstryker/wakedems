package net.oxbeef.wake.voter.model.precinct;

public class SubdivisionStreet {
	private String name;
	private int min = Integer.MIN_VALUE;
	private int max = Integer.MAX_VALUE;
	private int type;
	public static final int TYPE_ODD = 0;
	public static final int TYPE_EVEN = 1;
	public static final int TYPE_ALL = 2;
	
	
	
	public SubdivisionStreet(String name) {
		this(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	public SubdivisionStreet(String name, int type) {
		this(name, Integer.MIN_VALUE, Integer.MAX_VALUE, type);
	}
	public SubdivisionStreet(String name, int min, int max) {
		this(name, min, max, TYPE_ALL);
	}
	public SubdivisionStreet(String name, int min, int max, int type) {
		this.name = name;
		this.min = min;
		this.max = max;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public int getMin() {
		return min;
	}
	public int getMax() {
		return max;
	}
	public int getType() {
		return type;
	}
}
