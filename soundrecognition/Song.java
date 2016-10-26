package soundrecognition;

public class Song {
	
	private String name;
	private String path;
	public static String database = "songs_info";
	
	public Song(String name, String path) {
		setName(name);
		setPath(path);
	}
	
	public Song(String name) {
		setName(name);
		setAllPath(name);
	}
	
	public Song() { }
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setAllPath(String path) {
		this.path = "E:/workspace/MusicMatcherApp/songs/" + path + ".wav";
	}
	
	public String toString() {
		return getName() + " " + getPath();
	}
	
}
