package soundrecognition;

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
    
    public String getUrl() {
    	return url;
    }
    
    public void setUrl() {
    	this.url += dbName;
    }

	public static String getClassName() {
		return className;
	}

	public static void setClassName(String className) {
		Database.className = className;
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		Database.user = user;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		Database.password = password;
	}
}
