package utils;

public enum Interval {
	ONEMIN ("oneMin"),
	FIVEMIN ("fiveMin"),
	THIRTYMIN("thirtyMin"),
	HOUR("hour"),
	DAY("day");
	
	private final String value;
	
	Interval(String value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
