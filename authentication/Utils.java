package authentication;

import org.codehaus.jettison.json.*;

public class Utils {
	
	public enum DBOperations {
		REGISTER_USER, CHECK_LOGIN, CHECK_USER, CHECK_EMAIL, UPDATE_PASSWORD, CHANGE_PASSWORD
	}
	
	public enum DBResult {
		REGISTERED, USED_USERNAME, ALREADY_REGISTERED, SPECIAL_CHARACTERS, DB_ERROR, 
		LOGIN, FILL_FIELDS, USED_EMAIL, EMAIL_SENT, INIT, PASSWORD_CHANGED
	}
	
	/**
     * 
     * @param action
     * @param status
     * @param errorMessage
     * @return
     */
    public static String constructJSON (String action, boolean status, String errorMessage) {
        
    	JSONObject jsonObject = new JSONObject();
        
        try {
        	jsonObject.put("action", action);
        	jsonObject.put("status", new Boolean(status));
        	jsonObject.put("error_message", errorMessage);
        } catch (JSONException e) {
        	e.printStackTrace();
        }
        
        return jsonObject.toString();
    }

}
