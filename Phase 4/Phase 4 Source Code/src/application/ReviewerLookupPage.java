package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import databasePart1.QuestionsAnswersDatabase;

import java.sql.SQLException;
import java.util.List;

public class ReviewerLookupPage {
    private DatabaseHelper db;
    private QuestionsAnswersDatabase qaDb;
    private User user;
    private ObservableList<Hyperlink> links = FXCollections.observableArrayList();

    public ReviewerLookupPage(DatabaseHelper db, User user) {
        this.db   = db;
        this.user = user;
        this.qaDb = new QuestionsAnswersDatabase();
        try {
            qaDb.connectToQuestionAnswerDatabase();
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, 
            		"Failed to open QA database: " + ex.getMessage())
            .showAndWait();
        }
    }

    public void show(Stage stage) throws SQLException {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        ListView<Hyperlink> listView = new ListView<>(links);

        List<String> reviewers = db.getAllReviewers();
        for (String rev : reviewers) {
            Hyperlink link = new Hyperlink(rev);
            link.setOnAction(e -> {
                try {
                    new ReviewerRatingsPage(user, db, qaDb, rev).show(stage);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            });
            links.add(link);
        }

        root.getChildren().addAll(
            new javafx.scene.control.Label("Select a Reviewer:"),
            listView
        );
        stage.setScene(new Scene(root, 400, 500));
        stage.setTitle("Find Reviewers");
    }
}
