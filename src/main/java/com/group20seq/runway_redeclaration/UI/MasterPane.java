package com.group20seq.runway_redeclaration.UI;

import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MasterPane extends Pane {
    private final RunwayPane runwayPane = new RunwayPane(this);
    private final ChooseAirport chooseAirport = new ChooseAirport(this);
    private final ChooseObstacle chooseObstacle = new ChooseObstacle(this);
    private final PropertiesPane propertiesPane = new PropertiesPane(this);
    private final GuiConfig guiConfig = new GuiConfig(this);
    private final ExportPane exportPane = new ExportPane(this);

    public MasterPane() {
        var chooseAirportWrapper = new Accordion();
        chooseAirportWrapper.getPanes().add(new TitledPane("Airport", this.chooseAirport));

        var chooseObstacleWrapper = new Accordion();
        chooseObstacleWrapper.getPanes().add(new TitledPane("Obstacle", this.chooseObstacle));

        var propertiesPaneWrapper = new Accordion();
        propertiesPaneWrapper.getPanes().add(new TitledPane("Properties", this.propertiesPane));

        var themeWrapper = new Accordion();
        themeWrapper.getPanes().add(new TitledPane("Theme", this.guiConfig));

        var exportPaneWrapper = new Accordion();
        exportPaneWrapper.getPanes().add(new TitledPane("Export", this.exportPane));

        var lhsContainer = new VBox();
        lhsContainer.getChildren().addAll(chooseAirportWrapper, chooseObstacleWrapper, exportPaneWrapper);

        var rhsContainer = new VBox();
        rhsContainer.getChildren().addAll(propertiesPaneWrapper, themeWrapper);
        HBox.setHgrow(rhsContainer, Priority.ALWAYS);

        var container = new HBox();
        container.getChildren().addAll(lhsContainer, this.runwayPane, rhsContainer);

        container.prefWidthProperty().bind(this.widthProperty());
        container.prefHeightProperty().bind(this.heightProperty());
        this.getChildren().add(container);
        container.setSpacing(20);
        container.setPadding(new Insets(10, 10, 10, 10));

        lhsContainer.prefWidthProperty().bind(this.widthProperty().subtract(this.runwayPane.minWidthProperty()).divide(4));
        rhsContainer.prefWidthProperty().bind(this.widthProperty().subtract(this.runwayPane.minWidthProperty()).divide(4));

        chooseAirportWrapper.setExpandedPane(chooseAirportWrapper.getPanes().get(0));
        chooseObstacleWrapper.setExpandedPane(chooseObstacleWrapper.getPanes().get(0));
        propertiesPaneWrapper.setExpandedPane(propertiesPaneWrapper.getPanes().get(0));
        themeWrapper.setExpandedPane(themeWrapper.getPanes().get(0));
        exportPaneWrapper.setExpandedPane(exportPaneWrapper.getPanes().get(0));
    }

    public RunwayPane getRunwayPane() {
        return runwayPane;
    }

    public ChooseAirport getChooseAirport() {
        return chooseAirport;
    }

    public ChooseObstacle getChooseObstacle() {
        return chooseObstacle;
    }

    public PropertiesPane getPropertiesPane() {
        return propertiesPane;
    }
}
