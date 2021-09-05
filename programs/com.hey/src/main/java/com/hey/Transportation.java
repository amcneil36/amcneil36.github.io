package com.hey;

public class Transportation  extends GenericDataReadAndWriter {

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		if (!data.walkScore.contains("N/A")) {
			return;
		}
		String stateAbbreviation = Util.getStateAbbreviation(stateName).toUpperCase();
		String cityName = data.cityName.replace("\"", "").replace(" ", "_");
		String url = "https://www.walkscore.com/" + stateAbbreviation + "/" + cityName;
		String text = "";
		try {
			text = Util.RetrieveHtmlcodeFromPage(url).toString();
		}
		catch (Exception ex) {
			System.out.println("Transportation score not found for: " + url);
			return;
		}
		data.walkScore = getScore("walk", text);
		if (!text.contains("walk/score")) {
			System.out.println("walk score not found for: " + url);
			return;
		}
		data.walkScore = getScore("walk", text);
		data.transitScore = getScore("transit", text);
		data.bikeScore = getScore("bike", text);
	}
	
	private static String getScore(String prefix, String text) {
		String pattern = prefix + "/score/";
		int startIdx = text.indexOf(pattern) + pattern.length();
		int endIdx = text.indexOf(".", startIdx);
		return text.substring(startIdx, endIdx);
	}
	
	public static void main(String[] args) throws Exception {
		GenericDataReadAndWriter n = new Transportation();
		n.processState("Delaware");

	}

}
