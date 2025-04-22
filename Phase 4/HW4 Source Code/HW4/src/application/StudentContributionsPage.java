package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import databasePart1.QuestionsAnswersDatabase;
import java.sql.SQLException;
import java.util.List;

public class StudentContributionsPage {

    private DatabaseHelper dbHelper;
    private String studentUser;

    public StudentContributionsPage(DatabaseHelper dbHelper, String studentUser) {
        this.dbHelper = dbHelper;
        this.studentUser = studentUser;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Label title = new Label("Contributions for " + studentUser);

        QuestionsAnswersDatabase qaDatabase = new QuestionsAnswersDatabase();
        try {
            qaDatabase.connectToQuestionAnswerDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ListView<String> contributionsList = new ListView<>();
        int numQ = 0;
        try {
            numQ = qaDatabase.numQuestions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Loop through questions and filter by studentUser
        for (int i = 1; i <= numQ; i++) {
            try {
                String[] qInfo = qaDatabase.getQuestionInfo(i);
                if (qInfo[0].equals(studentUser)) {
                    contributionsList.getItems().add("Q: " + qInfo[1] + " - " + qInfo[2]);
                    List<String[]> answers = qaDatabase.getAnswers(i);
                    for (String[] ans : answers) {
                        if (ans[0].equals(studentUser)) {
                            contributionsList.getItems().add("    A: " + ans[1]);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new InstructorHomePage().show(dbHelper, primaryStage);
        });

        layout.getChildren().addAll(title, contributionsList, backButton);
        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Contributions");
    }
}
