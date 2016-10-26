package soundrecognition;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import soundrecognition.Database;
import soundrecognition.Utils.SongOperation;

public class DBConnection {
	
	public static Song songToCompare1, songToCompare2;
	
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
            Class.forName(Database.getClassName());
            connection = DriverManager.getConnection(database.getUrl(), Database.getUser(), Database.getPassword());
        } catch (Exception e) {
            throw e;
        } finally {
            return connection;
        }
    }
    
    /**
     * 
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public static Connection getConnection() throws SQLException, Exception {
    	
    	Connection dbConnection = null;
    	
    	try {
    		try {
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
    
    /**
     * 
     * @param dbConnection
     */
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
     * @param song1
     * @param song2
     * @param operation
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public static boolean databaseOperation(Song song1, Song song2, SongOperation operation) throws SQLException, Exception {
    	
    	Connection dbConnection = null;
    	boolean resultOperation = false;
    	
    	try {
    		dbConnection = getConnection();
    		Statement statement = dbConnection.createStatement();    		
    		resultOperation = createOperation(song1, song2, statement, operation);

    	} catch (SQLException sqle) {
    		sqle.printStackTrace();
    		throw sqle;
    	}
    	
    	closeConnection(dbConnection);
    	
    	return resultOperation;
    }
    
    /**
     * 
     * @param song1
     * @param song2
     * @param sqlStatement
     * @param operation
     * @return
     * @throws SQLException
     */
    public static boolean createOperation(Song song1, Song song2, Statement sqlStatement, SongOperation operation) throws SQLException {
    	
    	boolean resultOperation = false;
    	
    	if (operation == SongOperation.COMPARE) {
    		String query = "SELECT * FROM song WHERE name = '" + song1.getName() + "'";
    		ResultSet resultSetUser = sqlStatement.executeQuery(query);
    		while (resultSetUser.next()) {
    			songToCompare1 = new Song(song1.getName(), resultSetUser.getString(3));
    		}
    		
    		query = "SELECT * FROM song WHERE name = '" + song2.getName() + "'";
    		resultSetUser = sqlStatement.executeQuery(query);
    		while (resultSetUser.next()) {
    			songToCompare2 = new Song(song2.getName(), resultSetUser.getString(3));
    			resultOperation = true;
    		}
    	}
    	else if (operation == SongOperation.UPLOAD) {
    		String query = "SELECT * FROM song WHERE name = '" + song1.getName() + "'";
    		ResultSet resultSetUser = sqlStatement.executeQuery(query);
    		while (resultSetUser.next()) {
    			return false;
    		}
    		
    		query = "INSERT INTO song(name, path) values('"
					+ song1.getName() + "','" + song1.getPath() + "')";
			int recordInserted = sqlStatement.executeUpdate(query);
			if (recordInserted > 0)
				resultOperation = true;
    	}
    	
    	return resultOperation;
    }
}
