package test;

import org.junit.Test;
import static org.junit.Assert.*;

public class Team4UserStoriesValidationTest {

    // ======================
    // USER STORY 1: Reviewer Profile
    // ======================

    private void validateExperienceInput(String experience) {
        if (experience == null || experience.trim().isEmpty()) {
            throw new IllegalArgumentException("Experience cannot be empty.");
        }
        if (experience.length() > 1000) {
            throw new IllegalArgumentException("Experience is too long.");
        }
    }

    @Test
    public void testEmptyExperience() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateExperienceInput("");
        });
        assertTrue(ex.getMessage().contains("empty"));
    }

    @Test
    public void testLongExperience() {
        String longExp = "x".repeat(1001);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateExperienceInput(longExp);
        });
        assertTrue(ex.getMessage().contains("too long"));
    }

    // ======================
    // USER STORY 2: Instructor Message Feedback
    // ======================

    private void validateMessageReply(String reply) {
        if (reply == null || reply.trim().isEmpty()) {
            throw new IllegalArgumentException("Reply cannot be empty.");
        }
    }

    @Test
    public void testEmptyMessageReply() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateMessageReply("");
        });
        assertTrue(ex.getMessage().contains("empty"));
    }

    @Test
    public void testValidReply() {
        validateMessageReply("Thank you for your question.");
        assertTrue(true); // No exception thrown
    }

    // ======================
    // USER STORY 3: Scorecard Parameter Input
    // ======================

    private void validateScoreWeightInput(String input) {
        try {
            double weight = Double.parseDouble(input);
            if (weight < 0 || weight > 1) {
                throw new IllegalArgumentException("Weight must be between 0 and 1.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format.");
        }
    }

    @Test
    public void testInvalidWeightInput_NonNumeric() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateScoreWeightInput("abc");
        });
        assertTrue(ex.getMessage().contains("format"));
    }

    @Test
    public void testInvalidWeightInput_OutOfRange() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateScoreWeightInput("1.5");
        });
        assertTrue(ex.getMessage().contains("between"));
    }

    // ======================
    // USER STORY 4: Admin Moderation Request
    // ======================

    private void validateModerationReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Please provide a reason.");
        }
    }

    @Test
    public void testEmptyModerationReason() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateModerationReason("   ");
        });
        assertTrue(ex.getMessage().contains("provide a reason"));
    }

    @Test
    public void testValidModerationReason() {
        validateModerationReason("This comment violates community guidelines.");
        assertTrue(true);
    }
    
    @Test
    public void testWhitespaceExperienceInput() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            validateExperienceInput("   ");
        });
        assertTrue(ex.getMessage().contains("empty"));
    }

    
    @Test
    public void testZeroScoreWeightInput() {
        validateScoreWeightInput("0");
        assertTrue(true); // Zero is a valid input, should not throw
    }

}
