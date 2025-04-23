package application;

import java.sql.SQLException;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import databasePart1.QuestionsAnswersDatabase;
import databasePart1.DatabaseHelper;

public class QuestionsAnswersPage {

	// Initialize the database
    private static final QuestionsAnswersDatabase qaDatabase = new QuestionsAnswersDatabase();
    
    // Initialize the vbox for the right side of the screen
    private VBox vbox = new VBox(10);
    
    // Label to display errors
    Label errorLabel = new Label("");
    
    // Initialize the user name variable
    private String userName;
    
    // Initialize a boolean to check if the resolved checkbox is checked
    private boolean isChecked = false;
    
    // Initialize a boolean to check for filtering by trusted reviewers
    private boolean filterByTrusted = false; // Instance variable
    
    // Initialize filter text
    private String filterText = "";
    
    final User user;
    final DatabaseHelper databaseHelper;
    
    private boolean connected = false;
    
    public QuestionsAnswersPage(Stage primaryStage, DatabaseHelper databaseHelper, User user) {
    	this.databaseHelper = databaseHelper;
    	this.user = user;
    	show(primaryStage);
    }
    
    	
    // Show the main page
    public void show(Stage primaryStage) {
    	if (!connected) {
    		try {
    			qaDatabase.connectToQuestionAnswerDatabase();
    			connected = true;
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	userName = user.getUserName();
    	
        // Initialize vbox for right side of window
        vbox = new VBox(10);
        vbox.setAlignment(Pos.TOP_LEFT);

        // Set sizing and padding for posts vbox
        VBox posts = new VBox(10);
        posts.setPadding(new Insets(10));
        posts.setPrefWidth(300);
        posts.setMaxWidth(300);
        posts.setAlignment(Pos.TOP_LEFT);

        // Button to bring up question creation page
        Button addQuestion = new Button("Ask a Question");
        addQuestion.setMinWidth(250);
        addQuestion.setOnAction(a -> {
            createQuestion(primaryStage);
        });
        
        // filtering of unresolved questions
        CheckBox ownUnresolved = new CheckBox("View only own unresolved questions");
        ownUnresolved.setOnAction(a -> {
        	if (isChecked) {
        		isChecked = false;
        	} else {
        		isChecked = true;
        	}
        	show(primaryStage);
        });
        ownUnresolved.setSelected(isChecked);
        
        // Add filter text field and button
        TextField filterField = new TextField();
        filterField.setPromptText("Filter questions...");
        filterField.setText(filterText);
        filterField.setMinWidth(180);
        
        Button filterButton = new Button("Filter");
        filterButton.setOnAction(a -> {
            filterText = filterField.getText().trim().toLowerCase();
            show(primaryStage);
        });
        
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.getChildren().addAll(filterField, filterButton);
        
        posts.getChildren().addAll(addQuestion, ownUnresolved, filterBox);

        try {
            for (int i = qaDatabase.numQuestions(); i > 0; i--) {

                // Skip to next question if current was deleted
                if (qaDatabase.isDeleted(i)) {
                    continue;
                }
                
                if (ownUnresolved.isSelected()) {
                	if (!(!(qaDatabase.isResolved(i)) && qaDatabase.doesUserOwnQuestion(i, userName))) {
                		continue;
                	}
                }
                
                final int qID = i;
                String questionTitle = qaDatabase.getTitleFromQuestionID(qID);
                String questionBody = "";
                
                try {
                    String[] questionInfo = qaDatabase.getQuestionInfo(qID);
                    questionBody = questionInfo[2].toLowerCase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Skip if filter text is not empty and neither title nor body contains the filter text
                if (!filterText.isEmpty() && 
                    !questionTitle.toLowerCase().contains(filterText) && 
                    !questionBody.contains(filterText)) {
                    continue;
                }
                
                // Create button for each question
                Button postButton = new Button(questionTitle);
                
                // If the current question is resolved, add a tag
                if (qaDatabase.isResolved(i)) {
                	postButton.setText(postButton.getText() + " *Answered*");
                }
                
                postButton.setMinWidth(250);
                postButton.setStyle("-fx-background-color: transparent;");
                postButton.setWrapText(true);

                // When a question is clicked on, open it on the right
                postButton.setOnAction(a -> {
                    printQuestion(qaDatabase.getQuestionInfo(qID), primaryStage, qID);
                });

                // Create separator between questions
                Separator separator = new Separator();
                separator.setMinWidth(250);
                posts.getChildren().addAll(postButton, separator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // ScrollPane to make posts vbox scrollable
        ScrollPane postsScrollPane = new ScrollPane();
        postsScrollPane.setContent(posts);
        postsScrollPane.setFitToWidth(true);
        postsScrollPane.setPrefWidth(300);

        // ScrollPane to make vbox (questions and answers) scrollable
        ScrollPane rightScrollPane = new ScrollPane();
        rightScrollPane.setContent(vbox);
        rightScrollPane.setFitToWidth(true);
        rightScrollPane.setFitToHeight(true);
        rightScrollPane.setPrefWidth(550);
        rightScrollPane.setPadding(new Insets (10));

        // HBox to combine posts and vbox
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(postsScrollPane, rightScrollPane);
        hbox.setPadding(new Insets(10));
        hbox.setAlignment(Pos.TOP_LEFT);

        // Create scene
        Scene scene = new Scene(hbox, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Show a question and answers on right side of screen
    public void printQuestion(String[] info, Stage primaryStage, int id) {
    	
        // Clear the vbox initially
        vbox.getChildren().clear();
        vbox.setAlignment(Pos.CENTER_LEFT);

        // Create labels for title, user name, and body
        Label titleLabel = new Label(info[1]);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        titleLabel.setWrapText(true);

        Label userNameLabel = new Label(info[0]);
        userNameLabel.setStyle("-fx-font-size: 14px;");
        userNameLabel.setWrapText(true);
        
        Label bodyLabel = new Label(info[2]);
        bodyLabel.setWrapText(true);
        bodyLabel.setWrapText(true);
        
        // Separator between question and answers
        Separator separator = new Separator();
        separator.setMinWidth(250);

        vbox.getChildren().addAll(titleLabel, userNameLabel, bodyLabel, separator);
        
        // Only for student users, add a filter checkbox:
        CheckBox trustedFilterCheckBox = new CheckBox("Show only reviews from my trusted reviewers");
        trustedFilterCheckBox.setSelected(filterByTrusted);
        trustedFilterCheckBox.setOnAction(e -> {
            filterByTrusted = trustedFilterCheckBox.isSelected();
            show(primaryStage);
        });
        vbox.getChildren().add(trustedFilterCheckBox);

        
        // ---------------------------
        // Reviewer-specific: Review for the Question
        if(user.getRole().equalsIgnoreCase("reviewer")) {
            // Reviewer can add/update/delete their review for the question
            try {
                Review existingReview = qaDatabase.getReviewForTargetByReviewer(id, "question", user.getUserName());
                if(existingReview != null) {
                    Label existingReviewLabel = new Label("Your review: " + existingReview.getReviewText());
                    existingReviewLabel.setWrapText(true);
                    
                    Button updateReviewButton = new Button("Update Review");
                    updateReviewButton.setOnAction(a -> {
                        TextInputDialog dialog = new TextInputDialog(existingReview.getReviewText());
                        dialog.setTitle("Update Review");
                        dialog.setHeaderText("Update your review:");
                        dialog.setContentText("New review text:");
                        dialog.showAndWait().ifPresent(newText -> {
                            try {
                                qaDatabase.updateReview(existingReview.getId(), newText);
                                show(primaryStage); // Refresh page
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });
                    });
                    
                    Button deleteReviewButton = new Button("Delete Review");
                    deleteReviewButton.setOnAction(a -> {
                        try {
                            qaDatabase.deleteReview(existingReview.getId());
                            show(primaryStage);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });
                    
                    vbox.getChildren().addAll(existingReviewLabel, updateReviewButton, deleteReviewButton);
                } else {
                    Label promptLabel = new Label("Enter your review for this question:");
                    TextField reviewField = new TextField();
                    reviewField.setPromptText("Enter your review here");
                    Button submitReview = new Button("Submit Review");
                    submitReview.setOnAction(a -> {
                        try {
                            Review newReview = new Review(id, "question", user.getUserName(), reviewField.getText());
                            qaDatabase.addReview(newReview);
                            show(primaryStage);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });
                    vbox.getChildren().addAll(promptLabel, reviewField, submitReview);
                }
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        } else if(user.getRole().equalsIgnoreCase("student") || user.getRole().equalsIgnoreCase("staff")) {
            // Student: Display all reviews for the question, read-only.
        	try {
                List<Review> reviews = qaDatabase.getReviewsForTarget(id, "question");
                if(!reviews.isEmpty()){
                    Label reviewsHeader = new Label("Reviews for this question:");
                    reviewsHeader.setStyle("-fx-font-style: italic;");
                    vbox.getChildren().add(reviewsHeader);

                    // If filtering by trusted reviewers is enabled, get the trusted reviewers list.
                    List<String> trustedReviewers = null;
                    if (trustedFilterCheckBox.isSelected()) {
                        trustedReviewers = databaseHelper.getTrustedReviewers(user.getUserName());
                    }
                    for (Review r : reviews) {
                        // Optional: filter by trusted reviewers
                        if (trustedFilterCheckBox.isSelected() &&
                            (trustedReviewers == null || !trustedReviewers.contains(r.getReviewerName()))) {
                            continue;
                        }

                        // 1) Show the review text
                        Label reviewLabel = new Label(r.getReviewerName() + " says: " + r.getReviewText());
                        reviewLabel.setWrapText(true);
                        vbox.getChildren().add(reviewLabel);

                        // 2) Staff-only: Delete button
                        if (user.getRole().equalsIgnoreCase("staff")) {
                            Button deleteReviewButton = new Button("Delete Review");
                            deleteReviewButton.setOnAction(a -> {
                                try {
                                    qaDatabase.deleteReview(r.getId());
                                    show(primaryStage); // refresh
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            });
                            vbox.getChildren().add(deleteReviewButton);
                        }

                        // Student-feedback form: only if current user wrote the question
                        // (or you can check if they wrote the answer, depending on context)
                        String contentAuthor = qaDatabase.getQuestionInfo(id)[0];
                        if (user.getRole().equalsIgnoreCase("student")
                                && user.getUserName().equals(contentAuthor)) {

                            // Rating spinner 1–5
                            Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 5);
                            ratingSpinner.setEditable(true);

                            // Optional comment box
                            TextArea feedbackComment = new TextArea();
                            feedbackComment.setPromptText("Optional comment…");
                            feedbackComment.setMaxHeight(60);

                            // Submit button
                            Button submitFeedback = new Button("Submit Feedback");
                            submitFeedback.setOnAction(e -> {
                                try {
                                    databaseHelper.addAuthorFeedback(
                                        r.getReviewerName(),     // reviewer
                                        user.getUserName(),      // author giving feedback
                                        ratingSpinner.getValue(),
                                        feedbackComment.getText().trim()
                                    );
                                    // Provide confirmation
                                    new Alert(Alert.AlertType.INFORMATION,
                                              "Thank you for your feedback!")
                                        .showAndWait();
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    new Alert(Alert.AlertType.ERROR,
                                              "Failed to submit feedback: " + ex.getMessage())
                                        .showAndWait();
                                }
                            });

                            // Group them neatly
                            HBox feedbackBox = new HBox(10,
                                new Label("Your rating:"), ratingSpinner,
                                submitFeedback
                            );
                            feedbackBox.setPadding(new Insets(5, 0, 0, 20));

                            // Add to the view
                            vbox.getChildren().addAll(feedbackBox, feedbackComment);
                        }

                        // Separator before next review
                        vbox.getChildren().add(new Separator());
                    }

                }
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        // ---------------------------

        // Button to delete question
        Button deleteButton = new Button("Delete Question");
        deleteButton.setOnAction(a -> {
        	qaDatabase.deleteQuestion(id);
            show(primaryStage);
        });
        
        // Button to resolve question
        Button resolveButton = new Button("Resolve Question");
        resolveButton.setOnAction(a -> {
        	qaDatabase.resolveQuestion(id);
            show(primaryStage);
        });
        
        // Button to unresolve question
        Button unResolveButton = new Button("Unresolve Question");
        unResolveButton.setOnAction(a -> {
        	qaDatabase.unResolveQuestion(id);
            show(primaryStage);
        });
        
        // Array list to hold answers
        List<String[]> answers = qaDatabase.getAnswers(id);

        // Loop through answers and create user name and content labels for each
        for (int i = 0; i < answers.size(); i++) {
            final int answerCount = i;
            String[] answer = answers.get(i);

            // Create a container for this answer and its reviews.
            VBox answerBox = new VBox(5);
            answerBox.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");
            
            // Display answer text (author and content)
            Label answerUserLabel = new Label(answer[0]);
            answerUserLabel.setWrapText(true);
            Label answerContentLabel = new Label(" " + answer[1]);
            answerContentLabel.setWrapText(true);
            answerBox.getChildren().addAll(answerUserLabel, answerContentLabel);
            
            int answerID = qaDatabase.getAnswerID(id, answerCount);
            
            // --- NEW: If logged in user is staff, show a delete button for this answer ---
            if (user.getRole().equalsIgnoreCase("staff")) {
                Button deleteAnswerButton = new Button("Delete Answer");
                deleteAnswerButton.setOnAction(a -> {
                    try {
                        qaDatabase.deleteAnswer(answerID);
                        show(primaryStage); // Refresh the page after deletion
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
                answerBox.getChildren().add(deleteAnswerButton);
            }
            
            if (qaDatabase.doesAnswerResolve(answerID)) {
                answerUserLabel.setStyle("-fx-font-weight: bold;");
                answerContentLabel.setStyle("-fx-font-weight: bold;");
            }
            
         // Reviewer branch for each answer:
            if (user.getRole().toLowerCase().contains("reviewer")) {
                try {
                    // First, display all reviews for this answer
                    List<Review> allReviews = qaDatabase.getReviewsForTarget(answerID, "answer");
                    if (!allReviews.isEmpty()) {
                        Label allReviewsHeader = new Label("All reviews for this answer:");
                        allReviewsHeader.setStyle("-fx-font-style: italic;");
                        answerBox.getChildren().add(allReviewsHeader);
                        for (Review r : allReviews) {
                            Label reviewLabel = new Label(r.getReviewerName() + " says: " + r.getReviewText());
                            reviewLabel.setWrapText(true);
                            // Highlight logged-in reviewer's review
                            if (r.getReviewerName().equalsIgnoreCase(user.getUserName())) {
                                reviewLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #ddffdd;");
                            }
                            answerBox.getChildren().add(reviewLabel);
                        }
                    }
                    
                    // Now, allow the logged-in reviewer to add/update/delete their own review
                    Review myReview = qaDatabase.getReviewForTargetByReviewer(answerID, "answer", user.getUserName());
                    if (myReview == null) {
                        // No review exists for this answer by the reviewer, so show an input to add one
                        TextField addReviewField = new TextField();
                        addReviewField.setPromptText("Add your review for this answer");
                        Button addReviewButton = new Button("Submit Review");
                        addReviewButton.setOnAction(a -> {
                            try {
                                Review newReview = new Review(answerID, "answer", user.getUserName(), addReviewField.getText());
                                qaDatabase.addReview(newReview);
                                show(primaryStage);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });
                        answerBox.getChildren().addAll(addReviewField, addReviewButton);
                    } else {
                        // Review exists; provide controls to update or delete it.
                        Label myReviewLabel = new Label("Your Review: " + myReview.getReviewText());
                        myReviewLabel.setWrapText(true);
                        Button updateReviewButton = new Button("Update Review");
                        updateReviewButton.setOnAction(a -> {
                            TextInputDialog dialog = new TextInputDialog(myReview.getReviewText());
                            dialog.setTitle("Update Review");
                            dialog.setHeaderText("Update your review:");
                            dialog.setContentText("New review text:");
                            dialog.showAndWait().ifPresent(newText -> {
                                try {
                                    qaDatabase.updateReview(myReview.getId(), newText);
                                    show(primaryStage);
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        });
                        Button deleteReviewButton = new Button("Delete Review");
                        deleteReviewButton.setOnAction(a -> {
                            try {
                                qaDatabase.deleteReview(myReview.getId());
                                show(primaryStage);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });
                        answerBox.getChildren().addAll(myReviewLabel, updateReviewButton, deleteReviewButton);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else if (user.getRole().equalsIgnoreCase("student")) {
                // Student: display all reviews for this answer (read-only)
                try {
                    List<Review> reviews = qaDatabase.getReviewsForTarget(answerID, "answer");
                    if (!reviews.isEmpty()) {
                        Label reviewsHeader = new Label("Reviews for this answer:");
                        reviewsHeader.setStyle("-fx-font-style: italic;");
                        answerBox.getChildren().add(reviewsHeader);
                        
                        // Optionally, apply trusted reviewer filtering here...
                        List<String> trustedReviewers = null;
                        if (trustedFilterCheckBox.isSelected()) {
                            trustedReviewers = databaseHelper.getTrustedReviewers(user.getUserName());
                        }
                        for (Review r : reviews) {
                            if (trustedFilterCheckBox.isSelected() && (trustedReviewers == null || !trustedReviewers.contains(r.getReviewerName()))) {
                                continue;
                            }
                            Label reviewLabel = new Label(r.getReviewerName() + " says: " + r.getReviewText());
                            reviewLabel.setWrapText(true);
                            answerBox.getChildren().add(reviewLabel);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            // If the student who asked the question is viewing, add the "Answers the Question" button here.
            if (qaDatabase.doesUserOwnQuestion(id, userName)) {
                Button answeredButton = new Button("Answers the Question");
                answeredButton.setOnAction(a -> {
                    if (answerID == 0) {
                        System.out.println("Error in getAnswerID");
                        return;
                    }
                    qaDatabase.resolveQuestion(id);
                    qaDatabase.answerResolves(id, answerID);
                    show(primaryStage);
                });
                answerBox.getChildren().add(answeredButton);
            }
            
            // Finally, add the entire answerBox to the main vbox.
            vbox.getChildren().add(answerBox);
        }

        // Text area to enter the answer content
        TextArea newAnswerText = new TextArea();
        newAnswerText.setPromptText("Answer the Question");
        newAnswerText.setMaxWidth(400);
        newAnswerText.setPrefHeight(50);
        newAnswerText.setWrapText(true);

        // Button to add an answer to a question
        Button addAnswerButton = new Button("Post Answer");
        addAnswerButton.setOnAction(a -> {
    		// Create new answer and add it to the database
    		Answer answer = new Answer(id, userName, newAnswerText.getText());
    		try {
    			qaDatabase.addAnswer(answer);
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    		newAnswerText.clear();
    		printQuestion(info, primaryStage, id);
    	
        });
        
        //button to create a Comment
        Button viewCommentsButton = new Button("View Feedback");
        viewCommentsButton.setOnAction(a -> {
    		// prints question and comments 
    		printCommentPage(info, primaryStage, id);
    	
        });
        vbox.getChildren().addAll(newAnswerText, addAnswerButton, viewCommentsButton);
        
        
        
        // Button to update the question
        Button updateButton = new Button("Update Question");
        updateButton.setOnAction(a -> {
        	if (!(newAnswerText.getText().isEmpty())) {
        		qaDatabase.updateQuestion(id, newAnswerText.getText());
        		show(primaryStage);
        	} else {
        		errorLabel.setText("Must have an input to update question");
        		vbox.getChildren().add(errorLabel);
        	}
        });

        // If the user wrote the post, give them special permissions
        // Revised: Allow deletion/update if the user owns the question OR has staff privileges.
        if (qaDatabase.doesUserOwnQuestion(id, userName) || user.getRole().equalsIgnoreCase("staff")) {
            newAnswerText.setPromptText("Answer or Update the Question");
            vbox.getChildren().addAll(updateButton, deleteButton);
            // Also add resolve/unresolve buttons if appropriate
            if (!qaDatabase.isResolved(id)) {
                vbox.getChildren().add(resolveButton);
            } else {
                vbox.getChildren().add(unResolveButton);
            }
        }

        Button contactReviewerButton = new Button("Contact Reviewer");
        contactReviewerButton.setOnAction(a -> {
            // For simplicity, prompt the student for a reviewer's username.
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Contact Reviewer");
            dialog.setHeaderText("Enter the reviewer's username:");
            dialog.setContentText("Reviewer:");
            dialog.showAndWait().ifPresent(reviewer -> {
                 new ContactReviewerPage(databaseHelper, user.getUserName(), reviewer).show(primaryStage);
            });
        });
        vbox.getChildren().add(contactReviewerButton);

        vbox.setMinWidth(500);
    }
    

// comment page that shows comments under the question chosen
 public void printCommentPage(String[] info, Stage primaryStage, int id) {
    	
        // Clear the vbox initially
        vbox.getChildren().clear();
        vbox.setAlignment(Pos.CENTER_LEFT);

        // Create labels for title, user name, and body
        Label titleLabel = new Label(info[1]);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        titleLabel.setWrapText(true);

        Label userNameLabel = new Label(info[0]);
        userNameLabel.setStyle("-fx-font-size: 14px;");
        userNameLabel.setWrapText(true);
        
        Label bodyLabel = new Label(info[2]);
        bodyLabel.setWrapText(true);
        bodyLabel.setWrapText(true);
        
        // Separator between question and comments
        Separator separator = new Separator();
        separator.setMinWidth(250);

        vbox.getChildren().addAll(titleLabel, userNameLabel, bodyLabel, separator);

        // Array list to hold comments
        List<String[]> comments = qaDatabase.getComments(id);

        // Loop through comments and create user name and content labels for each
        for (int i = 0; i < comments.size(); i++) {
        	final int commentCount = i;
        	String[] comment = comments.get(i);
        	
            Label commentUserLabel = new Label(comment[0]);
            commentUserLabel.setWrapText(true);
            
            Label commentContentLabel = new Label("     " + comment[1]);
            commentContentLabel.setWrapText(true);
            
            // (Optional: Adjust style if needed)
            int commentID = qaDatabase.getCommentID(id, commentCount);

            // Add the comment labels to a container (e.g., an HBox or VBox)
            HBox commentBox = new HBox(10);
            commentBox.getChildren().addAll(commentUserLabel, commentContentLabel);

            // --- NEW: If logged in user is staff, add a "Delete Comment" button ---
            if (user.getRole().equalsIgnoreCase("staff")) {
                Button deleteCommentButton = new Button("Delete Comment");
                deleteCommentButton.setOnAction(a -> {
                    try {
                        qaDatabase.deleteComment(commentID);
                        // Refresh the comment page view after deletion
                        printCommentPage(info, primaryStage, id);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
                commentBox.getChildren().add(deleteCommentButton);
            }
            // -------------------------------------------------------------------

            vbox.getChildren().add(commentBox);
            
        }

        // Text area to enter the comment content
        TextArea newCommentText = new TextArea();
        newCommentText.setPromptText("Feedback for question");
        newCommentText.setMaxWidth(400);
        newCommentText.setPrefHeight(50);
        newCommentText.setWrapText(true);

  
        //button to create a Comment
        Button addCommentButton = new Button("Post Feedback");
        addCommentButton.setOnAction(a -> {
    		// Create new comment and add it to the database
    		Comment comment = new Comment(id, userName, newCommentText.getText());
    		try {
    			qaDatabase.addComments(comment);
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    		newCommentText.clear();
    		printCommentPage(info, primaryStage, id);
    	
        });

        Button viewAnswersButton = new Button("View Answers");
        viewAnswersButton.setOnAction(a -> {
    		printQuestion(info, primaryStage, id);
    	
        });
        

        vbox.getChildren().addAll(newCommentText, addCommentButton, viewAnswersButton);
        
        vbox.setMinWidth(500);
    }

    // Show the inputs to create a question on the right side of the screen
    public void createQuestion(Stage primaryStage) {
    	
        // Clear the vbox initially
        vbox.getChildren().clear();

        // Text field to enter a title
        Label titleLabel = new Label("Title");
        TextField titleText = new TextField();
        titleText.setMinWidth(350);

        // Text area to enter the question body
        TextArea bodyText = new TextArea();
        bodyText.setMaxWidth(450);
        bodyText.setMinHeight(200);
        bodyText.setWrapText(true);

        // Button to post the question
        Button postButton = new Button("Post");
        postButton.setOnAction(a -> {

            // Check if any of the fields are empty
            if (titleText.getText().equals("") || bodyText.getText().equals("")) {
            	errorLabel.setText("The title and body must contain characters\n");
            
            // Check if the title is too long
            } else if (titleText.getText().length() > 255){
            	errorLabel.setText("Titles must be shorter than 255 characters\n");
            
            } else {
                Question q = new Question(userName, titleText.getText(), bodyText.getText());
                try {
                	qaDatabase.addQuestion(q);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                show(primaryStage);
            }

        });

        // Button to cancel the post
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(a -> {
            vbox.getChildren().clear();
            return;
        });

        // Hbox to align the title label and text field
        HBox title = new HBox(10);
        title.setStyle("-fx-alignment: center;");
        title.getChildren().addAll(titleLabel, titleText);

        vbox.getChildren().addAll(title, bodyText, postButton, cancelButton, errorLabel);
        vbox.setStyle("-fx-alignment: center; -fx-padding: 20;");
        vbox.setMinWidth(450);
    }
    
    // Method to clear the filter text
    public void clearFilter() {
        this.filterText = "";
    }
}