package com.group20seq.runway_redeclaration.UI;

import com.group20seq.runway_redeclaration.Configs.Airport;
import com.group20seq.runway_redeclaration.Configs.Obstacle;
import com.group20seq.runway_redeclaration.Controllers.Controller;
import com.group20seq.runway_redeclaration.Controllers.FilesController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


public class ChooseObstacle extends GridPane {
    private final MenuButton obstacleMenuButton = new MenuButton();
    private final Slider distanceSlider;
    private MasterPane masterPane;

    Obstacle currentObstacle = null;

    private Label obstacleHeightLabel = new Label("...");
    private Label obstacleWidthLabel = new Label("...");
    private Button removeObstacleButton;

    ChooseObstacle(MasterPane pane) {
        super();

        var columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        this.getColumnConstraints().addAll(columnConstraints, columnConstraints);

        this.setVgap(15);
        this.setHgap(15);
        this.setPadding(new Insets(10, 10, 10, 10));

        this.obstacleMenuButton.setMaxWidth(Double.MAX_VALUE);
        this.distanceSlider = new Slider();
        this.distanceSlider.setDisable(true);
        this.distanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (this.currentObstacle != null)
                this.masterPane.getRunwayPane().newObstacle(this.currentObstacle, newValue.intValue());
        });
        this.masterPane = pane;

        this.add(new Label("Choose Obstacle"), 0, 0);
        this.add(this.obstacleMenuButton, 1, 0);

        this.add(new Label("Height"), 0, 1);
        this.add(obstacleHeightLabel, 1, 1);

        this.add(new Label("Width"), 0, 2);
        this.add(obstacleWidthLabel, 1, 2);

        this.add(new Label("Distance"), 0, 3);
        this.add(this.distanceSlider, 1, 3);

        this.removeObstacleButton = new Button("Remove Obstacle");
        this.removeObstacleButton.setOnAction(event -> {
            this.masterPane.getRunwayPane().newObstacle(null, 0);
            this.loadObstacleMenuButton();
        });
        this.add(this.removeObstacleButton, 1, 4);

        for (int i = 0; i < this.getChildren().size(); i++) {
            if (this.getChildren().get(i) instanceof Label) {
                if (this.getColumnIndex(this.getChildren().get(i)) == 0) {
                    ((Label) this.getChildren().get(i)).setStyle("-fx-font-weight: bold");
                    ((Label) this.getChildren().get(i)).setText(((Label) this.getChildren().get(i)).getText() + ":");
                }
            }
        }

        var changeObstacleButton = new Button("Change Obstacle File");
        changeObstacleButton.setOnAction(event -> {
            FilesController.getFilesController().setFile(Obstacle.class);
            this.loadObstacleMenuButton();
        });
        this.add(changeObstacleButton, 0, 4);

        var createNewObstacleButton = new Button("Create New Obstacle");
        createNewObstacleButton.setOnAction(event -> this.createNewObstacle());
        this.add(createNewObstacleButton, 2, 4);

        FilesController.getFilesController().getFile(Obstacle.class);
        this.loadObstacleMenuButton();
    }

    private void createNewObstacle() {
        var temp = new CreateNewObstacle();
        temp.showAndWait();
        this.loadObstacleMenuButton();
    }

    private void loadObstacleMenuButton() {
        this.obstacleMenuButton.getItems().clear();
        for (Obstacle o : Controller.getItems(Obstacle.class, FilesController.getFilesController().getFile(Obstacle.class))) {
            var menuItem = new MenuItem(o.getName());
            menuItem.setOnAction(event -> {
                this.obstacleMenuButton.setText(o.getName());
                this.obstacleHeightLabel.setText(o.getHeight() + "m");
                this.obstacleWidthLabel.setText(o.getWidth() + "m");
                this.currentObstacle = o;

                this.distanceSlider.setValue((double)o.getWidth() / 4);

                // todo
                this.distanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() > this.distanceSlider.getMax() - (double)o.getWidth() / 2)
                        this.distanceSlider.setValue(this.distanceSlider.getMax() - (double)o.getWidth() / 2);
                    else if (newValue.doubleValue() < (double)o.getWidth() / 4)
                        this.distanceSlider.setValue((double)o.getWidth() / 4);
                });
            });
            this.obstacleMenuButton.getItems().add(menuItem);
        }
    }

    public Slider getDistanceSlider() {
        return this.distanceSlider;
    }

    public Button getRemoveObstacleButton() {
        return this.removeObstacleButton;
    }
}
