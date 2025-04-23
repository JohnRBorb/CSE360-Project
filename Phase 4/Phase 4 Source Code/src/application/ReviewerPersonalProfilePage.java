package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ReviewerPersonalProfilePage {
    private final DatabaseHelper db;
    private final String reviewer;
    private final User user;

    public ReviewerPersonalProfilePage(DatabaseHelper db, String reviewer, User user) {
        this.db = db;
        this.reviewer = reviewer;
		this.user = user;
    }

    public void show(Stage stage) {
        TabPane tabs = new TabPane();

        // ——— Tab 1: Experience ———
        TextArea expView = new TextArea();
        expView.setEditable(false);
        try {
            expView.setText(db.getReviewerExperience(reviewer));
        } catch(SQLException e) { e.printStackTrace(); }

        VBox expBox = new VBox(10, new Label("Experience"), expView);
        expBox.setPadding(new Insets(10));
        tabs.getTabs().add(new Tab("About", expBox));

        // ——— Tab 2: My Reviews ———
        List<Review> myReviews;
        try {
            myReviews = db.getReviewsByReviewer(reviewer);
        } catch(SQLException e) {
            e.printStackTrace();
            myReviews = List.of();
        }
        ListView<String> reviewList = new ListView<>();
        for (Review r : myReviews) {
            reviewList.getItems().add(
              String.format("[%s #%d] %s",
                r.getTargetType(), r.getTargetID(), r.getReviewText())
            );
        }
        VBox revBox = new VBox(10, new Label("My Reviews"), reviewList);
        revBox.setPadding(new Insets(10));
        tabs.getTabs().add(new Tab("Reviews", revBox));

        // ——— Tab 3: Feedback Received ———
        List<AuthorFeedback> feedbacks;
        try {
            feedbacks = db.getAuthorFeedbackForReviewerDetailed(reviewer);
        } catch(SQLException e) {
            e.printStackTrace();
            feedbacks = List.of();
        }
        ObservableList<String> fbItems = FXCollections.observableArrayList();
        for (AuthorFeedback fb : feedbacks) {
            fbItems.add(String.format("[%d★] From %s: %s",
                fb.getRating(), fb.getAuthor(), fb.getComment()));
        }
        ListView<String> fbList = new ListView<>(fbItems);
        VBox fbBox = new VBox(10, new Label("Feedback From Students"), fbList);
        fbBox.setPadding(new Insets(10));
        tabs.getTabs().add(new Tab("Feedback", fbBox));

        // ——— Back Button ———
        Button back = new Button("Back");
        back.setOnAction(e ->
            new ReviewerHomePage(user, db).show(stage)
        );

        VBox root = new VBox(10, new Label("Reviewer Profile: " + reviewer), tabs, back);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 600, 500));
        stage.setTitle("Reviewer Profile");
        stage.show();
    }
}
