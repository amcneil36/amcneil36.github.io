package com.hey;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Reader {

	static class Result {
		String percentVotedDemocrat = "N/A";
		String percentVotedRepublican = "N/A";
		String percentAsian = "N/A";
		String percentBlack = "N/A";
		String percentWhite = "N/A";
		String percentHispanic = "N/A";
		String medianRent = "N/A";
		String percentWithAtleastBachelors = "N/A";
	}

	public static void main(String[] args) throws Exception {
		runThread("al", "Alabama");

	}

	public static void runThread(String stateAbbreviation, String stateFullName) throws Exception {
		new RunnableDemo3(stateAbbreviation, stateFullName).start();
	}

	public static void ProcessState(String stateAbbreviation, String stateFullName) {
		List<String> list = WeatherWxReader.readFileToList(stateFullName);
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		int numToUpdateOn = 20;
		int size = list.size();
		long initTime = System.currentTimeMillis();
		for (String line : list) {
			int count = StringUtils.countMatches(line, ",");
			if (count > 26) {
				sb.append(line).append("\n");
				continue;
			}
			Result result = new Result();
			String cityName = WeatherWxReader.extractCityName(line);
			populateResultWithVotingData(result, stateFullName, cityName);
			populateResultWithHispanicData(result, stateFullName, cityName);
			populateResultWithOtherData(result, stateAbbreviation, cityName);
			line = line.substring(0, line.length() - 3);
			sb.append(line).append(", \"").append(result.percentVotedDemocrat).append("\", \"")
					.append(result.percentVotedRepublican).append("\", \"").append(result.percentAsian).append("\", \"")
					.append(result.percentBlack).append("\", \"").append(result.percentWhite).append("\", \"")
					.append(result.percentHispanic).append("\", \"").append(result.medianRent).append("\", \"")
					.append(result.percentWithAtleastBachelors).append("\"));\n");
			counter++;
			if (counter == 5) {
				break;
			}
			if (counter % numToUpdateOn == 0) {
				long secondsTakenForLastTen = (System.currentTimeMillis() - initTime) / 1000;
				int numRemainingCities = size - counter;
				long minRemaining = secondsTakenForLastTen * numRemainingCities / (numToUpdateOn * 60);
				System.out
						.println(stateFullName + " time remaining: " + SperlingReader.minToString((int) minRemaining));
				initTime = System.currentTimeMillis();
			}
		}
		SperlingReader.WriteTextToFile(sb.toString(), stateFullName);
	}

	private static void populateResultWithHispanicData(Result result, String stateFullName, String cityName) {
		try {
			cityName = cityName.replace(" ", "_");
			String url = "https://www.bestplaces.net/people/city/" + stateFullName + "/" + cityName;
			String webPageText = SperlingReader.ReadTextFromPage(url);
			result.percentHispanic = SperlingReader.getNumbersBeforeText(webPageText, " claim Hispanic");
		} catch (Exception ex) {

		}
	}

	private static void populateResultWithOtherData(Result result, String stateAbbreviation, String cityName) {
		String webPageText = "";
		String url = "";
		try {
			cityName = cityName.replace(" ", "-");
			url = "https://worldpopulationreview.com/us-cities/" + cityName + "-" + stateAbbreviation
					+ "-population";
			webPageText = SperlingReader.ReadTextFromPage(url);
			result.percentAsian = getTextFromRaceString(webPageText, "Asian: ");
			result.percentBlack = getTextFromRaceString(webPageText, "Black or African American: ");
			result.percentWhite = getTextFromRaceString(webPageText, "White: ");
			result.medianRent = SperlingReader.getNumbersBeforeText(webPageText, "per month");
			float bachelorsDegreePercent = getDegreeRate(webPageText, "Bachelors Degree ");
			float graduateDegreePercent = getDegreeRate(webPageText, "Graduate Degree ");
			float d = bachelorsDegreePercent + graduateDegreePercent;
		    BigDecimal bd = new BigDecimal(Float.toString(d));
		    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		    result.percentWithAtleastBachelors =  String.valueOf(bd.floatValue());
		} catch (Exception ex) {
		}
	}

	private static float getDegreeRate(String webPageText, String pattern) {
		int startIdx = webPageText.indexOf(pattern) + pattern.length();
		webPageText = webPageText.substring(startIdx, webPageText.indexOf("%", startIdx));
		webPageText = webPageText.substring(webPageText.indexOf(" ") + 1, webPageText.length());
		return Float.parseFloat(webPageText);
	}

	private static String getTextFromRaceString(String fullText, String raceString) {
		int startIdx = fullText.indexOf(raceString) + raceString.length();
		return fullText.substring(startIdx, fullText.indexOf("%", startIdx));
	}

	private static void populateResultWithVotingData(Result result, String stateFullName, String cityName) {
		try {
			cityName = cityName.replace(" ", "_");
			String url = "https://www.bestplaces.net/voting/city/" + stateFullName + "/" + cityName;
			String webPageText = SperlingReader.ReadTextFromPage(url);
			result.percentVotedDemocrat = SperlingReader.getNumbersBeforeText(webPageText,
					" of the people voted Democrat");
			result.percentVotedRepublican = SperlingReader.getNumbersBeforeText(webPageText, " voted for the Republi");
		} catch (Exception ex) {

		}
	}

}

class RunnableDemo3 implements Runnable {
	private Thread t;
	private String stateAbbreviation;
	private String stateFullName;

	RunnableDemo3(String stateAbbreviation, String stateFullName) {
		this.stateAbbreviation = stateAbbreviation;
		this.stateFullName = stateFullName;
	}

	public void run() {
		try {
			Reader.ProcessState(stateAbbreviation, stateFullName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
}