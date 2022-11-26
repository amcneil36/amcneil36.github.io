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
		data.countyName = data.countyName.replace(" Census Area", "");
		data.countyName = data.countyName.replace(" city", "");
		data.countyName = data.countyName.replace(" Borough", "");
		// Borough
		if (!data.countyName.endsWith(" County")) {
			data.countyName = data.countyName + " County";
		}
		
	}


}
