package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RoleRemovalRequestPage {

    private DatabaseHelper dbHelper;
    private User user;

    public RoleRemovalRequestPage(DatabaseHelper dbHelper, User user) {
        this.dbHelper = dbHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        // Create UI elements
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-alignment: center;");

        Label titleLabel = new Label("Request Role Removal");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Input field for the target username
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter the username to remove roles from");

        // Input field for the reason
        Label reasonLabel = new Label("Reason for Request:");
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Explain why this user's roles should be removed or modified");

        // Button to submit the request
        Button submitButton = new Button("Submit Request");
        submitButton.setOnAction(e -> {
            String targetUsername = usernameField.getText().trim();
            String reason = reasonArea.getText().trim();

            if (targetUsername.isEmpty() || reason.isEmpty()) {
                showAlert("Input Error", "Both username and reason must be provided.");
            } else {
                submitRoleRemovalRequest(targetUsername, reason);
                // Optionally clear fields or notify user that the request was sent
                showAlert("Success", "Your role removal request was submitted successfully.");
                usernameField.clear();
                reasonArea.clear();
            }
        });

        // Back button to return to the StaffHomePage
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StaffHomePage(dbHelper, user).show(primaryStage));

        layout.getChildren().addAll(titleLabel, usernameLabel, usernameField, reasonLabel, reasonArea, submitButton, backButton);
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Request Role Removal");
    }

    // Insert the role removal request into the database
    private void submitRoleRemovalRequest(String username, String reason) {
        String sql = "INSERT INTO role_removal_requests (username, reason) VALUES (?, ?)";
        try (PreparedStatement ps = dbHelper.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, reason);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error submitting role removal request: " + ex.getMessage());
            showAlert("Database Error", "Failed to submit the request: " + ex.getMessage());
        }
    }

    // Utility method for showing alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
