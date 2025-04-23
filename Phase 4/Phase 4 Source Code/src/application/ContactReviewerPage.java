package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;

public class ContactReviewerPage {

    private DatabaseHelper dbHelper;
    private String studentUser;
    private String reviewerUser;

    public ContactReviewerPage(DatabaseHelper dbHelper, String studentUser, String reviewerUser) {
        this.dbHelper = dbHelper;
        this.studentUser = studentUser;
        this.reviewerUser = reviewerUser;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Label title = new Label("Contact Reviewer: " + reviewerUser);
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message here...");
        
        Button sendButton = new Button("Send Message");
        sendButton.setOnAction(e -> {
            try {
                Message msg = new Message(studentUser, reviewerUser, messageArea.getText(), false);
                dbHelper.addMessage(msg, false);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Message sent!");
                alert.show();
                // Optionally return to the Q&A page
                new QuestionsAnswersPage(primaryStage, dbHelper, new User(studentUser, "", "student"));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            new QuestionsAnswersPage(primaryStage, dbHelper, new User(studentUser, "", "student"));
        });
        
        layout.getChildren().addAll(title, messageArea, sendButton, cancelButton);
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Contact Reviewer");
    }
}

