package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label userpassErrors = new Label("\n");
        userpassErrors.setStyle("-fx-font-size: 16px; -fx-text-fill: rgb(255,100,100);");
        userpassErrors.setTextAlignment(TextAlignment.CENTER);

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
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
            	userpassErrors.setText(userpassErrors.getText() + "\n" + testedUserName);
            }
            
            if (testedPassword != "") {
            	validPass = false;
            	passwordField.clear();
            	System.out.println(testedPassword);
            	userpassErrors.setText(userpassErrors.getText() + "\n" + testedPassword);
            }
            
            if (!(validUser && validPass)) {
            	return;
            }
            
            String code = inviteCodeField.getText();
            
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            		// Validate the invitation code
            		if(databaseHelper.validateInvitationCode(code)) {
            			
            			// Create a new user and register them in the database
		            	User user=new User(userName, password, databaseHelper.getRoleFromCode(code));
		                databaseHelper.register(user);
		                databaseHelper.assignRolesFromCodeToUser(userName,code);
		                
		             // Navigate to the Welcome Login Page
		                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            		}
            		else {
            			userpassErrors.setText("Please enter a valid invitation code");
            		}
            	}
            	else {
            		userpassErrors.setText("This useruserName is taken!!.. Please use another to setup an account");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, inviteCodeField, setupButton, userpassErrors);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
