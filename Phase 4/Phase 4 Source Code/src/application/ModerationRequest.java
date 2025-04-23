package application;

import java.sql.Timestamp;

/**
 * Represents a single moderation request (flagged content) 
 * made by an instructor or staff member.
 */
public class ModerationRequest {
    private int id;
    private String contentType;
    private int contentId;
    private String instructor;
    private String reason;
    private String status;
    private Timestamp requestTime;
    private Timestamp closedTime;
    private Integer parentId;

    public ModerationRequest(int id, 
    		String contentType,
            int contentId,
            String instructor,
            String reason,
            String status,
            Timestamp requestTime,
            Timestamp closedTime,
            Integer parentId) {
        this.id          = id;
        this.contentType = contentType;
        this.contentId   = contentId;
        this.instructor  = instructor;
        this.reason      = reason;
        this.status      = status;
        this.requestTime = requestTime;
        this.closedTime  = closedTime;
        this.parentId    = parentId;
    }

    // Getters
    public int getId()                      { return id; }
    public String getContentType()          { return contentType; }
    public int getContentId()               { return contentId; }
    public String getInstructor()           { return instructor; }
    public String getReason()               { return reason; }
    public String getStatus()               { return status; }
    public Timestamp getRequestTime()       { return requestTime; }
    public Timestamp getClosedTime()        { return closedTime; }
    public Integer getParentId()            { return parentId; }
    
}

