package com.hey.old;

public class Num {
	private double avg = 0;
	private int numEntries = 0;
	
	public void updateAverage(double val) {
		avg = (avg * numEntries + val)/(numEntries+1);
		numEntries++;
	}
	
	public double getAvg() {return avg;}
	public int getNumEntries() {return numEntries;}
}
