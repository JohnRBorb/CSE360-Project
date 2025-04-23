package application;

import databasePart1.DatabaseHelper;
import databasePart1.QuestionsAnswersDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReviewerScoringService {
    private final DatabaseHelper dbHelper;
    private final QuestionsAnswersDatabase qaDb;

    public ReviewerScoringService(DatabaseHelper dbHelper,
                                  QuestionsAnswersDatabase qaDb) throws SQLException {
        this.dbHelper = dbHelper;
        this.qaDb     = qaDb;
        // ensure it’s connected exactly once:
        if (this.qaDb.getConnection() == null) {
            this.qaDb.connectToQuestionAnswerDatabase();
        }
    }
    
    /**
     * Compute the weighted score for a reviewer by fetching each metric
     * and then applying the instructor's configured weights.
     */
    public double computeReviewerScore(String reviewer) throws SQLException {
        String sql =
          "SELECT AVG(thoroughness_rating) AS avgTh, "
        + "       AVG(clarity_rating)     AS avgCl, "
        + "       AVG(timeliness_rating)  AS avgTi "
        + "FROM reviews WHERE reviewerName = ?";
        try (PreparedStatement ps = qaDb.getConnection().prepareStatement(sql)) {
            ps.setString(1, reviewer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double th = rs.getDouble("avgTh");
                    double cl = rs.getDouble("avgCl");
                    double ti = rs.getDouble("avgTi");

                    // fetch domain weights
                    List<ScoreParam> params = dbHelper.getScoreParameters();
                    double score = 0;
                    for (ScoreParam p : params) {
                        switch (p.getDomain()) {
                          case "thoroughness": score += p.getWeight() * th; break;
                          case "clarity":      score += p.getWeight() * cl; break;
                          case "timeliness":   score += p.getWeight() * ti; break;
                        }
                    }
                    return score;
                }
            }
        }
        return 0.0;
    }

}
