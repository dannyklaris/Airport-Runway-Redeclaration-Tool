package com.group20seq.runway_redeclaration.Controllers;

import com.group20seq.runway_redeclaration.Configs.Serialisable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.util.HashMap;
import java.io.File;
import java.net.URLDecoder;
import java.util.Optional;

public class FilesController {
    private HashMap<Class<? extends Serialisable>, File> files;
    private final Scene scene;
    static private FilesController filesController;

    public static class NoSceneSet extends RuntimeException {
        NoSceneSet() {
            super("No scene set");
        }
    }

    private FilesController(Scene scene) {
        this.scene = scene;
        files = Config.fromXML();
    }

    static public FilesController createFilesController(Scene scene) {
        filesController = new FilesController(scene);
        return filesController;
    }
    static public FilesController getFilesController() throws NoSceneSet {
        if (filesController == null)
            throw new NoSceneSet();
        return filesController;
    }

    public void setFile(Class<? extends Serialisable> n) {
        var opt_obstacles = getNewFile(n.getSimpleName());
        opt_obstacles.ifPresent(file -> files.put(n, file));
    }

    public File getFile(Class<? extends Serialisable> n) {
        File f = files.get(n);
        if (f != null)
            return f;
        setFile(n);
        return getFile(n);
    }

    public File getFileQuiet(Class<? extends Serialisable> n) {
        return files.get(n);
    }

    private Optional<File> getNewFile(String type) {
        Dialog<File> fileDialog = new Dialog<>();
        ButtonType select = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        fileDialog.setTitle("Choose " + type.toLowerCase() + " folder path");
        fileDialog.getDialogPane().getButtonTypes().add(select);


        TextArea filePath = new TextArea();
        HBox.setHgrow(filePath, Priority.ALWAYS);

        Button fileChooser = new Button("Choose folder");
        fileChooser.setOnAction(event -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Choose " + type.toLowerCase() + " folder path");
            var chosenDir = dirChooser.showDialog(scene.getWindow());
            filePath.setText(chosenDir != null ? chosenDir.getAbsolutePath() : "");
            fileDialog.getDialogPane().lookupButton(select).setDisable(chosenDir == null);
        });

        filePath.setOnKeyTyped(event ->
                fileDialog.getDialogPane().lookupButton(select).setDisable(filePath.textProperty().get() == null || filePath.textProperty().get().isEmpty()));

        try {
            filePath.setText(URLDecoder.decode(getClass().getResource("/" + type).toURI().toURL().toString()).replace("file:/", "").replaceAll("/$", ""));
        } catch (Exception ignored) {}

        var content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(filePath, fileChooser);
        fileDialog.getDialogPane().setContent(content);

        fileDialog.setResultConverter(dialogButton -> dialogButton != null && dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE
                ? new File(filePath.getText())
                : null);

        return fileDialog.showAndWait();
    }

    HashMap<Class<? extends Serialisable>, File> getHashMap() {
        return files;
    }

    void setHashMap(HashMap<Class<? extends Serialisable>, File> hs) {
        files = hs;
    }
}
