package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label userpassErrors = new Label("\n");
        userpassErrors.setStyle("-fx-font-size: 16px; -fx-text-fill: rgb(255,100,100);");
        userpassErrors.setTextAlignment(TextAlignment.CENTER);


        Button loginButton = new Button("Login");
        
        loginButton.setOnAction(a -> {
        	// Reset errorLabel
        	userpassErrors.setText("");
        	
        	// Retrieve user name and password
        	String userName = userNameField.getText();
        	String password = passwordField.getText();
            
        	// Check if user name and password are valid
        	new PasswordEvaluator();
        	new UserNameRecognizer();
            String testedUserName = UserNameRecognizer.checkForValidUserName(userName);
            String testedPassword = PasswordEvaluator.evaluatePassword(password);
            boolean validUser = true;
            boolean validPass = true;
            
            
            // If either user name or password is invalid, clear input box and return
            if (testedUserName != "") {
            	validUser = false;
            	userNameField.clear();
            	System.out.println(testedUserName);
            	userpassErrors.setText(userpassErrors.getText() + "\n" + testedUserName); // MAKE THE PRINTED VALUES MATCH DOCUMENTATION
            }
            
            if (testedPassword != "") {
            	validPass = false;
            	passwordField.clear();
            	System.out.println(testedPassword);
            	userpassErrors.setText(userpassErrors.getText() + "\n" + testedPassword); // MAKE THE PRINTED VALUES MATCH DOCUMENTATION
            }
            
            if (!(validUser && validPass)) {
            	return;
            }
            
            try {
            	User user=new User(userName, password, "");
            	WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
            	
            	// Retrieve the user's role from the database using userName
            	String role = databaseHelper.getUserRole(userName);
            	System.out.println("role: " + role);
            	
            	if(role!=null) {
            		user.setRole(role);
            		if(databaseHelper.login(user)) {
            			welcomeLoginPage.show(primaryStage,user);
            		}
            		else {
            			// Display an error if the login fails
            			userpassErrors.setText("Error logging in");
            		}
            	}
            	else {
            		// Display an error if the account does not exist
            		userpassErrors.setText("Account doesn't exist");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            } 
        });
        
        // Create a button to send user to OneTimeLogin page
        Button oneTimePassButton = new Button("Use One Time Password");
        oneTimePassButton.setOnAction(a -> {
        	new OneTimeLoginPage(databaseHelper).show(primaryStage);
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, oneTimePassButton, userpassErrors);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
