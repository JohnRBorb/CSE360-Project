package application;

public class Comment {
	private int qID;
	private String userName;
	private String content;
	
	// Constructor to initialize a comment with a user name, title, and body
	public Comment(int qID, String userName, String content) {
		this.qID = qID;
		this.userName = userName;
		this.content = content;
	}
	
	// Functions to return qID, user name, or content
	public int getQID() {return qID;}
	public String getUserName() {return userName;}
	public String getContent() {return content;}
}
