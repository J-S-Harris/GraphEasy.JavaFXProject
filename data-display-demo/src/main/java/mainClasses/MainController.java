package mainClasses;

import java.util.ArrayList;

import javax.script.ScriptException;

import easterEggs.EasterEggMethods;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainController {

	// TODO Possible next TODOs
	// correct scaling
		// Then make sure it's displaying as it should, with the right coordinates, etc
	// Why is the line dashed when there is a big gap between points?
	// Make the GraphData remember its colour, so that deleting earlier lines
		// doesn't change the colour
	// Make the TextField a typable dropdown that lets the user select previously
		// added formulas
	// Give user checkbox to de/select from list. Only display selected
	// Allow user to import/export data
	// Unit tests

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
	CheckBox scaleCurvesCheckBox;
	@FXML
	Button confirmButton;
	@FXML
	Button squareButton;
	@FXML
	Button sqrtButton;
	@FXML
	Label drawBackgroundLabel;
	@FXML
	Label drawAxesLabel;
	@FXML
	Label overlayMultipleLabel;
	@FXML
	Label scaleCurvesLabel;
	@FXML
	Label intervalSizeLabel, canvasSizeLabel;
	@FXML
	Button decreaseIntervalButton, increaseIntervalButton;
	@FXML
	Button decreasecanvasSizeButton, increasecanvasSizeButton;

	int canvasSizeChangeInterval = 100;
	int canvasMinimumSize = 400;
	int canvasMaximumSize = 1400;
	
	Color canvasBackGroundColour = Color.BEIGE;
	Color canvasAxisColour = Color.BLACK;
	Color canvasBackgroundColour = new Color(0.5, 0.5, 0.5, 0.5); // Grey
	Color[] lineColours = { Color.LIGHTGREEN, Color.ORANGE, Color.BLUEVIOLET, Color.CHARTREUSE, //
			Color.SALMON, Color.SANDYBROWN, Color.RED, Color.AQUA };

	int canvasLineWidth = 2;
	double backgroundLineWidth = 0.5;

	double gridInterval = 50;
	double gridIntervalMinimumValue = 25;
	double gridIntervalMaximumValue = 800;
	int gridIntervalChangeFactor = 2;
	
	int smallButtonMargin = 5;
	
	final static String fxmlFileName = "/mainFXML.fxml";

	final String removeRowString = "x";
	final String formulaBoxStartingText = "y=";

	final String pressEnterOnFormulaTooltip = "Type in a formula, then press enter or press \"Go!\"";
	final String goButtonTooltip = "Add the curve to the graph";
	final String listUiTooltip = "Click to add formula to text box";
	
	final String squareTooltip = "Square a number";
	final String squareRootTooltip = "Calculate the square root of a number";
	final String drawBackgroundTooltip = "Draw the background grid";
	final String drawAxesTooltip = "Draw axes behind the drawn curves";
	final String overlayMultipleTooltip = "Allow multiple curves to be drawn";
	final String scaleCurvesTooltip = "Scale all curves to fit the screen";
	
	public HBox getMain() {
		return main;
	}

	public VBox getCanvasBox() {
		return canvasBox;
	}

	public void prepareUI() {
		createDefaultCanvas();
		getCanvasBox().getChildren().add(canvas);
		overlayMultipleCheckBox.setSelected(true);
		drawAxes(1);
		drawBackgroundGrid(1);
		addTooltips();
		setCheckBoxEventHandlers();
		setSmallButtonProperties();

		// non-specific vvv
		
//		listOfLineScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		leftPanelScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		leftPanelScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		
		leftPanelScrollPane.hvalueProperty().addListener((obs, oldVal, newVal) -> {
			leftPanelScrollPane.setHvalue(0); // Always scroll left
		});
		
//		VBox.setVgrow(listOfLineScrollPane, Priority.ALWAYS);

		formulaEntryTF.setText(formulaBoxStartingText);
		Platform.runLater(() -> {
			moveCaretToEndOfFormulaBox();
		});

		HBox.setHgrow(confirmButton, Priority.ALWAYS);
		confirmButton.setMaxWidth(Double.MAX_VALUE); // Ensures full width

		int margin = 7;
		
		leftPanelVBox.getChildren().forEach(x -> VBox.setMargin(x, new Insets(margin, 0, margin, margin)));

		formulaEntryTF.setOnAction(event -> {
			setCurveToCanvas();
		});

		drawBackgroundCheckBox.setSelected(true);
		drawAxesCheckBox.setSelected(true);

	}

	private void addTooltips() {

		Tooltip tooltip;
		
		tooltip = new Tooltip(pressEnterOnFormulaTooltip);
		Tooltip.install(formulaEntryTF, tooltip);
		
		tooltip = new Tooltip(goButtonTooltip);
		Tooltip.install(confirmButton, tooltip);
		
		tooltip = new Tooltip(squareTooltip);
		Tooltip.install(squareButton, tooltip);
		
		tooltip = new Tooltip(squareRootTooltip);
		Tooltip.install(sqrtButton, tooltip);
		
		tooltip = new Tooltip(drawBackgroundTooltip);
		Tooltip.install(drawBackgroundLabel, tooltip);
		
		tooltip = new Tooltip(drawAxesTooltip);
		Tooltip.install(drawAxesLabel, tooltip);
		
		tooltip = new Tooltip(overlayMultipleTooltip);
		Tooltip.install(overlayMultipleLabel, tooltip);
		
		tooltip = new Tooltip(scaleCurvesTooltip);
		Tooltip.install(scaleCurvesLabel, tooltip);
		
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

		int canvasMargin = 5;
		HBox.setMargin(canvasBox, new Insets(canvasMargin, canvasMargin, canvasMargin, canvasMargin));
		canvasBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
		canvasBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
		canvasBox.setStyle("-fx-border-color: black; -fx-border-width: 3px;");

		gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(canvasLineWidth);
		paintBackground();
	}

	private void paintBackground() {
		gc.setFill(canvasBackGroundColour);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
		getRawValues(formula, rawYValues);

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
			displayPopup("Formula already exists: Y" + formula.toUpperCase());
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
		Tooltip tooltip = new Tooltip(listUiTooltip);
		Tooltip.install(outerHBox, tooltip);
		
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
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
		gc.setStroke(canvasBackgroundColour);

		// Draw Y lines
		double startingYValue = canvas.getHeight() / 2;
		while (startingYValue >= 0) {
			gc.strokeLine(0, startingYValue, canvas.getWidth(), startingYValue);
			startingYValue -= gridInterval;
		}
		startingYValue = canvas.getHeight() / 2;
		while (startingYValue <= canvas.getHeight()) {
			gc.strokeLine(0, startingYValue, canvas.getWidth(), startingYValue);
			startingYValue += gridInterval;
		}
		
		// Draw X Lines
		double startingXValue = canvas.getWidth() / 2;
		while (startingXValue >= 0) {
			gc.strokeLine(startingXValue, 0, startingXValue, canvas.getHeight());
			startingXValue -= gridInterval;
		}
		startingXValue = canvas.getWidth() / 2;
		while (startingXValue <= canvas.getWidth()) {
			gc.strokeLine(startingXValue, 0, startingXValue, canvas.getHeight());
			startingXValue += gridInterval;
		}
		
	}

	private double getScalingFactor(ArrayList<Double> rawYValues) {
		// TODO Consider; this squashes each line individually
		// Would it be preferable to return a scaling factor for ALL lines/data points?
		if (getScaleCurves()) {
			double largest = findLargest(rawYValues);
			return canvas.getHeight() / largest;
		}
		return 1;
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
		double lineThickness = 2;

		// Draw vertical line
		gc.strokeLine(canvas.getWidth() / 2 - lineThickness / 2, 0, canvas.getWidth() / 2 + lineThickness / 2,
				canvas.getHeight());

		// Draw horizontal line
		// TODO This isn't right; needs to take scale into account
		double yValue = canvas.getHeight() / 2;
		gc.strokeLine(0, yValue, canvas.getWidth(), yValue + lineThickness);

		// TODO draw little tick lines and numbers across the axes
		// TODO Make the scaling OPTIONAL. i.e so the user can just see the
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

		// TODO Why does y=x^2 etc give a series of dotted lines?
		// Due to difference between values?
		// Draw the line segments with smaller steps?

		for (int counter = 0; counter < yValues.size(); counter++) {
			double xValue = counter;

			if (Double.isNaN(yValues.get(counter))) {
				continue;
			}

			 // try-catch is here as the last point will fail
			double yStartValue = Math.round(canvas.getHeight() / 2 - (yValues.get(counter) * scale));
			try {
				double yEndValue = Math.round(canvas.getHeight() / 2 - (yValues.get(counter + 1) * scale));
				gc.strokeLine(xValue, yStartValue, xValue + 1, yEndValue);
//			System.out.println("DRAWN: " + xValue + " " + yValue);
			} catch (Exception e) {
				gc.strokeLine(xValue, yStartValue, xValue + 1, yStartValue+1);
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

	private void getRawValues(String formula, ArrayList<Double> rawYValues) {
		for (double xValue = (0 - (canvas.getWidth() / 2)); xValue <= canvas.getWidth() / 2; xValue += 1) {
			String formulaImpl = formula.replace("x", "(" + (int) xValue + ")");
			Expression expression = new ExpressionBuilder(formulaImpl.split("=")[1]).build();

//			System.out.println("FORMULA: " + formula + " || " + formulaImpl);
			if (formulaImpl.contains("/0")) { // Could this instead just return the highest possible value?
				continue;
			}
			double result = expression.evaluate();
			rawYValues.add(result);
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
			displayPopup("The formula must contain an equals sign");
			return false;
		}

		if (formulaSplit[0].contains("x")) {
			displayPopup("The lefthand side should not contain X");
			return false;
		}

		if (formulaSplit[1].contains("y")) {
			displayPopup("The righthand side should not contain Y");
			return false;
		}

		if (formulaSplit.length != 2) {
			displayPopup("The formula must contain exactly one equals sign");
			return false;
		}

//		if (!formulaSplit[0].contains("y") || !formulaSplit[1].contains("x")) {
//			displayPopup("The formula must have Y on the left and X on the right");
//			return false;
//		}

		if (!formulaSplit[0].contains("y")) {
			displayPopup("The formula must have Y on the left");
			return false;
		}

		return true;
	}

	public static void displayPopup(String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("There Was a Problem");
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
				e.printStackTrace(); // Throw popup here?
			}
		}
	}

	public boolean getScaleCurves() {
		return scaleCurvesCheckBox.isSelected();
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
		double newSize = canvas.getHeight() - canvasSizeChangeInterval;
		if(newSize >= canvasMinimumSize) {
			resizeCanvas(newSize, -canvasSizeChangeInterval);
		}
		recalculateAndRedrawLinesFromCache();
	}
	

	public void increaseCanvasSize() { // Note: atm it assumes width and height are the same
		double newSize = canvas.getHeight() + canvasSizeChangeInterval;
		if(newSize <= canvasMaximumSize) {
			resizeCanvas(newSize, canvasSizeChangeInterval);
		}
		recalculateAndRedrawLinesFromCache();
	}

	private void recalculateAndRedrawLinesFromCache() {
		graphDataPoints.forEach(data -> recalculateYValues(data));
		redrawLinesOnGraphFromCache();
	}
	
	private void resizeCanvas(double newCanvasSize, double newStageSize) {
		canvas.setHeight(newCanvasSize);
		canvas.setWidth(newCanvasSize);
		stage.setHeight(stage.getHeight() + newStageSize);
		stage.setWidth(stage.getWidth() + newStageSize);
	}
	
	private void recalculateYValues(GraphData data) {
		data.getyValues().clear();
		getRawValues(data.getFormula().toLowerCase(), data.getyValues());
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
		intervalSizeLabel.setMaxWidth(Double.MAX_VALUE);
		intervalSizeLabel.setMaxHeight(Double.MAX_VALUE);
		HBox.setMargin(increaseIntervalButton, new Insets(smallButtonMargin, smallButtonMargin, smallButtonMargin, smallButtonMargin));
		HBox.setMargin(decreaseIntervalButton, new Insets(smallButtonMargin, smallButtonMargin, smallButtonMargin, smallButtonMargin));
		
		canvasSizeLabel.setMaxWidth(Double.MAX_VALUE);
		canvasSizeLabel.setMaxHeight(Double.MAX_VALUE);
		HBox.setMargin(increasecanvasSizeButton, new Insets(smallButtonMargin, smallButtonMargin, smallButtonMargin, smallButtonMargin));
		HBox.setMargin(decreasecanvasSizeButton, new Insets(smallButtonMargin, smallButtonMargin, smallButtonMargin, smallButtonMargin));		
	}

	private void setCheckBoxEventHandlers() {
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
		scaleCurvesCheckBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				redrawLinesOnGraphFromCache();
			}
		});
	}
	
}
