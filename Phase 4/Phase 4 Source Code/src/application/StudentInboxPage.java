package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class StudentInboxPage {

    private DatabaseHelper dbHelper;
    private User studentUser;
    private ObservableList<Message> messages;

    public StudentInboxPage(DatabaseHelper dbHelper, User studentUser) {
        this.dbHelper = dbHelper;
        this.studentUser = studentUser;
        this.messages = FXCollections.observableArrayList();
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        Label title = new Label("Inbox - Messages for " + studentUser.getUserName());

        ListView<Message> messageListView = new ListView<>(messages);
        messageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                } else {
                    String direction = msg.getSender().equals(studentUser.getUserName()) ? "To: " : "From: ";
                    setText(direction + (msg.getSender().equals(studentUser.getUserName()) ? msg.getReceiver() : msg.getSender()) +
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
            new StudentHomePage().show(dbHelper, primaryStage, studentUser);
        });

        layout.getChildren().addAll(title, messageListView, backButton);
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Inbox");
    }

    private void loadMessages() {
        try {
            List<Message> receivedMessages = dbHelper.getMessagesForUser(studentUser.getUserName());
            messages.setAll(receivedMessages);
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
        Label contentLabel = new Label("Message: " + message.getContent());
        Label timestampLabel = new Label("Sent: " + message.getTimestamp());

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

        dialogVbox.getChildren().addAll(senderLabel, contentLabel, timestampLabel, markAsReadButton, deleteButton);

        Scene dialogScene = new Scene(dialogVbox, 400, 250);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}

