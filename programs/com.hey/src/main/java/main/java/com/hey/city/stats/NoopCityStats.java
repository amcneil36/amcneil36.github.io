package main.java.com.hey.city.stats;

import main.java.com.hey.CityStats;

public class NoopCityStats extends CityStats {

	
	public int numStatesComplete = 0;
	
	@Override
	protected void runCleanup() {
		numStatesComplete++;
	};
	
	public static void main(String[] args) throws Exception {
		CityStats n = new NoopCityStats();
		n.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		
		
	}

}
