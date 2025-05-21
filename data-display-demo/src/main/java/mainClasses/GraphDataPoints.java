package mainClasses;

import java.util.UUID;

public class GraphDataPoints {

	double x;
	double y;
	UUID uuid;
	
	public GraphDataPoints(double x, double y) {
		this.x = x;
		this.y = y;
		uuid = UUID.randomUUID();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
}
