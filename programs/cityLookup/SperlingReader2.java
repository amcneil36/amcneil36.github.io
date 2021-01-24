package com.hey;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SperlingReader2 {


	static class Result {
		String percentVotedDemocrat = "N/A";
		String percentVotedRepublican = "N/A";
		String unemploymentRate = "N/A";
		String jobGrowthLastYear = "N/A";
		String populationGrowthSince2010 = "N/A";
	}

	public static void main(String[] args) throws Exception {
			runThread("ak", "Alaska");
		runThread("az", "Arizona");
		runThread("ar", "Arkansas");
		runThread("ca", "California"); // overnight
		runThread("co", "Colorado");
		runThread("ct", "Connecticut");
		runThread("de", "Delaware");
		runThread("fl", "Florida"); // success
		runThread("ga", "Georgia"); // success
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
	    runThread("tx", "Texas"); // overnight
		runThread("ut", "Utah");
		runThread("vt", "Vermont");
		runThread("va", "Virginia");
		runThread("wa", "Washington"); 
		runThread("wv", "West Virginia");
		runThread("wi", "Wisconsin");
		runThread("wy", "Wyoming");

	}

	public static void runThread(String stateAbbreviation, String stateFullName) throws Exception {
		new RunnableDemo4(stateAbbreviation, stateFullName).start();
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
			populateResultWithMainPageData(result, stateFullName, cityName);
			line = line.substring(0, line.length() - 3);
			sb.append(line).append(", \"").append(result.percentVotedDemocrat).append("\", \"")
					.append(result.percentVotedRepublican).append("\", \"").append(result.unemploymentRate).append("\", \"")
					.append(result.jobGrowthLastYear).append("\", \"").append(result.populationGrowthSince2010).append("\"));\n");
			counter++;
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


	private static void populateResultWithMainPageData(Result result, String stateFullName, String cityName) {
		try {
			cityName = cityName.replace(" ", "_");
			String url = "https://www.bestplaces.net/city/" + stateFullName + "/" + cityName;
			String webPageText = SperlingReader.ReadTextFromPage(url);
			result.unemploymentRate = getUnemploymentRate(webPageText);
			result.jobGrowthLastYear = getJobGrowth(webPageText);
			result.populationGrowthSince2010 = getPopulationGrowth(webPageText);
		} catch (Exception ex) {

		}
		
	}
	
	private static String getUnemploymentRate(String webPageText) {
		try {
		int startIdx = webPageText.indexOf("Unemployment Rate ") + "Unemployment Rate ".length();
		return webPageText.substring(startIdx, webPageText.indexOf("%", startIdx));
		}
		catch(Exception ex) {
			return "N/A";
		}
		}

	private static String getPopulationGrowth(String webPageText) {
		try {
		int initialIdx = webPageText.indexOf("had a population");
		int numTries = 0;
		webPageText = webPageText.substring(initialIdx, webPageText.length());
		int idx = 0;
		while (!Character.isDigit(webPageText.charAt(idx))) {
			idx++;
			if (numTries > 100) {
				return "N/A";
			}
			numTries++;
		}
		String st = "";
		if (webPageText.contains("decline")) {
			st += "-";
		}
		int endIdx = webPageText.indexOf("%", idx);
		st += webPageText.substring(idx, endIdx);
		return st;
		}
		catch(Exception ex) {
			return "N/A";
		}
	}

	private static String getJobGrowth(String webPageText) {
		try {
		int initialIdx = webPageText.indexOf("Recent job growth");
		int numTries = 0;
		webPageText = webPageText.substring(initialIdx, webPageText.length());
		initialIdx = webPageText.indexOf("jobs have");
		webPageText = webPageText.substring(initialIdx, webPageText.length());
		int idx =0;
		while (!Character.isDigit(webPageText.charAt(idx))) {
			idx++;
			if (numTries > 100) {
				return "N/A";
			}
			numTries++;
		}
		String st = "";
		if (webPageText.contains("decrease")) {
			st += "-";
		}
		int endIdx = webPageText.indexOf("%", idx);
		st += webPageText.substring(idx, endIdx);
		return st;}
		catch (Exception ex) {
			return "N/A";
		}
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

class RunnableDemo4 implements Runnable {
	private Thread t;
	private String stateAbbreviation;
	private String stateFullName;

	RunnableDemo4(String stateAbbreviation, String stateFullName) {
		this.stateAbbreviation = stateAbbreviation;
		this.stateFullName = stateFullName;
	}

	public void run() {
		try {
			SperlingReader2.ProcessState(stateAbbreviation, stateFullName);
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