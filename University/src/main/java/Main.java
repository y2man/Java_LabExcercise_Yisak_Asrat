import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class Main extends Application {

        private TextField tfId;
        private TextField tfName;
        private TextField tfSection;
        private TextField tfYear;
        private TextField tfDep;

    @Override
    public void start(Stage stage) {
        tfId = createField("Enter student ID");
        tfName = createField("Enter student name");
        tfSection = createField("Enter section");
        tfYear = createField("Enter year");
        tfDep = createField("Enter department");

        Label badge = new Label("University System");
        badge.getStyleClass().add("badge");

        Label title = new Label("Add Student");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Capture student details quickly with a clean, guided form.");
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        VBox header = new VBox(8, badge, title, subtitle);
        header.getStyleClass().add("hero");

        GridPane form = new GridPane();
        form.getStyleClass().add("form-grid");
        form.setHgap(14);
        form.setVgap(14);
        form.addRow(0, createLabel("Student ID"), tfId);
        form.addRow(1, createLabel("Name"), tfName);
        form.addRow(2, createLabel("Section"), tfSection);
        form.addRow(3, createLabel("Year"), tfYear);
        form.addRow(4, createLabel("Department"), tfDep);

        Button saveButton = new Button("Save Student");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(e -> saveStudent());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("ghost-button");
        clearButton.setOnAction(e -> clearForm());

        HBox actions = new HBox(12, saveButton, clearButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(18, header, form, actions);
        card.getStyleClass().add("card");
        card.setMaxWidth(560);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setCenter(card);
        BorderPane.setMargin(card, new Insets(24));

        Scene scene = new Scene(root, 820, 560);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/styles/university.css")
        ).toExternalForm());

        stage.setScene(scene);
        stage.setMinWidth(720);
        stage.setMinHeight(520);
        stage.setTitle("University Student Manager");
        stage.show();
    }

    private TextField createField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.getStyleClass().add("form-field");
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private void saveStudent() {
        if (tfId.getText().isBlank() || tfName.getText().isBlank()
                || tfSection.getText().isBlank() || tfYear.getText().isBlank()
                || tfDep.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing information",
                    "Please complete every field before saving.");
            return;
        }

        try {
            int studentId = Integer.parseInt(tfId.getText().trim());
            int year = Integer.parseInt(tfYear.getText().trim());

            Connection connection = Server.getConnection();
            if (connection == null) {
                throw new SQLException("Database connection is not available.");
            }

            String query = "INSERT INTO students VALUES (?, ?, ?, ?, ?)";

            try (connection; PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, studentId);
                statement.setString(2, tfName.getText().trim());
                statement.setString(3, tfSection.getText().trim());
                statement.setInt(4, year);
                statement.setString(5, tfDep.getText().trim());
                statement.executeUpdate();
            }

            showAlert(Alert.AlertType.INFORMATION, "Saved", "Student added successfully.");
            clearForm();

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid number",
                    "Student ID and Year must be valid whole numbers.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Save failed", ex.getMessage());
        }
    }

    private void clearForm() {
        tfId.clear();
        tfName.clear();
        tfSection.clear();
        tfYear.clear();
        tfDep.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
