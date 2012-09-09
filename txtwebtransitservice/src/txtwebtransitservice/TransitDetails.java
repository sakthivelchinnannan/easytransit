package txtwebtransitservice;

public class TransitDetails {
	private int num_stops;
	private int headway;
	private String headsign;
	private ArrivalStop arrival_stop;
	private ArrivalTime arrival_time; 
	private DepartureStop departure_stop;
	private DepartureTime departure_time;
	private Line line;
	
	public int getNumStops(){
		return num_stops;
	}
	
	//Service running frequency in minutes
	
	public int getHeadwayInMins(){
		return (headway/60);
	}
	
	public int getHeadwayInSecs(){
		return headway;
	}
	
	//Towards direction of transit vehicle
	
	public String getHeadsign(){
		return headsign;
	}
	
	public String getArrivalStop(){
		return arrival_stop.getName();
	}
	
	public String getDepartureStop(){
		return departure_stop.getName();
	}
	
	//Bus details
	
	public Line getLine(){
		return line;
	}
	
	public String getArrivalTimeTxt(){
		return arrival_time.getText();
	}
	
	public String getDepartureTimeTxt(){
		return departure_time.getText();
	}
	
	public long getArrivalTimeVal(){
		return arrival_time.getValue();
	}
	
	public long getDepartureTimeVal(){
		return departure_time.getValue();
	}
	
}
