package main.java.com.hey.city.stats;

import java.util.HashSet;
import java.util.Set;

import main.java.com.hey.CityStats;

public class CountyFixer extends CityStats {

	public static void main(String[] args) throws Exception {
		CountyFixer cf = new CountyFixer();
		cf.processAllStates();

	}
	
	static Set<String> seen = new HashSet<>();
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String key = data.stateName + ";" + data.countyName;
		if (!data.countyName.endsWith(" County")) {
			data.countyName = data.countyName + " County";
		}
		
	}


}
