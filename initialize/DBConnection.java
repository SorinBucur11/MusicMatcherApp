package initialize;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import authentication.User;
import initialize.Utils.DBType;
import soundrecognition.Song;

public class DBConnection {
	
	public static User user;
	public static ArrayList<String> songs;
	
	/**
     * Method to create DB Connection
     * 
     * @return
     * @throws Exception
     */
    @SuppressWarnings("finally")
    public static Connection createConnection(String dbName) throws Exception {
    	
    	authentication.Database database = new authentication.Database(dbName);
    	
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
     * @param dbType
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public static Connection getConnection(DBType dbType) throws SQLException, Exception {
    	
    	Connection dbConnection = null;
    	
    	try {
    		try {
    			if (dbType == DBType.USER)
    				dbConnection = DBConnection.createConnection(User.database);
    			else if (dbType == DBType.SONG)
    				dbConnection = DBConnection.createConnection(Song.database);
    	
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} catch (Exception e) {
    		if (dbConnection != null)
    			dbConnection.close();
    		e.printStackTrace();
    		throw e;
    	} finally {
    		
    	}
    	
    	return dbConnection;
    }
    
    public static void closeConnection(Connection dbConnection) 
    {
    	if (dbConnection != null)
			try {
				dbConnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    /**
     * 
     * @param user
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public static boolean databaseOperation(User username) throws SQLException, Exception {
    	
    	Connection dbConnectionUser = null;
    	Connection dbConnectionSong = null;
    	boolean resultOperation = false;
    	
    	try {
    		dbConnectionUser = getConnection(DBType.USER);
    		dbConnectionSong = getConnection(DBType.SONG);
    		
    		Statement statementUser = dbConnectionUser.createStatement();
    		Statement statementSong = dbConnectionSong.createStatement();
    		
    		resultOperation = createOperation(username, statementUser, statementSong);

    		
    	} catch (SQLException sqle) {
    		sqle.printStackTrace();
    		throw sqle;
    	}
    	
    	closeConnection(dbConnectionUser);
    	closeConnection(dbConnectionSong);
    	
    	return resultOperation;
    }
    
    /**
     * 
     * @param user
     * @param sqlStatementUser
     * @param sqlStatementSong
     * @return
     * @throws SQLException
     */
    public static boolean createOperation(User username, Statement sqlStatementUser, Statement sqlStatementSong) throws SQLException {
    	
    	boolean resultOperation = false;
    	
    	user = new User();
        user.setUsername(username.getUsername());
        
        String query = "SELECT * FROM user WHERE username = '" + user.getUsername() + "'";
		ResultSet resultSetUser = sqlStatementUser.executeQuery(query);
		while (resultSetUser.next()) {
			user.setEmail(resultSetUser.getString(4));
			user.setDateCreated(resultSetUser.getString(5));
		}
		
    	songs = new ArrayList<String>();
    	query = "SELECT * FROM song";
		ResultSet resultSetSong = sqlStatementSong.executeQuery(query);
		while (resultSetSong.next()) {
			songs.add(resultSetSong.getString(2));
			resultOperation = true;
		}
    	
    	return resultOperation;
    }
}
