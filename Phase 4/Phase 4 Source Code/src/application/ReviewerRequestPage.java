package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewerRequestPage {

	private User user;
    private DatabaseHelper dbHelper;

    public ReviewerRequestPage(DatabaseHelper dbHelper, User user) {
        this.dbHelper = dbHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Label title = new Label("Reviewer Requests");

        ListView<String> requestList = new ListView<>();
        List<String> requests = new ArrayList<>();
        // Query the reviewer_requests table
        String sql = "SELECT studentUsername FROM reviewer_requests";
        try (PreparedStatement pstmt = dbHelper.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                requests.add(rs.getString("studentUsername"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        requestList.getItems().addAll(requests);

        Button approveButton = new Button("Approve Selected Request");
        approveButton.setOnAction(e -> {
            String selectedUser = requestList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                try {
                    // Approve the request: update the user's role and remove the request
                    dbHelper.approveReviewerRequest(selectedUser);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Approved " + selectedUser + " as reviewer.");
                    alert.show();
                    show(primaryStage); // Refresh the page
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        // New Disapprove button to remove a reviewer request
        Button disapproveButton = new Button("Disapprove Selected Request");
        disapproveButton.setOnAction(e -> {
            String selectedUser = requestList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                try {
                    dbHelper.removeReviewerRequest(selectedUser);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Disapproved reviewer request for " + selectedUser);
                    alert.show();
                    show(primaryStage); // Refresh the page
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new InstructorHomePage(user, dbHelper).show(primaryStage);
        });

        layout.getChildren().addAll(title, requestList, approveButton, disapproveButton, backButton);
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviewer Requests");
    }
}
