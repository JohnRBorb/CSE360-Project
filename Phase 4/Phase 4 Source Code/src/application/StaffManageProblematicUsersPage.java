package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffManageProblematicUsersPage {

    private DatabaseHelper dbHelper;
    private User user;
    private ObservableList<String> problematicUserList;

    public StaffManageProblematicUsersPage(DatabaseHelper dbHelper, User user) {
        this.dbHelper = dbHelper;
        this.user = user;
        this.problematicUserList = FXCollections.observableArrayList();
        loadProblematicUsers();
    }

    // Loads usernames from the problematic_users table into the list.
    private void loadProblematicUsers() {
        problematicUserList.clear();
        try {
            ResultSet rs = dbHelper.getConnection().createStatement()
                    .executeQuery("SELECT * FROM problematic_users");
            while (rs.next()) {
                String username = rs.getString("username");
                problematicUserList.add(username);
            }
        } catch (SQLException ex) {
            System.err.println("Error loading problematic users: " + ex.getMessage());
        }
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        
        Label titleLabel = new Label("Manage Problematic Users");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // ListView to display problematic user names.
        ListView<String> userListView = new ListView<>(problematicUserList);
        userListView.setPrefHeight(200);
        
        // TextField and Add button for new usernames.
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username to add");
        
        Button addButton = new Button("Add User");
        addButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                addUserToProblematicList(username);
                usernameField.clear();
                loadProblematicUsers(); // Refresh list after adding.
            } else {
                showAlert("Input Error", "Please enter a username.");
            }
        });

        // Remove the selected user.
        Button removeButton = new Button("Remove Selected User");
        removeButton.setOnAction(e -> {
            String selected = userListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                removeUserFromProblematicList(selected);
                loadProblematicUsers(); // Refresh list after removal.
            } else {
                showAlert("Selection Error", "Please select a user to remove.");
            }
        });

        // Back button to return to the Staff Home Page.
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StaffHomePage(dbHelper, user).show(primaryStage));

        layout.getChildren().addAll(titleLabel, userListView, usernameField, addButton, removeButton, backButton);
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Manage Problematic Users");
    }

    public void addUserToProblematicList(String username) {
        try {
            String sql = "INSERT INTO problematic_users (username) VALUES (?)";
            PreparedStatement ps = dbHelper.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error adding user: " + ex.getMessage());
            showAlert("Database Error", "Failed to add user: " + ex.getMessage());
        }
    }

    public void removeUserFromProblematicList(String username) {
        try {
            String sql = "DELETE FROM problematic_users WHERE username = ?";
            PreparedStatement ps = dbHelper.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error removing user: " + ex.getMessage());
            showAlert("Database Error", "Failed to remove user: " + ex.getMessage());
        }
    }
    
    // Utility method to show an alert dialog.
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
