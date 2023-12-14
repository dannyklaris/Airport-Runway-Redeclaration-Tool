package com.group20seq.runway_redeclaration.UI;


import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

import java.io.IOException;


public class ExportPane extends HBox {
    MasterPane masterPane;

    public ExportPane(MasterPane pane) {
        super();
        this.masterPane = pane;

        var exportCurrentImage = new Button("Export Current Image");
        exportCurrentImage.setOnAction(event -> {
            var currentPane = this.masterPane.getRunwayPane().getTabs().get(this.masterPane.getRunwayPane().getSelectionModel().getSelectedIndex());
            var image = new SnapshotParameters();
            image.setFill(null);
            var snapshot = currentPane.getContent().snapshot(image, null);

            var fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
            var file = fileChooser.showSaveDialog(this.masterPane.getScene().getWindow());

            // save the snapshot to the file
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
            } catch (IOException ignore) {}
        });

        var printCurrentImage = new Button("Print Current Image");
        printCurrentImage.setOnAction(event -> {
            var currentPane = this.masterPane.getRunwayPane().getTabs().get(this.masterPane.getRunwayPane().getSelectionModel().getSelectedIndex());
            var image = new SnapshotParameters();
            image.setFill(null);
            var snapshot = currentPane.getContent().snapshot(image, null);
            var bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            var job =  PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex != 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                graphics.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
                return Printable.PAGE_EXISTS;
            });

            try {
                job.print();
            } catch (Exception ignore) {}

        });

        this.getChildren().addAll(exportCurrentImage, printCurrentImage);
    }
}

