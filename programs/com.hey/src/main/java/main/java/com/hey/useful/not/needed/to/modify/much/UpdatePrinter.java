package main.java.com.hey.useful.not.needed.to.modify.much;

public class UpdatePrinter {

	int counter = 0;
	int numToUpdateOn = 50;
	long initTime;
	int size;
	String stateName;
	boolean hasBeen10Seconds = false;

	public UpdatePrinter(int size, String stateName) {
		this.initTime = System.currentTimeMillis();
		this.size = size;
		this.stateName = stateName;
	}

	// maybe have the init time not start until 10s have passed?
	// and then counter will be set and size will be set
	public void printUpdateIfNeeded() {
		/*
		 * counter++; long secondsSinceStart = (System.currentTimeMillis() - initTime) /
		 * 1000; if (!hasBeen10Seconds && secondsSinceStart > 10) { hasBeen10Seconds =
		 * true; size -= counter; counter = 0; initTime = System.currentTimeMillis();
		 * return; } if (hasBeen10Seconds && counter % numToUpdateOn == 0) { long
		 * secondsTakenSinceLastUpdate = (System.currentTimeMillis() - initTime) / 1000;
		 * int numRemainingCities = size - counter; long minRemaining =
		 * secondsTakenSinceLastUpdate * numRemainingCities / (numToUpdateOn * 60);
		 * System.out.println(stateName + " (" + counter + "/" + size +
		 * ") time remaining: " + Util.minToString((int) minRemaining)); initTime =
		 * System.currentTimeMillis(); }
		 */
	}
}
