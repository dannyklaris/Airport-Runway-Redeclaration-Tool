package com.group20seq.runway_redeclaration;

import com.group20seq.runway_redeclaration.UI.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


public class App extends Application {
    private MainWindow mainWindow;

    private final int width = 900, height = 600;
    public static App instance;

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        mainWindow = new MainWindow(stage, width, height);
    }

    public static void main(String[] args) {
        launch();
    }

    public void switch_theme(boolean dark) {
        String stylesheet = getClass().getResource("/Themes/dark.css").toExternalForm();
        Application.setUserAgentStylesheet(stylesheet);
    }
}
