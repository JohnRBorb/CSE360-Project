package application;


import databasePart1.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPage {
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
	    Label userLabel = new Label("Choose Roles:");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    HBox buttons = new HBox();
	    buttons.setAlignment(Pos.CENTER);
	    
	    // Button to select admin
	    Button inviteAdmin = new Button("Invite Admin");
	    inviteAdmin.setOnAction(a -> {
	    	inviteAdmin.setStyle("-fx-background-color: #A8A8A8;");
	    	if (admin) {
	    		admin = false;
	    		inviteAdmin.setStyle("");
	    		return;
	    	}
            admin = true;
        });
	    
	    // Button to select student
	    Button inviteStudent = new Button("Invite Student");
	    inviteStudent.setOnAction(a -> {
	    	inviteStudent.setStyle("-fx-background-color: #A8A8A8;");
	    	if (student) {
	    		student = false;
	    		inviteStudent.setStyle("");
	    		return;
	    	}
            student = true;
        });
	    
	    // Button to select instructor
	    Button inviteInstructor = new Button("Invite Instructor");
	    inviteInstructor.setOnAction(a -> {
	    	inviteInstructor.setStyle("-fx-background-color: #A8A8A8;");
	    	if (instructor) {
	    		instructor = false;
	    		inviteInstructor.setStyle("");
	    		return;
	    	}
            instructor = true;
        });
	    
	    // Button to select staff
	    Button inviteStaff = new Button("Invite Staff");
	    inviteStaff.setOnAction(a -> {
	    	inviteStaff.setStyle("-fx-background-color: #A8A8A8;");
	    	if (staff) {
	    		staff = false;
	    		inviteStaff.setStyle("");
	    		return;
	    	}
            staff = true;
        });
	    
	    // Button to select reviewer
	    Button inviteReviewer = new Button("Invite Reviewer");
	    inviteReviewer.setOnAction(a -> {
	    	inviteReviewer.setStyle("-fx-background-color: #A8A8A8;");
	    	if (reviewer) {
	    		reviewer = false;
	    		inviteReviewer.setStyle("");
	    		return;
	    	}
            reviewer = true;
        });
	    
	    // Button to generate the invitation code
	    Button showCodeButton = new Button("Generate Invitation Code");
	    
	    // Label to display the generated invitation code
	    Label inviteCodeLabel = new Label(""); ;
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        // Generate the invitation code using the databaseHelper and set it to the label
        showCodeButton.setOnAction(a -> {
        	// If no role was selected, don't produce a code
        	if (!admin && !student && !instructor && !staff && !reviewer) {
        		return;
        	}
            String invitationCode = databaseHelper.generateInvitationCode(admin, student, instructor, staff, reviewer);
            inviteCodeLabel.setText(invitationCode);
            
    	    // Set all user boolean values to false
            admin = false;
            student = false;
            instructor = false;
            staff = false;
            reviewer = false;
            
            inviteAdmin.setStyle("");
            inviteStudent.setStyle("");
            inviteInstructor.setStyle("");
            inviteStaff.setStyle("");
            inviteReviewer.setStyle("");
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
	    
	    buttons.getChildren().addAll(inviteAdmin, inviteStudent, inviteInstructor, inviteStaff, inviteReviewer);

        layout.getChildren().addAll(userLabel, buttons, showCodeButton, inviteCodeLabel, adminHomeButton, quitButton);
	    Scene inviteScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    	
    }
}