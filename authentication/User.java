package authentication;

public class User {
	
	private String username;
	private String password;
	private String newPassword;
	private String email;
	private String dateCreated;
	public static String database = "userinfo";
	
	public User() {	}
	
	public User(String username, String password, String email) {
		this.setUsername(username);
		this.setPassword(password);
		this.setEmail(email);
	}
	
	/**
	 * check if user credentials are not null
	 * 
	 * @param user
	 * @return
	 */
	public static boolean userCheck(User user) {
		
		
        return (user.getUsername() != null && !user.getUsername().isEmpty()) &&
        		(user.getPassword() != null && !user.getPassword().isEmpty());
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return getUsername() + " " + getPassword() + " " + getEmail() + " " + getDateCreated();
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getDateCreated() {
		return dateCreated.substring(0, dateCreated.length() - 2);
	}

	public void setDateCreated(String date_created) {
		this.dateCreated = date_created;
	}
	
}
