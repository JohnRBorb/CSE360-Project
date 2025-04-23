package application;

import java.sql.Timestamp;

public class Review {
    private int id;
    private int targetID;       // can be a question ID or answer ID
    private String targetType;  // "question" or "answer"
    private String reviewerName;
    private String reviewText;
    private Timestamp timestamp;
	private Integer clarityRating;
	private Integer thoroughnessRating;
	private Integer timelinessRating;

    // Constructor for new reviews (id and timestamp auto-generated)
    public Review(int targetID, String targetType, String reviewerName, String reviewText) {
        this.targetID = targetID;
        this.targetType = targetType;
        this.reviewerName = reviewerName;
        this.reviewText = reviewText;
    }

    // Constructor for reviews loaded from the database
    public Review(int id, int targetID, String targetType, String reviewerName, String reviewText, Timestamp timestamp) {
        this.id = id;
        this.targetID = targetID;
        this.targetType = targetType;
        this.reviewerName = reviewerName;
        this.reviewText = reviewText;
        this.timestamp = timestamp;
    }
    
    public Review(int id,
            int targetID,
            String targetType,
            String reviewerName,
            String reviewText,
            Timestamp timestamp,
            Integer thoroughnessRating,
            Integer clarityRating,
            Integer timelinessRating) {
    	this(id, targetID, targetType, reviewerName, reviewText, timestamp);
    	this.thoroughnessRating = thoroughnessRating;
    	this.clarityRating       = clarityRating;
    	this.timelinessRating    = timelinessRating;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getTargetID() {
        return targetID;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
    
    public Integer getThoroughnessRating() {
        return thoroughnessRating;
    }
    public void setThoroughnessRating(Integer thoroughnessRating) {
        this.thoroughnessRating = thoroughnessRating;
    }

    public Integer getClarityRating() {
        return clarityRating;
    }
    public void setClarityRating(Integer clarityRating) {
        this.clarityRating = clarityRating;
    }

    public Integer getTimelinessRating() {
        return timelinessRating;
    }
    public void setTimelinessRating(Integer timelinessRating) {
        this.timelinessRating = timelinessRating;
    }
}
