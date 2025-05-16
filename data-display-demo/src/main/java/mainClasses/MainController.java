package mainClasses;

import java.util.ArrayList;

import javax.script.ScriptException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainController {

	// TODO Possible next TODOs
	// y=x^2 doesn't produce a parabola; squared negatives stay negative
		// y=(x)^2 DOES display as expected, but not y=x^2? Why?
			// Possible solution: When assessing formula, consider replacing "X" with "(X)"
			// Then undoing it when the GraphData is created?
	// drawBackground() should draw a thin grid in the background, with ticks and correct scaling
	// Why is the line dashed when there is a big gap between points?
	// Make the TextField a typable dropdown that lets the user select previously added formulas
	// Give user checkbox to de/select from list. Only display selected
	// Allow user to import/export data
	// Unit tests

	ArrayList<GraphData> graphDataPoints = new ArrayList<>();

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
	ScrollPane listOfLineScrollPane;
	@FXML
	CheckBox scaleCurvesCheckBox;
	@FXML
	Button confirmButton;

	Color canvasBackGroundColour = Color.BEIGE;
	Color canvasAxisColour = Color.BLACK;
	Color[] lineColours = { Color.LIGHTGREEN, Color.ORANGE, Color.BLUEVIOLET, Color.CHARTREUSE,//
			Color.PINK, Color.SANDYBROWN, Color.RED, Color.AQUA };

	int canvasLineWidth = 2;
	double topBarOpacity = 0.4;

	final static String fxmlFileName = "/mainFXML.fxml";

	final String removeRowString = "x";
	final String formulaBoxStartingText = "y=";

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
		
		VBox.setVgrow(listOfLineScrollPane, Priority.ALWAYS);
		
		formulaEntryTF.setText(formulaBoxStartingText);
		Platform.runLater(() -> {
			moveCaretToEndOfFormulaBox();
		});

		HBox.setHgrow(confirmButton, Priority.ALWAYS);
		confirmButton.setMaxWidth(Double.MAX_VALUE); // Ensures full width

		int margin = 10;
		leftPanelVBox.getChildren().forEach(x -> VBox.setMargin(x, new Insets(margin, margin, margin, margin)));

		formulaEntryTF.setOnAction(event -> {
			setCurveToCanvas();
		});

		drawBackgroundCheckBox.setSelected(true);
		drawAxesCheckBox.setSelected(true);

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
//		overlayMultipleCheckBox
//		scaleCurvesCheckBox

	}

	private void moveCaretToEndOfFormulaBox() {
		formulaEntryTF.requestFocus();
		formulaEntryTF.positionCaret(formulaEntryTF.getLength());
		formulaEntryTF.deselect();
	}

	private void createDefaultCanvas() {
		canvas = new Canvas();
		canvas.setHeight(AppClass.minimumStageHeight);
		canvas.setWidth(AppClass.minimumStageWidth);

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
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Covers the whole area
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

		GraphData data = new GraphData("Y" + formula.toUpperCase().replace("*X", "X"), "Y" + formula.toUpperCase().replace("*X", "X"), rawYValues);

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

		moveCaretToEndOfFormulaBox();
		
	}

	private void addLineToUiList(GraphData data) {

		// TODO This would be better as its own FXML file
		// So it's less fiddly/clunky

		HBox outerHBox = new HBox();
		VBox.setMargin(outerHBox, new Insets(1, 1, 1, 1));
		listOfLinesVBox.getChildren().add(outerHBox);

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
		if(getDrawBackground()) {
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
		// TODO Auto-generated method stub
		// TODO vvv
			// TODO Also: add tickmarks and numbers
		System.out.println("TODO: Draw a thin grid in the background that scales with the scale");
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

			if(Double.isNaN(yValues.get(counter))) {
				continue;
			}
			
			double yValue = Math.round(canvas.getHeight() / 2 - (yValues.get(counter) * scale));
			gc.strokeLine(xValue, yValue, xValue + 1, yValue + 1);
//			System.out.println("DRAWN: " + xValue + " " + yValue);
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
			String formulaImpl = formula.replace("x", "" + (int) xValue);
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
		
		if(formula.equals(formulaBoxStartingText)) {
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
		try {
			createLine(formula);
		} catch (ScriptException e) {
			e.printStackTrace(); // Throw popup here?
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

}
