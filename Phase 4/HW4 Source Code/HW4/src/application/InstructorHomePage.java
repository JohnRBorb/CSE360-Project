package application;


import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;


/**
 * This page displays a simple welcome message for the Instructor.
 */


public class InstructorHomePage {


    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");


	    // Label to display Hello user
	    Label userLabel = new Label("Hello, Instructor!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    Button reviewStudentContributions = new Button("Review Student Contributions");
	    reviewStudentContributions.setOnAction(a -> {
	        TextField studentField = new TextField();
	        studentField.setPromptText("Enter Student Username");
	        Button viewButton = new Button("View Contributions");
	        viewButton.setOnAction(ev -> {
	            String studentUser = studentField.getText().trim();
	            // Call a method to display the contributions page
	            new StudentContributionsPage(databaseHelper, studentUser).show(primaryStage);
	        });
	        VBox contribLayout = new VBox(10, studentField, viewButton);
	        Scene scene = new Scene(contribLayout, 400, 200);
	        primaryStage.setScene(scene);
	    });
	    layout.getChildren().add(reviewStudentContributions);
	    
	    Button reviewerRequestsButton = new Button("Reviewer Requests");
	    reviewerRequestsButton.setOnAction(e -> {
	        new ReviewerRequestPage(databaseHelper).show(primaryStage);
	    });
	    layout.getChildren().add(reviewerRequestsButton);

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


	    layout.getChildren().addAll(userLabel, logoutButton, quitButton);
	    Scene userScene = new Scene(layout, 800, 400);


	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Instructor Page");


    }
}