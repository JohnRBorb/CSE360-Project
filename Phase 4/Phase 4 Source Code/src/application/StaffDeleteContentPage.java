package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StaffDeleteContentPage {

    private DatabaseHelper dbHelper;
    private User user;

    public StaffDeleteContentPage(DatabaseHelper dbHelper, User user) {
        this.dbHelper = dbHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        // Main layout for delete content page
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));

        Label headerLabel = new Label("Delete Inappropriate Content");
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // ------ Section for Deleting a Question ------
        Label questionLabel = new Label("Delete a Question");
        questionLabel.setStyle("-fx-font-weight: bold;");

        TextField qUserField = new TextField();
        qUserField.setPromptText("User Name");
        TextField qTitleField = new TextField();
        qTitleField.setPromptText("Title");
        TextField qBodyField = new TextField();
        qBodyField.setPromptText("Body");

        Button deleteQuestionBtn = new Button("Delete Question");
        deleteQuestionBtn.setOnAction(e -> {
            String userName = qUserField.getText().trim();
            String title = qTitleField.getText().trim();
            String body = qBodyField.getText().trim();
            if (userName.isEmpty() || title.isEmpty() || body.isEmpty()) {
                showAlert("Input Error", "Please fill in all fields for the question.");
            } else {
                deleteQuestionFromDatabase(userName, title, body);
            }
        });
        VBox questionSection = new VBox(5, questionLabel, qUserField, qTitleField, qBodyField, deleteQuestionBtn);

        // ------ Section for Deleting an Answer ------
        Label answerLabel = new Label("Delete an Answer");
        answerLabel.setStyle("-fx-font-weight: bold;");

        TextField aQIDField = new TextField();
        aQIDField.setPromptText("Question ID");
        TextField aUserField = new TextField();
        aUserField.setPromptText("User Name");
        TextField aContentField = new TextField();
        aContentField.setPromptText("Content");

        Button deleteAnswerBtn = new Button("Delete Answer");
        deleteAnswerBtn.setOnAction(e -> {
            String qIDText = aQIDField.getText().trim();
            String userName = aUserField.getText().trim();
            String content = aContentField.getText().trim();
            if (qIDText.isEmpty() || userName.isEmpty() || content.isEmpty()) {
                showAlert("Input Error", "Please fill in all fields for the answer.");
            } else {
                try {
                    int qID = Integer.parseInt(qIDText);
                    deleteAnswerFromDatabase(qID, userName, content);
                } catch (NumberFormatException ex) {
                    showAlert("Input Error", "Question ID must be a valid number.");
                }
            }
        });
        VBox answerSection = new VBox(5, answerLabel, aQIDField, aUserField, aContentField, deleteAnswerBtn);

        // ------ Section for Deleting a Comment ------
        Label commentLabel = new Label("Delete a Comment");
        commentLabel.setStyle("-fx-font-weight: bold;");

        TextField cQIDField = new TextField();
        cQIDField.setPromptText("Question ID");
        TextField cUserField = new TextField();
        cUserField.setPromptText("User Name");
        TextField cContentField = new TextField();
        cContentField.setPromptText("Content");

        Button deleteCommentBtn = new Button("Delete Comment");
        deleteCommentBtn.setOnAction(e -> {
            String qIDText = cQIDField.getText().trim();
            String userName = cUserField.getText().trim();
            String content = cContentField.getText().trim();
            if (qIDText.isEmpty() || userName.isEmpty() || content.isEmpty()) {
                showAlert("Input Error", "Please fill in all fields for the comment.");
            } else {
                try {
                    int qID = Integer.parseInt(qIDText);
                    deleteCommentFromDatabase(qID, userName, content);
                } catch (NumberFormatException ex) {
                    showAlert("Input Error", "Question ID must be a valid number.");
                }
            }
        });
        VBox commentSection = new VBox(5, commentLabel, cQIDField, cUserField, cContentField, deleteCommentBtn);

        // ------ Section for Deleting a Review ------
        Label reviewLabel = new Label("Delete a Review");
        reviewLabel.setStyle("-fx-font-weight: bold;");

        TextField reviewIDField = new TextField();
        reviewIDField.setPromptText("Review ID");

        Button deleteReviewBtn = new Button("Delete Review");
        deleteReviewBtn.setOnAction(e -> {
            String reviewIDText = reviewIDField.getText().trim();
            if (reviewIDText.isEmpty()) {
                showAlert("Input Error", "Please enter the Review ID.");
            } else {
                try {
                    int reviewID = Integer.parseInt(reviewIDText);
                    deleteReviewFromDatabase(reviewID);
                } catch (NumberFormatException ex) {
                    showAlert("Input Error", "Review ID must be a valid number.");
                }
            }
        });
        VBox reviewSection = new VBox(5, reviewLabel, reviewIDField, deleteReviewBtn);

        // Back button to return to StaffHomePage
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StaffHomePage(dbHelper, user).show(primaryStage));

        // Assemble the main layout
        layout.getChildren().addAll(headerLabel, questionSection, answerSection, commentSection, reviewSection, backButton);
        Scene scene = new Scene(layout, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Delete Content (Staff)");
    }

    private void deleteQuestionFromDatabase(String userName, String title, String body) {
        String sql = "DELETE FROM questions WHERE userName = ? AND title = ? AND body = ?";
        try (PreparedStatement ps = dbHelper.getConnection().prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.setString(2, title);
            ps.setString(3, body);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Success", "Question deleted successfully.");
            } else {
                showAlert("Not Found", "No matching question found.");
            }
        } catch (SQLException ex) {
            showAlert("Database Error", "Error deleting question: " + ex.getMessage());
        }
    }

    private void deleteAnswerFromDatabase(int qID, String userName, String content) {
        String sql = "DELETE FROM answers WHERE qID = ? AND userName = ? AND content = ?";
        try (PreparedStatement ps = dbHelper.getConnection().prepareStatement(sql)) {
            ps.setInt(1, qID);
            ps.setString(2, userName);
            ps.setString(3, content);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Success", "Answer deleted successfully.");
            } else {
                showAlert("Not Found", "No matching answer found.");
            }
        } catch (SQLException ex) {
            showAlert("Database Error", "Error deleting answer: " + ex.getMessage());
        }
    }

    private void deleteCommentFromDatabase(int qID, String userName, String content) {
        String sql = "DELETE FROM comments WHERE qID = ? AND userName = ? AND content = ?";
        try (PreparedStatement ps = dbHelper.getConnection().prepareStatement(sql)) {
            ps.setInt(1, qID);
            ps.setString(2, userName);
            ps.setString(3, content);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Success", "Comment deleted successfully.");
            } else {
                showAlert("Not Found", "No matching comment found.");
            }
        } catch (SQLException ex) {
            showAlert("Database Error", "Error deleting comment: " + ex.getMessage());
        }
    }

    private void deleteReviewFromDatabase(int reviewID) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (PreparedStatement ps = dbHelper.getConnection().prepareStatement(sql)) {
            ps.setInt(1, reviewID);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                showAlert("Success", "Review deleted successfully.");
            } else {
                showAlert("Not Found", "No matching review found.");
            }
        } catch (SQLException ex) {
            showAlert("Database Error", "Error deleting review: " + ex.getMessage());
        }
    }

    // Utility method to display alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
