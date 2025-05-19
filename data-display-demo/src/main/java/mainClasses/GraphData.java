package mainClasses;

import java.util.ArrayList;
import java.util.UUID;

public class GraphData {

	String name;
	String formula;
	ArrayList<Double> yValues;
	boolean shouldBeDrawn = true;
	UUID uuid;
	
	
	public GraphData(String name, String formula, ArrayList<Double> yValues) {
		this.name = name;
		this.formula = formula;
		this.yValues = yValues;
		uuid = UUID.randomUUID();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public ArrayList<Double> getyValues() {
		return yValues;
	}
	public void setyValues(ArrayList<Double> yValues) {
		this.yValues = yValues;
	}

	public boolean getShouldBeDrawn() {
		return shouldBeDrawn;
	}

	public void setShouldBeDrawn(boolean shouldBeDrawn) {
		this.shouldBeDrawn = shouldBeDrawn;
	}
	
	
	
}
