package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

/**
 * This page displays a welcome message for the Student.
 */
public class StudentHomePage {

    private User user;
    private DatabaseHelper databaseHelper;
    QuestionsAnswersDatabase qaDb = new QuestionsAnswersDatabase();

    public void show(DatabaseHelper databaseHelper, Stage primaryStage, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
        try {
			qaDb.connectToQuestionAnswerDatabase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        VBox layout = new VBox(10); // Added spacing of 10 between elements
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display Hello user
        Label userLabel = new Label("Hello, " + user.getUserName() + "!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Button to navigate to Questions and Answers page
        Button qabutton = new Button("Ask a Question or Propose an Answer");
        qabutton.setOnAction(a -> new QuestionsAnswersPage(primaryStage, databaseHelper, user));
        
        // Inbox button to view private messages (for messages received from authors)
        Button inboxButton = new Button("Inbox");
        inboxButton.setOnAction(e -> {
            new MessageInboxPage(databaseHelper, user.getUserName()).show(primaryStage);
        });

        // Button to request reviewer role
        Button requestReviewerButton = new Button("Request Reviewer Role");
        requestReviewerButton.setOnAction(e -> {
            try {
                databaseHelper.addReviewerRequest(user.getUserName());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Reviewer request submitted!");
                alert.show();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Button to manage trusted reviewers
        Button manageTrustedReviewersButton = new Button("Manage Trusted Reviewers");
        manageTrustedReviewersButton.setOnAction(e -> new TrustedReviewersPage(databaseHelper, user.getUserName()).show(primaryStage));

        Button findReviewersButton = new Button("Find Reviewers");
        findReviewersButton.setOnAction(e -> {
            try {
                new ReviewerLookupPage(databaseHelper, user).show(primaryStage);
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Button to logout and navigate back to login page
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(a -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        // Button to quit the application
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit(); // Exit the JavaFX application
        });

        // Adding all buttons to the layout
        layout.getChildren().addAll(userLabel, qabutton, inboxButton, requestReviewerButton, 
        		manageTrustedReviewersButton, findReviewersButton, logoutButton, quitButton);
        Scene userScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Student Page");
    }
}
