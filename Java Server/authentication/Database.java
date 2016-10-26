package authentication;

public class Database {
	
	private static String className = "com.mysql.jdbc.Driver";
    public static String dbName;
    private String url = "jdbc:mysql://localhost:3306/";
    private static String user = "root";
    private static String password = "sorin1994";
    
    public Database(String dbName) {
    	Database.dbName = dbName;
    	setUrl();
    }
    
    public String getClassName() {
    	return className;
    }
    
    public String getDBName() {
    	return dbName;
    }

    public String getUrl() {
    	return url;
    }
    
    public void setUrl() {
    	this.url += dbName;
    }
    
    public String getUser() {
    	return user;
    }
    
    public String getPassword() {
    	return password;
    }

}

