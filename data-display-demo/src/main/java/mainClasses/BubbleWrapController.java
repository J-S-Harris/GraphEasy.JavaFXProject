package mainClasses;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BubbleWrapController {

	public static String fxml = "/BubbleWrapMain.fxml";

	private static int columns = 10;
	private static int rows = 10;
	
	private static int minimumWidth = 50;
	private static int minimumHeight = 50;
	
//	private static String prePressText = "🙂";
	private static String prePressText = "*";
//	private static String postPressText = "😲";
	private static String postPressText = "o";
	
	private static int fontSize = 25;
	
	
	@FXML VBox main, bubbleHolder;
	
	public Parent getMain() {
		return main;
	}

	public void createBubbles() {
		for(int counterRows = 0; counterRows < rows; counterRows++) {
			HBox row = new HBox();
			for(int counterColumns = 0; counterColumns < columns; counterColumns++) {
				row.getChildren().add(generateButton());
			}
			bubbleHolder.getChildren().add(row);
		}
	}
	
	private static Button generateButton() {
		final Button button = new Button(prePressText);
		button.setMinWidth(minimumWidth);
		button.setMinHeight(minimumHeight);
		button.setFocusTraversable(false);

		int colourA = (int) (Math.random() * 255);
		int colourB = (int) (Math.random() * 255);
		int colourC = (int) (Math.random() * 255);
		String colourAString = Integer.toHexString(colourA);
		colourAString = (colourAString.length() == 1) ? "0" + colourAString : colourAString; 
		String colourBString = Integer.toHexString(colourB);
		colourBString = (colourBString.length() == 1) ? "0" + colourBString : colourBString; 
		String colourCString = Integer.toHexString(colourC);
		colourCString = (colourCString.length() == 1) ? "0" + colourCString : colourCString; 
		
		final String colourString = colourAString + colourBString + colourCString;
		
//		button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		button.setStyle("-fx-background-color: #" + colourString +"; -fx-text-fill: white; -fx-font-size: " + fontSize + "px;");
		
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!button.isDisabled()) {
				button.setDisable(true);
				button.setText(postPressText);
				button.setStyle("-fx-background-color: #" + colourString +"; -fx-text-fill: black; -fx-font-size: " + fontSize + "px;");
				}
			}
			
		});
		
		return button;
	}
	
}
