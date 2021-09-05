package com.hey;

public class UpdatePrinter {

	int counter = 0;
	int numToUpdateOn = 50;
	long initTime;
	int size;
	String stateName;

	public UpdatePrinter(int size, String stateName) {
		this.initTime = System.currentTimeMillis();
		this.size = size;
		this.stateName = stateName;
	}

	public void printUpdateIfNeeded() {
		counter++;
		if (counter % 3 == 0) {
			long secondsTakenForLastThree = (System.currentTimeMillis() - initTime) / 1000;
			if (secondsTakenForLastThree < 0.1) {
				size -= counter;
				counter = 0;
				initTime = System.currentTimeMillis();
				return;
			}
		}
		if (counter % numToUpdateOn == 0) {
			long secondsTakenSinceLastUpdate = (System.currentTimeMillis() - initTime) / 1000;
			int numRemainingCities = size - counter;
			long minRemaining = secondsTakenSinceLastUpdate * numRemainingCities / (numToUpdateOn * 60);
			System.out.println(stateName + " (" + counter + "/" + size + ") time remaining: "
					+ Util.minToString((int) minRemaining));
			initTime = System.currentTimeMillis();
		}
	}
}
