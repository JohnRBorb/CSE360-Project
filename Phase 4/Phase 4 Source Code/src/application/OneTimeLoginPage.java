package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class OneTimeLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public OneTimeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter One Time Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        Button loginButton = new Button("Login");
        
        loginButton.setOnAction(a -> {
        	// Reset errorLabel
        	errorLabel.setText(null);
        	
        	// Retrieve user name and password
        	String userName = userNameField.getText();
        	String password = passwordField.getText();
            
        	// Check if user name is valid
        	new UserNameRecognizer();
            String testedUserName = UserNameRecognizer.checkForValidUserName(userName);
            
            // If either user name or password is invalid, clear input box and return
            if (!(databaseHelper.doesUserExist(userName))) {
            	userNameField.clear();
            	System.out.println(testedUserName);
            	errorLabel.setText(errorLabel.getText() + "\nuser does not exist"); // MAKE THE PRINTED VALUES MATCH DOCUMENTATION
            	return;
            }
            
            try {
            	if (databaseHelper.checkOneTimePass(userName, password)) {
            		new PasswordResetPage(databaseHelper).show(primaryStage, userName);
            	} else {
            		// Display an error if the account does not exist
                    errorLabel.setText("invalid username or password");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            } 
        });


        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("One Time Password Login");
        primaryStage.show();
    }
}
