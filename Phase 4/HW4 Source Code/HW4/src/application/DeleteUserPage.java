package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class DeleteUserPage {	
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
    	VBox layout = new VBox();
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label deleteLabel = new Label("Enter UserName of User to Delete");
	    deleteLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Text field to enter userName
	    TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
        
        // Label to display name of deleted user, or error id not deleted
        Label deletedUser = new Label("");
		deleteLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to travel to invite page
	    Button deleteButton = new Button("Delete");
	    deleteButton.setOnAction(a -> {
	    	String userName = userNameField.getText();
	    	
	    	// Clear label
	    	deletedUser.setText("");
	    	
	    	// Yes and No buttons
		    Button yes = new Button("Yes");
		    Button no = new Button("No");
		    
		    // If no is clicked, clear input box
		    no.setOnAction(b -> {
		    	userNameField.clear();
		    	return;
		    });
		    
		    // If yes is clicked attempt to delete user
		    yes.setOnAction(b -> {
		    	try {
		    		
		    		// If user is not deleted clear userName field and tell user
		    		String output = databaseHelper.tryDeleteUser(userName);
		    		if (!(output.equals(userName))) {
		    			userNameField.clear();
		    			System.out.println(output);
		    			deletedUser.setText(output);
		    		} else {
		    			// If user is deleted tell user
		    			userNameField.clear();
		    			deletedUser.setText("Deleted " + userName);
		    		}
		    		
		    	    // Remove Yes and No buttons
		    	    layout.getChildren().removeAll(yes, no);
		    	    
		    	} catch (SQLException e) {
	                System.err.println("Database error: " + e.getMessage());
	                e.printStackTrace();
	            }
		    });
		    layout.getChildren().addAll(yes, no);
	    });
	    
        Button adminHomeButton = new Button("Back to Home");
	    adminHomeButton.setOnAction(a -> {
	    	new AdminHomePage().show(databaseHelper, primaryStage);
	    });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });

	    
	    layout.getChildren().addAll(deleteLabel, userNameField, deleteButton, deletedUser, adminHomeButton, quitButton);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Delete User");
    }
}