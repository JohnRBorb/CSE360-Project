package application;

import java.sql.Timestamp;

// Represents a piece of feedback that an author (student) gives to a reviewer,
// including a star-rating and optional comment.
public class AuthorFeedback {
    private int id;
    private String reviewer;
    private String author;
    private int rating;
    private String comment;
    private Timestamp feedbackTime;

    // Constructor for loading from the database (all fields known).
    public AuthorFeedback(int id,
                          String reviewer,
                          String author,
                          int rating,
                          String comment,
                          Timestamp feedbackTime) {
        this.id           = id;
        this.reviewer     = reviewer;
        this.author       = author;
        this.rating       = rating;
        this.comment      = comment;
        this.feedbackTime = feedbackTime;
    }

    // Constructor for new feedback prior to insertion (id and timestamp auto-generated).
    public AuthorFeedback(String reviewer,
                          String author,
                          int rating,
                          String comment) {
        this.reviewer = reviewer;
        this.author   = author;
        this.rating   = rating;
        this.comment  = comment;
        // feedbackTime and id will be set by the database
    }

    // Getters
    public int getId() { return id; }
    public String getReviewer() { return reviewer; }
    public String getAuthor() { return author; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Timestamp getFeedbackTime() { return feedbackTime; }

    // Setters
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    public void setAuthor(String author) { this.author = author; }
    public void setRating(int rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setFeedbackTime(Timestamp feedbackTime) { this.feedbackTime = feedbackTime; }
}

