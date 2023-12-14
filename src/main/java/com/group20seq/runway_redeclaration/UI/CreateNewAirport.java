package com.group20seq.runway_redeclaration.UI;

import com.group20seq.runway_redeclaration.Configs.Airport;
import com.group20seq.runway_redeclaration.Configs.Runway;
import com.group20seq.runway_redeclaration.Configs.RunwayGroup;
import com.group20seq.runway_redeclaration.Configs.XMLLoader;
import com.group20seq.runway_redeclaration.Controllers.FilesController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

public class CreateNewAirport extends Dialog<Integer> {
    VBox runwayGroups;
    Button addNewRunwayGroup = new Button("+");
    SimpleIntegerProperty bearingProp;

    public CreateNewAirport() {
        super();
        this.setResizable(true);
        this.setTitle("Create Airport");
        this.runwayGroups = new VBox();
        this.addNewRunwayGroup.setOnAction(event -> this.addRunwayGroup());

        var layout = new GridPane();
        layout.add(new Label("Airport Name"), 0, 0);
        layout.add(new TextField(), 1, 0);
        layout.add(new Label("Runway Groups"), 0, 1, 2, 1);
        layout.add(this.addNewRunwayGroup, 1, 1);
        layout.add(runwayGroups, 0, 2, 2, 1);
        layout.setPadding(new Insets(10, 10, 10, 10));

        this.getDialogPane().setContent(layout);

        this.setOnCloseRequest(event -> {

                boolean valid = true;
                for (var nextGroup : this.runwayGroups.getChildren()) {
                    var group = (GridPane) nextGroup;
                    var upper = (GridPane) group.getChildren().get(1);
                    var lower = (GridPane) group.getChildren().get(2);
                    
                    var upperTORA = ((Spinner<Integer>) upper.getChildren().get(5)).getValue();
                    var upperTODA = ((Spinner<Integer>) upper.getChildren().get(7)).getValue();
                    var upperLDA = ((Spinner<Integer>) upper.getChildren().get(11)).getValue();
                    var lowerTORA = ((Spinner<Integer>) lower.getChildren().get(5)).getValue();
                    var lowerTODA = ((Spinner<Integer>) lower.getChildren().get(7)).getValue();
                    var lowerLDA = ((Spinner<Integer>) lower.getChildren().get(11)).getValue();
                    valid &= upperTORA <= upperTODA;
                    valid &= lowerTORA <= lowerTODA;
                    valid &= upperLDA <= upperTORA;
                    valid &= lowerLDA <= lowerTORA;
            }
            if (!valid) {
                new Alert(Alert.AlertType.ERROR,
                        "You have entered an invalid Airport configuration. Please check the values.",
                        new ButtonType("Ok")).showAndWait();
            }
        }
                );
        this.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                var groups = new ArrayList<RunwayGroup>();
                for (var nextGroup: this.runwayGroups.getChildren()) {
                    var group = (GridPane) nextGroup;
                    var upper = (GridPane) group.getChildren().get(1);
                    var lower = (GridPane) group.getChildren().get(2);

                    var upperPos = ((ComboBox<String>) upper.getChildren().get(1)).getValue();
                    var upperDir = ((Spinner<Integer>) upper.getChildren().get(3)).getValue();
                    var upperTORA = ((Spinner<Integer>) upper.getChildren().get(5)).getValue();
                    var upperTODA = ((Spinner<Integer>) upper.getChildren().get(7)).getValue();
                    var upperASDA = ((Spinner<Integer>) upper.getChildren().get(9)).getValue();
                    var upperLDA = ((Spinner<Integer>) upper.getChildren().get(11)).getValue();

                    var lowerPos = ((ComboBox<String>) lower.getChildren().get(1)).getValue();
                    var lowerDir = ((Spinner<Integer>) lower.getChildren().get(3)).getValue();
                    var lowerTORA = ((Spinner<Integer>) lower.getChildren().get(5)).getValue();
                    var lowerTODA = ((Spinner<Integer>) lower.getChildren().get(7)).getValue();
                    var lowerASDA = ((Spinner<Integer>) lower.getChildren().get(9)).getValue();
                    var lowerLDA = ((Spinner<Integer>) lower.getChildren().get(11)).getValue();

                    var upperRunway = new Runway(String.valueOf(upperDir) + upperPos, upperTORA, upperTODA, upperASDA, upperLDA, upperTORA - upperLDA);
                    var lowerRunway = new Runway(String.valueOf(lowerDir) + lowerPos, lowerTORA, lowerTODA, lowerASDA, lowerLDA, lowerTORA - lowerLDA);
                    var runwayGroup = new RunwayGroup(upperRunway, lowerRunway);
                    groups.add(runwayGroup);
                }
                var a = new Airport(((TextField)layout.getChildren().get(1)).getText(), groups);
                try {
                    if (a.getName() == "") {
                        Alert b = new Alert(Alert.AlertType.ERROR);
                        b.setContentText("No name selected for airport");
                        b.showAndWait();
                    } else {
                        XMLLoader.serialize(a);
                    }
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

    private Spinner<Integer> makeSpinner(Integer x, Integer y, Integer z) {
        Spinner<Integer> s = new Spinner<>(x, y, z);
        s.setEditable(true);
        return s;
    }

    private <K, V> V addGet(HashMap<K, V> m, K k, V v) {
        m.put(k, v);
        return v;

    }

    private void addRunwayGroup() {
        var runwayGroupInfo = new GridPane();
        runwayGroupInfo.setVgap(15);
        runwayGroupInfo.setHgap(15);

        runwayGroupInfo.add(new Separator(), 0, 0, 2, 1);

        var names = new ArrayList<String>();
        names.add("Upper");
        names.add("Lower");

        HashMap<String, Spinner<Integer>> uiElems = new HashMap<>();

        ArrayList<Pair<String, Integer>> uiTypes = new ArrayList<>();
        uiTypes.add(new Pair<>(" Bearing", 35));
        uiTypes.add(new Pair<>(" TORA", 5000));
        uiTypes.add(new Pair<>(" TODA", 5000));
        uiTypes.add(new Pair<>(" ASDA", 5000));
        uiTypes.add(new Pair<>(" LDA", 5000));
        for (var name: names) {
            var runwayInfo = new GridPane();
            runwayInfo.setVgap(15);
            runwayInfo.setHgap(15);
            runwayInfo.add(new Label(name + " Sign"), 0, 0);
            ComboBox<String> cb = new ComboBox<>();
            cb.getItems().addAll("Left", "Center", "Right");
            runwayInfo.add(cb, 0, 1);
            int count = 0;
            for (Pair<String, Integer> type : uiTypes) {
                count++;
                String total = name + (type.getKey());
                runwayInfo.add(new Label(total), count, 0);
                runwayInfo.add(addGet(uiElems, total, makeSpinner(0, type.getValue(), 0)), count, 1);
            }
            runwayGroupInfo.add(runwayInfo, 0, runwayGroupInfo.getRowCount());
        }
        // a -> a + 18 % 36 ->
        uiElems.get("Upper Bearing").getValueFactory().valueProperty()
            .addListener((a, o, v) -> {
                    if (((v + 18) % 36) != uiElems.get("Lower Bearing").getValue())
                        uiElems.get("Lower Bearing").getValueFactory().setValue((v + 18) % 36);
                });
        uiElems.get("Lower Bearing").getValueFactory().valueProperty()
            .addListener((a, o, v) -> {
                    if (((v + 18) % 36) != uiElems.get("Upper Bearing").getValue())
                        uiElems.get("Upper Bearing").getValueFactory().setValue((v + 18) % 36);
                        });
        this.runwayGroups.getChildren().add(runwayGroupInfo);
    }
}
