package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

public class InstructorScoreParametersPage {
	private User user;
    private DatabaseHelper db;
    private String instructor;
    private ObservableList<ScoreParam> params = FXCollections.observableArrayList();

    public InstructorScoreParametersPage(User user, DatabaseHelper db, String instructor) {
    	this.user = user;
        this.db = db;
        this.instructor = instructor;
        loadParams();
    }

    private void loadParams() {
        params.clear();
        try {
            List<ScoreParam> list = db.getScoreParameters();
            params.addAll(list);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void show(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10); grid.setVgap(10);

        // Column headers
        grid.add(new Label("Domain"), 0, 0);
        grid.add(new Label("Weight"), 1, 0);

        // Editable rows for each domain
        for(int i=0; i<params.size(); i++) {
            ScoreParam p = params.get(i);
            TextField weightField = new TextField(p.getWeight().toString());
            Button saveBtn = new Button("Save");
            int row = i+1;

            saveBtn.setOnAction(e -> {
                try {
                    double w = Double.parseDouble(weightField.getText());
                    p.setWeight(w);
                    db.updateScoreParameter(p.getDomain(), w, instructor);
                    loadParams();
                    new Alert(Alert.AlertType.INFORMATION, "Saved").showAndWait();
                } catch(Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Invalid weight").showAndWait();
                }
            });

            grid.add(new Label(p.getDomain()), 0, row);
            grid.add(weightField, 1, row);
            grid.add(saveBtn,    2, row);
        }

        Button back = new Button("Back");
        back.setOnAction(e -> new InstructorHomePage(user, db).show(stage));
        grid.add(back, 0, params.size()+1);

        stage.setScene(new Scene(grid, 400, 300));
        stage.setTitle("Reviewer Score Parameters");
    }
}
