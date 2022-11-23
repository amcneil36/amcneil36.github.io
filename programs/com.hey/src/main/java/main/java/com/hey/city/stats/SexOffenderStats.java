package main.java.com.hey.city.stats;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class SexOffenderStats extends CityStats{

	private static final String SEARCH_STRING = " a ratio of ";
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String city = data.cityName.replace(" ", "-");
		// https://www.homefacts.com/offenders/Texas/Nueces-County/Corpus-Christi.html
		String url = "https://www.homefacts.com/offenders/" + stateName + "/" + data.countyName.replace(" ", "-")  + "/" + city + ".html";
		String text = Util.ReadTextFromPage(url);
		if (!text.contains(SEARCH_STRING)) {
			return;
		}
		int startIdx = text.indexOf(SEARCH_STRING)+SEARCH_STRING.length();
		text = text.substring(startIdx);
		text = text.substring(0, text.indexOf(" "));
		data.sexOffenderCount = text;
	}
	
	public static void main(String[] args) throws Exception {
		CityStats s = new SexOffenderStats();
		s.processAllStates();
	}

}
