package soundrecognition;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Utils {
	
	public enum SongOperation {
		UPLOAD, COMPARE, INIT
	}
	
	
	/**
	 * 
	 * @param status
	 * @param similarityNumber
	 * @param error_message
	 * @return
	 */
	public static String constructCompareJSON(boolean status, double similarityNumber, String error_message) {
		
		JSONObject jsonObject = new JSONObject();
    	
    	try {
			jsonObject.put("action", "compare");
			jsonObject.put("status", status);
			jsonObject.put("number", similarityNumber);
			jsonObject.put("error_message", error_message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return jsonObject.toString();
	}
	
	
	/**
	 * 
	 * @param status
	 * @param error_message
	 * @return
	 */
	public static String constructUploadJSON(boolean status, String error_message) {
		
		JSONObject jsonObject = new JSONObject();
		
		try {
			jsonObject.put("action", "upload");
			jsonObject.put("status", status);
			jsonObject.put("error_message", error_message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonObject.toString();
	}
	
}
