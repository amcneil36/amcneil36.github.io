package main.java.com.hey.city.stats;

import main.java.com.hey.CityStats;

public class NoopCityStats extends CityStats {
	
	public static void main(String[] args) throws Exception {
		CityStats n = new NoopCityStats();
		n.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {

		
	}

}
