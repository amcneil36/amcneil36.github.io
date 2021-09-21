package com.hey;


public class NoopCityStats extends CityStats {

	public static void main(String[] args) throws Exception {
		CityStats n = new NoopCityStats();
		//n.processAllStates();
		n.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		
		
	}

}
