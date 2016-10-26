package initialize;

import authentication.User;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		User username = new User();
		username.setUsername("sosososik");
		boolean result = false;
		String JSONResponse = "";
    	
		try {
    		result = DBConnection.databaseOperation(username);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
    	
    	if (result == true)
    		JSONResponse = Utils.constructHomeJSON("home", true);

		System.out.println(JSONResponse);
	}

}
