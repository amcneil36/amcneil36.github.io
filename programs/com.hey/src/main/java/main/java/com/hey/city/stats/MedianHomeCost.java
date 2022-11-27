package main.java.com.hey.city.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class MedianHomeCost extends CityStats {

	/*
	 * data from https://www.redfin.com/news/data-center/. go to home price. download as Crosstab. open the .csv with notepad++
	 * and copy paste into a .txt file. for some reason it can't read from .csv here
	 * because it is putting symbols
	 * 
	 * note: i am not handling conflicts for same city/state name
	 */

	static Map<String, String> mapOfKeyToPrice = new HashMap<>();

	private static String getKey(String city, String stateAbbreviation) {
		return city.toLowerCase() + ", " + stateAbbreviation.toLowerCase();
	}

	public static void main(String[] args) throws Exception {
		List<String> list = Util.readTextFromFile(
				"C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\RealEstateData\\medianSalePrice.txt");
		list.remove(0);
		list.remove(0);
		for (String st : list) {
			if (!st.contains("$")) {
				continue;
			}
			String city = st.substring(0, st.indexOf(","));
			st = st.substring(st.indexOf(",") + 2);
			String stateAbbreviation = st.substring(0, st.indexOf("\t"));
			st = st.substring(st.indexOf("\t"));
			st = st.replace("\t", "");
			st = st.replace("$", "");
			st = st.replace("K", "");
			String cost = st;
			cost = cost.replace(",", "");
			cost = cost + "000";
			int costInt = Integer.valueOf(cost);
			if (costInt > 400000000) {
				continue;
			}
			if (costInt < 1) {
				continue;
			}
			cost = "$" + cost;
			mapOfKeyToPrice.put(getKey(city, stateAbbreviation), cost);
		}
		CityStats cs = new MedianHomeCost();
		cs.processAllStates();
	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.medianHomePrice = "N/A";
		String stateAbbreviation = Util.getStateAbbreviation(data.stateName);
		String key = getKey(Util.removeStuffFromCityName(data.cityName), stateAbbreviation);
		if (mapOfKeyToPrice.containsKey(key)) {
			data.medianHomePrice = mapOfKeyToPrice.get(key);
		}
	}
}
