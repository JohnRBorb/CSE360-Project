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

public class ListUsersPage {	
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the title of the page
	    Label listLabel = new Label("");
	    listLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to list all users
	    Button listUsersButton = new Button("List all Users");
	    listUsersButton.setOnAction(a -> {
	    	try {
	    		listLabel.setText(databaseHelper.listUsers());
	    	} catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
	    });
	    
	    // Button to navigate to admin home
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

	    
	    layout.getChildren().addAll(listUsersButton, listLabel, adminHomeButton, quitButton);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("List Users");
    }
}