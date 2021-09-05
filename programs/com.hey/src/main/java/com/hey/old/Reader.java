package com.hey.old;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hey.Util;

public class Reader {

	static class Result {
		String percentAsian = "N/A";
		String percentBlack = "N/A";
		String percentWhite = "N/A";
		String percentHispanic = "N/A";
		String medianRent = "N/A";
		String percentWithAtleastBachelors = "N/A";
	}

	public static void main(String[] args) throws Exception {
		runThread("al", "Alabama");

		runThread("ak", "Alaska");
		runThread("az", "Arizona");
		runThread("ar", "Arkansas");
		runThread("ca", "California");
		runThread("co", "Colorado");
		runThread("ct", "Connecticut");
		runThread("de", "Delaware");
		runThread("fl", "Florida");
		runThread("ga", "Georgia");
		runThread("hi", "Hawaii");
		runThread("id", "Idaho");
		runThread("il", "Illinois");
		runThread("in", "Indiana");
		runThread("ia", "Iowa");
		runThread("ks", "Kansas");
		runThread("ky", "Kentucky");
		runThread("la", "Louisiana");
		runThread("me", "Maine");
		runThread("md", "Maryland");
		runThread("ma", "Massachusetts");
		runThread("mi", "Michigan");
		runThread("mn", "Minnesota");
		runThread("ms", "Mississippi");
		runThread("mo", "Missouri");
		runThread("mt", "Montana");
		runThread("ne", "Nebraska");
		runThread("nv", "Nevada");
		runThread("nh", "New Hampshire");
		runThread("nj", "New Jersey");
		runThread("nm", "New Mexico");
		runThread("ny", "New York");
		runThread("nc", "North Carolina");
		runThread("nd", "North Dakota");
		runThread("oh", "Ohio");
		runThread("ok", "Oklahoma");
		runThread("or", "Oregon");
		runThread("pa", "Pennsylvania");
		runThread("ri", "Rhode Island");
		runThread("sc", "South Carolina");
		runThread("sd", "South Dakota");
		runThread("tn", "Tennessee");
		runThread("tx", "Texas");
		runThread("ut", "Utah");
		runThread("vt", "Vermont");
		runThread("va", "Virginia");
		runThread("wa", "Washington");
		runThread("wv", "West Virginia");
		runThread("wi", "Wisconsin");
		runThread("wy", "Wyoming");

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
			if (count > 31) {
				line = line.replace("\"1,", "\"1").replace("\"2,", "\"2").replace("\"3,", "\"3").replace("\"4,", "\"4")
						.replace("\"5,", "\"5").replace("\"6,", "\"6").replace("\"7,", "\"7").replace("\"8,", "\"8")
						.replace("\"9,", "\"9");
				count = StringUtils.countMatches(line, ",");
				if (count > 31) {
					sb.append(line).append("\n");
					continue;
				} else {
					System.out.println("fixing: " + line);
				}
			}
			Result result = new Result();
			String cityName = WeatherWxReader.extractCityName(line);
			populateResultWithHispanicData(result, stateFullName, cityName);
			populateResultWithOtherData(result, stateAbbreviation, cityName);
			line = line.substring(0, line.length() - 3);
			sb.append(line).append(", \"").append(result.percentAsian).append("\", \"").append(result.percentBlack)
					.append("\", \"").append(result.percentWhite).append("\", \"").append(result.percentHispanic)
					.append("\", \"").append(result.medianRent).append("\", \"")
					.append(result.percentWithAtleastBachelors).append("\"));\n");
			counter++;
			if (counter % numToUpdateOn == 0) {
				long secondsTakenForLastTen = (System.currentTimeMillis() - initTime) / 1000;
				int numRemainingCities = size - counter;
				long minRemaining = secondsTakenForLastTen * numRemainingCities / (numToUpdateOn * 60);
				System.out
						.println(stateFullName + " time remaining: " + Util.minToString((int) minRemaining));
				initTime = System.currentTimeMillis();
			}
		}
		Util.WriteTextToFile(sb.toString(), stateFullName);
	}

	private static void populateResultWithHispanicData(Result result, String stateFullName, String cityName) {
		try {
			cityName = cityName.replace(" ", "_");
			String url = "https://www.bestplaces.net/people/city/" + stateFullName + "/" + cityName;
			String webPageText = Util.ReadTextFromPage(url);
			result.percentHispanic = returnValidValue(
					SperlingReader.getNumbersBeforeText(webPageText, " claim Hispanic"));
			if (result.percentHispanic.charAt(0) == '0') {
				result.percentHispanic = "N/A";
			}
		} catch (Exception ex) {

		}
	}

	public static String returnValidValue(String st) {
		if ("N/A".equals(st)) {
			return st;
		}
		if (st.charAt(0) == '0') {
			return "N/A";
		}
		if (st.length() > 14) {
			return "N/A";
		}
		try {
			Float.parseFloat(st);
			return st;
		} catch (Exception ex) {
			return "N/A";
		}
	}

	private static void populateResultWithOtherData(Result result, String stateAbbreviation, String cityName) {
		String webPageText = "";
		String url = "";
		try {
			cityName = cityName.replace(" ", "-");
			url = "https://worldpopulationreview.com/us-cities/" + cityName + "-" + stateAbbreviation + "-population";
			webPageText = Util.ReadTextFromPage(url);
			result.percentAsian = returnValidValue(getTextFromRaceString(webPageText, "Asian: "));
			result.percentBlack = returnValidValue(getTextFromRaceString(webPageText, "Black or African American: "));
			result.percentWhite = returnValidValue(getTextFromRaceString(webPageText, "White: "));
			result.medianRent = returnValidValue(SperlingReader.getNumbersBeforeText(webPageText, "per month"));
			float bachelorsDegreePercent = getDegreeRate(webPageText, "Bachelors Degree ");
			float graduateDegreePercent = getDegreeRate(webPageText, "Graduate Degree ");
			float d = bachelorsDegreePercent + graduateDegreePercent;
			BigDecimal bd = new BigDecimal(Float.toString(d));
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			result.percentWithAtleastBachelors = returnValidValue(String.valueOf(bd.floatValue()));
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