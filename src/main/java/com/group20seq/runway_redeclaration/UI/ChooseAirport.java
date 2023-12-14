package com.group20seq.runway_redeclaration.UI;

import com.group20seq.runway_redeclaration.Configs.Airport;
import com.group20seq.runway_redeclaration.Controllers.Controller;
import com.group20seq.runway_redeclaration.Controllers.FilesController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.transform.Rotate;


public class ChooseAirport extends GridPane {
    private final MenuButton airportMenuButton;
    private final MenuButton runwayMenuButton;
    private final MasterPane masterPane;

    private final Label stripEndLabel = new Label();
    private final Label lowerRunwayLabel = new Label();
    private final Label upperRunwayLabel = new Label();
    private final Label upperDisplacedThresholdLabel = new Label();
    private final Label lowerDisplacedThresholdLabel = new Label();
    private final Label lowerClearwayLabel = new Label();
    private final Label upperClearwayLabel = new Label();
    private final Label lowerStopwayLabel = new Label();
    private final Label upperStopwayLabel = new Label();
    ;

    private final Slider rotateRunwaySlider = new Slider(0, 360, 0);

    ChooseAirport(MasterPane pane) {
        super();

        var columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        this.getColumnConstraints().addAll(columnConstraints, columnConstraints, columnConstraints);

        this.setVgap(15);
        this.setHgap(15);
        this.setPadding(new Insets(10, 10, 10, 10));

        this.airportMenuButton = new MenuButton();
        this.runwayMenuButton = new MenuButton();
        this.masterPane = pane;

        // stretch menu button to fill the column
        this.airportMenuButton.setMaxWidth(Double.MAX_VALUE);
        this.runwayMenuButton.setMaxWidth(Double.MAX_VALUE);

        this.add(new Label("Choose Airport"), 0, 0);
        this.add(this.airportMenuButton, 1, 0, 2, 1);

        this.add(new Label("Blast Protection"), 0, 1);
        this.add(new Label("300m"), 1, 1, 2, 1);

        this.add(new Label("Strip End"), 0, 2);
        this.add(this.stripEndLabel, 1, 2, 2, 1);

        this.add(new Label("Strip End"), 0, 3);
        this.add(new Label("Lower Runway"), 1, 3, 2, 1);

        this.add(new Separator(), 0, 4, 3, 1);

        this.add(new Label("Choose Runway"), 0, 5);
        this.add(this.runwayMenuButton, 1, 5, 2, 1);

        this.add(new Label("Designator"), 0, 6);
        this.add(this.lowerRunwayLabel, 1, 6);
        this.add(this.upperRunwayLabel, 2, 6);

        this.add(new Label("Displaced Threshold"), 0, 7);
        this.add(this.lowerDisplacedThresholdLabel, 1, 7);
        this.add(this.upperDisplacedThresholdLabel, 2, 7);

        this.add(new Label("Clearway"), 0, 8);
        this.add(this.lowerClearwayLabel, 1, 8);
        this.add(this.upperClearwayLabel, 2, 8);

        this.add(new Label("Stopway"), 0, 9);
        this.add(this.lowerStopwayLabel, 1, 9);
        this.add(this.upperStopwayLabel, 2, 9);

        // set the column 0 labels to have bold
        for (int i = 0; i < this.getChildren().size(); i++) {
            if (this.getChildren().get(i) instanceof Label) {
                if (this.getColumnIndex(this.getChildren().get(i)) == 0) {
                    ((Label) this.getChildren().get(i)).setStyle("-fx-font-weight: bold");
                    ((Label) this.getChildren().get(i)).setText(((Label) this.getChildren().get(i)).getText() + ":");
                }
            }
        }

        this.rotateRunwaySlider.setShowTickLabels(true);
        this.rotateRunwaySlider.setShowTickMarks(true);
        this.rotateRunwaySlider.setMajorTickUnit(10);
        this.rotateRunwaySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.rotateRunwaySlider.setValue(Math.round(newValue.doubleValue() / 10) * 10);

            var angle = newValue;
            var transform = new Rotate(angle.doubleValue(),
                    this.masterPane.getRunwayPane().getTabs().get(0).getContent().getLayoutBounds().getWidth() / 2,
                    this.masterPane.getRunwayPane().getTabs().get(0).getContent().getLayoutBounds().getHeight() / 2);

            this.masterPane.getRunwayPane().getTabs().get(0).getContent().getTransforms().clear();
            this.masterPane.getRunwayPane().getTabs().get(0).getContent().getTransforms().add(transform);
        });

        var rotateRunwayAutoButton = new Button("Auto");
        rotateRunwayAutoButton.setOnAction(event -> {
            var angle = this.masterPane.getRunwayPane().getChosenConfig().runwayGroup.getLower().getAngle() * 10;
            this.rotateRunwaySlider.setValue(angle);
        });

        var rotateRunwayResetButton = new Button("Reset");
        rotateRunwayResetButton.setOnAction(event -> {
            this.rotateRunwaySlider.setValue(0);
        });

        this.add(new Separator(), 0, 10, 3, 1);
        this.add(new Label("Rotate Runway"), 0, 11, 3, 1);
        this.add(this.rotateRunwaySlider, 0, 12, 2, 1);

        var rotateButtonContainer = new HBox();
        rotateButtonContainer.getChildren().addAll(rotateRunwayAutoButton, rotateRunwayResetButton);
        rotateButtonContainer.setSpacing(10);
        this.add(rotateButtonContainer, 2, 12);

        var changeAirportButton = new Button("Change Airport File");
        changeAirportButton.setOnAction(event -> {
            FilesController.getFilesController().setFile(Airport.class);
            this.loadAirportMenuButton();
        });

        var createAirportButton = new Button("Create Airport");
        createAirportButton.setOnAction(event -> this.createNewAirport());

        this.add(changeAirportButton, 0, 13);
        this.add(createAirportButton, 2, 13);

        FilesController.getFilesController().getFile(Airport.class);
        this.loadAirportMenuButton();
    }

    private void createNewAirport() {
        var tempDialog = new CreateNewAirport();
        tempDialog.showAndWait();
        this.loadAirportMenuButton();
    }

    private void loadAirportMenuButton() {
        this.airportMenuButton.getItems().clear();
        for (Airport airport : Controller.getItems(Airport.class, FilesController.getFilesController().getFile(Airport.class))) {
            if (airport == null) continue;
            var airportMenuItem = new MenuItem(airport.getName());
            airportMenuItem.setOnAction(event -> {
                this.airportMenuButton.setText(airport.getName());
                this.loadRunwayMenuButton(airport);
                this.runwayMenuButton.getItems().get(0).fire();
            });
            this.airportMenuButton.getItems().add(airportMenuItem);
        }
    }

    private void loadRunwayMenuButton(Airport airport) {
        this.runwayMenuButton.getItems().clear();
        this.masterPane.getChooseObstacle().getDistanceSlider().setDisable(false);
        for (var runwayGroup : airport.getRunwayGroups()) {
            var runwayMenuItem = new MenuItem(runwayGroup.getNames());
            runwayMenuItem.setOnAction(event -> {
                var s = (runwayGroup.getLower().getStripEnd() + runwayGroup.getUpper().getStripEnd()) / 2;
                var l = Math.max(runwayGroup.getLower().getTORA(), runwayGroup.getUpper().getTORA());

                this.stripEndLabel.setText(String.valueOf(s) + "m");
                this.runwayMenuButton.setText(runwayGroup.getNames());
                this.lowerRunwayLabel.setText(runwayGroup.getLower().getName());
                this.upperRunwayLabel.setText(runwayGroup.getUpper().getName());
                this.lowerDisplacedThresholdLabel.setText(String.valueOf(runwayGroup.getLower().getDisplacedThreshold()));
                this.upperDisplacedThresholdLabel.setText(String.valueOf(runwayGroup.getUpper().getDisplacedThreshold()));
                this.lowerClearwayLabel.setText(String.valueOf(runwayGroup.getLower().getClearway()));
                this.upperClearwayLabel.setText(String.valueOf(runwayGroup.getUpper().getClearway()));
                this.lowerStopwayLabel.setText(String.valueOf(runwayGroup.getLower().getStopway()));
                this.upperStopwayLabel.setText(String.valueOf(runwayGroup.getUpper().getStopway()));

                this.masterPane.getRunwayPane().newRunway(runwayGroup);
                this.masterPane.getChooseObstacle().getDistanceSlider().setMin(-s);
                this.masterPane.getChooseObstacle().getDistanceSlider().setMax(l + s);
                this.masterPane.getChooseObstacle().getDistanceSlider().setValue(0);
                this.masterPane.getChooseObstacle().getRemoveObstacleButton().fire();
            });
            this.runwayMenuButton.getItems().add(runwayMenuItem);
        }
    }
}
