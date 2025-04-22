package application;

public class User {
    private String userName;
    private String password;
    private String role;
    private boolean reviewerRequested = false;

    public User(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean hasRequestedReviewerRole() {
        return reviewerRequested;
    }

    public void setReviewerRequested(boolean requested) {
        this.reviewerRequested = requested;
    }
}
