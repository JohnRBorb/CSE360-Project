package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;
/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show( Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to your Page");
	    continueButton.setOnAction(a -> {
	    	String role =user.getRole();
	    	System.out.println(role);
	    	String[] roleList = databaseHelper.roleToArray(role); //gets role list as an array
	    	int numberOfRoles = roleList.length;

	    	//if statement checks if more than 1 role assigned if <2 sends checks for role assigned
	    	//then sends them to correct page
	    	if(numberOfRoles < 2) {
	    		String onlyRole = String.valueOf(roleList[0]);

	    		switch (onlyRole) {
	    		    case "admin":
	    		    	new AdminHomePage().show(databaseHelper, primaryStage);
	    		    	break;
	    		    case "student":
	    		    	new StudentHomePage().show(databaseHelper, primaryStage, user);
	    		    	break;
	    		    case "staff":
	    		    	new StaffHomePage(databaseHelper, user).show(primaryStage);
	    		    	break;
	    		    case "instructor":
	    		    	new InstructorHomePage(user, databaseHelper).show(primaryStage);
	    		    	break;
	    		    case "reviewer":
	    		    	new ReviewerHomePage(user, databaseHelper).show(primaryStage);
	    		}
	    	}
	    	else {
	    		new RoleSelectionPage().show(databaseHelper, primaryStage, user);
	    	}
	    });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });

	    layout.getChildren().addAll(welcomeLabel,continueButton,quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}