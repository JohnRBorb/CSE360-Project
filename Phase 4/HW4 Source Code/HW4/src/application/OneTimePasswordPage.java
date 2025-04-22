package application;


import databasePart1.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class OneTimePasswordPage {
	// Create booleans to hold the roles of the invite
	boolean admin = false;
	boolean student = false;
	boolean instructor = false;
	boolean staff = false;
	boolean reviewer = false;

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper,Stage primaryStage) {
    	
    	
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("One Time Password ");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Text field to enter userName
	    TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
	    
	    // Button to generate the one time password
	    Button showPassButton = new Button("Generate One Time Password");
	    
	    // Label to display the generated one time password
	    Label oneTimePassLabel = new Label(""); ;
	    oneTimePassLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        // Generate the oneTimePassword and assign it to a specific user
        showPassButton.setOnAction(a -> {
        	String userName = userNameField.getText();
        	String oneTimePass = databaseHelper.createOneTimePass(userName);
        	oneTimePassLabel.setText(oneTimePass);
        });
        
        // Button to go back to admin home page
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
	    

        layout.getChildren().addAll(showPassButton, userNameField, oneTimePassLabel, adminHomeButton, quitButton);
	    Scene oneTimePassScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(oneTimePassScene);
	    primaryStage.setTitle("One Time Password Page");
    	
    }
}