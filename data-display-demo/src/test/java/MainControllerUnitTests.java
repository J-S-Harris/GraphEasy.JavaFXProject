import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import mainClasses.GraphData;
import mainClasses.MainController;

public class MainControllerUnitTests {

	MainController controller;

	@Before
	public void before() {
		try {

			new JFXPanel();
			System.out.println(getClass().getResource(MainController.fxmlFileName));

			FXMLLoader loader = new FXMLLoader(getClass().getResource(MainController.fxmlFileName));
			loader.load();
			controller = loader.getController();
			controller.prepareUI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSerialiseAndDeserialise() {

		try {

			// Create, save, and load GraphData
			ArrayList<GraphData> data = createGraphDataPoints();
            File file = new File("src/test/resources/test-file.txt");
			controller.saveImpl(file, data);
			ArrayList<GraphData> data2 = new ArrayList<GraphData>();
			controller.loadImpl(file, data2);
			
			// Tidy up created File
			assertTrue(file.exists());
			file.delete();
			assertFalse(file.exists());

			// Check each value
			Field[] graphDataFields = GraphData.class.getDeclaredFields();
			assertTrue(data.size() == data2.size());

			// For each GraphData...
			for (int counter = 0; counter < data.size(); counter++) {

				// For each field...
				for (Field field : graphDataFields) {
					field.setAccessible(true);

					Object value1 = field.get(data.get(counter));
					Object value2 = field.get(data2.get(counter));

					if (field.getType() == String.class) {
						assertTrue(value1.equals(value2));
//						System.out.println(field.getName() +": "+ value1 +" "+ value2);

					} else if (field.getType() == ArrayList.class && value1 instanceof ArrayList<?>
							&& value2 instanceof ArrayList<?>) {
						ArrayList<?> list1 = (ArrayList<?>) value1;
						ArrayList<?> list2 = (ArrayList<?>) value2;
						assertEquals(list1.size(), list2.size());
						for (int i = 0; i < list1.size(); i++) {
							assertTrue(list1.get(i).equals(list2.get(i))); // Compare elements individually
//							System.out.println(list1.get(i) + " " + list2.get(i));
						}

					} else {
						assertTrue(value1 == value2);
//						System.out.println("ELSE EQUALS: " + value1 + " " + value2);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_prompt() {
		System.out.println("Write unit tests!");
		assertTrue(true);
	}

	private ArrayList<GraphData> createGraphDataPoints() {

		ArrayList<GraphData> graphDataPoints = new ArrayList<>();

		GraphData data1 = new GraphData("name", "y=2x", new ArrayList<Double>());
		graphDataPoints.add(data1);
		controller.calculateRawValues("y=2x", data1.getyValues());

		GraphData data2 = new GraphData("name", "y=3x", new ArrayList<Double>());
		graphDataPoints.add(data2);
		controller.calculateRawValues(data2.getFormula(), data2.getyValues());

		GraphData data3 = new GraphData("name", "y=-x-300", new ArrayList<Double>());
		graphDataPoints.add(data3);
		controller.calculateRawValues(data3.getFormula(), data3.getyValues());

		GraphData data4 = new GraphData("name", "y=(0.1x)^2-500", new ArrayList<Double>());
		graphDataPoints.add(data4);
		controller.calculateRawValues(data4.getFormula(), data4.getyValues());

		GraphData data5 = new GraphData("name", "y=100", new ArrayList<Double>());
		graphDataPoints.add(data5);
		controller.calculateRawValues(data5.getFormula(), data5.getyValues());

		return graphDataPoints;
	}

}
