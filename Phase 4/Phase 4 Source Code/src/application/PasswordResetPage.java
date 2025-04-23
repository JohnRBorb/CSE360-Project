package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class PasswordResetPage {
	
    private final DatabaseHelper databaseHelper;

    public PasswordResetPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, String userName) {
    	// Input field for new password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter New Password");
        passwordField.setMaxWidth(250);

        Button setupButton = new Button("Set New Password");
        
        Label userpassErrors = new Label("\n");
        userpassErrors.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        userpassErrors.setTextAlignment(TextAlignment.CENTER);
        
        setupButton.setOnAction(a -> {
        	// Retrieve password
        	String password = passwordField.getText();
            
        	// Check if user name and password are valid
        	new PasswordEvaluator();
            String testedPassword = PasswordEvaluator.evaluatePassword(password);
            
            
            // If password is invalid, clear input box and return
            if (testedPassword != "") {
            	passwordField.clear();
            	System.out.println(testedPassword);
            	userpassErrors.setText(userpassErrors.getText() + "\n" + testedPassword); // MAKE THE PRINTED VALUES MATCH DOCUMENTATION
            	return;
            }
            
            try {
            	// Update database to the new password
            	databaseHelper.updateUserPassword(userName, password);
                
                // Navigate to the User Login Page
                new UserLoginPage(databaseHelper).show(primaryStage);
                
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, passwordField, setupButton, userpassErrors);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Password Reset");
        primaryStage.show();
    }
}
