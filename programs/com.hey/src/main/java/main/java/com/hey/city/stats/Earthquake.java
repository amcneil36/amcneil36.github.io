package main.java.com.hey.city.stats;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class Earthquake extends CityStats {

	private static final String SEARCH_STRING = "since 1931 within 30 miles ";

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String city = data.cityName.replace(" ", "-");
		String url = "https://www.homefacts.com/earthquakes/" + stateName + "/" + data.countyName.replace(" ", "-")
				+ "/" + city + ".html";
		String text = Util.ReadTextFromPage(url);
		if (!text.contains(SEARCH_STRING)) {
			return;
		}
		try {
			int startIdx = text.indexOf(SEARCH_STRING) + SEARCH_STRING.length();
			text = text.substring(startIdx);
			text = text.substring(0, text.indexOf(" "));
			text = text.replace(",", "");
			int numEarthQuakes = Integer.valueOf(text);
			data.earthQuakes = String.valueOf(numEarthQuakes);
		} catch (Exception ex) {
		}
	}

	public static void main(String[] args) throws Exception {
		CityStats s = new Earthquake();
		s.processAllStates();

	}

}