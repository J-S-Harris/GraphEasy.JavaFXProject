package mainClasses;

import java.util.ArrayList;

import javax.script.ScriptException;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainController {

	Canvas canvas;
	GraphicsContext gc;

	@FXML HBox main;
	@FXML VBox canvasBox;
	@FXML TextField formulaEntryTF;
	@FXML VBox topButtonsRow;
	@FXML CheckBox overlayMultipleCheckBox;
	@FXML CheckBox drawAxesCheckBox;
	@FXML VBox leftPanelVBox;
	@FXML CheckBox scaleCurvesCheckBox;

	// TODO Make it dynamically fill Stage
//	double canvasWidth = 500;
//	double canvasHeight = 400;
	Color canvasBackGroundColour = Color.LIGHTBLUE;
	Color canvasStrokeColour = Color.GREEN;
	Color canvasAxisColour = Color.BLACK;
	int canvasLineWidth = 2;
	double topBarOpacity = 0.4;
	
	static String fxmlFileName = "/mainFXML.fxml";

	public HBox getMain() {
		return main;
	}

	public VBox getCanvasBox() {
		return canvasBox;
	}

	public void prepareUI() {
		createDefaultCanvas();
		getCanvasBox().getChildren().add(canvas);
		
		drawAxes(1);
		
		int margin = 10;
		leftPanelVBox.getChildren().forEach(x -> VBox.setMargin(x, new Insets(margin, margin, margin, margin)));
		
		formulaEntryTF.setOnAction(event -> {
		    setCurveToCanvas();
		});

		drawAxesCheckBox.setSelected(true);
		
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
		gc.setFill(canvasBackGroundColour);
		gc.setStroke(canvasStrokeColour);
		gc.setLineWidth(canvasLineWidth);
	}

	private void createLine(String formula) throws ScriptException {
		
		formula = tidyAndCorrectFormula(formula);
		if(formula == null) {
			System.out.println("Something wrong with the formula. Not drawing: " + formula);
			return;
		}

		// Get raw values, i.e values before scaling/translation etc
		ArrayList<Double> rawYValues = new ArrayList<Double>();
		getRawValues(formula, rawYValues);

		
		double lowest = getLowestValue(rawYValues);
		ArrayList<Double> yValues = new ArrayList<Double>();
		for (int counter = 0; counter < rawYValues.size(); counter++) {
//			yValues.add(rawYValues.get(counter) - lowest);
			yValues.add(rawYValues.get(counter) - 0);
		}

		// Optionally: Clear the canvas if only one is to be drawn
		if(!getOverlayMultiple()) {
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
		
		// Optionally: scale the y values to fit canvas
		double scale = 1;
		if(getScaleCurves()) {
			double largest = findLargest(yValues);
			scale = canvas.getHeight() / largest;
		}
		
		// Optionally: Draw axes
		if(getDrawAxes()) {
			drawAxes(scale);
		}

		// Draw the line
		drawYValues(yValues, scale);

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
		gc.strokeLine(canvas.getWidth()/2 - lineThickness/2, 0, canvas.getWidth()/2 + lineThickness/2, canvas.getHeight());
		
		
		// Draw horizontal line
		// TODO This isn't right; needs to take scale into account
		double yValue = canvas.getHeight() / 2;
		gc.strokeLine(0, yValue, canvas.getWidth(), yValue + lineThickness);
		
		// TODO draw little tick lines and numbers across the axes
		// TODO Stop scaling? i.e so things can actually be placed relative to axes
		// TODO Make the scaling OPTIONAL. i.e so the user can just see the 
	}

	private boolean getDrawAxes() {
		return drawAxesCheckBox.isSelected();
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
		
		gc.setStroke(canvasStrokeColour);
		
		// TODO Why does y=x^2 etc give a series of dotted lines?
		// Due to difference between values?
		// Draw the line segments with smaller steps?
		
		for (int counter = 0; counter < yValues.size(); counter++) {
			double xValue = counter;
			double yValue = Math.round(canvas.getHeight()/2 - (yValues.get(counter) * scale));
			gc.strokeLine(xValue, yValue, xValue + 1, yValue + 1);
			System.out.println("DRAWN: " + xValue + " " + yValue);
		}
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
		double lowest =  Double.MAX_VALUE;
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

			System.out.println("FORMULA: " + formula + " || " + formulaImpl);
			if(formulaImpl.contains("/0")) {
				continue;
			}
			double result = expression.evaluate();
			rawYValues.add(result);
			System.out.println("(" + xValue + ", " + result + ")");
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

		if (!formula.contains("x") || !formula.contains("y")) {
			displayPopup("The formula must contain an X and Y value");
			return false;
		}

		if (!formula.contains("=")) {
			displayPopup("The formula must contain an equals sign");
		}

		String[] formulaSplit = formula.split("=");
		if (formulaSplit.length != 2) {
			displayPopup("The formula must contain exactly one equals sign");
			return false;
		}

		if (!formulaSplit[0].contains("y") || !formulaSplit[1].contains("x")) {
			displayPopup("The formula must have Y on the left and X on the right");
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
	
}
