package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class TrustedReviewersPage {

    private DatabaseHelper dbHelper;
    private String studentUser;

    public TrustedReviewersPage(DatabaseHelper dbHelper, String studentUser) {
        this.dbHelper = dbHelper;
        this.studentUser = studentUser;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label title = new Label("Manage Trusted Reviewers for " + studentUser);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Input fields for reviewer username and weight
        TextField reviewerField = new TextField();
        reviewerField.setPromptText("Enter reviewer's username");
        TextField weightField = new TextField();
        weightField.setPromptText("Enter weight (integer)");

        Button addOrUpdateButton = new Button("Add/Update Trusted Reviewer");
        addOrUpdateButton.setOnAction(e -> {
            String reviewer = reviewerField.getText().trim();
            try {
                int weight = Integer.parseInt(weightField.getText().trim());
                dbHelper.addOrUpdateTrustedReviewer(studentUser, reviewer, weight);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Trusted reviewer added/updated.");
                alert.show();
                show(primaryStage); // refresh page
            } catch (NumberFormatException nfe) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid integer for weight.");
                alert.show();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // List current trusted reviewers
        Label currentLabel = new Label("Current Trusted Reviewers:");
        ListView<String> trustedList = new ListView<>();
        try {
            List<String> reviewers = dbHelper.getTrustedReviewers(studentUser);
            trustedList.getItems().addAll(reviewers);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Button removeButton = new Button("Remove Selected Reviewer");
        removeButton.setOnAction(e -> {
            String selected = trustedList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    dbHelper.removeTrustedReviewer(studentUser, selected);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Trusted reviewer removed.");
                    alert.show();
                    show(primaryStage); // refresh page
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            // Return to StudentHomePage (or another appropriate page)
            new StudentHomePage().show(dbHelper, primaryStage, new User(studentUser, "", "student"));
        });

        layout.getChildren().addAll(title, reviewerField, weightField, addOrUpdateButton,
                                      currentLabel, trustedList, removeButton, backButton);
        Scene scene = new Scene(layout, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Trusted Reviewers");
    }
}
