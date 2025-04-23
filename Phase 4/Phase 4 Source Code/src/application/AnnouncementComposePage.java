package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

public class AnnouncementComposePage {
    private DatabaseHelper dbHelper;

    public AnnouncementComposePage(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void show(Stage primaryStage) {
        Stage stage = new Stage();
        stage.setTitle("Compose Announcement");

        // Create UI elements
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Enter your announcement content here...");
        Button sendButton = new Button("Send Announcement");

        // Set up the layout
        VBox layout = new VBox(10, contentArea, sendButton);
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();

        // Set up the button action
        sendButton.setOnAction(e -> {
            String content = contentArea.getText();
            if (!content.isEmpty()) {
                // Create and distribute the announcement
                String title = "System Announcement";
                try {
					dbHelper.createAnnouncement(title, content);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                try {
					dbHelper.distributeAnnouncement(title, content);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                stage.close(); // Close the compose window
            } else {
                // Handle empty content (optional)
            }
        });
    }
}
