package easterEggs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mainClasses.AppClass;
import mainClasses.BubbleWrapController;

public class EasterEggMethods {
	
	String youFoundAnEasterEggTitle = "You found an easter egg!";

	public static boolean displayMatchingPopup(String input) throws Exception {
		EasterEggMethods methods = new EasterEggMethods();
		switch (input) {
		case "poppop":
			methods.showEasterEggPopup();
			return true;
		}

		return false;
	}

	private void showEasterEggPopup() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(BubbleWrapController.fxml));
		loader.load();
		BubbleWrapController controller = loader.getController();

		controller.createBubbles();

		Stage stage = new Stage();
		stage.setTitle(youFoundAnEasterEggTitle);
		Image icon = new Image(getClass().getResourceAsStream("/" + AppClass.appIconFileName)); // Adjust path if needed
		stage.getIcons().add(icon);
		Scene scene = new Scene(controller.getMain());
		stage.setScene(scene);
		stage.showAndWait();
	}

}
