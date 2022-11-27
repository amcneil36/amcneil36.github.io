package main.java.com.hey.city.stats;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class HomeSize extends CityStats {

	public static void main(String[] args) throws Exception {
		CityStats cs = new HomeSize();
		cs.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.homeSquareFeet = "N/A";
		if (!data.costPerSquareFoot.equals("N/A") && !data.medianHomePrice.equals("N/A")) {
			double cost = Integer.valueOf(data.medianHomePrice.replace("$", ""));
			double costPerSqFt = Integer.valueOf(data.costPerSquareFoot.replace("$", ""));
			double size = cost/costPerSqFt;
			data.homeSquareFeet = String.valueOf(Util.getIntFromDouble(size));
		}
		
	}

}
