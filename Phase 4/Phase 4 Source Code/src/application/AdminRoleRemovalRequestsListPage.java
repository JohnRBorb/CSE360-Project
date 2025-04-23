package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRoleRemovalRequestsListPage {

    private DatabaseHelper dbHelper;
    private VBox requestsListVBox;

    public AdminRoleRemovalRequestsListPage(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.requestsListVBox = new VBox(10);
        requestsListVBox.setPadding(new Insets(10));
    }

    public void show(Stage primaryStage) {
        // Create the main layout.
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));

        Label headerLabel = new Label("Role Removal Requests");
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Populate the VBox with the current requests.
        refreshRequests();

        // Refresh button
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            refreshRequests();
        });

        // Back button to return to admin home.
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new AdminHomePage().show(dbHelper, primaryStage);
        });

        layout.getChildren().addAll(headerLabel, requestsListVBox, refreshButton, backButton);
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin - Role Removal Requests");
    }

    private void refreshRequests() {
        requestsListVBox.getChildren().clear();

        String query = "SELECT * FROM role_removal_requests";
        try {
            ResultSet rs = dbHelper.getConnection().createStatement().executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String reason = rs.getString("reason");
                String requestTime = rs.getString("request_time"); // if available

                // Create a horizontal container for this request.
                HBox requestRow = new HBox(10);
                requestRow.setPadding(new Insets(5));

                Label requestLabel = new Label("User: " + username + " | Reason: " + reason);
                // Optionally show the timestamp:
                if (requestTime != null) {
                    requestLabel.setText(requestLabel.getText() + " | Time: " + requestTime);
                }
                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(e -> {
                    deleteRequest(id);
                    refreshRequests();
                });
                requestRow.getChildren().addAll(requestLabel, deleteButton);
                requestsListVBox.getChildren().add(requestRow);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Database Error", "Error retrieving role removal requests: " + ex.getMessage());
        }
    }

    private void deleteRequest(int requestId) {
        String sql = "DELETE FROM role_removal_requests WHERE id = ?";
        try (PreparedStatement ps = dbHelper.getConnection().prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Database Error", "Error deleting request: " + ex.getMessage());
        }
    }

    // Utility method to show alerts.
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
