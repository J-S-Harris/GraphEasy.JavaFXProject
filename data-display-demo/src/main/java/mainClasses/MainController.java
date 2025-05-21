package mainClasses;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.script.ScriptException;

import easterEggs.EasterEggMethods;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainController {

	// TODO Possible next TODOs
	// Export this to an exe
	// Unit tests
	// Add numbered ticks to the axes
	// Put lefthand controls into collapsable accordions
	// Add an option to display the formulas next to each curve?
	// Add buttons on the graph to pan in tidy/discrete units up/down/L/R?
	// Make the zoom work; the buttons are disabled in the FXML
	// Make the GraphData remember its colour, so that deleting earlier lines
		// doesn't change the colour
		// Also, generally: Tighten up/expand the usage of the GraphData objects
	// Give user checkbox to de/select from list. Only display selected
		// This can be stored in GraphData, and referenced from there

	private final static String appTitle = "GraphEasy";
	public final static String appIconFileName = "linechart.png";
	
	ArrayList<GraphData> graphDataPoints = new ArrayList<>();

	Stage stage;
	
	Canvas canvas;
	GraphicsContext gc;

	@FXML
	HBox main;
	@FXML
	VBox canvasBox;
	@FXML
	TextField formulaEntryTF;
	@FXML
	VBox topButtonsRow;
	@FXML
	CheckBox overlayMultipleCheckBox;
	@FXML
	CheckBox lockPanningCheckBox;
	@FXML
	CheckBox drawAxesCheckBox;
	@FXML
	CheckBox drawBackgroundCheckBox;
	@FXML
	VBox leftPanelVBox;
	@FXML
	VBox listOfLinesVBox;
	@FXML
	ScrollPane listOfLineScrollPane, leftPanelScrollPane;
	@FXML
	Button confirmButton;
	@FXML
	Button snapshotButton;
	@FXML
	Button squareButton;
	@FXML
	Button sqrtButton;
	@FXML
	Label drawBackgroundLabel;
	@FXML
	Label zoomLabel;
	@FXML
	Label drawAxesLabel;
	@FXML
	Label overlayMultipleLabel;
	@FXML
	Label intervalSizeLabel, canvasSizeLabel;
	@FXML
	Button decreaseIntervalButton, increaseIntervalButton;
	@FXML
	Button decreasecanvasSizeButton, increasecanvasSizeButton;
	@FXML
	Button panCentreButton;
	@FXML
	Button zoomOutButton, zoomInButton;
	@FXML
	HBox specialCharsButtonHBox;
	@FXML
	VBox zoomAdjustmentRow;
	@FXML
	CheckBox fillBackgroundCheckBox;
	@FXML
	CheckBox setAlwaysOnTopCheckBox;
	@FXML
	TitledPane drawingPropertiesTitledPane, preferencesTitledPane, canvasPropertiesTitledPane, panningTitledPane;
	@FXML
	Button helpButton;
	@FXML
	Label setAlwaysOnTopLabel, fillBackgroundLabel, lockPanningLabel;
	
	int leftPanelWidth = 200;
	int canvasSizeChangeInterval = 100;
	int canvasMinimumSize = 400;
	int canvasMaximumSize = 1400;
	
	double initialZoomFactor = 1;
	double zoomFactor = 1;
	double zoomInterval = 0.5;
	
	double mouseX = 0;
	double mouseY = 0;
	
	private final int defaultCanvasWidth = 800;
	private final int defaultCanvasHeight = 600;
	
	ArrayList<GraphDataPoints> rightClickDataPoints = new ArrayList<>();
	
	Color canvasBackgroundColour = Color.BEIGE;
	Color canvasAxisColour = Color.BLACK;
	Color rightClickPointsColour = Color.RED;
	Color canvasBackgroundGridColour = new Color(0.5, 0.5, 0.5, 0.5); // Grey
	Color[] lineColours = { Color.LIGHTGREEN, Color.ORANGE, Color.BLUEVIOLET, Color.CHARTREUSE, //
			Color.SALMON, Color.SANDYBROWN, Color.RED, Color.AQUA };

	int colVal = 80, interval = 15;
	Color canvasHelpBorderColour1 = Color.rgb(colVal, colVal, colVal);
	Color canvasHelpBorderColour2 = Color.rgb(colVal-interval, colVal-interval, colVal-interval);
	Color canvasHelpBorderColour3 = Color.rgb(colVal-(2*interval), colVal-(2*interval), colVal-(2*interval));
	Color canvasHelpBorderColour4 = Color.rgb(colVal-(3*interval), colVal-(3*interval), colVal-(3*interval));
	
	Color canvasHelpBorderAccentColour1 = Color.WHITE;
	Color canvasHelpTextColour = Color.BLACK;
	
	int colVal2 = 250;
	int interval2 = 7;
	Color canvasHelpBackgroundColour1 = Color.rgb(colVal2, colVal2, colVal2);
	Color canvasHelpBackgroundColour2 = Color.rgb(colVal2-interval2, colVal2-interval2, colVal2-interval2);
	Color canvasHelpBackgroundColour3 = Color.rgb(colVal2-(2*interval2), colVal2-(2*interval2), colVal2-(2*interval2));
	
	int canvasLineWidth = 2;
	double backgroundLineWidth = 0.5;
	double axesLineWidth = 2;
	
	double gridInterval = 50;
	double gridIntervalMinimumValue = 25;
	double gridIntervalMaximumValue = 800;
	int gridIntervalChangeFactor = 2;
	
	int smallButtonMargin = 10;
	
	double rightClickPointsCrossSize = 7;
	
	boolean needToRedrawGrid = true;
	
	final static String fxmlFileName = "/mainFXML.fxml";

	final String removeRowString = "x";
	final String formulaBoxStartingText = "y=";

	final String pressEnterOnFormulaTooltip = "Type in a formula, then press enter or press \"Draw Curve\"";
	final String goButtonTooltip = "Add the curve to the graph";
	final String snapshotTooltip = "Save the current canvas to your computer as an image";
	final String listUiTooltip = "Click to add this formula to the text box";
	final String canvasTooltip = "Click and drag to pan. Double click to centre on (0,0)";
	
	final String squareTooltip = "Square a number";
	final String squareRootTooltip = "Square root a number";
	final String drawBackgroundTooltip = "Draw the background grid";
	final String drawAxesTooltip = "Draw the X and Y axes";
	final String overlayMultipleTooltip = "Allow multiple curves to be drawn";
	final String helpButtonTooltip = "Display help";
	final String alwaysOnTopTooltip = "Pin this window to the front of the screen";
	final String paintBackgroundTooltip = "Colour the background";
	
	final String intervalSizeTooltip = "Make the grid cells larger or smaller";
	final String intervalSizeTooltip_Grow = "Make the grid cells larger ";
	final String intervalSizeTooltip_Shrink = "Make the grid cells smaller";
	final String canvasSizeTooltip = "Make the canvas larger or smaller";
	final String canvasSizeTooltip_Grow = "Make the canvas larger";
	final String canvasSizeTooltip_Shrink = "Make the canvas smaller";
	final String panCentreTooltip = "Pan back to the origin (0, 0)";
	final String panLockTooltip = "Lock panning to current location";
	
	final String drawingPropertiesTooltip = "Change how curves are drawn to the canvas";
	final String preferencesTooltip = "Change settings and view help";
	final String canvasPropertiesTooltip = "Change the size of the canvas and the background grid";
	final String panningTitledTooltip = "Recentre to the origin (0,0) or lock panning to present position";
	
	private int panningYInterval = 25;
	private int initialPanningOffsetY = 0;
	private int panningOffsetY = 0;
	
	private int panningXInterval = 25;
	private int initialPanningOffsetX = 0;
	private int panningOffsetX = 0;
	
	public HBox getMain() {
		return main;
	}

	public VBox getCanvasBox() {
		return canvasBox;
	}

	public void prepareUI() {
		selectDefaultCheckBoxes();
		createDefaultCanvas();
		addTooltips();
		setEventHandlers();
		setSmallButtonProperties();
		addTooltips();
		setCanvasListeners();
		setMarginsToLeftPanelNodes();
		formatTitlePanesAndContents();
		closeTitlePanesOnStartup();

		// non-specific vvv
		
//		listOfLineScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		leftPanelScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		leftPanelScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		
		leftPanelScrollPane.hvalueProperty().addListener((obs, oldVal, newVal) -> {
			leftPanelScrollPane.setHvalue(0); // Always scroll left
		});
		
//		VBox.setVgrow(listOfLineScrollPane, Priority.ALWAYS);

		VBox.setMargin(panCentreButton, new Insets(0, 0, 5, 0));
		
		formulaEntryTF.setText(formulaBoxStartingText);
		Platform.runLater(() -> {
			moveCaretToEndOfFormulaBox();
		});

		confirmButton.setMaxWidth(Double.MAX_VALUE);
		helpButton.setMaxWidth(Double.MAX_VALUE);
		snapshotButton.setMaxWidth(Double.MAX_VALUE);

		leftPanelScrollPane.setMinWidth(leftPanelWidth);
		leftPanelScrollPane.setPrefWidth(leftPanelWidth);
		leftPanelScrollPane.setMaxWidth(leftPanelWidth);
		leftPanelVBox.setMinWidth(leftPanelWidth);
		leftPanelVBox.setPrefWidth(leftPanelWidth);
		leftPanelVBox.setMaxWidth(leftPanelWidth);
		
	}

	private void setMarginsToLeftPanelNodes() {
		Insets defaultInsets = new Insets(4, 6, 4, 6);
		Insets titledPaneInsets = new Insets(4, 2, 0, 1);
		
		for(Node node : leftPanelVBox.getChildren()) {
			if(node instanceof TitledPane) {
				VBox.setMargin((TitledPane)node, titledPaneInsets);
			} else {
				VBox.setMargin(node, defaultInsets);
			}
		}
	}

	private void closeTitlePanesOnStartup() {
		// in future I might want to leave one open
		closeAllTitledPanes();
	}

	private void formatTitlePanesAndContents() {

		drawingPropertiesTitledPane.setAnimated(false);
		preferencesTitledPane.setAnimated(false);
		canvasPropertiesTitledPane.setAnimated(false);
		panningTitledPane.setAnimated(false);
		
		setSmallMarginToInternalComponents(drawingPropertiesTitledPane);
		setSmallMarginToInternalComponents(preferencesTitledPane);

	}

	private void selectDefaultCheckBoxes() {
		fillBackgroundCheckBox.setSelected(true);
		overlayMultipleCheckBox.setSelected(true);
		drawBackgroundCheckBox.setSelected(true);
		drawAxesCheckBox.setSelected(true);
	}

	private void setCanvasListeners() {
		canvas.setOnMousePressed(event -> {
			mouseX = event.getX();
			mouseY = event.getY();
		});
		
		canvas.setOnMouseDragged(event -> {
			if(!getAllowedToPan(event.getButton())) {
				return;
			}
			if (event.getButton() != MouseButton.SECONDARY) {
				panningOffsetX += (event.getX() - mouseX);
				mouseX = event.getX();
				panningOffsetY += (event.getY() - mouseY);
				mouseY = event.getY();
				recalculateAndRedrawLinesFromCache();
			}
		});
		canvas.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				rightClickDataPoints.add(new GraphDataPoints(event.getX(), event.getY()));
				if(rightClickDataPoints.size() == 2) {
					inferAndDrawNewLine();
				} else {
					redrawCanvasFromCacheImpl();
				}
			} else {
				if (event.getClickCount() == 2) {
					panCentre();
				}
			}
		});
	}

	private void inferAndDrawNewLine() {
		try {
			String formula = inferFormulaFromRightClickDataPoints();
			createLine(formula);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	private String inferFormulaFromRightClickDataPoints() {
		String output = "y=";
		output += calculateRatio();
		rightClickDataPoints.clear();
		return output;
	}

	private String calculateRatio() {

		BigDecimal x1 = BigDecimal.valueOf(rightClickDataPoints.get(0).getX()).subtract(BigDecimal.valueOf(0.5 * canvas.getWidth()));
		BigDecimal y1 = BigDecimal.valueOf(0.5 * canvas.getHeight()).subtract(BigDecimal.valueOf(rightClickDataPoints.get(0).getY()));

		BigDecimal x2 = BigDecimal.valueOf(rightClickDataPoints.get(1).getX()).subtract(BigDecimal.valueOf(0.5 * canvas.getWidth()));
		BigDecimal y2 = BigDecimal.valueOf(0.5 * canvas.getHeight()).subtract(BigDecimal.valueOf(rightClickDataPoints.get(1).getY()));
		
		if (x1.equals(x2)) {
			displayInfoPopup("Cannot calculate slope: x1 and x2 are identical.");
			return "";
		}
		
		BigDecimal ratio = y2.subtract(y1).divide(x2.subtract(x1), MathContext.DECIMAL128);
		BigDecimal offset = y1.add(BigDecimal.valueOf(panningOffsetY)).subtract((x1.subtract(BigDecimal.valueOf(panningOffsetX))).multiply(ratio)).negate();
		
		return String.format("%.2fx%+.2f", ratio, offset.negate());
		
	}

//	private double getYOffset(double ratio) {
//		// TODO Calculate this
////		System.out.println("TODO: Currently this just returns 0 and intersects the origin");
////		return 0;
//		
//		TODO Calculate Y offset
//		
//		return 0;
//		
//	}

	private void placeRedDotOnCanvas() {
		// TODO
		System.out.println("TODO: Put temp red dots on canvas");

	}

	private void addTooltips() {
		Tooltip.install(formulaEntryTF, new Tooltip(pressEnterOnFormulaTooltip));
		Tooltip.install(confirmButton, new Tooltip(goButtonTooltip));
		Tooltip.install(snapshotButton, new Tooltip(snapshotTooltip));
		Tooltip.install(squareButton, new Tooltip(squareTooltip));
		Tooltip.install(sqrtButton, new Tooltip(squareRootTooltip));
		Tooltip.install(drawBackgroundLabel, new Tooltip(drawBackgroundTooltip));
		Tooltip.install(drawAxesLabel, new Tooltip(drawAxesTooltip));
		Tooltip.install(overlayMultipleLabel, new Tooltip(overlayMultipleTooltip));
//		Tooltip.install(canvas, new Tooltip(canvasTooltip));
		Tooltip.install(helpButton, new Tooltip(helpButtonTooltip));
		Tooltip.install(setAlwaysOnTopLabel, new Tooltip(alwaysOnTopTooltip));
		Tooltip.install(fillBackgroundLabel, new Tooltip(paintBackgroundTooltip));
		
		Tooltip.install(intervalSizeLabel, new Tooltip(intervalSizeTooltip));
		Tooltip.install(increaseIntervalButton, new Tooltip(intervalSizeTooltip_Grow));
		Tooltip.install(decreaseIntervalButton, new Tooltip(intervalSizeTooltip_Shrink));
		
		Tooltip.install(canvasSizeLabel, new Tooltip(canvasSizeTooltip));
		Tooltip.install(increasecanvasSizeButton, new Tooltip(canvasSizeTooltip_Grow));
		Tooltip.install(decreasecanvasSizeButton, new Tooltip(canvasSizeTooltip_Shrink));
		
		Tooltip.install(panCentreButton, new Tooltip(panCentreTooltip));
		Tooltip.install(lockPanningLabel, new Tooltip(panLockTooltip));

		Tooltip.install(drawingPropertiesTitledPane, new Tooltip(drawingPropertiesTooltip));
		Tooltip.install(preferencesTitledPane, new Tooltip(preferencesTooltip));
		Tooltip.install(canvasPropertiesTitledPane, new Tooltip(canvasPropertiesTooltip));
		Tooltip.install(panningTitledPane, new Tooltip(panningTitledTooltip));
	}

	private void moveCaretToEndOfFormulaBox() {
		formulaEntryTF.requestFocus();
		formulaEntryTF.positionCaret(formulaEntryTF.getLength());
		formulaEntryTF.deselect();
	}

	private void createDefaultCanvas() {
		canvas = new Canvas();
		canvas.setHeight(defaultCanvasHeight);
		canvas.setWidth(defaultCanvasWidth);

		int canvasMargin = 2;
		HBox.setMargin(canvasBox, new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
		canvasBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
		canvasBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
		canvasBox.setStyle("-fx-border-color: black; -fx-border-width: 3px;");

		gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(canvasLineWidth);
		getCanvasBox().getChildren().add(canvas);
		redrawCanvasFromCacheImpl();
	}

	private void paintBackground() {
		if(getFillBackgroundCheckBoxSelected()) {
			gc.setFill(canvasBackgroundColour);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		} else {
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
		needToRedrawGrid = true;
	}
	
	private void createLine(String formula) throws ScriptException {

		formula = tidyAndCorrectFormula(formula);
		if (formula == null) {
//			System.out.println("Something wrong with the formula. Not drawing: " + formula);
			return;
		}
		formulaEntryTF.setText(formulaBoxStartingText);
		moveCaretToEndOfFormulaBox();

		ArrayList<Double> rawYValues = new ArrayList<Double>();
		calculateRawValues(formula, rawYValues);

		GraphData data = new GraphData("Y" + formula.toUpperCase().replace("*X", "X"),
				"Y" + formula.toUpperCase().replace("*X", "X"), rawYValues);

		// TODO This is flimsy ATM
		// Avoiding duplicates as a temporary solution to make sure the UI list
		// deletes the right one
		boolean add = true;
		for (GraphData existing : graphDataPoints) {
			if (existing.getFormula().equals(data.getFormula())) {
				add = false;
				break;
			}
		}

		if (add) {
			graphDataPoints.add(data);
			redrawCanvasFromCacheImpl();
		} else {
			displayErrorPopup("Formula already exists: Y" + formula.toUpperCase());
		}

	}

	private void redrawCanvasFromCacheImpl() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		listOfLinesVBox.getChildren().clear();
		paintBackground();

		if (getOverlayMultiple()) {
			for (GraphData data : graphDataPoints) {
				addLineToUiList(data);
				drawLineImpl(data);
			}
		} else {
			boolean drawn = false;
			for (GraphData data : graphDataPoints) {
				addLineToUiList(data);
				if (!drawn) {
					drawLineImpl(data);
					if (data.shouldBeDrawn) {
						drawn = true;
					}
				}
			}
		}

		if (graphDataPoints.size() == 0) {
			
			if (graphDataPoints.size() == 0) {
				drawAxes(1);
			}
			
			if (getDrawBackground()) {
				drawBackgroundGrid(1);
			}
			
			drawRightClickDataPoints();
		}

		moveCaretToEndOfFormulaBox();

	}

	private void addLineToUiList(GraphData data) {

		// TODO This would be better as its own FXML file
		// So it's less fiddly/clunky

		HBox outerHBox = new HBox();
		VBox.setMargin(outerHBox, new Insets(1, 1, 1, 1));
		listOfLinesVBox.getChildren().add(outerHBox);
		
//		outerHBox
		Tooltip.install(outerHBox, new Tooltip(listUiTooltip));
		
		// TODO Make this let the user de/select lines to show
//		CheckBox checkBox = new CheckBox();
//		outerHBox.getChildren().add(checkBox);

		Button deleteButton = new Button(removeRowString);
		BackgroundFill backgroundFill = new BackgroundFill(getNextColour(), new CornerRadii(3), Insets.EMPTY);
		deleteButton.setBackground(new Background(backgroundFill));
		deleteButton.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 3px;");
		outerHBox.getChildren().add(deleteButton);

		Label label = new Label(" " + data.getFormula());
		label.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(label, Priority.ALWAYS);
		outerHBox.getChildren().add(label);
		
		CheckBox checkBox = new CheckBox();
		Insets checkBoxInsets = new Insets(0,20,0,0);
		HBox.setMargin(checkBox, checkBoxInsets);
		checkBox.setSelected(data.getShouldBeDrawn());
		outerHBox.getChildren().add(checkBox);
		
		outerHBox.setOnMouseClicked(event -> {
			formulaEntryTF.setText(label.getText().trim());
			moveCaretToEndOfFormulaBox();
		});

		checkBox.setOnAction(event -> {
			// TODO This is not ideal - the relationship between model and view is very fragile 
			try {
				GraphData identifiedData = getGraphDataFromFormula(label.getText().trim());
				identifiedData.setShouldBeDrawn(checkBox.isSelected());
				redrawCanvasFromCacheImpl();
			} catch (Exception e) {
				e.printStackTrace();
				displayErrorPopup("This shouldn't happen - error setting data to not be drawn");
			}
		});
		
		deleteButton.setOnAction(event -> {
			boolean identified = false;
			for (int counter = 0; counter < graphDataPoints.size(); counter++) {
				if (graphDataPoints.get(counter).getFormula().trim().equalsIgnoreCase(label.getText().trim())) {
					graphDataPoints.remove(counter);
					identified = true;
					break;
				}
			}

			if (identified) {

				Parent parent = deleteButton.getParent();
				if (parent instanceof Pane) {
					((Pane) parent).getChildren().clear();
					Parent grandParent = parent.getParent();
					if (grandParent instanceof Pane) {
						((Pane) grandParent).getChildren().remove(parent);
					}
				}
				redrawCanvasFromCacheImpl();
				moveCaretToEndOfFormulaBox();
			}
		});

	}

	private GraphData getGraphDataFromFormula(String trim) {
		for(GraphData data : graphDataPoints) {
			if(data.getFormula().equalsIgnoreCase(trim)) {
				return data;
			}
		}
		return null;
	}

	private void drawLineImpl(GraphData data) {

		// Optionally: Clear the canvas if only one is to be drawn
		if (!getOverlayMultiple()) {
			paintBackground();
		}

		// Optionally: scale the y values to fit canvas
		double scale = getScalingFactor(data.getyValues());

		// Optionally: Draw background
		if (getDrawBackground()) {
			drawBackgroundGrid(scale);
		}

		// Optionally: Draw axes
		if (getDrawAxes()) {
			drawAxes(scale);
		}

		// Draw the line
		drawYValuesImpl(data, scale);
		
		// Draw any right-click values();
		drawRightClickDataPoints();

	}

	private void drawRightClickDataPoints() {

		gc.setStroke(rightClickPointsColour);
		
		double size = rightClickPointsCrossSize;

		for(GraphDataPoints point : rightClickDataPoints) {
			gc.strokeLine(point.getX() - size, point.getY() - size, point.getX() + size, point.getY() + size);
			gc.strokeLine(point.getX() - size, point.getY() + size, point.getX() + size, point.getY() - size);
		}
		
	}

	private void drawBackgroundGrid(double scale) {
		
		needToRedrawGrid = false;
		
		gc.setStroke(canvasBackgroundGridColour);

		// Draw Horizontal lines
		double startingYValue = panningOffsetY + (canvas.getHeight() / 2);
		while (startingYValue >= 0) {
			gc.strokeLine(0, startingYValue, canvas.getWidth(), startingYValue);
			startingYValue -= gridInterval;
		}
		startingYValue = panningOffsetY + (canvas.getHeight() / 2);
		while (startingYValue <= canvas.getHeight()) {
			gc.strokeLine(0, startingYValue, canvas.getWidth(), startingYValue);
			startingYValue += gridInterval;
		}
		
		// Draw Vertical Lines
		double startingXValue = panningOffsetX + (canvas.getWidth() / 2);
		while (startingXValue >= 0) {
			gc.strokeLine(startingXValue, 0, startingXValue, canvas.getHeight());
			startingXValue -= gridInterval;
		}
		startingXValue = panningOffsetX + (canvas.getWidth() / 2);
		while (startingXValue <= canvas.getWidth()) {
			gc.strokeLine(startingXValue, 0, startingXValue, canvas.getHeight());
			startingXValue += gridInterval;
		}
		
	}

	private double getScalingFactor(ArrayList<Double> rawYValues) {
		// TODO Consider - is this needed any more? Just returns 1
		return 1;
	}
	
	private boolean getAllowedToPan(MouseButton buttonType) {
		if(buttonType == MouseButton.PRIMARY) {
			return getAllowedToPan();
		}
		return false;
	}
	private boolean getAllowedToPan() {
		// TODO Not ideal, temporary fix;
		// crosses don't work properly if the user pans after placing first cross 
		if(rightClickDataPoints.size() > 0) {
			displayInfoPopup("Place the second cross (right-click) before panning");
			return false;
		}
		
		return !getLockPanningCheckBoxSelected();
				
	}
	
	private boolean getLockPanningCheckBoxSelected() {
		return lockPanningCheckBox.isSelected();
	}
	
	private boolean getFillBackgroundCheckBoxSelected() {
		return fillBackgroundCheckBox.isSelected();
	}

	private String tidyAndCorrectFormula(String formula) {
		formula = tidyFormulaString(formula);
		boolean correctFormula = checkFormulaIsCorrect(formula);
		if (!correctFormula) {
			return null;
		}

		// Preprocess; put a "*" before any X if preceded by a number
		StringBuilder sb = new StringBuilder();
		addAsterisksForMultiplesOfX(sb, formula);
		return sb.toString();
	}

	private void drawAxes(double scale) {

		gc.setStroke(canvasAxisColour);

		// Draw vertical line
		double xValue = panningOffsetX + (canvas.getWidth()/2) - (axesLineWidth/2); 
		gc.strokeLine(xValue, 0, xValue, canvas.getHeight());

		// Draw horizontal line
		// TODO Is this right?; needs to take scale into account?
		double yValue = panningOffsetY + canvas.getHeight() / 2;
		gc.strokeLine(0, yValue, canvas.getWidth(), yValue + axesLineWidth);

		// TODO draw little tick lines and numbers across the axes
	}

	private boolean getDrawAxes() {
		return drawAxesCheckBox.isSelected();
	}

	private boolean getDrawBackground() {
		return needToRedrawGrid && drawBackgroundCheckBox.isSelected();
	}

	private boolean getOverlayMultiple() {
		return overlayMultipleCheckBox.isSelected();
	}

	private String tidyFormulaString(String formula) {
		formula = formula.replace(" ", "");
		formula = formula.toLowerCase();
		formula = formula.trim();
		return formula;
	}

	private void drawYValuesImpl(GraphData data, double scale) {
		
		if(!data.getShouldBeDrawn()) {
			return;
		}

		Color colour = getNextColour();
		gc.setStroke(colour);

		for (int counter = 0; counter < data.getyValues().size(); counter++) {
			double xValue = counter;

			if (Double.isNaN(data.getyValues().get(counter))) {
				continue;
			}

			 // try-catch is here as the last point will fail
			double yStartValue = Math.round((canvas.getHeight()/2) - (data.getyValues().get(counter) * scale));
			try {
				double yEndValue = Math.round((canvas.getHeight()/2) - (data.getyValues().get(counter + 1) * scale));
				gc.strokeLine(xValue, yStartValue, xValue + 1, yEndValue);
			} catch (Exception e) {
				gc.strokeLine(xValue, yStartValue, xValue + 1, yStartValue);
			}
		}
	}

	private Color getNextColour() {
		return lineColours[listOfLinesVBox.getChildren().size() % lineColours.length];
	}

	private double findLargest(ArrayList<Double> yValues) {
		double largest = Double.MIN_VALUE;
		for (double value : yValues) {
			if (value > largest) {
				largest = value;
			}
		}
		return largest;
	}

	private double getLowestValue(ArrayList<Double> rawYValues) {
		double lowest = Double.MAX_VALUE;
		for (double value : rawYValues) {
			if (value < lowest) {
				lowest = value;
			}
		}
		return lowest;
	}

	private void calculateRawValues(String formula, ArrayList<Double> destinationArray) {
		destinationArray.clear();
		for (double xValue = 0 - (canvas.getWidth() / 2); xValue <= (canvas.getWidth() / 2); xValue += 1) {
			String formulaImpl = formula.replace("x", "(" + (int) (xValue - panningOffsetX) + ")");
			Expression expression = new ExpressionBuilder(formulaImpl.split("=")[1]).build();

//			System.out.println("FORMULA: " + formula + " || " + formulaImpl);
			if (formulaImpl.contains("/0")) { // To avoid divide by 0 exceptions 
				continue;
			}
			double result = expression.evaluate();
			result -= panningOffsetY;
			destinationArray.add(result);
//			System.out.println("(" + xValue + ", " + result + ")");
		}
	}

	private void addAsterisksForMultiplesOfX(StringBuilder sb, String formula) {
		for (int counter = 0; counter < formula.length(); counter++) {
			if (counter > 0) {
				char character = formula.charAt(counter);
				if (formula.charAt(counter) == 'x') {
					try {
						int maybe = Integer.parseInt("" + formula.charAt(counter - 1));
						sb.append("*");
					} catch (Exception e) {
					}
				}
				sb.append(character);
			}
		}
	}

	// Unit test this
	private boolean checkFormulaIsCorrect(String formula) {

		formula = formula.toLowerCase();

		if (formula.equals(formulaBoxStartingText)) {
			return false;
		}

		String[] formulaSplit = formula.split("=");

//		if (!formula.contains("x") || !formula.contains("y")) {
//			displayPopup("The formula must contain an X and Y value");
//			return false;
//		}

		if (!formula.contains("=")) {
			displayErrorPopup("The formula must contain an equals sign");
			return false;
		}
		
		

		if (formulaSplit[0].contains("x")) {
			displayErrorPopup("The lefthand side should not contain X");
			return false;
		}

		if (formulaSplit[1].contains("y")) {
			displayErrorPopup("The righthand side should not contain Y");
			return false;
		}

		if (formulaSplit.length != 2) {
			displayErrorPopup("The formula must contain exactly one equals sign");
			return false;
		}

//		if (!formulaSplit[0].contains("y") || !formulaSplit[1].contains("x")) {
//			displayPopup("The formula must have Y on the left and X on the right");
//			return false;
//		}

		if (!formulaSplit[0].contains("y")) {
			displayErrorPopup("The formula must have Y on the left");
			return false;
		}

		return true;
	}

	public static void displayErrorPopup(String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("There Was a Problem");
		alert.setContentText(text);
		alert.showAndWait();
	}
	
	public static void displayInfoPopup(String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText("Information");
		alert.setContentText(text);
		alert.showAndWait();
	}

	public void setCurveToCanvas() {
		String formula = formulaEntryTF.getText();
		
		boolean easterEggTriggered = false;
		try {
			easterEggTriggered = EasterEggMethods.displayMatchingPopup(formula);
			formulaEntryTF.setText(formulaBoxStartingText);
			moveCaretToEndOfFormulaBox();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!easterEggTriggered) {
			try {
				createLine(formula);
			} catch (ScriptException e) {
//				e.printStackTrace(); // Throw popup here?
			}
		}
	}

	public void addSqrtToTextField() {
		addSymbolToTfAndRefocus("sqrt()", -1);
	}

	public void addSquareToTextField() {
		addSymbolToTfAndRefocus("^", 0);
	}

	public void addSymbolToTfAndRefocus(String input, int endOffset) {
		String output = formulaEntryTF.getText();
		output += input;
		formulaEntryTF.setText(output);
		formulaEntryTF.requestFocus();
		formulaEntryTF.positionCaret(output.length() + endOffset);
	}

	public void increaseBackgroundGridInterval() {
		double newValue = gridInterval * gridIntervalChangeFactor; 
		if(newValue <= gridIntervalMaximumValue) {
			gridInterval = newValue;
		}
		redrawCanvasFromCacheImpl();
	}
	
	public void decreaseBackgroundGridInterval() {
		double newValue = gridInterval / gridIntervalChangeFactor; 
		if(newValue >= gridIntervalMinimumValue) {
			gridInterval = newValue;
		}
		redrawCanvasFromCacheImpl();
	}

	public void decreaseCanvasSize() { // Note: atm it assumes width and height are the same
		double newHeight = canvas.getHeight() - canvasSizeChangeInterval;
		double newWidth = canvas.getWidth() - canvasSizeChangeInterval;
		if(newHeight >= canvasMinimumSize) {
			resizeCanvas(newHeight, newWidth, -canvasSizeChangeInterval);
		}
		recalculateAndRedrawLinesFromCache();
	}
	

	public void increaseCanvasSize() { // Note: atm it assumes width and height are the same
		double newHeight = canvas.getHeight() + canvasSizeChangeInterval;
		double newWidth = canvas.getWidth() + canvasSizeChangeInterval;
		if(newHeight <= canvasMaximumSize) {
			resizeCanvas(newHeight, newWidth, canvasSizeChangeInterval);
		}
		recalculateAndRedrawLinesFromCache();
	}

	private void recalculateAndRedrawLinesFromCache() {
		graphDataPoints.forEach(data -> recalculateYValues(data));
		redrawCanvasFromCacheImpl();
	}
	
	private void resizeCanvas(double newHeight, double newWidth, int canvasSizeChangeInterval) {
		canvas.setHeight(newHeight);
		stage.setHeight(stage.getHeight() + canvasSizeChangeInterval);
		canvas.setWidth(newWidth);
		stage.setWidth(stage.getWidth() + canvasSizeChangeInterval);
	}
	
	private void recalculateYValues(GraphData data) {
		calculateRawValues(data.getFormula().toLowerCase(), data.getyValues());
	}

	public Stage getStage() {

		if(stage == null) {
			stage = new Stage();
			stage.setTitle(appTitle);
			Image icon = new Image(getClass().getResourceAsStream("/" + appIconFileName)); // Adjust path if needed
			stage.getIcons().add(icon);
			stage.setResizable(false);
			Scene scene = new Scene(getMain());
			stage.setScene(scene);
		}
		return stage;
	}

	private void setSmallButtonProperties() {
		
		Insets insets = new Insets(0, smallButtonMargin, 0, 0);
		
		specialCharsButtonHBox.getChildren().forEach(button -> HBox.setMargin(button, insets));
		
		HBox.setMargin(increaseIntervalButton, insets);
		HBox.setMargin(decreaseIntervalButton, insets);

		HBox.setMargin(increasecanvasSizeButton, insets);
		HBox.setMargin(decreasecanvasSizeButton, insets);		
		
		// TODO Finish implementing zoom then re-enable this
//		HBox.setMargin(zoomInButton, insets);
//		HBox.setMargin(zoomOutButton, insets);		
	}

	private void setEventHandlers() {
		
		formulaEntryTF.setOnAction(event -> {
			setCurveToCanvas();
		});
		
		fillBackgroundCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				redrawCanvasFromCacheImpl();
			}
		});
		drawBackgroundCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				redrawCanvasFromCacheImpl();
			}
		});
		drawAxesCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				redrawCanvasFromCacheImpl();
			}
		});
		overlayMultipleCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				redrawCanvasFromCacheImpl();
			}
		});
		setAlwaysOnTopCheckBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				stage.setAlwaysOnTop(getPinnedToFront());
			}
			
		});
	}

	public void panLeft() {
		if(!getAllowedToPan()) {
			return;
		}
		panningOffsetX += panningXInterval;
		recalculateAndRedrawLinesFromCache();
	}
	
	public void panRight() {
		if(!getAllowedToPan()) {
			return;
		}
		panningOffsetX -= panningXInterval;
		recalculateAndRedrawLinesFromCache();
	}
	
	public void panUp() {
		if(!getAllowedToPan()) {
			return;
		}
		panningOffsetY += panningYInterval;
		recalculateAndRedrawLinesFromCache();
	}
	
	public void panDown() {
		if(!getAllowedToPan()) {
			return;
		}
		panningOffsetY -= panningYInterval;
		recalculateAndRedrawLinesFromCache();
	}
	
	public void panCentre() {
		if(!getAllowedToPan()) {
			return;
		}
		panningOffsetY = initialPanningOffsetY;
		panningOffsetX = initialPanningOffsetX;
		recalculateAndRedrawLinesFromCache();
	}

	public void zoomIn() {
//		System.out.println("ZOOM IN");
		zoomFactor += zoomInterval;
		// TODO Actually do the zoom here
		recalculateAndRedrawLinesFromCache();
	}
	
	public void zoomOut() {
//		System.out.println("ZOOM OUT");
		zoomFactor -= zoomInterval;
		// TODO Actually do the zoom here
		recalculateAndRedrawLinesFromCache();
	}

	public void saveSnapshot() {
		    WritableImage image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
		    canvas.snapshot(null, image);
		    FileChooser fileChooser = new FileChooser();
		    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
		    File file = fileChooser.showSaveDialog(stage);
		    try {
		        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		        displayInfoPopup("Image saved to:\n" + file.getAbsolutePath());
		    } catch (IOException e) {
		        e.printStackTrace();
		        displayErrorPopup("Something went wrong saving the image to:\n" + file.getAbsolutePath());
		    }
		}

	private boolean getPinnedToFront() {
		return setAlwaysOnTopCheckBox.isSelected();
	}

	private void setSmallMarginToInternalComponents(TitledPane pane) {
		Insets insets = new Insets(4,0,0,0);
		if(pane.getContent() instanceof VBox) {
			((VBox)pane.getContent()).getChildren().forEach(x -> VBox.setMargin(x, insets));
		}
		if(pane.getContent() instanceof HBox) {
			((HBox)pane.getContent()).getChildren().forEach(x -> HBox.setMargin(x, insets));
		}
	}

	public void openAllTitledPanes() {
		for (Node node : leftPanelVBox.getChildren()) {
			if (node instanceof TitledPane) {
				((TitledPane) node).setExpanded(true);
			}
		}
	}
	
	public void closeAllTitledPanes() {
		for (Node node : leftPanelVBox.getChildren()) {
			if (node instanceof TitledPane) {
				((TitledPane) node).setExpanded(false);
			}
		}
	}

	public void openHelpPopup() {
		
		
//		Stage helpStage = new Stage();
//		helpStage.setAlwaysOnTop(true);
//		VBox helpMain = new VBox();
//		Scene helpScene = new Scene(helpMain);
//		helpStage.setScene(helpScene);
//		
//		helpMain.getChildren().add(new Label(helpString_Title));
//		helpMain.getChildren().add(new Separator());
//
//		Button closeButton = new Button("Close");
//		helpMain.getChildren().add(closeButton);
//		
//		closeButton.setOnAction(new EventHandler<ActionEvent>() {
//			
//			@Override
//			public void handle(ActionEvent event) {
//				helpStage.close();
//			}
//		});
//		
//		helpStage.showAndWait();
		
		paintBackground();
		drawHelpBorder();
//		drawBackgroundGrid(1);
		
        gc.setFill(canvasHelpTextColour);
        gc.setFont(Font.font("Georgia", 20));

        int x = 100;
        int startingY = 100;
        int y = startingY;
        int increment = 60;
        
        for(String string : getHelpStrings()) {
        	if (string.contains("-")) {
        		y -= 20;
        	}
        	gc.fillText(string, x, y);
			if (string.contains("\n")) {
				y += 10;
			}
        	y += increment;
        }
		
		
	}

	private void drawHelpBorder() {

		
		// Draw background
        int tileSize = 20; // Size of individual squares
        double width = canvas.getWidth();
		double height = canvas.getHeight();

        for (int row = 0; row < height / tileSize; row++) {
            for (int col = 0; col < width / tileSize; col++) {
                // Create diagonal pattern logic
                if ((row + col) % 3 == 0) {
                    gc.setFill(canvasHelpBackgroundColour1);
                } else if ((row + col) % 3 == 1) {
                    gc.setFill(canvasHelpBackgroundColour2);
                } else {
                    gc.setFill(canvasHelpBackgroundColour3);
                }
                // Draw square tile
                gc.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }


		// Draw border
		width = 20;
		height = 20;
		gc.setFill(canvasHelpBorderColour1);
		gc.fillRect(0, 0, width, canvas.getHeight());
		gc.setFill(canvasHelpBorderColour2);
		gc.fillRect(0, 0, canvas.getWidth(), height);
		gc.setFill(canvasHelpBorderColour3);
		gc.fillRect(0, canvas.getHeight()-height, canvas.getWidth(), height);
		gc.setFill(canvasHelpBorderColour4);
		gc.fillRect(canvas.getWidth()-width, 0, width, canvas.getHeight()+1);
		
		// Draw outer border
		gc.setFill(canvasHelpBorderAccentColour1);
		width = 1;
		height = 1;
		gc.fillRect(0, 0, width, canvas.getHeight());
		gc.fillRect(0, 0, canvas.getWidth(), height);
		gc.fillRect(0, canvas.getHeight()-height, canvas.getWidth(), height);
		gc.fillRect(canvas.getWidth()-width, 0, width, canvas.getHeight()+1);
		
        // Set fill color for lines
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);

	}

	private ArrayList<String> getHelpStrings() {
		return new ArrayList<String>(Arrays.asList(//
			"This tool will let you draw curves onto the canvas.", 
			"Try hovering over any of the dropdowns, buttons, and checkboxes\n to get a tooltip explaining them.",
			"Other things you can do:",
			"    - Click and drag to pan around the grid.",
			"    - Right click on 2 points to add a line between them.",
			"Double click the canvas to close help"
			));
	}
	
}
