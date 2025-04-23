package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class StaffHomePage {

    private DatabaseHelper dbHelper;
    private User user; // the currently logged-in staff user

    // Updated constructor to include the User object
    public StaffHomePage(DatabaseHelper dbHelper, User user) {
        this.dbHelper = dbHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label welcomeLabel = new Label("Welcome, Staff Member " + user.getUserName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Existing buttons...
        Button messagingButton = new Button("Send Message to Users");
        messagingButton.setOnAction(e -> {
            new StaffMessagingPage(dbHelper, user).show(primaryStage);
        });

        // Inbox button to view private messages (for messages received from authors)
        Button inboxButton = new Button("Inbox");
        inboxButton.setOnAction(e -> {
            new StaffInboxPage(dbHelper, user.getUserName(), user).show(primaryStage);
        });

        // New button to access the Questions & Answers page.
        Button qaPageButton = new Button("View Questions & Answers");
        qaPageButton.setOnAction(e -> {
            new QuestionsAnswersPage(primaryStage, dbHelper, user).show(primaryStage);
        });
        
        Button manageUsersButton = new Button("Manage Problematic Users");
        manageUsersButton.setOnAction(e -> new StaffManageProblematicUsersPage(dbHelper, user).show(primaryStage));
        
        Button announceButton = new Button("Create Announcement");
        announceButton.setOnAction(e -> {
            // Open a new window to compose the announcement
            new AnnouncementComposePage(dbHelper).show(primaryStage);
        });
        
        Button requestRoleRemovalButton = new Button("Request Role Removal");
        requestRoleRemovalButton.setOnAction(e -> {
            new RoleRemovalRequestPage(dbHelper, user).show(primaryStage);
        });
        
        Button moderationButton = new Button("Moderation Queue");
        moderationButton.setOnAction(e ->
            new ModerationRequestsPage(dbHelper).show(primaryStage)
        );

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            new SetupLoginSelectionPage(dbHelper).show(primaryStage);
        });

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> {
            dbHelper.closeConnection();
            Platform.exit();
        });

        // Adding buttons to layout (order as needed)
        layout.getChildren().addAll(
            welcomeLabel, 
            messagingButton, 
            inboxButton, 
            qaPageButton,
            manageUsersButton,
            announceButton,
            requestRoleRemovalButton,
            moderationButton,
            logoutButton, 
            quitButton
        );

        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Home Page");
    }
}
