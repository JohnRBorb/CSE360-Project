package application;

import java.sql.Timestamp;

public class Message {
    private int id;
    private String sender;
    private String receiver;
    private String content;
    private Timestamp timestamp;
    private boolean isRead;
    private boolean isAnnouncement; // New field to indicate if the message is an announcement

    // Constructor for new messages (id and timestamp auto-generated)
    public Message(String sender, String receiver, String content, boolean isAnnouncement) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.isAnnouncement = isAnnouncement;
    }

    // Constructor for messages loaded from the database
    public Message(int id, String sender, String receiver, String content, Timestamp timestamp, boolean isRead, boolean isAnnouncement) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.isAnnouncement = isAnnouncement;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getContent() { return content; }
    public Timestamp getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public boolean isAnnouncement() { return isAnnouncement; }
    
    public void setAnnouncement(boolean isAnnouncement) {
        this.isAnnouncement = isAnnouncement;
    }
}
