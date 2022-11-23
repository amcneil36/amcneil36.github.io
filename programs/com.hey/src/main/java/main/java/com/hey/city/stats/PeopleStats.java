package main.java.com.hey.city.stats;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class PeopleStats extends CityStats {

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		if (!"N/A".equals(data.percentWhite)) {
			return;
		}
		String cityName = data.cityName.replace("\"", "").replace(" ", "_");
		String url = "https://www.bestplaces.net/people/city/" + stateName.replace(" ", "_") + "/" + cityName;
		String text = Util.ReadTextFromPage(url);
		data.percentWhite = parseRaceOrEthnicity(text, url, "% are white");
		data.percentBlack = parseRaceOrEthnicity(text, url, "% are black");
		data.percentAsian = parseRaceOrEthnicity(text, url, "% are asian");
		data.percentHispanic = parseRaceOrEthnicity(text, url, "% claim Hispanic");
	}

	private String parseRaceOrEthnicity(String text, String url, String searchPattern) {
		if (!text.contains(searchPattern)) {
			//System.out.println(searchPattern + " not found for: " + url + "; " + text);
			return "N/A";
		}
		text = text.substring(0, text.indexOf(searchPattern) + 1);
		int startIdx = text.length() - 1;
		char ch = text.charAt(startIdx);
		int totalIterations = 0;
		while (ch != ' ') {
			startIdx--;
			ch = text.charAt(startIdx);
			totalIterations++;
			if (totalIterations > 8) {
				//System.out.println("iteration fail!");
				return "N/A";
			}
		}
		text = text.substring(startIdx + 1);
		return text;
	}

	public static void main(String[] args) throws Exception {
		CityStats cityStats = new PeopleStats();
		cityStats.processAllStates();
	}

}
