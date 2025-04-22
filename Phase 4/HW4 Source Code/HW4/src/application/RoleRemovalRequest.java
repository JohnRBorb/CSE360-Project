package application;

import java.sql.Timestamp;

public class RoleRemovalRequest {
    private int id;
    private String username;
    private String reason;
    private Timestamp timestamp;

    public RoleRemovalRequest(int id, String username, String reason, Timestamp timestamp) {
        this.id = id;
        this.username = username;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getReason() {
        return reason;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
