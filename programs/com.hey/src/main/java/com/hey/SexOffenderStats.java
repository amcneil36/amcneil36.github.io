package com.hey;

public class SexOffenderStats extends CityStats{

	private static final String SEARCH_STRING = "The ratio of all residents to sex offenders in ";
	private static final String SEARCH_STRING2 = " is ";
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String city = data.cityName.replace(" ", "-");
		String url = "https://www.city-data.com/so/so-" + city + "-" + stateName + ".html";
		String text = Util.ReadTextFromPage(url);
		if (!text.contains(SEARCH_STRING)) {
			return;
		}
		int startIdx = text.indexOf(SEARCH_STRING)+SEARCH_STRING.length();
		text = text.substring(startIdx);
		if (!text.contains(SEARCH_STRING2) || !text.contains(".")) {
			return;
		}
		startIdx = text.indexOf(SEARCH_STRING2)+SEARCH_STRING2.length();
		text = text.substring(startIdx, text.indexOf("."));
		data.sexOffenderCount = text;
	}
	
	public static void main(String[] args) throws Exception {
		CityStats s = new SexOffenderStats();
		s.processState("Texas");
	}

}
