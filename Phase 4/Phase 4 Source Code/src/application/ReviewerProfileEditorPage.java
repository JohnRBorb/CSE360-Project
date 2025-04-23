package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ReviewerProfileEditorPage {
    private final DatabaseHelper db;
    private final String reviewer;
    private final User user;
    private TextArea experienceArea;

    public ReviewerProfileEditorPage(DatabaseHelper db, String reviewer, User user) {
        this.db = db;
        this.reviewer = reviewer;
        this.user = user;
    }

    public void show(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label header = new Label("My Reviewer Profile");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        experienceArea = new TextArea();
        experienceArea.setPromptText("Describe your reviewing experience, expertise, etc.");

        // Load existing experience
        try {
            String exp = db.getReviewerExperience(reviewer);
            experienceArea.setText(exp);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button saveBtn = new Button("Save Experience");
        saveBtn.setOnAction(e -> {
            try {
                db.updateReviewerExperience(reviewer, experienceArea.getText().trim());
                new Alert(Alert.AlertType.INFORMATION, "Profile updated!").showAndWait();
            } catch (SQLException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save.").showAndWait();
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e ->
            new ReviewerHomePage(user, db).show(stage)
        );

        root.getChildren().addAll(header, new Label("Experience:"), experienceArea, saveBtn, back);

        stage.setScene(new Scene(root, 500, 400));
        stage.setTitle("Edit Reviewer Profile");
        stage.show();
    }
}
