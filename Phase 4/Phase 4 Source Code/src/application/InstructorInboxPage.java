package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

public class InstructorInboxPage {

    private DatabaseHelper dbHelper;
    private User user;
    private String instructorUser;
    private ObservableList<Message> messages;

    public InstructorInboxPage(DatabaseHelper dbHelper, String instructorUser, User user) {
        this.dbHelper = dbHelper;
        this.instructorUser = instructorUser;
        this.user = user;
        this.messages = FXCollections.observableArrayList();
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        Label title = new Label("Inbox - Messages for " + instructorUser);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<Message> messageListView = new ListView<>(messages);
        messageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                } else {
                    setText("From: " + msg.getSender() + "\n" +
                            msg.getContent() + "\nSent: " + msg.getTimestamp());
                    setStyle(msg.isRead() ? "" : "-fx-font-weight: bold;");
                }
            }
        });

        loadMessages();

        messageListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showMessageDetails(primaryStage, newVal);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new InstructorHomePage(user, dbHelper).show(primaryStage);
        });

        layout.getChildren().addAll(title, messageListView, backButton);
        VBox.setVgrow(messageListView, Priority.ALWAYS);
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Instructor Message Inbox");
    }

    private void loadMessages() {
        try {
            List<Message> receivedMessages = dbHelper.getMessagesForUser(instructorUser);
            messages.setAll(receivedMessages);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showMessageDetails(Stage primaryStage, Message message) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Message Details");

        VBox dialogVbox = new VBox(10);
        dialogVbox.setPadding(new Insets(20));

        Label senderLabel = new Label("From: " + message.getSender());
        Label contentLabel = new Label("Message: " + message.getContent());
        Label timestampLabel = new Label("Sent: " + message.getTimestamp());

        TextArea replyArea = new TextArea();
        replyArea.setPromptText("Type your reply here...");
        replyArea.setWrapText(true);

        Button sendReplyButton = new Button("Send Reply");
        sendReplyButton.setOnAction(e -> {
            String replyContent = replyArea.getText().trim();
            if (!replyContent.isEmpty()) {
                Message replyMessage = new Message(instructorUser, message.getSender(), replyContent, false);
                try {
                    dbHelper.addMessage(replyMessage, false);
                    dialog.close();
                    loadMessages();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button markReadButton = new Button("Mark as Read");
        markReadButton.setOnAction(e -> {
            try {
                dbHelper.markMessageAsRead(message.getId());
                dialog.close();
                loadMessages();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        HBox buttonBox = new HBox(10, sendReplyButton, markReadButton);
        dialogVbox.getChildren().addAll(senderLabel, contentLabel, timestampLabel, replyArea, buttonBox);

        Scene dialogScene = new Scene(dialogVbox, 400, 300);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}