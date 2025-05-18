package mainClasses;

import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppClass extends Application {
	
	Logger logger;
	public static final int defaultStageWidth = 800;
	public static final int defaultStageHeight = 600;
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		AppClass mainClass = new AppClass();
		mainClass.startApp();
		mainClass.displayMainWindow();
		mainClass.closeApp();
	}

	public static void main(String[] args) {
		launch(args);
	}

	
	private void displayMainWindow() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(MainController.fxmlFileName));
		loader.load();
		MainController controller = loader.getController();
		controller.prepareUI();
		Stage stage = controller.getStage();
		stage.showAndWait();
		
	}
	
	
	private void startApp() {
//		log("Started app");
		logger = Logger.getLogger(AppClass.class.getName());
	}
	
	private void closeApp() {
//		log("Closing app");
	}
	
	private void log(String string) {
		
		System.out.println(string);
//		logger.log(Level.INFO, string);
	}


	
}
