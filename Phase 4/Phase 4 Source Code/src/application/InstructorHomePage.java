package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import databasePart1.QuestionsAnswersDatabase;

public class InstructorHomePage {
    
    private User user;
    private DatabaseHelper databaseHelper;
    private ReviewerScoringService scoringService;

    public InstructorHomePage(User user, DatabaseHelper databaseHelper) {
        this.user = user;
        this.databaseHelper = databaseHelper;

        // Initialize the QA DB and scoring service
        QuestionsAnswersDatabase qaDb = new QuestionsAnswersDatabase();
        try {
            qaDb.connectToQuestionAnswerDatabase();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
			this.scoringService = new ReviewerScoringService(databaseHelper, qaDb);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label userLabel = new Label("Hello, Instructor " + user.getUserName() + "!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button reviewStudentContributions = new Button("Review Student Contributions");
        reviewStudentContributions.setOnAction(a -> {
            TextField studentField = new TextField();
            studentField.setPromptText("Enter Student Username");
            Button viewButton = new Button("View Contributions");
            viewButton.setOnAction(ev -> {
                String studentUser = studentField.getText().trim();
                new StudentContributionsPage(user, databaseHelper, studentUser)
                    .show(primaryStage);
            });
            VBox contribLayout = new VBox(10, studentField, viewButton);
            primaryStage.setScene(new Scene(contribLayout, 400, 200));
        });

        Button reviewerRequestsButton = new Button("Reviewer Requests");
        reviewerRequestsButton.setOnAction(e ->
            new ReviewerRequestPage(databaseHelper, user).show(primaryStage)
        );

        Button reviewContentButton = new Button("Review Content");
        reviewContentButton.setOnAction(e ->
            new InstructorReviewPage(databaseHelper, user).show(primaryStage)
        );

        Button manageParamsButton = new Button("Edit Score Parameters");
        manageParamsButton.setOnAction(e ->
            new InstructorScoreParametersPage(user, databaseHelper, user.getUserName())
                .show(primaryStage)
        );

        Button lookupScoreButton = new Button("Compute Reviewer Score");
        lookupScoreButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Compute Reviewer Score");
            dialog.setHeaderText("Enter reviewer username:");
            dialog.setContentText("Reviewer:");
            dialog.showAndWait().ifPresent(reviewer -> {
                try {
                    double score = scoringService.computeReviewerScore(reviewer);
                    new Alert(Alert.AlertType.INFORMATION,
                        "Reviewer \"" + reviewer + "\" has a score of: "
                         + String.format("%.2f", score))
                    .showAndWait();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR,
                        "Error computing score: " + ex.getMessage())
                    .showAndWait();
                }
            });
        });

        Button inboxButton = new Button("Inbox");
        inboxButton.setOnAction(e ->
            new InstructorInboxPage(databaseHelper, user.getUserName(), user)
                .show(primaryStage)
        );
        
        Button moderationButton = new Button("Moderation Queue");
        moderationButton.setOnAction(e ->
            new ModerationRequestsPage(databaseHelper).show(primaryStage)
        );

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(a ->
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage)
        );

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit();
        });

        layout.getChildren().addAll(
            userLabel,
            reviewStudentContributions,
            reviewerRequestsButton,
            reviewContentButton,
            manageParamsButton,
            lookupScoreButton,
            moderationButton,
            inboxButton,
            logoutButton,
            quitButton
        );

        primaryStage.setScene(new Scene(layout, 800, 450));
        primaryStage.setTitle("Instructor Dashboard");
    }
}
