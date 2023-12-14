package com.group20seq.runway_redeclaration.UI;

import com.group20seq.runway_redeclaration.Configs.*;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.w3c.dom.Document;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Function;


public class RunwayPane extends TabPane {
    private final Pane topViewPane = new AnchorPane();
    private final Pane sideViewPane = new Pane();
    private final Pane mapViewPane = new Pane();
    private final Pane view3DPane = new Pane();

    private final MasterPane masterPane;

    private ChosenConfig chosenConfig = null;

    private final double RUNWAY_HEIGHT_MULT = 0.1;
    private final double RUNWAY_Y_MULT = (1 - RUNWAY_HEIGHT_MULT) / 2;
    private final double ARROW_LINE_Y_DIF = RUNWAY_HEIGHT_MULT / 2;
    private final double ARROW_Y_GAP = 10;

    private Boolean primaryButtonPressed = false;
    private final CheckBox drawGradedCheckBox = new CheckBox("Draw Graded Area?");
    private final Button zoomInButton = new Button("+");
    private final Button zoomOutButton = new Button("-");

    public static class ChosenConfig {
        Airport airport;
        RunwayGroup runwayGroup;
        Obstacle obstacle;
        Integer obstaclePosition;
    }
    
    public RunwayPane(MasterPane masterPane) {
        super();
        this.masterPane = masterPane;
        this.chosenConfig = new ChosenConfig();
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        this.setStyle("-fx-border-color: #cbcbcb; -fx-border-width: 1px;");

        this.drawGradedCheckBox.setOnAction(event -> this.drawTopView());
        this.zoomInButton.setOnAction(event -> {
            var pane = (Pane)((ScrollPane) this.getTabs().get(this.getSelectionModel().getSelectedIndex()).getContent()).getContent();
            pane.setScaleX(Math.min(10, pane.getScaleX() + 0.1));
            pane.setScaleY(Math.min(10, pane.getScaleY() + 0.1));
        });
        this.zoomOutButton.setOnAction(event -> {
            var pane = (Pane)((ScrollPane) this.getTabs().get(this.getSelectionModel().getSelectedIndex()).getContent()).getContent();
            pane.setScaleX(Math.max(1, pane.getScaleX() - 0.1));
            pane.setScaleY(Math.max(1, pane.getScaleY() - 0.1));
        });

        this.getTabs().add(new Tab("Top View", new ScrollPane(this.topViewPane)));
        this.getTabs().add(new Tab("Side View", new ScrollPane(this.sideViewPane)));
        this.getTabs().add(new Tab("Map View", new ScrollPane(this.mapViewPane)));
        this.getTabs().add(new Tab("3D View", new ScrollPane(this.view3DPane)));

        this.getTabs().get(0).setOnSelectionChanged(event -> drawTopView());
        this.getTabs().get(1).setOnSelectionChanged(event -> drawSideView());
        this.getTabs().get(2).setOnSelectionChanged(event -> drawMapView());
        this.getTabs().get(3).setOnSelectionChanged(event -> draw3DView());

        this.getTabs().get(2).setDisable(true);
        this.getTabs().get(3).setDisable(true);

        // Add gesture support to the scroll panes
        for (var tab : this.getTabs()) {
            var scrollPane = (ScrollPane) tab.getContent();
            var innerPane = (Pane) scrollPane.getContent();

            scrollPane.setOnScroll(event -> {
                if (event.isControlDown()) {
                    // zoom
                    var scale = innerPane.getScaleX();
                    scale += event.getDeltaY() * 0.001;
                    scale = Math.max(1, Math.min(scale, 10));
                    innerPane.setScaleX(scale);
                    innerPane.setScaleY(scale);
                }
                else if (innerPane.getScaleX() > 1) {
                    // move
                    innerPane.setTranslateX(innerPane.getTranslateX() + event.getDeltaX());
                    innerPane.setTranslateY(innerPane.getTranslateY() + event.getDeltaY());
                }
            });

            scrollPane.setOnZoom(event -> {
                // Zoom in or out
                var scale = innerPane.getScaleX();
                scale += event.getZoomFactor() - 1;
                scale = Math.max(1, Math.min(10, scale));
                innerPane.setScaleX(scale);
                innerPane.setScaleY(scale);
            });

            innerPane.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    this.primaryButtonPressed = true;
                }
            });

            innerPane.setOnMouseReleased(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    this.primaryButtonPressed = false;
                }
            });

            scrollPane.setOnMouseMoved(event -> {
                // move the zoomed in runway pane
                if (innerPane.getScaleX() > 1 && this.primaryButtonPressed) {
                    var x = event.getX() - innerPane.getWidth() / 2;
                    var y = event.getY() - innerPane.getHeight() / 2;
                    innerPane.setTranslateX(x);
                    innerPane.setTranslateY(y);
                }
            });

            scrollPane.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    innerPane.setScaleX(1);
                    innerPane.setScaleY(1);
                    innerPane.setTranslateX(0);
                    innerPane.setTranslateY(0);
                }
            });
        }


        // Fix the width to the height so its always a square
        this.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(this, Priority.ALWAYS);
        this.prefWidthProperty().bind(this.heightProperty());
    }

    private boolean configInvalid() {
        if (chosenConfig == null || chosenConfig.airport == null || chosenConfig.runwayGroup == null) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Configuration Error");
            alert.setContentText("To show the view of a runway, you need to choose an airport and a runway group first.");
            // alert.showAndWait();
            return true;
        }

        return false;
    }

    private void drawTopView() {
        if (configInvalid()) return;

        this.topViewPane.getChildren().clear();

        if (this.drawGradedCheckBox.selectedProperty().get()) {
            var backgroundRectangle = new Rectangle();
            backgroundRectangle.setFill(Color.GREEN);
            backgroundRectangle.widthProperty().bind(this.widthProperty());
            backgroundRectangle.heightProperty().bind(this.heightProperty());

            var clearedArea = new Path();
            clearedArea.setFill(Color.BLUE);
            clearedArea.getElements().addAll(
                    new MoveTo(0, this.getHeight() * 0.6),
                    new LineTo(this.getWidth() * 0.15, this.getHeight() * 0.6),
                    new LineTo(this.getWidth() * 0.3, this.getHeight() * 0.8),
                    new LineTo(this.getWidth() * 0.7, this.getHeight() * 0.8),
                    new LineTo(this.getWidth() * 0.85, this.getHeight() * 0.6),
                    new LineTo(this.getWidth(), this.getHeight() * 0.6),

                    new LineTo(this.getWidth(), this.getHeight() * 0.4),
                    new LineTo(this.getWidth() * 0.86, this.getHeight() * 0.4),
                    new LineTo(this.getWidth() * 0.7, this.getHeight() * 0.2),
                    new LineTo(this.getWidth() * 0.3, this.getHeight() * 0.2),
                    new LineTo(this.getWidth() * 0.15, this.getHeight() * 0.4),
                    new LineTo(0, this.getHeight() * 0.4),

                    new LineTo(0, this.getHeight() * 0.6),
                    new ClosePath()
            );
            this.topViewPane.getChildren().addAll(backgroundRectangle, clearedArea);
        }
        else {
            var backgroundUpperRectangle = new Rectangle();
            backgroundUpperRectangle.setFill(Color.BLUE);
            backgroundUpperRectangle.widthProperty().bind(this.widthProperty());
            backgroundUpperRectangle.heightProperty().bind(this.heightProperty().multiply(0.5));
            backgroundUpperRectangle.setX(0);
            backgroundUpperRectangle.setY(0);

            var backgroundLowerRectangle = new Rectangle();
            backgroundLowerRectangle.setFill(Color.GREEN);
            backgroundLowerRectangle.widthProperty().bind(this.widthProperty());
            backgroundLowerRectangle.heightProperty().bind(this.heightProperty().multiply(0.5));
            backgroundLowerRectangle.setX(0);
            backgroundLowerRectangle.setY(this.getHeight() * 0.5);

            this.topViewPane.getChildren().addAll(backgroundUpperRectangle, backgroundLowerRectangle);
        }


        // upper label, anchored to top left
        var upperRunwayLabel = new Label(this.chosenConfig.runwayGroup.getUpper().getFormattedName() + "\n---------->");
        upperRunwayLabel.setAlignment(Pos.CENTER);
        upperRunwayLabel.setFont(new Font(20));
        upperRunwayLabel.prefWidthProperty().bind(this.widthProperty());
        upperRunwayLabel.setStyle("-fx-font-weight: bold; -fx-text-alignment: center");
        AnchorPane.setTopAnchor(upperRunwayLabel, 50.0);

        // lower label, anchored to bottom right
        var lowerRunwayLabel = new Label("<----------\n" + this.chosenConfig.runwayGroup.getLower().getFormattedName());
        lowerRunwayLabel.setAlignment(Pos.CENTER);
        lowerRunwayLabel.setFont(new Font(20));
        lowerRunwayLabel.prefWidthProperty().bind(this.widthProperty());
        lowerRunwayLabel.setStyle("-fx-font-weight: bold; -fx-text-alignment: center");
        AnchorPane.setBottomAnchor(lowerRunwayLabel, 50.0);
        this.topViewPane.getChildren().addAll(upperRunwayLabel, lowerRunwayLabel);

        // runway rectangles
        var runwayRects = this.generateRunway();
        var centerLine = new Line();
        centerLine.setStroke(Color.WHITE);
        centerLine.setStrokeWidth(4);
        centerLine.startXProperty().bind(runwayRects.getKey().xProperty().add(runwayRects.getKey().widthProperty().multiply(0.1)));
        centerLine.endXProperty().bind(runwayRects.getKey().xProperty().add(runwayRects.getKey().widthProperty().multiply(0.9)));
        centerLine.startYProperty().bind(runwayRects.getKey().yProperty().add(runwayRects.getKey().heightProperty().divide(2)));
        centerLine.endYProperty().bind(runwayRects.getValue().yProperty().add(runwayRects.getKey().heightProperty().divide(2)));
        centerLine.getStrokeDashArray().addAll(10d, 10d);
        this.topViewPane.getChildren().addAll(runwayRects.getKey(), runwayRects.getValue(), centerLine);

        // switch to graded background
        AnchorPane.setTopAnchor(this.drawGradedCheckBox, 10.0);
        AnchorPane.setLeftAnchor(this.drawGradedCheckBox, 10.0);
        this.topViewPane.getChildren().add(this.drawGradedCheckBox);

        // zoom buttons
        var zoomContainer = new VBox();
        zoomContainer.getChildren().add(this.zoomInButton);
        zoomContainer.getChildren().add(this.zoomOutButton);
        zoomContainer.setFillWidth(true);
        AnchorPane.setTopAnchor(zoomContainer, 10.0);
        AnchorPane.setRightAnchor(zoomContainer, 20.0);
        this.topViewPane.getChildren().add(zoomContainer);

        // arrows
        this.drawRunwayMetaData(this.chosenConfig.runwayGroup.getUpper(), true);
        this.drawRunwayMetaData(this.chosenConfig.runwayGroup.getLower(), false);

        // obstacle
        if (this.chosenConfig.obstacle != null) {
            var obstacleCircle = new Circle();
            obstacleCircle.radiusProperty().bind(runwayRects.getKey().heightProperty().multiply(0.25));
            obstacleCircle.setFill(Color.TRANSPARENT);
            obstacleCircle.setStyle("-fx-stroke: black; -fx-stroke-width: 3px");

            if (this.live_x() != 0.25)
                obstacleCircle.setCenterX(this.widthProperty().multiply(this.live_x()).subtract(obstacleCircle.radiusProperty().multiply(2)).get());
            else
                obstacleCircle.setCenterX(this.widthProperty().multiply(this.live_x() + this.live_width()).add(obstacleCircle.radiusProperty().multiply(2)).get());
            obstacleCircle.setCenterY(this.heightProperty().multiply(0.5).get());

            var text = new Text("x");
            text.setFont(new Font(60));
            text.setTextAlignment(TextAlignment.CENTER);
            text.setStyle("-fx-font-weight: bold; -fx-text-alignment: center");
            text.setTranslateX(obstacleCircle.getCenterX() - text.getBoundsInLocal().getWidth() / 2);
            text.setTranslateY(obstacleCircle.getCenterY() + text.getBoundsInLocal().getHeight() / 4);

            this.topViewPane.getChildren().addAll(text);
        }
    }

    private void drawSideView() {
        if (configInvalid()) return;
        this.sideViewPane.getChildren().clear();

        // Get the runway rectangle and placeholder rectangle.
        var p = this.generateRunway();
        var runwayRect = p.getKey();

        // Side view runway line
        var runway = new Line();
        runway.startXProperty().bind(runwayRect.xProperty());
        runway.startYProperty().bind(runwayRect.yProperty());
        runway.endXProperty().bind(runwayRect.xProperty().add(runwayRect.widthProperty()));
        runway.endYProperty().bind(runwayRect.yProperty());

        // Side view runway line
        var placeholder = new Line();
        placeholder.startXProperty().bind(this.widthProperty().multiply(0.25));
        placeholder.startYProperty().bind(runwayRect.yProperty());
        placeholder.endXProperty().bind(this.widthProperty().multiply(0.75));
        placeholder.endYProperty().bind(runwayRect.yProperty());
        placeholder.getStrokeDashArray().addAll(3d, 4d);

        if (this.chosenConfig.obstacle != null && this.chosenConfig.obstaclePosition != null)
        {
            var airplane = this.generateAirplaneSVG();
            this.sideViewPane.getChildren().add(airplane);

            var diagonalLine = new Line();
            // diagonal line from the innermost lower corner of the airplane box to the outermost upper corner
            diagonalLine.startXProperty().bind(airplane.layoutXProperty().add(airplane.getScaleX() > 0 ? 0 : airplane.getBoundsInLocal().getWidth()));
            diagonalLine.startYProperty().bind(runway.endYProperty().subtract(runwayRect.heightProperty()));
            diagonalLine.endXProperty().bind(airplane.layoutXProperty().add(airplane.getScaleX() > 0 ? airplane.getBoundsInLocal().getWidth() : 0));
            diagonalLine.endYProperty().bind(runway.endYProperty());
            this.sideViewPane.getChildren().add(diagonalLine);
        }

        this.sideViewPane.getChildren().addAll(placeholder, runway);
    }

    private SVGPath generateAirplaneSVG() {
        var p = this.generateRunway();
        var runwayRect = p.getKey();
        var requiresSwitch = this.live_x() == 0.25;

        try {
            Document doc = XMLLoader.parse("airplane.svg");
            SVGPath airplane = new SVGPath();
            String airplaneSVG = doc.getElementsByTagName("svg").item(0).getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem("d").getNodeValue();
            airplane.setContent(airplaneSVG);
            airplane.setRotate(335);
            airplane.layoutXProperty().bind(this.widthProperty().multiply(requiresSwitch ? this.live_x() + this.live_width() : this.live_x() - 0.2));
            airplane.layoutYProperty().bind(runwayRect.yProperty().subtract(runwayRect.heightProperty().multiply(1.15)));
            airplane.setScaleX(0.3);
            airplane.setScaleY(0.3);
            if (requiresSwitch) {
                airplane.setScaleX(-airplane.getScaleX());
                airplane.setRotate(30);
            }
            return airplane;
        }
        catch (Exception e) {
            return null;
        }
    }

    private void drawMapView() {}

    private void draw3DView() {}

    private void drawRunwayMetaData(Runway runway, boolean isUpper) {
        var metadata = new PriorityQueue<Pair<Integer, Function<Triple<Runway, Boolean, Double>, Void>>>(Comparator.comparing(Pair::getKey));
        metadata.add(new Pair<>(runway.getLDA(), this::drawLDA));
        metadata.add(new Pair<>(runway.getTORA(), this::drawTORA));
        metadata.add(new Pair<>(runway.getASDA(), this::drawASDA));
        metadata.add(new Pair<>(runway.getTODA(), this::drawTODA));

        var currentY = this.ARROW_LINE_Y_DIF;
        for (var pair : metadata) {
            pair.getValue().apply(new Triple<>(runway, isUpper, currentY));
            currentY += this.ARROW_LINE_Y_DIF;
        }
    }

    private Void drawLDA(Triple<Runway, Boolean, Double> info) {
        var runway = info.first();
        var up = info.second();
        var height = info.third();
        var label = String.format("LDA (%dm)", up ? this.chosenConfig.runwayGroup.getUpper().getLDA() : this.chosenConfig.runwayGroup.getLower().getLDA());

        final var DT_LENGTH_MULT = 0.01;
        final var DT_Y_MULT = RUNWAY_Y_MULT;
        final var DT_HEIGHT_MULT = RUNWAY_HEIGHT_MULT;

        var displacedThresholdRatio = (double)runway.getDisplacedThreshold() / runway.getStartTORA();
        displacedThresholdRatio += (0.5 - displacedThresholdRatio) / 2; // merge to middle 0.5
        if (!up)
            displacedThresholdRatio = 1 - displacedThresholdRatio;

        var displacedThresholdInBounds =
                displacedThresholdRatio > this.live_x() && displacedThresholdRatio < this.live_x() + this.live_width();

        if (runway.getDisplacedThreshold() != 0 && displacedThresholdInBounds) {
            var displacedThresholdRender = new Rectangle();
            var displacedThresholdPosition = ((double)runway.getStartTORA() / 2 - runway.getDisplacedThreshold()) / runway.getStartTORA() / 2;
            displacedThresholdRender.xProperty().bind(this.widthProperty().multiply(0.5 + displacedThresholdPosition * (!up ? 1 : -1) - DT_LENGTH_MULT/2));
            displacedThresholdRender.yProperty().bind(this.heightProperty().multiply(DT_Y_MULT));
            displacedThresholdRender.widthProperty().bind(this.widthProperty().multiply(DT_LENGTH_MULT));
            displacedThresholdRender.heightProperty().bind(this.heightProperty().multiply(DT_HEIGHT_MULT));
            displacedThresholdRender.setFill(Color.LIGHTGRAY);

            var ldaRender = this.generateLabel(label,
                    up ? 0.5 - displacedThresholdPosition : this.live_x(),
                    0.5 + (ARROW_LINE_Y_DIF * (up ? -1 : 1)),
                    up ? this.live_x() + this.live_width() : 0.5 + displacedThresholdPosition,
                    height, up);

            this.topViewPane.getChildren().addAll(displacedThresholdRender, ldaRender);
        }

        else {
            var ldaRender = this.generateLabel(label,
                    this.live_x(),
                    0.5 + (ARROW_LINE_Y_DIF * (up ? -1 : 1)),
                    this.live_x() + this.live_width(),
                    height, up);

            this.topViewPane.getChildren().add(ldaRender);
        }

        return null;
    }

    private Void drawTORA(Triple<Runway, Boolean, Double> info) {
        var runway = info.first();
        var up = info.second();
        var height = info.third();
        var label = String.format("TORA (%dm)", up ? this.chosenConfig.runwayGroup.getUpper().getTORA() : this.chosenConfig.runwayGroup.getLower().getTORA());

        var toraRender = this.generateLabel(label,
                this.live_x(),
                0.5 + (ARROW_LINE_Y_DIF * (up ? -1 : 1)),
                this.live_x() + this.live_width(),
                height, up);

        this.topViewPane.getChildren().add(toraRender);
        return null;
    }

    private Void drawTODA(Triple<Runway, Boolean, Double> info) {
        var runway = info.first();
        var up = info.second();
        var height = info.third();
        var label = String.format("TODA (%dm)", up ? this.chosenConfig.runwayGroup.getUpper().getTODA() : this.chosenConfig.runwayGroup.getLower().getTODA());

        final var CLEARWAY_LENGTH_MULT = 0.1;
        final var CLEARWAY_Y_MULT = RUNWAY_Y_MULT;
        final var CLEARWAY_HEIGHT_MULT = RUNWAY_HEIGHT_MULT;
        final var FF_CLEARWAY_X_ADJUST = (0.5 - 2 * CLEARWAY_LENGTH_MULT) / 2;

        // If the TODA is the same as the TORA, there is no clearway, so just draw the TODA line.
        if (runway.getTORA() == runway.getTODA()) {
            var todaRender = this.generateLabel(label,
                    this.live_x(),
                    0.5 + (ARROW_LINE_Y_DIF * (up ? -1 : 1)),
                    this.live_x() + this.live_width(),
                    height, up);

            this.topViewPane.getChildren().add(todaRender);
        }

        else {
            var clearwayRender = new Rectangle();
            clearwayRender.xProperty().bind(this.widthProperty().multiply(up ? this.live_x() + this.live_width() : FF_CLEARWAY_X_ADJUST));
            clearwayRender.yProperty().bind(this.heightProperty().multiply(CLEARWAY_Y_MULT));
            clearwayRender.widthProperty().bind(this.widthProperty().multiply(CLEARWAY_LENGTH_MULT));
            clearwayRender.heightProperty().bind(this.heightProperty().multiply(CLEARWAY_HEIGHT_MULT));
            clearwayRender.setFill(Color.TRANSPARENT);
            clearwayRender.setStroke(Color.BLACK);

            var todaRender = this.generateLabel(label,
                    this.live_x() - (up ? 0 : CLEARWAY_LENGTH_MULT),
                    0.5 + (ARROW_LINE_Y_DIF * (up ? -1 : 1)),
                    this.live_x() + this.live_width() + (up ? CLEARWAY_LENGTH_MULT : 0),
                    height, up);

            this.topViewPane.getChildren().addAll(clearwayRender, todaRender);
        }

        return null;
    }

    private Void drawASDA(Triple<Runway, Boolean, Double> info) {
        var runway = info.first();
        var up = info.second();
        var height = info.third();
        var label = String.format("ASDA (%dm)", up ? this.chosenConfig.runwayGroup.getUpper().getASDA() : this.chosenConfig.runwayGroup.getLower().getASDA());

        final var STOPWAY_LENGTH_MULT = 0.05;
        final var STOPWAY_Y_MULT = RUNWAY_Y_MULT;
        final var STOPWAY_HEIGHT_MULT = RUNWAY_HEIGHT_MULT;
        final var FF_STOPWAY_X_ADJUST = (0.5 - 2 * STOPWAY_LENGTH_MULT) / 2;

        // If the ASDA is the same as the TORA, there is no stopway, so just draw the ASDA line.
        if (runway.getTORA() == runway.getASDA()) {
            var asdaRender = this.generateLabel(label,
                    this.live_x(),
                    0.5 + (ARROW_LINE_Y_DIF * (up ? -1 : 1)),
                    this.live_x() + this.live_width(),
                    height, up);

            this.topViewPane.getChildren().add(asdaRender);
        }

        else {
            var stopwayRender = new Rectangle();
            stopwayRender.xProperty().bind(this.widthProperty().multiply(up ? this.live_x() + this.live_width() : FF_STOPWAY_X_ADJUST));
            stopwayRender.yProperty().bind(this.heightProperty().multiply(STOPWAY_Y_MULT));
            stopwayRender.widthProperty().bind(this.widthProperty().multiply(STOPWAY_LENGTH_MULT));
            stopwayRender.heightProperty().bind(this.heightProperty().multiply(STOPWAY_HEIGHT_MULT));
            stopwayRender.setFill(Color.TRANSPARENT);
            stopwayRender.setStroke(Color.BLACK);

            var asdaRender = this.generateLabel(label,
                    this.live_x() - (up ? 0 : STOPWAY_LENGTH_MULT),
                    0.5 + (ARROW_LINE_Y_DIF * (up ? -1 : 1)),
                    this.live_x() + this.live_width() + (up ? STOPWAY_LENGTH_MULT : 0),
                    height, up);

            this.topViewPane.getChildren().addAll(stopwayRender, asdaRender);
        }

        return null;
    }

    private Group generateLabel(String label, double startX, double startY, double endX, double height, boolean up) {
        var middle = startY + height * (up ? -1 : 1);

        var leftLine = new Line();
        leftLine.startXProperty().bind(this.widthProperty().multiply(startX));
        leftLine.startYProperty().bind(this.heightProperty().multiply(startY));
        leftLine.endXProperty().bind(this.widthProperty().multiply(startX));
        leftLine.endYProperty().bind(this.heightProperty().multiply(middle));
        leftLine.getStrokeDashArray().addAll(3d, 4d);

        var rightLine = new Line();
        rightLine.startXProperty().bind(this.widthProperty().multiply(endX));
        rightLine.startYProperty().bind(this.heightProperty().multiply(startY));
        rightLine.endXProperty().bind(this.widthProperty().multiply(endX));
        rightLine.endYProperty().bind(this.heightProperty().multiply(middle));
        rightLine.getStrokeDashArray().addAll(3d, 4d);

        var acrossLine = new Line();
        acrossLine.startXProperty().bind(this.widthProperty().multiply(startX));
        acrossLine.startYProperty().bind(this.heightProperty().multiply(middle));
        acrossLine.endXProperty().bind(this.widthProperty().multiply(endX));
        acrossLine.endYProperty().bind(this.heightProperty().multiply(middle));
        acrossLine.setStrokeWidth(2);
        acrossLine.getStrokeDashArray().addAll(3d, 4d);

        var runwayRectRender = this.generateRunway().getKey();
        var arrowRender = up ? this.drawArrow(endX, middle, up) : this.drawArrow(startX, middle, up);
        var labelRender = new Label(label);
        labelRender.layoutXProperty().bind(runwayRectRender.xProperty().add(runwayRectRender.widthProperty().divide(2)).subtract(labelRender.widthProperty().divide(2)));
        labelRender.layoutYProperty().bind(this.heightProperty().multiply(middle));
        labelRender.setAlignment(Pos.CENTER);
        labelRender.setFont(Font.font(12));

        var lineGroup = new Group();
        lineGroup.getChildren().addAll(leftLine, rightLine, acrossLine, arrowRender, labelRender);
        return lineGroup;
    }

    private Group drawArrow(double start, double end, boolean pointRight) {
        var ARROW_HEAD_SIZE = 0.005;
        var X_FLIP = pointRight ? -1 : 1;
        var Y_FLIP = pointRight ? -1 : 1;

        Line arrowTop = new Line();
        arrowTop.startXProperty().bind(this.widthProperty().multiply(start));
        arrowTop.startYProperty().bind(this.heightProperty().multiply(end));
        arrowTop.endXProperty().bind(this.widthProperty().multiply(start + ARROW_HEAD_SIZE * X_FLIP));
        arrowTop.endYProperty().bind(this.heightProperty().multiply(end + ARROW_HEAD_SIZE * Y_FLIP));

        Line arrowBot = new Line();
        arrowBot.startXProperty().bind(this.widthProperty().multiply(start));
        arrowBot.startYProperty().bind(this.heightProperty().multiply(end));
        arrowBot.endXProperty().bind(this.widthProperty().multiply(start + ARROW_HEAD_SIZE * X_FLIP));
        arrowBot.endYProperty().bind(this.heightProperty().multiply(end + ARROW_HEAD_SIZE * -Y_FLIP));

        var out = new Group();
        out.getChildren().addAll(arrowBot, arrowTop);
        out.getChildren().forEach(node -> ((Line)node).setStrokeWidth(2));
        return out;
    }

    public Pair<Rectangle, Rectangle> generateRunway() {
        var runwayRectRender = new Rectangle();
        runwayRectRender.xProperty().bind(this.widthProperty().multiply(this.live_x()));
        runwayRectRender.yProperty().bind(this.heightProperty().multiply(RUNWAY_Y_MULT));
        runwayRectRender.widthProperty().bind(this.widthProperty().multiply(this.live_width()));
        runwayRectRender.heightProperty().bind(this.heightProperty().multiply(RUNWAY_HEIGHT_MULT));
        runwayRectRender.setFill(new Color(0.2, 0.2, 0.2, 1));

        var placementRectRender = new Rectangle();
        placementRectRender.xProperty().bind(this.widthProperty().multiply(0.25));
        placementRectRender.yProperty().bind(this.heightProperty().multiply(RUNWAY_Y_MULT));
        placementRectRender.widthProperty().bind(this.widthProperty().multiply(0.5));
        placementRectRender.heightProperty().bind(this.heightProperty().multiply(RUNWAY_HEIGHT_MULT));
        placementRectRender.setFill(new Color(0.2, 0.2, 0.2, 0.2));
        placementRectRender.setStroke(Color.BLACK);
        placementRectRender.getStrokeDashArray().addAll(3d, 4d);

        return new Pair<>(runwayRectRender, placementRectRender);
    }

    public void newRunway(RunwayGroup runwayGroup) {
        this.chosenConfig.airport = runwayGroup.getAirport();
        this.chosenConfig.runwayGroup = runwayGroup;
        this.newObstacle(null, 0);
    }

    public void newObstacle(Obstacle obstacle, int where) {
        this.chosenConfig.obstacle = obstacle;
        this.chosenConfig.obstaclePosition = where;

        this.chosenConfig.runwayGroup.getLower().removeObstacle();
        this.chosenConfig.runwayGroup.getUpper().removeObstacle();

        if (obstacle != null) {
            this.chosenConfig.runwayGroup.getLower().addObstacle(obstacle, this.chosenConfig.runwayGroup.getUpper().getTORA() - where);
            this.chosenConfig.runwayGroup.getUpper().addObstacle(obstacle, where);
        }
        this.recalculate();
    }

    public double live_x() {
        var current = this.chosenConfig.runwayGroup.getUpper();
        if (current.getObstaclePosition() > current.getStartTORA() / 2)
            return 0.25;
        return 0.25 + (0.5 - 0.5 * current.getTORA() / current.getStartTORA());
    }

    public double live_width() {
        var current = this.chosenConfig.runwayGroup.getUpper();
        return 0.5 * current.getTORA() / current.getStartTORA();
    }

    public void recalculate() {
        var runwayGroup = this.chosenConfig.runwayGroup;
        var aircraft = new Aircraft();
        var requiresSwitch = this.chosenConfig.obstacle != null && this.chosenConfig.obstaclePosition > (double)runwayGroup.getUpper().getTORA() / 2;

        if (requiresSwitch) {
            runwayGroup.getUpper().TOTLT(aircraft);
            runwayGroup.getLower().TOALO(aircraft);
        } else {
            runwayGroup.getUpper().TOALO(aircraft);
            runwayGroup.getLower().TOTLT(aircraft);
        }

        this.masterPane.getPropertiesPane().setData(runwayGroup);
        this.redrawAll();
    }

    public void redrawAll() {
        this.drawTopView();
        this.drawSideView();
        this.drawMapView();
        this.draw3DView();
    }

    public ChosenConfig getChosenConfig() {
        return chosenConfig;
    }
}


record Triple<T, U, V>(T first, U second, V third) {}
