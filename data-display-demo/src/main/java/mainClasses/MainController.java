package mainClasses;

import java.util.ArrayList;

import net.objecthunter.exp4j.Expression;
import javax.script.ScriptException;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainController {

	Canvas canvas;
	GraphicsContext gc;
	
	// TODO Make it dynamically fill Stage
	double canvasWidth = 500;
	double canvasHeight = 400;
	Color canvasBackGroundColour = Color.LIGHTBLUE;
	Color canvasStrokeColour = Color.GREEN;
	int canvasLineWidth = 2;
	
	// TODO DELETE ME
	String testFormula = "2x+1";
	
	@FXML VBox main;
	@FXML VBox canvasBox;
	
	static String fxmlFileName = "/mainFXML.fxml";

	public VBox getMain() {
		return main;
	}
	
	public VBox getCanvasBox() {
		return canvasBox;
	}

	public void prepareUI() {
		
		createDefaultCanvas();
		getCanvasBox().getChildren().add(canvas);
	}

	private void createDefaultCanvas() {
		canvas = new Canvas(canvasWidth, canvasHeight);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(canvasBackGroundColour);
		gc.setStroke(canvasStrokeColour);
		gc.setLineWidth(canvasLineWidth);
		
		// TODO Let the user input this:
		try {
			createLine("y=x*x+2");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// TODO Generalise this
	private void createLine(String formula) throws ScriptException {
		
		formula = formula.replace(" ", "");
		formula = formula.toLowerCase();
		formula = formula.trim();
		
		boolean correctFormula = checkFormulaIsCorrect(formula);
		if(!correctFormula) {
			return;
		}
		
		ArrayList<Double> rawYValues = new ArrayList<Double>();

		for(double xValue = (0 - (canvasWidth/2)); xValue <= canvasWidth/2; xValue+=1) {
			String formulaImpl = formula.replace("x", ""+(int)xValue);
			
	        Expression expression = new ExpressionBuilder(formulaImpl.split("=")[1]).build();
	        double result = expression.evaluate();

//			double yValue = result * (canvasHeight/canvasWidth); // TODO What was this here for?
			rawYValues.add(result);
		}
		
		
		// Add 0 minus the lowest value to make sure they are all on-screen
		double lowest = Double.MAX_VALUE;
		for(double value : rawYValues) {
			if(value < lowest) {
				lowest = value;
			}
		}
		ArrayList<Double> yValues = new ArrayList<Double>();
		for(int counter = 0; counter < rawYValues.size(); counter++) {
			yValues.add(rawYValues.get(counter) - lowest);
		}
		
		
		// Scale the y values to fit canvas
		double largest = Double.MIN_VALUE;
		for(double value : yValues) {
			if(value > largest) {
				largest = value;
			}
		}
		double scale = canvasHeight / largest;
		
		// Draw the line
		for(int counter = 0; counter < yValues.size(); counter++) {
			double xValue = counter;
			double yValue = canvasHeight - (yValues.get(counter) * scale);
			gc.strokeLine(xValue, yValue, xValue+1, yValue+1);
		}
		
	}
	
	// Unit test this
	private boolean checkFormulaIsCorrect(String formula) {
		
		if(!formula.contains("x") || !formula.contains("y")) {
			displayPopup("The formula must contain an X and Y value");
			return false;
		}
		
		if(!formula.contains("=")) {
			displayPopup("The formula must contain an equals sign");
		}
		
		String[] formulaSplit = formula.split("=");
		if(formulaSplit.length != 2) {
			displayPopup("The formula must contain exactly one equals sign");
			return false;
		}
		
		if(!formulaSplit[0].contains("y") || !formulaSplit[1].contains("x")) {
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
	
}
