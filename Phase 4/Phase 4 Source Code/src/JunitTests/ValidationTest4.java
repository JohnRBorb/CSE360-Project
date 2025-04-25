package test;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationTest4 {


    private void validateReviewerProfile(String experience, int reviewCount, 
                                       boolean hasPhoto, String contactInfo, 
                                       String expertiseArea) {
        if (experience == null || experience.trim().isEmpty()) {
            throw new IllegalArgumentException("Experience section cannot be empty.");
        }
        if (reviewCount < 5) {
            throw new IllegalArgumentException("Reviewer must have at least 5 reviews.");
        }
        if (!hasPhoto) {
            throw new IllegalArgumentException("Profile must include a photo.");
        }
        if (contactInfo == null || contactInfo.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact information is required.");
        }
        if (expertiseArea == null || expertiseArea.trim().isEmpty()) {
            throw new IllegalArgumentException("Expertise area must be specified.");
        }
    }

    @Test
    public void testEmptyExperience() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateReviewerProfile("", 10, true, "email@example.com", "Java Programming");
        });
        assertTrue(ex.getMessage().contains("Experience"));
    }

    @Test
    public void testInsufficientReviews() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateReviewerProfile("5 years experience", 3, true, "email@example.com", "Java Programming");
        });
        assertTrue(ex.getMessage().contains("5 reviews"));
    }

    
    private void validateInstructorReview(String coachingNote, int mentorRating, 
                                        boolean actionRequired, boolean isPrivate) {
        if (coachingNote == null || coachingNote.trim().length() < 20) {
            throw new IllegalArgumentException("Coaching notes must be at least 20 characters.");
        }
        if (mentorRating < 1 || mentorRating > 5) {
            throw new IllegalArgumentException("Mentor rating must be between 1 and 5.");
        }
    }
    
    @Test
    public void testShortCoachingNote() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateInstructorReview("Too short", 4, true, false);
        });
        assertTrue(ex.getMessage().contains("20 characters"));
    }
    
    @Test
    public void testInvalidMentorRating() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateInstructorReview("This is a detailed coaching note for improvement", 6, true, false);
        });
        assertTrue(ex.getMessage().contains("between 1 and 5"));
    }
    
    private void validateScorecardParameters(double thoroughnessWeight, 
                                          double clarityWeight, 
                                          double timelinessWeight, 
                                          double feedbackWeight) {
        if (thoroughnessWeight < 0 || thoroughnessWeight > 100) {
            throw new IllegalArgumentException("Thoroughness weight must be between 0 and 100.");
        }
        if (clarityWeight < 0 || clarityWeight > 100) {
            throw new IllegalArgumentException("Clarity weight must be between 0 and 100.");
        }
        if (timelinessWeight < 0 || timelinessWeight > 100) {
            throw new IllegalArgumentException("Timeliness weight must be between 0 and 100.");
        }
        if (feedbackWeight < 0 || feedbackWeight > 100) {
            throw new IllegalArgumentException("Feedback weight must be between 0 and 100.");
        }
        
        double totalWeight = thoroughnessWeight + clarityWeight + timelinessWeight + feedbackWeight;
        if (Math.abs(totalWeight - 100.0) > 0.001) {
            throw new IllegalArgumentException("Weights must sum to 100.");
        }
    }
    
    @Test
    public void testInvalidThoroughnessWeight() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateScorecardParameters(110, 30, 30, 30);
        });
        assertTrue(ex.getMessage().contains("Thoroughness weight"));
    }
    
    @Test
    public void testWeightsNotSumTo100() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateScorecardParameters(20, 20, 20, 20);
        });
        assertTrue(ex.getMessage().contains("sum to 100"));
    }

    
    private void validateAdminRequest(String title, String description, 
                                    String priority, String category) {
        if (title == null || title.trim().length() < 10) {
            throw new IllegalArgumentException("Title must be at least 10 characters.");
        }
        if (description == null || description.trim().length() < 30) {
            throw new IllegalArgumentException("Description must be at least 30 characters.");
        }
        
        String priorityLower = priority.toLowerCase();
        if (!priorityLower.equals("low") && !priorityLower.equals("medium") && 
            !priorityLower.equals("high") && !priorityLower.equals("urgent")) {
            throw new IllegalArgumentException("Priority must be low, medium, high, or urgent.");
        }
        
        String categoryLower = category.toLowerCase();
        if (!categoryLower.equals("user management") && 
            !categoryLower.equals("system configuration") &&
            !categoryLower.equals("content moderation") && 
            !categoryLower.equals("role management")) {
            throw new IllegalArgumentException("Category must be valid.");
        }
    }
    
    @Test
    public void testShortTitle() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateAdminRequest("Short", "This is a detailed description of the request", 
                              "high", "user management");
        });
        assertTrue(ex.getMessage().contains("Title"));
    }
    
    @Test
    public void testInvalidPriority() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateAdminRequest("Add new user role capabilities", 
                              "This is a detailed description of the request", 
                              "critical", "user management");
        });
        assertTrue(ex.getMessage().contains("Priority"));
    }

    
    private void validateModerationAction(String action, String reason, 
                                        String violationType, boolean hasInstructorApproval) {
        if (action == null || (!action.equalsIgnoreCase("warn") && 
                              !action.equalsIgnoreCase("delete") && 
                              !action.equalsIgnoreCase("suspend") && 
                              !action.equalsIgnoreCase("no action"))) {
            throw new IllegalArgumentException("Action must be warn, delete, suspend, or no action.");
        }
        
        if (reason == null || reason.trim().length() < 20) {
            throw new IllegalArgumentException("Reason must be at least 20 characters.");
        }
        
        if (violationType == null || (!violationType.equalsIgnoreCase("inappropriate content") && 
                                    !violationType.equalsIgnoreCase("harassment") && 
                                    !violationType.equalsIgnoreCase("spam") && 
                                    !violationType.equalsIgnoreCase("none"))) {
            throw new IllegalArgumentException("Violation type must be valid.");
        }
        
        if (!hasInstructorApproval) {
            throw new IllegalArgumentException("Instructor approval is required.");
        }
    }
    
    @Test
    public void testInvalidAction() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateModerationAction("ban", "This content violates community guidelines", 
                                  "inappropriate content", true);
        });
        assertTrue(ex.getMessage().contains("Action must be"));
    }
    
    @Test
    public void testMissingInstructorApproval() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateModerationAction("delete", "This content violates community guidelines", 
                                  "inappropriate content", false);
        });
        assertTrue(ex.getMessage().contains("Instructor approval"));
    }

    
    private void validateFeedbackResponse(String response, boolean hasAcknowledgment, 
                                        String actionTaken, int responseHours) {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("Response cannot be empty.");
        }
        
        if (!hasAcknowledgment) {
            throw new IllegalArgumentException("Response must acknowledge feedback.");
        }
        
        if (actionTaken == null || actionTaken.trim().length() < 15) {
            throw new IllegalArgumentException("Action taken must be described (min 15 chars).");
        }
        
        if (responseHours > 24) {
            throw new IllegalArgumentException("Response time must be within 24 hours.");
        }
    }
    
    @Test
    public void testMissingAcknowledgment() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateFeedbackResponse("We will take action.", false, 
                                  "Content was removed", 12);
        });
        assertTrue(ex.getMessage().contains("acknowledge"));
    }
    
    @Test
    public void testLateResponse() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateFeedbackResponse("Thank you for your feedback. We will take action.", 
                                  true, "Content was removed", 36);
        });
        assertTrue(ex.getMessage().contains("24 hours"));
    }



    
    
    private void validateReopenRequest(int originalRequestId, String reopenReason, 
                                     String updatedDescription, String notifySetting) {
        if (originalRequestId <= 0) {
            throw new IllegalArgumentException("Original request ID is required.");
        }
        
        if (reopenReason == null || reopenReason.trim().length() < 30) {
            throw new IllegalArgumentException("Reopen reason must be at least 30 characters.");
        }
        
        if (updatedDescription == null || updatedDescription.trim().length() < 30) {
            throw new IllegalArgumentException("Updated description must be at least 30 characters.");
        }
        
        if (notifySetting == null || (!notifySetting.equalsIgnoreCase("admin") && 
                                    !notifySetting.equalsIgnoreCase("all") && 
                                    !notifySetting.equalsIgnoreCase("none"))) {
            throw new IllegalArgumentException("Notification setting must be admin, all, or none.");
        }
    }
    
    @Test
    public void testInvalidOriginalRequest() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateReopenRequest(0, 
                               "The issue was not fully resolved as expected", 
                               "Need additional changes to the user interface", 
                               "admin");
        });
        assertTrue(ex.getMessage().contains("ID is required"));
    }
    
    @Test
    public void testInvalidNotifySetting() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateReopenRequest(123, 
                               "The issue was not fully resolved as expected", 
                               "Need additional changes to the user interface", 
                               "selected-users");
        });
        assertTrue(ex.getMessage().contains("Notification setting"));
    }

    
    private void validateReportRequest(String timeframe, String metrics, 
                                     String format, String visibility) {
        if (timeframe == null || (!timeframe.equalsIgnoreCase("daily") && 
                                !timeframe.equalsIgnoreCase("weekly") && 
                                !timeframe.equalsIgnoreCase("monthly") && 
                                !timeframe.equalsIgnoreCase("custom"))) {
            throw new IllegalArgumentException("Timeframe must be daily, weekly, monthly, or custom.");
        }
        
        if (metrics == null || (metrics != "all" && !metrics.contains("activity") && 
                              !metrics.contains("performance") && 
                              !metrics.contains("satisfaction"))) {
            throw new IllegalArgumentException("Metrics must include valid categories.");
        }
        
        if (format == null || (!format.equalsIgnoreCase("pdf") && 
                             !format.equalsIgnoreCase("csv") && 
                             !format.equalsIgnoreCase("html") && 
                             !format.equalsIgnoreCase("excel"))) {
            throw new IllegalArgumentException("Format must be pdf, csv, html, or excel.");
        }
        
        if (visibility == null || (!visibility.equalsIgnoreCase("private") && 
                                 !visibility.equalsIgnoreCase("instructors") && 
                                 !visibility.equalsIgnoreCase("staff") && 
                                 !visibility.equalsIgnoreCase("public"))) {
            throw new IllegalArgumentException("Visibility must be valid.");
        }
    }
    
    @Test
    public void testInvalidTimeframe() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateReportRequest("biweekly", "all", "pdf", "private");
        });
        assertTrue(ex.getMessage().contains("Timeframe"));
    }
    
    @Test
    public void testInvalidFormat() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateReportRequest("weekly", "all", "word", "private");
        });
        assertTrue(ex.getMessage().contains("Format"));
    }
    
    private void validateContentQuality(String content, boolean hasSources, 
                                      boolean isStructured, boolean hasExplanations) {
        if (content == null || content.split("\\s+").length < 100) {
            throw new IllegalArgumentException("Content must have at least 100 words.");
        }
        
        if (!hasSources) {
            throw new IllegalArgumentException("Content must include sources or references.");
        }
        
        if (!isStructured) {
            throw new IllegalArgumentException("Content must be clearly structured.");
        }
        
        if (!hasExplanations) {
            throw new IllegalArgumentException("Content must include explanations.");
        }
        
        String lowerContent = content.toLowerCase();
        if (lowerContent.contains("stupid") || lowerContent.contains("idiot") || 
            lowerContent.contains("dumb")) {
            throw new IllegalArgumentException("Content contains unprofessional language.");
        }
    }
    
    @Test
    public void testShortContent() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateContentQuality("This is too short", true, true, true);
        });
        assertTrue(ex.getMessage().contains("100 words"));
    }
    
    @Test
    public void testUnprofessionalLanguage() {
        String longContent = "This is a very long content that contains more than 100 words. " + 
                           "It has plenty of text to qualify as comprehensive. " + 
                           "But unfortunately it includes the word stupid which is unprofessional. " + 
                           "The content continues for a while with more text to ensure it exceeds " + 
                           "the minimum word count requirement set in the validation method. " + 
                           "We need to make sure it has enough words to pass that part of the check " + 
                           "so that we can properly test the professionalism check separate from " + 
                           "the length check. This should be enough words now to qualify as a long content.";
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateContentQuality(longContent, true, true, true);
        });
        assertTrue(ex.getMessage().contains("unprofessional"));
    }

    
    private void validateReviewAnalytics(int reviewerCount, String period, 
                                       String insights, boolean hasTrendAnalysis) {
        if (reviewerCount <= 0) {
            throw new IllegalArgumentException("Reviewer count must be positive.");
        }
        
        if (period == null || !period.matches("\\d+\\s*(days|weeks|months)")) {
            throw new IllegalArgumentException("Period must be specified in days, weeks, or months.");
        }
        
        if (insights == null || insights.length() < 50) {
            throw new IllegalArgumentException("Insights section must be at least 50 characters.");
        }
        
        if (!hasTrendAnalysis) {
            throw new IllegalArgumentException("Trend analysis is required.");
        }
    }
    
    @Test
    public void testInvalidReviewerCount() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateReviewAnalytics(0, "30 days", "This is a detailed insights section that exceeds fifty characters in length", true);
        });
        assertTrue(ex.getMessage().contains("Reviewer count"));
    }
    
    @Test
    public void testInvalidPeriod() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateReviewAnalytics(25, "2 years", "This is a detailed insights section that exceeds fifty characters in length", true);
        });
        assertTrue(ex.getMessage().contains("Period"));
    }
}