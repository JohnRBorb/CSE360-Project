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

public class MessageInboxPage {

    private DatabaseHelper dbHelper;
    private String reviewerUser;
    private ObservableList<Message> messages;

    public MessageInboxPage(DatabaseHelper dbHelper, String reviewerUser) {
        this.dbHelper = dbHelper;
        this.reviewerUser = reviewerUser;
        this.messages = FXCollections.observableArrayList();
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        Label title = new Label("Inbox - Messages for " + reviewerUser);

        ListView<Message> messageListView = new ListView<>(messages);
        messageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                } else {
                    String direction = msg.getSender().equals(reviewerUser) ? "To: " : "From: ";
                    setText(direction + (msg.getSender().equals(reviewerUser) ? msg.getReceiver() : msg.getSender()) +
                            "\n" + msg.getContent() +
                            "\nSent: " + msg.getTimestamp());
                    setStyle(msg.isRead() ? "" : "-fx-font-weight: bold;");
                }
            }
        });

        loadMessages();

        messageListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showMessageDetails(primaryStage, newValue);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new ReviewerHomePage(new User(reviewerUser, "", "reviewer"), dbHelper).show(primaryStage);
        });

        layout.getChildren().addAll(title, messageListView, backButton);
        VBox.setVgrow(messageListView, Priority.ALWAYS);
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Message Inbox");
    }

    private void loadMessages() {
        try {
            List<Message> receivedMessages = dbHelper.getMessagesForUser(reviewerUser);
            List<Message> sentMessages = dbHelper.getSentMessagesForUser(reviewerUser);
            messages.setAll(receivedMessages);
            messages.addAll(sentMessages);
            messages.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp())); // Sort by most recent
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
        Label receiverLabel = new Label("To: " + message.getReceiver());
        Label contentLabel = new Label("Message: " + message.getContent());
        Label timestampLabel = new Label("Sent: " + message.getTimestamp());

        TextArea replyArea = new TextArea();
        replyArea.setPromptText("Type your reply here...");
        replyArea.setWrapText(true);

        Button sendReplyButton = new Button("Send Reply");
        sendReplyButton.setOnAction(e -> {
            String replyContent = replyArea.getText().trim();
            if (!replyContent.isEmpty()) {
                Message replyMessage = new Message(reviewerUser, message.getSender(), replyContent, false);
                try {
                    dbHelper.addMessage(replyMessage, false);
                    loadMessages();
                    dialog.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button markAsReadButton = new Button("Mark as Read");
        markAsReadButton.setOnAction(e -> {
            try {
                dbHelper.markMessageAsRead(message.getId());
                loadMessages();
                dialog.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            try {
                dbHelper.deleteMessage(message.getId());
                loadMessages();
                dialog.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        HBox actionButtons = new HBox(10, sendReplyButton, markAsReadButton, deleteButton);

        dialogVbox.getChildren().addAll(senderLabel, receiverLabel, contentLabel, timestampLabel, replyArea, actionButtons);

        Scene dialogScene = new Scene(dialogVbox, 400, 300);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
