## PSA: Change your 'module-info.java' to this:

'module HW4 {
	requires javafx.controls;
	requires java.sql;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
}'
