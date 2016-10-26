package authentication;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import authentication.Utils.*;

@Path("/login")
public class Login {
	
    @GET
    @Path("/loginuser")
    @Produces(MediaType.APPLICATION_JSON)
    public String login(@QueryParam("username") String username, @QueryParam("password") String password) {
    	
    	User loginUser = new User(username, password, "");
    	String JSONResponse = "";
    	DBResult returnedCode = loginUser(loginUser);
    	
    	if (returnedCode == DBResult.LOGIN) {
    		JSONResponse = Utils.constructJSON("login", true, "");
    	}
    	else JSONResponse = Utils.constructJSON("login", false, "Invalid username or password");
    	System.out.println(JSONResponse);
    	
    	return JSONResponse;
    }
    
    /**
     * 
     * @param user
     * @return
     */
    private DBResult loginUser(User user) {
    	
    	DBResult loginCode = DBResult.INIT;
    	
    	if(User.userCheck(user)) {
    		try {
				loginCode = DBConnection.databaseOperation(user, DBOperations.CHECK_LOGIN);
			} catch (Exception e) {
				loginCode = DBResult.DB_ERROR;
				e.printStackTrace();
			}
    	}
    	else {
    		loginCode = DBResult.FILL_FIELDS;
    	}
    	
    	return loginCode;
    }

}
