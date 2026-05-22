package org;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class NotePadFX extends Application {

    private Stage stage;
    private TextArea editor;
    private Label statusLabel;
    private File currentFile;
    private boolean dirty;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        editor = new TextArea();
        editor.getStyleClass().add("editor-area");
        editor.setWrapText(true);
        editor.textProperty().addListener((observable, oldValue, newValue) -> setDirty(true));

        statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-text");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(createMenuBar());
        root.setCenter(editor);
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(
                getClass()
                        .getResource("/notepad.css")
                        .toExternalForm()
        );

        stage.setTitle("Notepad FX");
        stage.setScene(scene);
        stage.setMinWidth(720);
        stage.setMinHeight(520);
        stage.setOnCloseRequest(event -> {
            if (!confirmClose()) {
                event.consume();
            }
        });
        updateTitle();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New");
        newItem.setOnAction(event -> newFile());

        MenuItem openItem = new MenuItem("Open...");
        openItem.setOnAction(event -> openFile());

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(event -> saveFile());

        MenuItem saveAsItem = new MenuItem("Save As...");
        saveAsItem.setOnAction(event -> saveFileAs());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> {
            if (confirmClose()) {
                Platform.exit();
            }
        });

        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, new SeparatorMenuItem(), exitItem);

        Menu editMenu = new Menu("Edit");

        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setOnAction(event -> editor.cut());

        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(event -> editor.copy());

        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setOnAction(event -> editor.paste());

        MenuItem selectAllItem = new MenuItem("Select All");
        selectAllItem.setOnAction(event -> editor.selectAll());

        editMenu.getItems().addAll(cutItem, copyItem, pasteItem, new SeparatorMenuItem(), selectAllItem);

        Menu viewMenu = new Menu("View");

        MenuItem clearItem = new MenuItem("Clear Screen");
        clearItem.setOnAction(event -> {
            editor.clear();
            setDirty(true);
        });

        viewMenu.getItems().add(clearItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu);
        return menuBar;
    }

    private HBox createStatusBar() {
        Button newButton = new Button("New");
        newButton.getStyleClass().add("toolbar-button");
        newButton.setOnAction(event -> newFile());

        Button openButton = new Button("Open");
        openButton.getStyleClass().add("toolbar-button");
        openButton.setOnAction(event -> openFile());

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("toolbar-button");
        saveButton.setOnAction(event -> saveFile());

        HBox actions = new HBox(10, newButton, openButton, saveButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        HBox statusBar = new HBox(12, actions, statusLabel);
        statusBar.getStyleClass().add("status-bar");
        statusBar.setPadding(new Insets(10, 14, 12, 14));
        HBox.setMargin(statusLabel, new Insets(0, 0, 0, 8));
        return statusBar;
    }

    private void newFile() {
        if (!confirmDiscardChanges()) {
            return;
        }

        editor.clear();
        currentFile = null;
        setDirty(false);
        setStatus("New document");
    }

    private void openFile() {
        if (!confirmDiscardChanges()) {
            return;
        }

        FileChooser chooser = createFileChooser("Open Text File");
        File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try {
            editor.setText(Files.readString(file.toPath(), StandardCharsets.UTF_8));
            currentFile = file;
            setDirty(false);
            setStatus("Opened " + file.getName());
            updateTitle();
        } catch (IOException ex) {
            showError("Unable to open file", ex.getMessage());
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            saveFileAs();
            return;
        }

        writeCurrentFile(currentFile);
    }

    private void saveFileAs() {
        FileChooser chooser = createFileChooser("Save Text File");
        File file = chooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        currentFile = file;
        writeCurrentFile(file);
    }

    private void writeCurrentFile(File file) {
        try {
            Files.writeString(file.toPath(), editor.getText(), StandardCharsets.UTF_8);
            setDirty(false);
            setStatus("Saved " + file.getName());
            updateTitle();
        } catch (IOException ex) {
            showError("Unable to save file", ex.getMessage());
        }
    }

    private FileChooser createFileChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        if (currentFile != null && currentFile.getParentFile() != null) {
            chooser.setInitialDirectory(currentFile.getParentFile());
        }
        return chooser;
    }

    private boolean confirmDiscardChanges() {
        if (!dirty) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes.");
        alert.setContentText("Do you want to save your work before continuing?");

        ButtonType saveButton = new ButtonType("Save");
        ButtonType discardButton = new ButtonType("Discard", ButtonData.NO);
        ButtonType cancelButton = ButtonType.CANCEL;
        alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() == cancelButton) {
            return false;
        }

        if (result.get() == saveButton) {
            saveFile();
            return !dirty;
        }

        return true;
    }

    private boolean confirmClose() {
        return confirmDiscardChanges();
    }

    private void setDirty(boolean value) {
        dirty = value;
        updateTitle();
        if (dirty) {
            setStatus("Unsaved changes");
        } else if (currentFile == null) {
            setStatus("Ready");
        }
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    private void updateTitle() {
        String name = currentFile == null ? "Untitled" : currentFile.getName();
        stage.setTitle((dirty ? "*" : "") + name + " - Notepad FX");
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message == null || message.isBlank() ? "An unknown error occurred." : message);
        alert.showAndWait();
    }
}
