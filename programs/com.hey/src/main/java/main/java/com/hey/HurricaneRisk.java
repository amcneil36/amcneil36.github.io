package main.java.com.hey;

public class HurricaneRisk extends CityStats {

	private static final String SEARCH_STRING = " risk hurricane zone. ";
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String city = data.cityName.replace(" ", "-");
		// https://www.homefacts.com/offenders/Texas/Nueces-County/Corpus-Christi.html
		String url = "https://www.homefacts.com/hurricanes/" + stateName + "/" + data.countyName.replace(" ", "-")  + "/" + city + ".html";
		String text = Util.ReadTextFromPage(url);
		if (!text.contains(SEARCH_STRING)) {
			return;
		}
		int startIdx = text.indexOf(SEARCH_STRING)+SEARCH_STRING.length();
		text = text.substring(startIdx);
		text = text.substring(0, text.indexOf(" "));
		try {
			int numHurricanes = Integer.valueOf(text);
			data.hurricanes = String.valueOf(numHurricanes);
		}
		catch(Exception ex) {
		}
	}
	
	public static void main(String[] args) throws Exception {
		CityStats s = new HurricaneRisk();
		s.processAllStates();

	}

}
