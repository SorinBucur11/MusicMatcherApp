package authentication;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import authentication.Utils.*;

@Path("/changepassword")
public class ChangePassword {
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String change(@QueryParam("username") String username, @QueryParam("password") String password, @QueryParam("newpassword") String newpassword) {
    	
    	User changeUser = new User(username, password, "");
    	changeUser.setNewPassword(newpassword);
    	String JSONResponse = "";
    	DBResult returnedCode = changePassword(changeUser);
    	
    	if (returnedCode == DBResult.PASSWORD_CHANGED)
    		JSONResponse = Utils.constructJSON("change", true, "");
    	else if (returnedCode == DBResult.LOGIN)
    		JSONResponse = Utils.constructJSON("change", false, "Invalid password");
    	else JSONResponse = Utils.constructJSON("change", false, "Error occured");
    	
    	return JSONResponse;
    }
    
    /**
     * 
     * @param user
     * @return
     */
    private DBResult changePassword(User user) {
    	
    	DBResult changeCode = DBResult.INIT;
    	
    	if(User.userCheck(user)) {
    		try {
				changeCode = DBConnection.databaseOperation(user, DBOperations.CHANGE_PASSWORD);
			} catch (Exception e) {
				changeCode = DBResult.DB_ERROR;
				e.printStackTrace();
			}
    	}
    	else {
    		changeCode = DBResult.FILL_FIELDS;
    	}
    	
    	return changeCode;
    }

}
