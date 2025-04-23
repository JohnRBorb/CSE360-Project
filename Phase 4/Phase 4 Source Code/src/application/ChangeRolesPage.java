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

public class ChangeRolesPage {	
	boolean admin = false;
	boolean student = false;
	boolean instructor = false;
	boolean staff = false;
	boolean reviewer = false;
	String roles = "";
	
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label deleteLabel = new Label("Enter UserName of User to Change and Click Roles to Set");
	    deleteLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Text field to enter userName
	    TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
	    
        
	    Label adminLabel = new Label("");
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
        // Button to select admin
        Button adminButton = new Button("Admin");
        adminButton.setOnAction(b -> {
        	if (admin) {
        		adminLabel.setText("");
        		admin = false;
        	} else {
        		adminLabel.setText("Admin");
        		admin = true;
        	}
        });
        
        Label studentLabel = new Label("");
	    studentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to select student
        Button studentButton = new Button("Student");
        studentButton.setOnAction(b -> {
        	if (student) {
        		studentLabel.setText("");
        		student = false;
        	} else {
        		studentLabel.setText("Student");
        		student = true;
        	}
        });
        
        Label instructorLabel = new Label("");
	    instructorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to select instructor
        Button instructorButton = new Button("Instructor");
        instructorButton.setOnAction(b -> {
        	if (instructor) {
        		instructorLabel.setText("");
        		instructor = false;
        	} else {
        		instructorLabel.setText("Instructor");
        		instructor = true;
        	}
        });
        
        Label staffLabel = new Label("");
	    staffLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to select staff
        Button staffButton = new Button("Staff");
        staffButton.setOnAction(b -> {
        	if (staff) {
        		staffLabel.setText("");
        		staff = false;
        	} else {
        		staffLabel.setText("Staff");
        		staff = true;
        	}
        });
        
        Label reviewerLabel = new Label("");
	    reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to select reviewer
        Button reviewerButton = new Button("Reviewer");
        reviewerButton.setOnAction(b -> {
        	if (reviewer) {
        		reviewerLabel.setText("");
        		reviewer = false;
        	} else {
        		reviewerLabel.setText("Reviewer");
        		reviewer = true;
        	}
        });
        
        
	    // Button to change roles
	    Button changeButton = new Button("Change");
	    changeButton.setOnAction(a -> {
	    	String userName = userNameField.getText();
	    	userNameField.clear();
	    	deleteLabel.setText("Enter UserName of User to Change and Click Roles to Set");
	    	
	    	// If invalid username, return
	    	if (!(databaseHelper.doesUserExist(userName))) {
	    		deleteLabel.setText("User Does Not Exist");
	    		return;
	    	}
	    	
	    	if (admin) {
	    		roles = roles + "admin, ";
	    	}
	    	if (student) {
	    		roles = roles + "student, ";
	    	}
	    	if (instructor) {
	    		roles = roles + "instructor, ";
	    	}
	    	if (staff) {
	    		roles = roles + "staff, ";
	    	}
	    	if (reviewer) {
	    		roles = roles + "reviewer, ";
	    	}
	    	
	    	if (roles.length() == 0) {
	    		deleteLabel.setText("Must Select a Role");
	    	}
	    	
	    	
		    try {
		    	roles = roles.substring(0, roles.length()-2); 
		    	databaseHelper.changeRoles(userName, roles);
		    } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
		    }
	    });
	    
	    // Label to the selected roles
	    Label newRoles = new Label("Enter UserName of User to Change and Click Roles to Set");
	    deleteLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
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

	    
	    layout.getChildren().addAll(deleteLabel, userNameField, adminButton, studentButton, instructorButton, 
	    		staffButton, reviewerButton, changeButton, newRoles, adminHomeButton, quitButton,
	    		adminLabel, studentLabel, instructorLabel, staffLabel, reviewerLabel);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Delete User");
    }
}