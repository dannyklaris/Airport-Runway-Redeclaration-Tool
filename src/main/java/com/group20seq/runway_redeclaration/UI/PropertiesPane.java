package com.group20seq.runway_redeclaration.UI;

import com.group20seq.runway_redeclaration.Configs.RunwayGroup;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

public class PropertiesPane extends Pane {
    Label breakDowns = new Label("");
    Label notification = new Label("");
    GridPane info = new GridPane();

    public PropertiesPane(MasterPane masterPane) {
        super();

        this.notification.setStyle("-fx-font-weight: bold; -fx-text-fill: #ff0000");
        this.notification.setText("No runway selected");

        var container = new VBox();
        container.setPadding(new Insets(10, 10, 10, 10));
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(new Separator(), this.info, new Separator(), this.breakDowns, this.notification);
        container.minWidthProperty().bind(this.widthProperty());
        container.setSpacing(15);

        // expand each column in the grid to fill the available space
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        columnConstraints.setHalignment(HPos.CENTER);
        this.info.getColumnConstraints().addAll(columnConstraints, columnConstraints, columnConstraints, columnConstraints);
        this.info.setVgap(15);
        this.getChildren().add(container);
    }

    void setData(RunwayGroup runwayGroup) {
        this.notification.setTextFill(Paint.valueOf("#ff0000"));
        this.notification.setText("Continue as normal");
        this.info.getChildren().clear();
        this.info.setAlignment(Pos.CENTER);

        this.info.add(new Label("TORA"), 0, 0);
        this.info.add(new Label("TODA"), 1, 0);
        this.info.add(new Label("ASDA"), 2, 0);
        this.info.add(new Label("LDA"), 3, 0);
        
        for (var i = 0; i < 4; i++)
            this.info.getChildren().get(i).setStyle("-fx-font-weight: bold");

        var originalLabel = new Label("Original");
        originalLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #cbcbcb");
        originalLabel.setMaxWidth(Double.MAX_VALUE);
        this.info.add(originalLabel, 0, 1, 4, 1);
        
        this.info.add(new Label(String.valueOf(runwayGroup.getUpper().getStartTORA())), 0, 2);
        this.info.add(new Label(String.valueOf(runwayGroup.getUpper().getStartTODA())), 1, 2);
        this.info.add(new Label(String.valueOf(runwayGroup.getUpper().getStartASDA())), 2, 2);
        this.info.add(new Label(String.valueOf(runwayGroup.getUpper().getStartLDA())), 3, 2);

        this.info.add(new Label(String.valueOf(runwayGroup.getLower().getStartTORA())), 0, 3);
        this.info.add(new Label(String.valueOf(runwayGroup.getLower().getStartTODA())), 1, 3);
        this.info.add(new Label(String.valueOf(runwayGroup.getLower().getStartASDA())), 2, 3);
        this.info.add(new Label(String.valueOf(runwayGroup.getLower().getStartLDA())), 3, 3);


        var recalculatedLabel = new Label("Recalculated");
        recalculatedLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #cbcbcb");
        recalculatedLabel.setMaxWidth(Double.MAX_VALUE);
        this.info.add(recalculatedLabel, 0, 4, 4, 1);


        this.info.add(new Label(String.valueOf(runwayGroup.getUpper().getTORA())), 0, 5);
        this.info.add(new Label(String.valueOf(runwayGroup.getUpper().getTODA())), 1, 5);
        this.info.add(new Label(String.valueOf(runwayGroup.getUpper().getASDA())), 2, 5);
        this.info.add(new Label(String.valueOf(runwayGroup.getUpper().getLDA())), 3, 5);

        this.info.add(new Label(String.valueOf(runwayGroup.getLower().getTORA())), 0, 6);
        this.info.add(new Label(String.valueOf(runwayGroup.getLower().getTODA())), 1, 6);
        this.info.add(new Label(String.valueOf(runwayGroup.getLower().getASDA())), 2, 6);
        this.info.add(new Label(String.valueOf(runwayGroup.getLower().getLDA())), 3, 6);

        // center all labels
        for (var i = 0; i < this.info.getChildren().size(); i++) {
            if (this.info.getChildren().get(i) instanceof Label) {
                ((Label) this.info.getChildren().get(i)).setAlignment(Pos.CENTER);
                this.info.getChildren().get(i).setStyle(this.info.getChildren().get(i).getStyle() + "; -fx-text-alignment: center");
            }
        }

        if (runwayGroup.getUpper().getObstacle() != null) {
            var breakdown = new StringBuilder();
            this.notification.setText("Notify pilot: redeclaration required due to obstacle added.");
            breakdown.append("\nUpper\n");
            runwayGroup.getUpper().getBreakdown().forEach(info -> breakdown.append(info).append("\n"));
            breakdown.append("\nLower\n");
            runwayGroup.getLower().getBreakdown().forEach(info -> breakdown.append(info).append("\n"));
            this.breakDowns.setText(breakdown.toString());
        } else
            this.breakDowns.setText("");

        runwayGroup.setObstacleChangedListener(exists -> {
                this.notification.setText("Notify Pilot: Obstacle added");
        });
    }
}
