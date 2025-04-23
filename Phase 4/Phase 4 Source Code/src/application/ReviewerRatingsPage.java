package application;

import java.sql.Timestamp;

import databasePart1.DatabaseHelper;
import databasePart1.QuestionsAnswersDatabase;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReviewerRatingsPage {
    private final DatabaseHelper db;
    private final QuestionsAnswersDatabase qaDb;
    private final User viewer;
    private final String reviewer;

    public ReviewerRatingsPage(User viewer, DatabaseHelper db, QuestionsAnswersDatabase qaDb, String reviewer) {
    	this.viewer = viewer;
        this.db = db;
        this.qaDb = qaDb;
        this.reviewer = reviewer;
    }

	public void show(Stage stage) throws Exception {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label header = new Label("Reviewer Profile: " + reviewer);
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Overall Score display
        double overall = new ReviewerScoringService(db, qaDb).computeReviewerScore(reviewer);
        ProgressBar overallBar = new ProgressBar(overall / 5.0);
        Label overallLabel = new Label(
            String.format("Overall Trust Score: %.2f / 5.00", overall)
        );

        // Domain Breakdown BarChart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 5, 1);
        BarChart<String,Number> chart = new BarChart<>(xAxis,yAxis);
        xAxis.setLabel("Domain");
        yAxis.setLabel("Avg Rating");

        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Thoroughness",
            qaDb.getAvgRating(reviewer, "thoroughness")));
        series.getData().add(new XYChart.Data<>("Clarity",
            qaDb.getAvgRating(reviewer, "clarity")));
        series.getData().add(new XYChart.Data<>("Timeliness",
            qaDb.getAvgRating(reviewer, "timeliness")));
        chart.getData().add(series);
        chart.setLegendVisible(false);

        // Volume & last active
        int count = qaDb.getRatedReviewCount(reviewer);
        Label volume = new Label("Reviews Rated: " + count);
        Timestamp last = qaDb.getLastReviewTime(reviewer);
        Label recent = new Label("Most Recent Review: " + last);

        // Close button
        Button back = new Button("Back");
        back.setOnAction(e -> {
            if (viewer.getRole().equalsIgnoreCase("student")) {
                new StudentHomePage().show(db, stage, viewer);
            } else {
                new ReviewerHomePage(viewer, db).show(stage);
            }
        });


        root.getChildren().addAll(
            header,
            overallLabel, overallBar,
            new Label("Domain Breakdown:"), chart,
            volume, recent,
            back
        );

        stage.setScene(new Scene(root, 500, 500));
        stage.setTitle("Reviewer Scorecard");
        stage.show();
    }
}
