package mainClasses;

import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppClass extends Application {
	
	private final static String appTitle = "GraphEasy";
	private final static String appIconFileName = "linechart.png";
	
	Logger logger;
	public static final int minimumStageWidth = 600;
	public static final int minimumStageHeight = 600;
	

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
		
		Stage stage = new Stage();
		stage.setTitle(appTitle);
		Image icon = new Image(getClass().getResourceAsStream("/" + appIconFileName)); // Adjust path if needed
		stage.getIcons().add(icon);
		stage.setResizable(false);
		Scene scene = new Scene(controller.getMain());
		stage.setScene(scene);
		
		
		
		
		stage.showAndWait();
		
	}
	
	
	private void startApp() {
		logger = Logger.getLogger(AppClass.class.getName());
		log("Started app");
	}
	
	private void closeApp() {
		log("Closing app");
	}
	
	private void log(String string) {
		
		System.out.println(string);
//		logger.log(Level.INFO, string);
	}


	
}
