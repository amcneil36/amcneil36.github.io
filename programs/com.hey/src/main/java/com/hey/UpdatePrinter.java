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
		if (counter % numToUpdateOn == 0) {
			long secondsTakenForLastTen = (System.currentTimeMillis() - initTime) / 1000;
			int numRemainingCities = size - counter;
			long minRemaining = secondsTakenForLastTen * numRemainingCities / (numToUpdateOn * 60);
			System.out.println(stateName + " (" + counter + "/" + size + ") time remaining: "
					+ WebPageReader.minToString((int) minRemaining));
			initTime = System.currentTimeMillis();
		}
	}
}
