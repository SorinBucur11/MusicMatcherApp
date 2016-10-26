package authentication;

import java.sql.SQLException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import authentication.Utils.*;

@Path("/register")
public class Register {
	
    @GET
    @Path("/registeruser")
    @Produces(MediaType.APPLICATION_JSON)
    public String register(@QueryParam("username") String username, @QueryParam("password") String password, @QueryParam("email") String email) {
    	
    	User registerUser = new User(username, password, email);
    	String JSONResponse = "";
    	DBResult returnedCode = registerUser(registerUser);
    	
    	switch (returnedCode) {
		case REGISTERED:
			JSONResponse = Utils.constructJSON("register", true, "");
			break;
		case ALREADY_REGISTERED:
			JSONResponse = Utils.constructJSON("register", false, "You are already registered");			
			break;
		case USED_USERNAME:
			JSONResponse = Utils.constructJSON("register", false, "Username is already in use");			
			break;
		case SPECIAL_CHARACTERS:
			JSONResponse = Utils.constructJSON("register", false, "Invalid characters");			
			break;
		case FILL_FIELDS:
			JSONResponse = Utils.constructJSON("register", false, "Please don't leave any of the fields blank");			
			break;
		case DB_ERROR:
			JSONResponse = Utils.constructJSON("register", false, "Error Occured");		
			break;

		default:
			break;
		}
    	
    	return JSONResponse;
    }
    
    private DBResult registerUser(User user) {
    	
        DBResult registerCode = DBResult.INIT;
        
        if (User.userCheck(user)) {
            try {
            	DBResult operation = DBConnection.databaseOperation(user, DBOperations.REGISTER_USER);
                if (operation == DBResult.REGISTERED) {
                    registerCode = operation;
                }
                else if (operation == DBResult.ALREADY_REGISTERED) {
                	registerCode = operation;
                }
                else if (operation == DBResult.USED_USERNAME) {
                	registerCode = operation;
                }
            } catch (SQLException sqle){
                if (sqle.getErrorCode() == 1064) {
                    registerCode = DBResult.SPECIAL_CHARACTERS;
                }
            } catch (Exception e) {
                registerCode = DBResult.DB_ERROR;
            }
        } else {
            registerCode = DBResult.FILL_FIELDS;
        }
 
        return registerCode;
    }
}
