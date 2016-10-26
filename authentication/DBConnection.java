package authentication;

import java.sql.*;

import authentication.Utils.DBOperations;
import authentication.Utils.DBResult;

public class DBConnection {

	/**
     * Method to create DB Connection
     * 
     * @return
     * @throws Exception
     */
    @SuppressWarnings("finally")
    public static Connection createConnection(String dbName) throws Exception {
    	
    	Database database = new Database(dbName);
    	
        Connection connection = null;
        try {
            Class.forName(database.getClassName());
            connection = DriverManager.getConnection(database.getUrl(), database.getUser(), database.getPassword());
        } catch (Exception e) {
            throw e;
        } finally {
            return connection;
        }
    }
    
    /**
     * 
     * @param user
     * @param operation
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public static DBResult databaseOperation(User user, DBOperations operation) throws SQLException, Exception {
    	
    	DBResult operationStatus = DBResult.INIT;
    	Connection dbConnection = null;
    	
    	try {
    		try {
    			dbConnection = DBConnection.createConnection(User.database);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		Statement statement = dbConnection.createStatement();
    		operationStatus = createOperation(user, statement, operation);
    		
    	} catch (SQLException sqle) {
    		sqle.printStackTrace();
    		throw sqle;
    	} catch (Exception e) {
    		if (dbConnection != null)
    			dbConnection.close();
    		e.printStackTrace();
    		throw e;
    	} finally {
    		if (dbConnection != null)
    			dbConnection.close();
    	}
    	
    	return operationStatus;
    }
    
    /**
     * 
     * @param user
     * @param sqlStatement
     * @param operation
     * @return
     * @throws SQLException
     */
    public static DBResult createOperation(User user, Statement sqlStatement, DBOperations operation) throws SQLException {
    	
    	DBResult operationStatus = DBResult.INIT;
    	String query;
    	
    	switch (operation) {
		case REGISTER_USER:
			
			if (createOperation(user, sqlStatement, DBOperations.CHECK_EMAIL) == DBResult.USED_EMAIL)
				operationStatus = DBResult.ALREADY_REGISTERED;
			else {
				if (createOperation(user, sqlStatement, DBOperations.CHECK_USER) == DBResult.USED_USERNAME)
					operationStatus = DBResult.USED_USERNAME;
				else {
					query = "INSERT INTO user(username, password, email) values('"
							+ user.getUsername() + "','" + user.getPassword() + "','"
							+ user.getEmail() + "')";
					int recordInserted = sqlStatement.executeUpdate(query);
					if (recordInserted > 0)
						operationStatus = DBResult.REGISTERED;
				}
			}
			
			break;
			
		case CHECK_LOGIN:
			query = "SELECT * FROM user WHERE username = '" + user.getUsername() 
							+ "' AND password='" + user.getPassword() + "'";
			ResultSet resultSetLogin = sqlStatement.executeQuery(query);
			while (resultSetLogin.next()) {
				operationStatus = DBResult.LOGIN;
			}
			break;
			
		case CHECK_USER:
			query = "SELECT * FROM user WHERE username = '" + user.getUsername() + "'";
			ResultSet resultSetUser = sqlStatement.executeQuery(query);
			while (resultSetUser.next()) {	
				operationStatus = DBResult.USED_USERNAME;
			}
			break;
			
		case CHECK_EMAIL:
			query = "SELECT * FROM user WHERE email = '" + user.getEmail() + "'";
			ResultSet resultSetEmail = sqlStatement.executeQuery(query);
			while (resultSetEmail.next()) {	
				operationStatus = DBResult.USED_EMAIL;
			}
			break;
		
		case UPDATE_PASSWORD:
			if (createOperation(user, sqlStatement, DBOperations.CHECK_EMAIL) == DBResult.USED_EMAIL) {
				query = "UPDATE user SET password='" + user.getPassword() + "' WHERE email='"
						+ user.getEmail() + "'";
				int recordUpdated = sqlStatement.executeUpdate(query);
				if (recordUpdated > 0)
					operationStatus = DBResult.EMAIL_SENT;
			}
			else
				operationStatus = DBResult.USED_EMAIL;
			break;
			
		case CHANGE_PASSWORD:
			if (createOperation(user, sqlStatement, DBOperations.CHECK_LOGIN) == DBResult.LOGIN) {
				query = "UPDATE user SET password='" + user.getNewPassword() + "' WHERE username='"
						+ user.getUsername() + "'";
				int recordUpdated = sqlStatement.executeUpdate(query);
				if (recordUpdated > 0)
					operationStatus = DBResult.PASSWORD_CHANGED;
			}
			else
				operationStatus = DBResult.LOGIN;
			break;

		default:
			System.out.println("Not a registered operation");
			break;
		}
    	
    	return operationStatus;
    }
	
}
