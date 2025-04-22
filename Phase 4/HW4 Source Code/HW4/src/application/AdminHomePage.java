package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {	
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
    	VBox layout = new VBox();
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!");
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to travel to invite page
	    Button inviteButton = new Button("Invite");
	    inviteButton.setOnAction(a -> {
	    	new InvitationPage().show(databaseHelper, primaryStage);
	    });
	    
	    // Button to create a one time password
	    Button oneTimePassButton = new Button("Create a One Time Password");
	    oneTimePassButton.setOnAction(a -> {
	    	new OneTimePasswordPage().show(databaseHelper, primaryStage);
	    });
	    
	    //Button to change user roles
	    Button changeRolesButton = new Button("Change User Roles");
	    changeRolesButton.setOnAction(a -> {
	    	new ChangeRolesPage().show(databaseHelper, primaryStage);
	    });
	    
	    // Button to list users
	    Button listUsersButton = new Button("List all Users");
	    listUsersButton.setOnAction(a -> {
	    	new ListUsersPage().show(databaseHelper, primaryStage);
	    });
	    
	    // Button to delete a chosen user
	    Button deleteUserButton = new Button("Delete a User");
	    deleteUserButton.setOnAction(a -> {
	    	new DeleteUserPage().show(databaseHelper, primaryStage);
	    });
	    
	    Button viewRequestsButton = new Button("View Role Removal Requests");
	    viewRequestsButton.setOnAction(e -> {
	        // Open a new window to display role removal requests
	        new AdminRoleRemovalRequestsListPage(databaseHelper).show(primaryStage);
	    });
	    
	    // Button to logout and navigate back to login page
	    Button logoutButton = new Button("Logout");
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });

	    
	    layout.getChildren().addAll(adminLabel, inviteButton, oneTimePassButton, changeRolesButton, listUsersButton, deleteUserButton, viewRequestsButton, logoutButton, quitButton);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}