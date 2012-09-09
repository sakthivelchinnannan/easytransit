package txtwebtransitservice;

public class Line {
	private String name;
	private String short_name;
	private Agency[] agencies;
	private Vehicle vehicle;
	
	public String getName(){
		return name;
	}
	
	public String getShortName(){
		return short_name;
	}
	
	public Agency[] getAgencies(){
		return agencies;
	}
	
	public Vehicle getVehicle(){
		return vehicle;
	}
}
