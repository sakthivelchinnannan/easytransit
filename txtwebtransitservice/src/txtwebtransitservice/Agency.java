package txtwebtransitservice;

public class Agency {
	private String name;
	private String phone;
	private String url;
	
	public String getAgencyDetails(){
		return "DataFrom:"+name+"PhoneNo:"+phone+"Url:"+url;
	} 
}
