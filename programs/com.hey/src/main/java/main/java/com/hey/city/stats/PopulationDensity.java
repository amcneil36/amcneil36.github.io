package main.java.com.hey.city.stats;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class PopulationDensity extends CityStats {

	public static void main(String[] args) throws Exception {
		PopulationDensity pd = new PopulationDensity();
		pd.processAllStates();
	}
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		
		double population = Double.valueOf(data.population);
		if (!data.landArea.equals("N/A")) {
			double landArea = Double.valueOf(data.landArea);
			int populationDensity = Util.getIntFromDouble(population/landArea);	
			data.populationDensity = String.valueOf(populationDensity);
		}
		
	}

}
