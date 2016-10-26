package authentication;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import authentication.Utils.*;

@Path("/recover")
public class RecoverPassword {
	
    @GET
    @Path("/recoverpassword")
    @Produces(MediaType.APPLICATION_JSON)
    public String recover(@QueryParam("email") String email) {
    	
    	User recoverUser = new User("", "", email);
    	String JSONResponse = "";
    	DBResult returnedCode = recoverPassword(recoverUser);
    	
    	if (returnedCode == DBResult.EMAIL_SENT)
    		JSONResponse = Utils.constructJSON("recover", true, "");
    	else if (returnedCode == DBResult.USED_EMAIL)
    		JSONResponse = Utils.constructJSON("recover", false, "The email address was not found in the database");
    	else
    		JSONResponse = Utils.constructJSON("recover", false, "Error occured");
    	
    	return JSONResponse;
    }
    
    /**
     * 
     * @param user
     * @return
     */
    private DBResult recoverPassword(User user) {
    	
    	DBResult recoverCode = DBResult.INIT;
    	RandomString rstring = new RandomString(10);
    	String newPassword = rstring.nextString();
    	String cryptedPassword = MD5Encryption.cryptWithMD5(newPassword);
    	user.setPassword(cryptedPassword);
    	
    	if(user.getEmail() != null) {
    		try {
				recoverCode = DBConnection.databaseOperation(user, DBOperations.UPDATE_PASSWORD);
				if (recoverCode == DBResult.EMAIL_SENT) {
					String[] emailRecipient = { user.getEmail() };
					SendEmail.sendFromGMail(emailRecipient, newPassword);
				}
			} catch (Exception e) {
				recoverCode = DBResult.DB_ERROR;
				e.printStackTrace();
			}
    	}
    	else {
    		recoverCode = DBResult.FILL_FIELDS;
    	}
    	
    	return recoverCode;
    }

}
