package txtwebtransitservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.*;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.Gson;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TxtwebtransitserviceServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		processRequest(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		processRequest(req, resp);
	}

	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) {
		String txtWebResponse = null;
		String txtWebMessage = request.getParameter("txtweb-message");

		if (txtWebMessage == null || txtWebMessage.isEmpty()) {
			txtWebResponse = getWelcomeMessage();
			sendResponse(response, txtWebResponse);
			return;
		}

		String[] parameters = txtWebMessage.split(",");
		
		int numofparams = parameters.length; 

		if (numofparams < 2 || numofparams > 3) {
			txtWebResponse = getSpecifiedMessage("Wrong Parameters!");
			sendResponse(response, txtWebResponse);
			return;
		}
		
		String source = parameters[0].trim();
		String dest = parameters[1].trim();
		
		if (parameters.length == 2) {
			
			Calendar calendar =
			Calendar.getInstance(TimeZone.getTimeZone("IST"));
			long depTimeInMillis = calendar.getTimeInMillis();
			
			String timeParam = String.valueOf(depTimeInMillis/1000);
			
			String usrParams = "Transit details for Departing from " + source + " to " + dest + " now<br>\n\n";
			
			DirectionAPIResponse apiResponse = requestDirectionsAPI(source, dest, "departure_time", timeParam);

			if(apiResponse != null)
				txtWebResponse = parseJsonRes(apiResponse);
			else {
				txtWebResponse = "Failed to get response from Google Maps!";
				sendResponse(response, formatTWResponse(txtWebResponse));
				return;
			}

			if (!txtWebResponse.isEmpty()) {
				sendResponse(response, formatTWResponse(usrParams + txtWebResponse));
				return;
			}

		} else {
			String timeParam = parameters[2].trim();
			String timeType = null;
			String usrParams = "";
			if (timeParam.indexOf("a:") == 0) {
				timeType = "arrival_time";
				timeParam = timeParam.replace("a:", "");
				usrParams = "Transit details for Arriving in " + dest + " from " + source + " at ";
			} else if (timeParam.indexOf("d:") == 0) {
				timeType = "departure_time";
				timeParam = timeParam.replace("d:", "");
				usrParams = "Transit details for Departing from " + source + " to " + dest + " at ";
			} else {
				timeType = "departure_time";
			}
			timeParam = timeParam.trim();
			usrParams += timeParam + "<br>\n\n";
			
			if (timeParam.indexOf('-') != -1) {
				// timeParam is of format "d-M-yy (h|h.mm)a" i guess!
				SimpleDateFormat format = null;
				timeParam = timeParam.replaceAll(" +", " ");
				String ptrnToChckMinFldPresence = "(^\\d{1,2}-\\d{1,2}-(\\d{2}|\\d{4})) (\\d{1,2}\\.\\d{1,2}(am|pm))";
				if (timeParam.matches(ptrnToChckMinFldPresence)) {
					format = new SimpleDateFormat("d-M-yy h.mma");
					format.setTimeZone(TimeZone.getTimeZone("IST"));
				} else if(timeParam.matches("(^\\d{1,2}-\\d{1,2}-(\\d{2}|\\d{4})) (\\d{1,2}(am|pm))")) {
					timeParam = timeParam.substring(0, timeParam.indexOf('m')-1) + ".00" + timeParam.substring(timeParam.indexOf('m')-1, timeParam.length());
					format = new SimpleDateFormat("d-M-yy h.mma");
					format.setTimeZone(TimeZone.getTimeZone("IST"));
				} else {
					txtWebResponse = getSpecifiedMessage("Time parameter is wrong! Please specify it as shown in usage Eg.");
					sendResponse(response, txtWebResponse);
					return;
				}
				try {
					Date userDttm = format.parse(timeParam);
					String time = String.valueOf((userDttm.getTime())/1000);
										
					/*Calendar calendar = Calendar.getInstance(TimeZone
							.getTimeZone("IST"));
					long currTimeInSecs = (calendar.getTimeInMillis())/1000;
					if (userDttmInSecs < currTimeInSecs) {
						
					}*/ //Let the API service do the checking for now!
					
					DirectionAPIResponse apiResponse = requestDirectionsAPI(source, dest, timeType, time);

					if(apiResponse != null)
						txtWebResponse = parseJsonRes(apiResponse);
					else {
						txtWebResponse = "Failed to get response from Google Maps!";
						sendResponse(response, formatTWResponse(txtWebResponse));
						return;
					}
					
					// txtWebResponse += userDttm.toString() + " " + time;
					if (!txtWebResponse.isEmpty()) {
						sendResponse(response, formatTWResponse(usrParams + txtWebResponse));
						return;
					}
					
				} catch (ParseException e) {
					txtWebResponse += e.toString();
				}
			}
			
		}
	
		sendResponse(response, getSpecifiedMessage(txtWebResponse + "<br>\nSomething went wrong!"));
		return;
	}

	private void sendResponse(HttpServletResponse response, String smsResponse) {
		try {
			PrintWriter out = response.getWriter();
			out.println(smsResponse);
		} catch (IOException e) {

		}
	}
	
	private DirectionAPIResponse requestDirectionsAPI(String source, String dest, String timeType, String time){
		DirectionAPIResponse apiResponse = null;
		try {
			String url = "http://maps.googleapis.com/maps/api/directions/json";
			String charset = "UTF-8";

			String query = String
					.format("origin=%s&destination=%s&%s=%s&sensor=false&mode=transit&alternatives=true&region=in",
							URLEncoder.encode(source, charset),
							URLEncoder.encode(dest, charset), 
							URLEncoder.encode(timeType, charset),
							URLEncoder.encode(time, charset));

			InputStream input = new URL(url + '?' + query).openStream();

			apiResponse = new Gson().fromJson(
					new InputStreamReader(input, "UTF-8"),
					DirectionAPIResponse.class);
		} catch (Exception e) {
		}
		return apiResponse;
	}
	
	//TODO: Have to display copyrights and warnings!
	private String parseJsonRes(DirectionAPIResponse apiResponse){
		String txtWebResponse = "";
		if (apiResponse.getStatus().equals("OK")) {
			
			Route[] rt = apiResponse.getRoutes();

			for (int i = 0; i < rt.length; i++) {
				txtWebResponse = txtWebResponse + "(" + (i + 1)
						+ ")";
				Leg[] lg = rt[i].getLegs();

				for (int j = 0; j < lg.length; j++) {
					Step[] st = lg[j].getSteps();

					for (int k = 0; k < st.length; k++) {
						
						if (st[k].getTravelmode().equals("TRANSIT")) {
							TransitDetails td = st[k]
									.getTransitDetails();
							Line ln = td.getLine();
							
							if(ln.getVehicle().getVehicleType().equals("BUS")){
								txtWebResponse = txtWebResponse + " Get " /*
										+ ln.getName() + " "*/ 
										+ ln.getShortName() + " "
										+ ln.getVehicle().getVehicleName()
										+ "[twrds " + td.getHeadsign() + "] "
										+ " in " + td.getDepartureStop()
										+ " at (apprx)" + td.getDepartureTimeTxt()
										+ ". Get off at " + td.getArrivalStop()
										+ "[After " + (td.getNumStops()-1)
										+ " stop(s) "+ st[k].getDuration() + " apprx]. ";
							}else if(ln.getVehicle().getVehicleType().equals("HEAVY_RAIL")){
								
								//Works for Chennai
								
								txtWebResponse = txtWebResponse + " Get "
										+ ln.getName() + " "
										+ ln.getVehicle().getVehicleName()
										+ "[twrds " + td.getHeadsign() + "] "
										+ " in " + td.getDepartureStop()
										+ " at (apprx)" + td.getDepartureTimeTxt()
										+ ". Get off at " + td.getArrivalStop()
										+ "[After " + (td.getNumStops()-1)
										+ " stop(s) "+ st[k].getDuration() + " apprx]. ";
							}
						} else {
							txtWebResponse = txtWebResponse
									+ st[k].getHtmlInstruction() + "[" + st[k].getDuration() + " apprx].";
						}
					}
				}
			}
		} else if (apiResponse.getStatus().equals("NOT_FOUND")) {
			txtWebResponse = "One or both of the locations could not be identified!"
					+ "<br>\nReply using @easytransit source,destination[,time] to try again<br>\ntime format: (d/a):d-m-yy h.mm(am/pm) <br>\nEg: @easytransit guindy chennai, tambaram, d:8-10-12 8.15am<br>\n"
					+ "This gives transit details for \"departing from the source\" at the given time.<br>\nReplace 'd' in the time with 'a' to get results for \"arriving at the destintn\" at specifd time.<br>\n"
					+ "Avoid the time to get results for \"departing from the source now\".<br>\n(Powered by Intuit TxtWeb and Google Directions API)</body></html>";
		} else if (apiResponse.getStatus().equals("ZERO_RESULTS")) {
			txtWebResponse = "No route found for this search!"
					+ "<br>\nReply using @easytransit source,destination[,time] to try again<br>\ntime format: (d/a):d-m-yy h.mm(am/pm) <br>\nEg: @easytransit guindy chennai, tambaram, d:8-10-12 8.15am<br>\n"
					+ "This gives transit details for \"departing from the source\" at the given time.<br>\nReplace 'd' in the time with 'a' to get results for \"arriving at the destintn\" at specifd time.<br>\n"
					+ "Avoid the time to get results for \"departing from the source now\".<br>\n(Powered by Intuit TxtWeb and Google Directions API)</body></html>";
		} else if (apiResponse.getStatus().equals("OVER_QUERY_LIMIT")) {
			txtWebResponse = "My application exceeded the Directions service's query limit (625 requests for 'TRANSIT' travel mode) for this day!"
					+ "<br>\nReply using @easytransit source,destination[,time] to try later(May be tmrw)<br>\ntime format: (d/a):d-m-yy h.mm(am/pm) <br>\nEg: @easytransit guindy chennai, tambaram, d:8-10-12 8.15am<br>\n"
					+ "This gives transit details for \"departing from the source\" at the given time.<br>\nReplace 'd' in the time with 'a' to get results for \"arriving at the destintn\" at specifd time.<br>\n"
					+ "Avoid the time to get results for \"departing from the source now\".<br>\n(Powered by Intuit TxtWeb and Google Directions API)</body></html>";
		} else if (apiResponse.getStatus().equals("REQUEST_DENIED")) {
			txtWebResponse = "Access denied by the Directions service!"
					+ "<br>\nReply using @easytransit source,destination[,time] to try again<br>\ntime format: (d/a):d-m-yy h.mm(am/pm) <br>\nEg: @easytransit guindy chennai, tambaram, d:8-10-12 8.15am<br>\n"
					+ "This gives transit details for \"departing from the source\" at the given time.<br>\nReplace 'd' in the time with 'a' to get results for \"arriving at the destintn\" at specifd time.<br>\n"
					+ "Avoid the time to get results for \"departing from the source now\".<br>\n(Powered by Intuit TxtWeb and Google Directions API)</body></html>";
		}
	
		return txtWebResponse;
	}
	  
	private String getWelcomeMessage() {
		return "<html><head><meta name=\"txtweb-appkey\" content=\"f5848a5f-038b-491b-bbc7-3efa9fa5c867\" /></head><body>"
				+ "Reply using @easytransit source,destination[,time]<br>\ntime format: (d/a):d-m-yy h.mm(am/pm) <br>\nEg: @easytransit guindy chennai, tambaram, d:8-10-12 8.15am<br>\n"
				+ "This gives transit details for \"departing from the source\" at the given time.<br>\nReplace 'd' in the time with 'a' to get results for \"arriving at the destintn\" at specifd time.<br>\n"
				+ "Avoid the time to get results for \"departing from the source now\".<br>\n(Powered by Intuit TxtWeb and Google Directions API)</body></html>";
	}
	  
	private String getSpecifiedMessage(String arg) {
		return "<html><head><meta name=\"txtweb-appkey\" content=\"f5848a5f-038b-491b-bbc7-3efa9fa5c867\" /></head><body>" 
				+ arg + "<br>\nReply using @easytransit source,destination[,time] to try again<br>\ntime format: (d/a):d-m-yy h.mm(am/pm) <br>\nEg: @easytransit guindy chennai, tambaram, d:8-10-12 8.15am<br>\n"
				+ "This gives transit details for \"departing from the source\" at the given time.<br>\nReplace 'd' in the time with 'a' to get results for \"arriving at the destintn\" at specifd time.<br>\n"
				+ "Avoid the time to get results for \"departing from the source now\".<br>\n(Powered by Intuit TxtWeb and Google Directions API)</body></html>";
	}
	
	private String formatTWResponse(String tWRes) {
		return "<html><head><meta name=\"txtweb-appkey\" content=\"f5848a5f-038b-491b-bbc7-3efa9fa5c867\" /></head><body>"
				+ tWRes + "<br>\n(Powered by Intuit TxtWeb and Google Directions API)</body></html>";
	}
}