package com.group20seq.runway_redeclaration.UI;

import com.group20seq.runway_redeclaration.Configs.XMLLoader;
import com.group20seq.runway_redeclaration.Controllers.Config;
import com.group20seq.runway_redeclaration.Controllers.FilesController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


/**
 * The main window of the application that contains the runway pane and the properties pane needs its own class so that
 * it's easier to access certain attributes more easily.
 */
public class MainWindow extends BorderPane {
    private final int width, height;
    private final Stage stage;
    private MasterPane masterPane;


    /**
     * Creates a new main window with the given stage, width and height. It also sets the minimum width and height of
     * the stage and the main window, and handles the airport and obstacle folders.
     * @param stage The stage of the main window.
     * @param width The width of the main window.
     * @param height The height of the main window.
     */
    public MainWindow(Stage stage, int width, int height) {
        // Set the stage, width and height
        this.stage = stage;
        this.height = height;
        this.width = width;

        // Set the minimum width and height of the stage and the main window
        stage.setMinWidth(width);
        stage.setMinHeight(height);
        this.setMinWidth(width);
        this.setMinHeight(height);

        // Set the properties pane and the runway pane
//        this.propertiesPane = new PropertiesPane();
//        this.propertiesPane.minWidthProperty().bind(this.widthProperty().divide(6));
//        this.runwayPane = RunwayPane.getRunwayPane();
//
//        // Set the properties pane and the runway pane to the main window (center)
//        this.setRight(propertiesPane);

        // Set the scene and show the stage
        Scene scene = new Scene(this, this.width, this.height);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("Runway Redeclaration");
        stage.setOnCloseRequest(event -> {
                try {
                XMLLoader.serialize(Config.getConfig());
            } catch (Exception Ignored) {
            }
        });
        stage.show();

        FilesController.createFilesController(scene);

        this.masterPane = new MasterPane();
        this.setCenter(masterPane);
    }
}
