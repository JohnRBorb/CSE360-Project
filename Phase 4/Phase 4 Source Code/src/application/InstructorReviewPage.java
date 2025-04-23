package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;

import java.sql.SQLException;
import java.util.List;

public class InstructorReviewPage {
	private User user;
    private DatabaseHelper dbHelper;
    private QuestionsAnswersDatabase qaDb;
    private VBox vbox;
    private Stage stage;

    public InstructorReviewPage(DatabaseHelper dbHelper, User user) {
        this.dbHelper = dbHelper;
        this.user = user;
        this.qaDb = new QuestionsAnswersDatabase();
        try {
            qaDb.connectToQuestionAnswerDatabase();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void show(Stage primaryStage) {
        stage = primaryStage;
        HBox root = new HBox(10);
        root.setPadding(new Insets(10));

        // Left: list of questions
        ListView<Button> questionList = new ListView<>();
        vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        try {
            int maxQ = qaDb.numQuestions();
            for (int i = 1; i <= maxQ; i++) {
                if (qaDb.isDeleted(i)) continue;
                String title = qaDb.getTitleFromQuestionID(i);
                Button qBtn = new Button(title);
                qBtn.setMaxWidth(Double.MAX_VALUE);
                final int qID = i;
                qBtn.setOnAction(e -> {
					try {
						showQuestionDetail(qID);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
                questionList.getItems().add(qBtn);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Back button
        Button back = new Button("Back");
        back.setOnAction(e -> new InstructorHomePage(user, dbHelper).show(stage));
        VBox leftBox = new VBox(10, new Label("All Questions"), questionList, back);
        leftBox.setPadding(new Insets(10));

        root.getChildren().addAll(leftBox, new ScrollPane(vbox));

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Instructor Review");
    }

    private void showQuestionDetail(int qID) throws SQLException {
        vbox.getChildren().clear();
        String[] qInfo = qaDb.getQuestionInfo(qID);
		Label qTitle = new Label("Q: " + qInfo[1]);
		Label qBody = new Label(qInfo[2]);
		qBody.setWrapText(true);

		Button flagQ = new Button("Flag Question");
		flagQ.setOnAction(e -> flagContent("question", qID));

		vbox.getChildren().addAll(qTitle, qBody, flagQ, new Separator());

		// Show answers
		List<String[]> answers = qaDb.getAnswers(qID);
	    for (int i = 0; i < answers.size(); i++) {
	        String[] ans = answers.get(i);
	        int ansID = qaDb.getAnswerID(qID, i);

	        Label aAuthor = new Label("Answer by " + ans[0]);
	        aAuthor.setStyle("-fx-font-weight: bold;");
	        Label aBody = new Label(ans[1]);
	        aBody.setWrapText(true);

	        // Flag & Mark Reviewed buttons for this answer
	        Button flagA = new Button("Flag Answer");
	        flagA.setOnAction(e -> flagContent("answer", ansID));

	        Button markAReviewed = new Button("Mark Answer Reviewed");
	        markAReviewed.setOnAction(e -> {
	            try {
	                qaDb.markContentReviewed("answer", ansID);
	                new Alert(Alert.AlertType.INFORMATION, "Answer marked reviewed!").showAndWait();
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        });

	        vbox.getChildren().addAll(
	            aAuthor, 
	            aBody, 
	            new HBox(10, flagA, markAReviewed),
	            new Separator()
	        );

	        // Now list all reviews for this answer
	        List<Review> ansReviews = qaDb.getReviewsForTarget(ansID, "answer");
	        if (!ansReviews.isEmpty()) {
	            vbox.getChildren().add(new Label("All Reviews for this Answer:"));
	            for (Review r : ansReviews) {
	                Label revLabel = new Label(r.getReviewerName() + " says: " + r.getReviewText());
	                revLabel.setWrapText(true);

	                Button rateBtn = new Button("Rate Review");
	                rateBtn.setOnAction(evt -> showReviewDetail(r));

	                vbox.getChildren().addAll(revLabel, rateBtn, new Separator());
	            }
	        }
	    }

	    // Show Comments
	    List<String[]> comments = qaDb.getComments(qID);
	    for (int i = 0; i < comments.size(); i++) {
	        String[] c = comments.get(i);
	        int cID = qaDb.getCommentID(qID, i);

	        Label cAuthor = new Label("Comment by " + c[0]);
	        cAuthor.setStyle("-fx-font-weight: bold;");
	        Label cBody = new Label(c[1]);
	        cBody.setWrapText(true);

	        // Flag & Mark Reviewed buttons for this comment
	        Button flagC = new Button("Flag Comment");
	        flagC.setOnAction(e -> flagContent("comment", cID));

	        Button markCReviewed = new Button("Mark Comment Reviewed");
	        markCReviewed.setOnAction(e -> {
	            try {
	                qaDb.markContentReviewed("comment", cID);
	                new Alert(Alert.AlertType.INFORMATION, "Comment marked reviewed!").showAndWait();
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        });

	        vbox.getChildren().addAll(
	            cAuthor,
	            cBody,
	            new HBox(10, flagC, markCReviewed),
	            new Separator()
	        );

	    }

	    // Show Reviews of the question itself
	    List<Review> qReviews = qaDb.getReviewsForTarget(qID, "question");
	    if (!qReviews.isEmpty()) {
	        vbox.getChildren().add(new Label("All Reviews for this Question:"));
	        for (Review r : qReviews) {
	            Label revLabel = new Label(r.getReviewerName() + " says: " + r.getReviewText());
	            revLabel.setWrapText(true);

	            Button rateBtn = new Button("Rate Review");
	            rateBtn.setOnAction(evt -> showReviewDetail(r));

	            vbox.getChildren().addAll(revLabel, rateBtn, new Separator());
	        }
	    }
		
		TextArea feedbackArea = new TextArea();
		feedbackArea.setPromptText("Send coaching feedback to author…");
		Button sendFeedback = new Button("Send Feedback");
		sendFeedback.setOnAction(e -> {
		    // determine author username:
		    String author = qaDb.getQuestionInfo(qID)[0];
		    String msg = feedbackArea.getText().trim();
		    if (!msg.isEmpty()) {
		        try {
					dbHelper.addMessage(new Message("Instructor", author, msg, false), false);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        feedbackArea.clear();
		        new Alert(Alert.AlertType.INFORMATION, "Feedback sent!").show();
		    }
		});
		vbox.getChildren().addAll(feedbackArea, sendFeedback);

    }
    
    private void showReviewDetail(Review r) {
        VBox reviewBox = new VBox(5);
        Label text = new Label(r.getReviewText());
        text.setWrapText(true);

        // Spinners for ratings
        Spinner<Integer> thoroughSpinner = new Spinner<>(1, 5, 
            r.getThoroughnessRating() != null ? r.getThoroughnessRating() : 3);
        Spinner<Integer> claritySpinner = new Spinner<>(1, 5, 
            r.getClarityRating() != null ? r.getClarityRating() : 3);
        Spinner<Integer> timelySpinner = new Spinner<>(1, 5, 
            r.getTimelinessRating() != null ? r.getTimelinessRating() : 3);

        Button saveRatings = new Button("Save Ratings");
        saveRatings.setOnAction(e -> {
            try {
                qaDb.updateReviewRatings(
                    r.getId(),
                    thoroughSpinner.getValue(),
                    claritySpinner.getValue(),
                    timelySpinner.getValue()
                );
                new Alert(Alert.AlertType.INFORMATION, "Ratings saved").showAndWait();
            } catch (SQLException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save").showAndWait();
            }
        });

        reviewBox.getChildren().addAll(
            new Label("Your Ratings (1–5):"),
            new HBox(10, new Label("Thoroughness:"), thoroughSpinner),
            new HBox(10, new Label("Clarity:"),     claritySpinner),
            new HBox(10, new Label("Timeliness:"),  timelySpinner),
            saveRatings
        );
        vbox.getChildren().add(reviewBox);
    }


    private void flagContent(String contentType, int contentId) {
        // Prompt the instructor for a reason
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Flag " + contentType);
        dialog.setHeaderText("Why are you flagging this " + contentType + "?");
        dialog.setContentText("Reason:");
        
        dialog.showAndWait().ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please provide a reason.").showAndWait();
                return;
            }
            
            try {
                // insert into moderation_requests:
                //    content_type, content_id, instructor, reason
                String instructorUser = user.getUserName();  
                dbHelper.addModerationRequest(contentType, contentId, instructorUser, reason);
                
                new Alert(Alert.AlertType.INFORMATION, 
                          "Flag submitted for review.").showAndWait();
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, 
                          "Failed to flag content: " + ex.getMessage()).showAndWait();
            }
        });
    }

}

