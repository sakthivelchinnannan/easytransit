package txtwebtransitservice;

public class Step {
	private String html_instructions;
	private String travel_mode;
	private Distance distance;
	private Duration duration;
	private TransitDetails transit_details;
	
	public String getHtmlInstruction(){
		return html_instructions;
	}
	
	public String getTravelmode(){
		return travel_mode;
	}
	
	public String getDistance(){
		return distance.getText();
	}
	
	public String getDuration(){
		return duration.getText();
	}
	
	public TransitDetails getTransitDetails(){
		return transit_details;
	}
}
