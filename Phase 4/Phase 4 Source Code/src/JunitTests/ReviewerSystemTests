import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class ReviewerSystemTests {

    private Review review;
    private RoleRemovalRequest roleRequest;
    private User reviewerUser;
    private AdminRoleRemovalRequestsListPage adminRequestsPage;

    @BeforeEach
    public void setup() {
        review = new Review();
        roleRequest = new RoleRemovalRequest();
        reviewerUser = new User("Hrishi Resham", "reviewer");
        adminRequestsPage = new AdminRoleRemovalRequestsListPage();
    }

    @Test
    public void CreateReviewerProfileTest() {
        reviewerUser.setProfile("Experience: 5 years tutoring CS");
        review.setText("Great explanation on LTL");
        reviewerUser.addReview(review);
        assertNotNull(reviewerUser.getProfile());
        assertTrue(reviewerUser.getReviews().contains(review));
    }

    @Test
    public void UpdateReviewerProfileTest() {
        review.setText("Helpful example on MIPS");
        reviewerUser.addReview(review);
        reviewerUser.removeReview(review);
        List<Review> reviews = reviewerUser.getReviews();
        assertFalse(reviews.contains(review));
    }

    @Test
    public void ViewReviewerScorecardTest() {
        reviewerUser.setScorecard("Quality: High, Speed: Fast");
        String scorecard = reviewerUser.getScorecard();
        assertNotNull(scorecard);
        assertTrue(scorecard.contains("Quality"));
    }

    @Test
    public void ComputeReviewerScorecardTest() {
        reviewerUser.computeScorecard("Accuracy, Tone");
        String scorecard = reviewerUser.getScorecard();
        assertTrue(scorecard.contains("Accuracy"));
        assertTrue(scorecard.contains("Tone"));
    }

    @Test
    public void InstructorViewReviewDetailsTest() {
        review.setText("Well explained Dijkstra's Algorithm");
        reviewerUser.addReview(review);
        List<Review> history = reviewerUser.getReviews();
        assertTrue(history.contains(review));
    }

    @Test
    public void InstructorModerationActionsTest() {
        reviewerUser.flagAsProblematic("Spam content");
        assertTrue(reviewerUser.isFlagged());
    }

    @Test
    public void AdminRequestSubmissionTest() {
        roleRequest.setDescription("Delete user");
        adminRequestsPage.submitRequest(roleRequest);
        assertTrue(adminRequestsPage.getOpenRequests().contains(roleRequest));
    }

    @Test
    public void AdminRequestCompletionTest() {
        roleRequest.setDescription("Reset password");
        adminRequestsPage.submitRequest(roleRequest);
        adminRequestsPage.markRequestCompleted(roleRequest);
        assertTrue(adminRequestsPage.getClosedRequests().contains(roleRequest));
    }

    @Test
    public void ReopenClosedRequestTest() {
        roleRequest.setDescription("Flag abuse");
        adminRequestsPage.submitRequest(roleRequest);
        adminRequestsPage.markRequestCompleted(roleRequest);
        adminRequestsPage.reopenRequest(roleRequest, "Needs further action");
        assertTrue(adminRequestsPage.getOpenRequests().contains(roleRequest));
        assertFalse(adminRequestsPage.getClosedRequests().contains(roleRequest));
    }

    @Test
    public void ViewAllRequestStatesTest() {
        RoleRemovalRequest req1 = new RoleRemovalRequest();
        req1.setDescription("Block IP");
        adminRequestsPage.submitRequest(req1);

        RoleRemovalRequest req2 = new RoleRemovalRequest();
        req2.setDescription("Enable logging");
        adminRequestsPage.submitRequest(req2);
        adminRequestsPage.markRequestCompleted(req2);

        int totalRequests = adminRequestsPage.getAllRequests().size();
        assertEquals(2, totalRequests);
    }
}
