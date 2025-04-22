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

public class StaffMessagingPage {

    private DatabaseHelper dbHelper;
    private User user;
    private ObservableList<User> userList;

    public StaffMessagingPage(DatabaseHelper dbHelper, User user) {
        this.dbHelper = dbHelper;
        this.user = user;
        this.userList = FXCollections.observableArrayList();
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        Label title = new Label("All Users (Select to Message)");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<User> listView = new ListView<>(userList);
        // Use a custom cell to show username and role
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getUserName() + " (" + user.getRole() + ")");
                }
            }
        });

        // Load users from the database
        loadUsers();

        // When a user is selected, enable the "Message" button.
        Button messageButton = new Button("Message Selected User");
        messageButton.setDisable(true);
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            messageButton.setDisable(newVal == null);
        });
        messageButton.setOnAction(e -> {
            User selectedUser = listView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                showMessageDialog(primaryStage, selectedUser);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new StaffHomePage(dbHelper, user).show(primaryStage);
        });

        VBox.setVgrow(listView, Priority.ALWAYS);
        layout.getChildren().addAll(title, listView, messageButton, backButton);
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Messaging");
    }

    private void loadUsers() {
        try {
            List<User> users = dbHelper.getAllUsers();
            userList.setAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showMessageDialog(Stage primaryStage, User recipient) {
        // Create a new window (dialog) for sending a message.
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Send Message to " + recipient.getUserName());

        VBox dialogVbox = new VBox(10);
        dialogVbox.setPadding(new Insets(20));

        Label recipientLabel = new Label("To: " + recipient.getUserName());
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message here...");
        messageArea.setWrapText(true);
        messageArea.setPrefHeight(100);

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String content = messageArea.getText().trim();
            if (!content.isEmpty()) {
                // Create a new message from staff to recipient.
                Message newMessage = new Message("staff", recipient.getUserName(), content, false);
                try {
                    dbHelper.addMessage(newMessage, false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Message sent to " + recipient.getUserName() + "!");
                    alert.show();
                    dialog.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, sendButton, cancelButton);
        dialogVbox.getChildren().addAll(recipientLabel, messageArea, buttonBox);

        Scene dialogScene = new Scene(dialogVbox, 400, 250);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
