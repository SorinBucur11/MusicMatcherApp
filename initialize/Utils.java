package initialize;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Utils {
	
	public enum DBType {
		USER, SONG
	}
	
	/**
	 * 
	 * @param action
	 * @param status
	 * @return
	 */
	public static String constructUserDetailsJSON (String action, boolean status) {
		
    	JSONObject jsonObject = new JSONObject();
    	JSONObject jsonObjectUserDetails = new JSONObject();

    	try {
        	jsonObject.put("action", action);
        	jsonObject.put("status", new Boolean(status));
        	jsonObjectUserDetails.put("username", DBConnection.user.getUsername());
        	jsonObjectUserDetails.put("email", DBConnection.user.getEmail());
        	jsonObjectUserDetails.put("dateCreated", DBConnection.user.getDateCreated());
        	jsonObject.put("user_details", jsonObjectUserDetails);
        	
        } catch (JSONException e) {
        	e.printStackTrace();
        }
		
		return jsonObject.toString();
	}
	
	/**
	 * 
	 * @param action
	 * @param status
	 * @return
	 */
	public static String constructSongsJSON (String action, boolean status) {
		
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();

    	try {
        	jsonObject.put("action", action);
        	jsonObject.put("status", new Boolean(status));
        	for (String song : DBConnection.songs) {
        		JSONObject jsonObjectSongs = new JSONObject();
        		jsonObjectSongs.put("name", song);
        		jsonArray.put(jsonObjectSongs);
        	}
        	
        } catch (JSONException e) {
        	e.printStackTrace();
        }
    	
		try {
			jsonObject.put("songs", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonObject.toString();
	}
	
	/**
	 * 
	 * @param action
	 * @param status
	 * @return
	 */
	public static String constructHomeJSON (String action, boolean status) {
		
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObjectUserDetails = new JSONObject();

    	try {
        	jsonObject.put("action", action);
        	jsonObject.put("status", new Boolean(status));
        	
        	jsonObjectUserDetails.put("username", DBConnection.user.getUsername());
        	jsonObjectUserDetails.put("email", DBConnection.user.getEmail());
        	jsonObjectUserDetails.put("dateCreated", DBConnection.user.getDateCreated());
        	
        	jsonObject.put("user_details", jsonObjectUserDetails);
        	for (String song : DBConnection.songs) {
        		JSONObject jsonObjectSongs = new JSONObject();
        		jsonObjectSongs.put("name", song);
        		jsonArray.put(jsonObjectSongs);
        	}
        	
        } catch (JSONException e) {
        	e.printStackTrace();
        }
    	
		try {
			jsonObject.put("songs", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonObject.toString();
	}
	
	public static String constructJSONWithErrors (String action, boolean status, String error_message) {
		
		JSONObject jsonObject = new JSONObject();
		
		try {
        	jsonObject.put("action", action);
        	jsonObject.put("status", new Boolean(status));
        	jsonObject.put("error_message", error_message);
        	
        } catch (JSONException e) {
        	e.printStackTrace();
        }

		return jsonObject.toString();
	}
	
	public static boolean deconstructJSON (JSONObject jsonObject) {
		
		boolean resultJSON = false;
		JSONObject jsonObjectUserDetails = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		try {
			jsonObjectUserDetails = jsonObject.getJSONObject("user_details");
			System.out.println("username: " + jsonObjectUserDetails.getString("username"));
			System.out.println("email: " + jsonObjectUserDetails.getString("email"));
			System.out.println("dateCreated: " + jsonObjectUserDetails.getString("dateCreated"));
			//TODO put info in shared pref
			jsonArray = jsonObject.getJSONArray("songs");
			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject jsonObjectSongs = jsonArray.getJSONObject(i);
				System.out.println("song: " + jsonObjectSongs.getString("name"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return resultJSON;
	}

}
