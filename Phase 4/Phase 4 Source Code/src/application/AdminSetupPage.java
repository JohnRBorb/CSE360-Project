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
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        Button setupButton = new Button("Setup");
        
        Label userpassErrors = new Label("\n");
        userpassErrors.setStyle("-fx-font-size: 16px; -fx-text-fill: rgb(255,100,100);");
        userpassErrors.setTextAlignment(TextAlignment.CENTER);
        
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
            
            try {
            	// Create a new User object with admin role and register in the database
            	User user=new User(userName, password, "admin");
                databaseHelper.register(user);
                System.out.println("Administrator setup completed.");
                
                // Navigate to the Welcome Login Page
                new SetupCompletePage(databaseHelper).show(primaryStage);
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, userNameField, passwordField, setupButton, userpassErrors);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
