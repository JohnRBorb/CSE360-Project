package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import databasePart1.*;

/**
 * The SetupComplete class displays a setup complete screen for first time users
 * This is intended to be used by first users to be sent to the SetupLoginSelection.
 */
public class SetupCompletePage {
	 private final DatabaseHelper databaseHelper;

	 public SetupCompletePage(DatabaseHelper databaseHelper) {
	        this.databaseHelper = databaseHelper;
	    }

	public void show( Stage primaryStage) {
		VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	    Label completeLabel = new Label( "Account Setup Complete");
	    completeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    completeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    completeLabel.setTextAlignment(TextAlignment.CENTER);

	    //Button to navigate to SetupLoginSelection page
	    Button backToLoginButton = new Button("Back to Login page");
	    backToLoginButton.setOnAction(a -> {
	    	new UserLoginPage(databaseHelper).show(primaryStage); 
	    });

	    layout.getChildren().addAll(completeLabel, backToLoginButton);
	    Scene setUpCompleteScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(setUpCompleteScene);
	    primaryStage.setTitle("Setup Complete Page");

	}
}