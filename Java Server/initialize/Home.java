package initialize;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import authentication.User;

@Path("/home")
public class Home {
	
    @GET
    @Path("/userDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public String userDetails(@QueryParam("username") String username) {
    	
    	User user = new User(username, "", "");
    	String JSONResponse = "";
    	boolean result = getUserDetails(user);
    	
    	if (result == true) {
    		JSONResponse = Utils.constructUserDetailsJSON("home", true);
    	}
    	else 
    		JSONResponse = Utils.constructJSONWithErrors("home", false, "Something happened");
    	
    	return JSONResponse;
    }
    
    /**
     * 
     * @param user
     * @return
     */
    private boolean getUserDetails(User user) {
    	
    	boolean result = false;
    	
    	try {
    		result = DBConnection.databaseOperation(user);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    @GET
    @Path("/songs")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSongs() {
    	
    	String JSONResponse = "";
    	boolean result = getSongList();
    	
    	if (result == true) {
    		JSONResponse = Utils.constructSongsJSON("home", true);
    	}
    	else 
    		JSONResponse = Utils.constructJSONWithErrors("home", false, "Something happened");
    	
    	return JSONResponse;
    }
    
    /**
     * 
     * @return
     */
    private boolean getSongList() {
    	
    	boolean result = false;
    	
    	try {
    		result = DBConnection.databaseOperation(null);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
    	
		return result;
	}
    
    @GET
//    @Path("/homeplate")
    @Produces(MediaType.APPLICATION_JSON)
    public String home(@QueryParam("username") String username) {
    	
    	User user = new User(username, "", "");
    	String JSONResponse = "";
    	boolean result = goHome(user);
    	
    	if (result == true) {
    		JSONResponse = Utils.constructHomeJSON("home", true);
    	}
    	else 
    		JSONResponse = Utils.constructJSONWithErrors("home", false, "Something happened");
    	
    	System.out.println(JSONResponse);
    	
    	return JSONResponse;
    }
    
    /**
     * 
     * @param user
     * @return
     */
    private boolean goHome(User user) {
    	
    	boolean result = false;
    	
    	try {
    		result = DBConnection.databaseOperation(user);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
    	
    	return result;
    }
    
}
