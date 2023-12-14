package com.group20seq.runway_redeclaration.UI;

import com.group20seq.runway_redeclaration.Configs.Obstacle;
import com.group20seq.runway_redeclaration.Configs.XMLLoader;
import com.group20seq.runway_redeclaration.Controllers.FilesController;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class CreateNewObstacle extends Dialog<Integer> {
    private final TextField obstacleName = new TextField();
    private final Spinner<Integer> obstacleHeight = new Spinner<>(0, 40, 0);

    public CreateNewObstacle() {
        super();
        this.setResizable(true);

        var layout = new GridPane();
        layout.add(new Label("Obstacle Name"), 0, 0);
        layout.add(obstacleName, 1, 0);

        layout.add(new Label("Obstacle Height"), 0, 1);
        layout.add(obstacleHeight, 1, 1);
        obstacleHeight.setEditable(true);

        this.getDialogPane().setContent(layout);

        this.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                var obstacle = new Obstacle(obstacleName.getText(), obstacleHeight.getValue());
                var path = FilesController.getFilesController().getFile(Obstacle.class).getPath();
                try {
                    XMLLoader.serialize(obstacle);
                } catch (Exception ignore) {
                    return -1;
                }
                return 0;
            }
            return -1;
        });

        // add an OK and cancel button
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }
}
