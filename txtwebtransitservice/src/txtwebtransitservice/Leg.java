package txtwebtransitservice;

public class Leg {
	private String start_address;
	private String end_address;
	private Distance distance;
	private Duration duration;
	private Step[] steps;

	public String getStartAddress() {
		return start_address;
	}

	public String getEndAddress() {
		return end_address;
	}

	public Step[] getSteps() {
		return steps;
	}

	public String getDistance() {
		return distance.getText();
	}

	public String getDuration() {
		return duration.getText();
	}
}
