package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ModerationRequestsPage {
    private final DatabaseHelper db;
    private final ObservableList<ModerationRequest> data = FXCollections.observableArrayList();
    private TableView<ModerationRequest> table;
    private Stage stage;

    public ModerationRequestsPage(DatabaseHelper db) {
        this.db = db;
    }

    public void show(Stage primaryStage) {
        this.stage = primaryStage;
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Title
        Label title = new Label("Moderation Queue");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // TableView setup
        table = new TableView<>();
        TableColumn<ModerationRequest, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ModerationRequest, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("contentType"));

        TableColumn<ModerationRequest, Integer> cidCol = new TableColumn<>("Content ID");
        cidCol.setCellValueFactory(new PropertyValueFactory<>("contentId"));

        TableColumn<ModerationRequest, String> instrCol = new TableColumn<>("Flagged By");
        instrCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));

        TableColumn<ModerationRequest, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<ModerationRequest, String> timeCol = new TableColumn<>("When");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("requestTime"));

        TableColumn<ModerationRequest, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button closeBtn = new Button("Close");
            {
                closeBtn.setOnAction(e -> {
                    ModerationRequest req = getTableView().getItems().get(getIndex());
                    try {
                        db.closeModerationRequest(req.getId());
                        refresh();
                        // Notify the moderator
                        new Alert(Alert.AlertType.INFORMATION, 
                                "Closed request #" + req.getId()).showAndWait();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : closeBtn);
            }
        });

        table.getColumns().setAll(idCol, typeCol, cidCol, instrCol, reasonCol, timeCol, actionCol);
        table.setPlaceholder(new Label("No open moderation requests"));

        // Load data
        refresh();

        // Back button
        Button back = new Button("Back");
        back.setOnAction(e -> stage.close());

        root.getChildren().addAll(title, table, back);
        stage.setScene(new Scene(root, 800, 400));
        stage.setTitle("Moderation Requests");
        stage.show();
    }

    private void refresh() {
        data.clear();
        try {
            List<ModerationRequest> list = db.getOpenModerationRequests();
            data.addAll(list);
            table.setItems(data);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
}
