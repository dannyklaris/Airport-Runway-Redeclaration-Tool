package com.group20seq.runway_redeclaration.UI;

import com.group20seq.runway_redeclaration.App;
import com.group20seq.runway_redeclaration.UI.Themes.DarkTheme;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;


public class GuiConfig extends GridPane {
    private final MenuButton themes = new MenuButton("");
    private final MasterPane masterPane;

    public GuiConfig(MasterPane pane) {
        super();

        var columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        this.getColumnConstraints().addAll(columnConstraints, columnConstraints);

        this.setVgap(15);
        this.setHgap(15);
        this.setPadding(new Insets(10, 10, 10, 10));
        this.themes.setMaxWidth(Double.MAX_VALUE);

        this.masterPane = pane;

        this.add(new Label("Theme"), 0, 1);
        this.add(this.themes, 1, 1);
        this.setVgap(15);

        for (int i = 0; i < this.getChildren().size(); i++) {
            if (this.getChildren().get(i) instanceof Label) {
                if (this.getColumnIndex(this.getChildren().get(i)) == 0) {
                    ((Label) this.getChildren().get(i)).setStyle("-fx-font-weight: bold");
                    ((Label) this.getChildren().get(i)).setText(((Label) this.getChildren().get(i)).getText() + ":");
                }
            }
        }

        this.loadThemes();
    }

    private void loadThemes() {
        var theme_light_action = new MenuItem("Light");
        theme_light_action.setOnAction(event -> {
            this.themes.setText("Light");
            App.instance.switch_theme(false);
        });

        var theme_dark_action = new MenuItem("Dark");
        theme_dark_action.setOnAction(event -> {
            this.themes.setText("Dark");
            App.instance.switch_theme(true);
        });

        this.themes.getItems().addAll(theme_light_action, theme_dark_action);
    }
}
