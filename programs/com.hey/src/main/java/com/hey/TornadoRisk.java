package main.java.com.hey;

public class TornadoRisk extends CityStats {

	private static final String SEARCH_STRING = "Yearly Avg. of Tornados in ";
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String city = data.cityName.replace(" ", "-");
		// https://www.homefacts.com/offenders/Texas/Nueces-County/Corpus-Christi.html
		String url = "https://www.homefacts.com/tornadoes/" + stateName + "/" + data.countyName.replace(" ", "-")  + "/" + city + ".html";
		//String url = "https://www.homefacts.com/tornadoes/Missouri/Jackson-County/Kansas-City.html";
		String text = Util.ReadTextFromPage(url);
		if (!text.contains(SEARCH_STRING)) {
			return;
		}
		int startIdx = text.indexOf(SEARCH_STRING)+SEARCH_STRING.length();
		System.out.println("hey");
		text = text.substring(startIdx);
		text = text.substring(text.indexOf(", ") + 2);
		text = text.substring(text.indexOf(" ") + 1);
		text = text.substring(0, text.indexOf(" "));
		try {
			int numTornadoes = Integer.valueOf(text);
			data.tornadoes = String.valueOf(numTornadoes);
		}
		catch(Exception ex) {
		}
	}
	
	public static void main(String[] args) throws Exception {
		CityStats s = new TornadoRisk();
		s.processState("Delaware");

	}

}
