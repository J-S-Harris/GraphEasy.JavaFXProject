package mainClasses;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
	

	int canvasSizeChangeInterval = 100;
	int canvasMinimumSize = 400;
	int canvasMaximumSize = 1400;
	
	double initialZoomFactor = 1;
	double zoomFactor = 1;
	double zoomInterval = 0.5;
	
	double mouseX = 0;
	double mouseY = 0;
	
	Color canvasBackgroundColour = Color.BEIGE;
	Color canvasAxisColour = Color.BLACK;
	Color canvasBackgroundGridColour = new Color(0.5, 0.5, 0.5, 0.5); // Grey
	Color[] lineColours = { Color.LIGHTGREEN, Color.ORANGE, Color.BLUEVIOLET, Color.CHARTREUSE, //
			Color.SALMON, Color.SANDYBROWN, Color.RED, Color.AQUA };

	int canvasLineWidth = 2;
	double backgroundLineWidth = 0.5;
	double axesLineWidth = 2;
	
	double gridInterval = 50;
	double gridIntervalMinimumValue = 25;
	double gridIntervalMaximumValue = 800;
	int gridIntervalChangeFactor = 2;
	
	int smallButtonMargin = 10;
	
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
		snapshotButton.setMaxWidth(Double.MAX_VALUE);

	}

	private void setMarginsToLeftPanelNodes() {
		Insets defaultInsets = new Insets(6, 0, 6, 6);
		Insets titledPaneInsets = new Insets(4, 0, 0, 0);
		
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
			if(!getAllowedToPan()) {
				return;
			}
			panningOffsetX += (event.getX() - mouseX);
			mouseX = event.getX();
			panningOffsetY += (event.getY() - mouseY);
			mouseY = event.getY();
			recalculateAndRedrawLinesFromCache();
		});
		canvas.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				panCentre();
			}
		});
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
	}

	private void moveCaretToEndOfFormulaBox() {
		formulaEntryTF.requestFocus();
		formulaEntryTF.positionCaret(formulaEntryTF.getLength());
		formulaEntryTF.deselect();
	}

	private void createDefaultCanvas() {
		canvas = new Canvas();
		canvas.setHeight(AppClass.defaultStageHeight);
		canvas.setWidth(AppClass.defaultStageWidth);

		int canvasMargin = 2;
		HBox.setMargin(canvasBox, new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
		canvasBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
		canvasBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
		canvasBox.setStyle("-fx-border-color: black; -fx-border-width: 3px;");

		gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(canvasLineWidth);
		getCanvasBox().getChildren().add(canvas);
		paintBackground();
		drawAxes(1);
		drawBackgroundGrid(1);
	}

	private void paintBackground() {
		if(getFillBackgroundCheckBoxSelected()) {
			gc.setFill(canvasBackgroundColour);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		} else {
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
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
			redrawLinesOnGraphFromCache();
		} else {
			displayErrorPopup("Formula already exists: Y" + formula.toUpperCase());
		}

	}

	private void redrawLinesOnGraphFromCache() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		listOfLinesVBox.getChildren().clear();
		paintBackground();

		if (getOverlayMultiple()) {
			for (GraphData data : graphDataPoints) {
				addLineToUiList(data);
				drawLineImpl(data.yValues);
			}
		} else {
			boolean drawn = false;
			for (GraphData data : graphDataPoints) {
				addLineToUiList(data);
				if (!drawn) {
					drawLineImpl(data.yValues);
					drawn = true;
				}
			}
		}

		if (graphDataPoints.size() == 0 && getDrawAxes()) {
			drawAxes(1);
		}

		if (graphDataPoints.size() == 0 && getDrawBackground()) {
			drawBackgroundGrid(1);
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
		
		outerHBox.setOnMouseClicked(event -> {
			formulaEntryTF.setText(label.getText().trim());
			moveCaretToEndOfFormulaBox();
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
				redrawLinesOnGraphFromCache();
				moveCaretToEndOfFormulaBox();
			}
		});

	}

	private void drawLineImpl(ArrayList<Double> rawYValues) {

		// Optionally: Clear the canvas if only one is to be drawn
		if (!getOverlayMultiple()) {
			paintBackground();
		}

		// Optionally: scale the y values to fit canvas
		double scale = getScalingFactor(rawYValues);

		// Optionally: Draw background
		if (getDrawBackground()) {
			drawBackgroundGrid(scale);
		}

		// Optionally: Draw axes
		if (getDrawAxes()) {
			drawAxes(scale);
		}

		// Draw the line
		drawYValues(rawYValues, scale);

	}

	private void drawBackgroundGrid(double scale) {
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
	
	private boolean getAllowedToPan() {
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
		return drawBackgroundCheckBox.isSelected();
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

	private void drawYValues(ArrayList<Double> yValues, double scale) {

		Color colour = getNextColour();
		gc.setStroke(colour);

		for (int counter = 0; counter < yValues.size(); counter++) {
			double xValue = counter;

			if (Double.isNaN(yValues.get(counter))) {
				continue;
			}

			 // try-catch is here as the last point will fail
			double yStartValue = Math.round((canvas.getHeight()/2) - (yValues.get(counter) * scale));
			try {
				double yEndValue = Math.round((canvas.getHeight()/2) - (yValues.get(counter + 1) * scale));
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
		redrawLinesOnGraphFromCache();
	}
	
	public void decreaseBackgroundGridInterval() {
		double newValue = gridInterval / gridIntervalChangeFactor; 
		if(newValue >= gridIntervalMinimumValue) {
			gridInterval = newValue;
		}
		redrawLinesOnGraphFromCache();
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
		redrawLinesOnGraphFromCache();
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
				redrawLinesOnGraphFromCache();
			}
		});
		drawBackgroundCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				redrawLinesOnGraphFromCache();
			}
		});
		drawAxesCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				redrawLinesOnGraphFromCache();
			}
		});
		overlayMultipleCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				redrawLinesOnGraphFromCache();
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

}
