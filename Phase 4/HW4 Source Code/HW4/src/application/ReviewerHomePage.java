package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class ReviewerHomePage {
    
    private User user;
    private DatabaseHelper databaseHelper;

    public ReviewerHomePage(User user, DatabaseHelper databaseHelper) {
        this.user = user;
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label userLabel = new Label("Hello, Reviewer " + user.getUserName() + "!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Add a button for accessing the Q&A page
        Button qaButton = new Button("Access Questions & Answers");
        qaButton.setOnAction(e -> {
            // Create and display the Q&A page (which is set up to handle reviewer actions)
            new QuestionsAnswersPage(primaryStage, databaseHelper, user);
        });
        
        // Inbox button to view private messages (for messages received from authors)
        Button inboxButton = new Button("Inbox");
        inboxButton.setOnAction(e -> {
            new MessageInboxPage(databaseHelper, user.getUserName()).show(primaryStage);
        });
        
        // Logout and Quit buttons as before
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit();
        });
        
        layout.getChildren().addAll(userLabel, qaButton, inboxButton, logoutButton, quitButton);
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Reviewer Page");
    }
}
