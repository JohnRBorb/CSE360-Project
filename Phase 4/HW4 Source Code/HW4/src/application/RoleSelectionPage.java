package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import databasePart1.*;
import java.util.Arrays;

/**
 * This page displays a simple welcome message for the Instructor.
 */

public class RoleSelectionPage {

    public void show(DatabaseHelper databaseHelper, Stage primaryStage, User user) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	    // Label to instruct user to select role
	    Label userLabel = new Label("Hello, please select your role!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    Label InvalidRoleError = new Label("\n");
	    InvalidRoleError.setStyle("-fx-font-size: 16px; -fx-text-fill: rgb(255,100,100);");
	    InvalidRoleError.setTextAlignment(TextAlignment.CENTER);

		String role =user.getRole();
	    String[] roleList = databaseHelper.roleToArray(role);


	    //button to send to admin page
	    Button AdminButton = new Button("Admin");
	    AdminButton.setOnAction(a -> {
	    	String target = "admin";
	    	boolean hasRole = Arrays.asList(roleList).contains(target);

	    	if(hasRole) {
	    	new AdminHomePage().show(databaseHelper, primaryStage);
	    	}
	    	else {
	    		InvalidRoleError.setText(InvalidRoleError.getText() + "\n" + InvalidRoleError);

	    	}
	    });

	    Button StudentButton = new Button("Student");
	    StudentButton.setOnAction(a -> {
	    	String target = "student";
	    	boolean hasRole = Arrays.asList(roleList).contains(target);

	    	if(hasRole) {
	    	new StudentHomePage().show(databaseHelper, primaryStage, user);
	    	}
	    	else {
	    		InvalidRoleError.setText(InvalidRoleError.getText() + "\n" + InvalidRoleError);

	    	}
	    });

	    Button StaffButton = new Button("Staff");
	    StaffButton.setOnAction(a -> {
	    	String target = "staff";
	    	boolean hasRole = Arrays.asList(roleList).contains(target);

	    	if(hasRole) {
	    	new StaffHomePage(databaseHelper, user).show(primaryStage);
	    	}
	    	else {
	    		InvalidRoleError.setText(InvalidRoleError.getText() + "\n" + InvalidRoleError);

	    	}
	    });

	    Button InstructorButton = new Button("Instructor");
	    InstructorButton.setOnAction(a -> {
	    	String target = "instructor";
	    	boolean hasRole = Arrays.asList(roleList).contains(target);

	    	if(hasRole) {
	    	new InstructorHomePage().show(databaseHelper, primaryStage);
	    	}
	    	else {
	    		InvalidRoleError.setText(InvalidRoleError.getText() + "\n" + InvalidRoleError);

	    	}
	    });

	    Button ReviewerButton = new Button("Reviewer");
	    ReviewerButton.setOnAction(a -> {
	    	String target = "reviewer";
	    	boolean hasRole = Arrays.asList(roleList).contains(target);

	    	if(hasRole) {
	    	new ReviewerHomePage(user, databaseHelper).show(primaryStage);
	    	}
	    	else {
	    		InvalidRoleError.setText(InvalidRoleError.getText() + "\n" + InvalidRoleError);

	    	}
	    });



	    // Button to logout and navigate back to login page
	    Button logoutButton = new Button("Logout");
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
	    });

	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });

	    layout.getChildren().addAll(
	    		userLabel,
	    		AdminButton,
	    		StudentButton,
	    		StaffButton,
	    		InstructorButton,
	    		ReviewerButton,
	    		logoutButton,
	    		quitButton);
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Reviewer Page");

    }
}