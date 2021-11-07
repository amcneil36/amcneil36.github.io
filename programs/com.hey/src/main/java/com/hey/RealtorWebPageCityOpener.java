package main.java.com.hey;

import java.awt.Desktop;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealtorWebPageCityOpener {

	private static boolean shouldTabBeOpened(CityStats.Data data) throws Exception {
		return !data.metroPopulation.contains("N/A") && data.metro.contains("Dallas")
				&& Util.getDaysSinceLastUpdated(data) > 10 && Integer.valueOf(data.population) > 5000
				&& data.stateName.equals("Texas");
	}

	private static Map<String, String> map = new HashMap<String, String>();

	public static void main(String[] args) throws Exception {
		populateMap();
		processStates();
	}

	private static void processStates() throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		int numTabsOpened = 0;
		for (CityStats.Data data : dataList) {

			if (shouldTabBeOpened(data)) {
				String suffix = map.get(data.stateName.toLowerCase());
				String url = "https://www.realtor.com/realestateandhomes-search/" + data.cityName.replace(" ", "-")
						+ "_" + suffix.toUpperCase() + "/overview";
				Desktop.getDesktop().browse(new URI(url));
				numTabsOpened++;
			}
		}
		System.out.println("number of tabs opened: " + numTabsOpened);
	}

	private static void populateMap() {
		map = new HashMap<String, String>();
		fillMapWithItem("al", "Alabama");
		fillMapWithItem("ak", "Alaska");
		fillMapWithItem("az", "Arizona");
		fillMapWithItem("ar", "Arkansas");
		fillMapWithItem("ca", "California");
		fillMapWithItem("co", "Colorado");
		fillMapWithItem("ct", "Connecticut");
		fillMapWithItem("de", "Delaware");
		fillMapWithItem("fl", "Florida");
		fillMapWithItem("ga", "Georgia");
		fillMapWithItem("hi", "Hawaii");
		fillMapWithItem("id", "Idaho");
		fillMapWithItem("il", "Illinois");
		fillMapWithItem("in", "Indiana");
		fillMapWithItem("ia", "Iowa");
		fillMapWithItem("ks", "Kansas");
		fillMapWithItem("ky", "Kentucky");
		fillMapWithItem("la", "Louisiana");
		fillMapWithItem("me", "Maine");
		fillMapWithItem("md", "Maryland");
		fillMapWithItem("ma", "Massachusetts");
		fillMapWithItem("mi", "Michigan");
		fillMapWithItem("mn", "Minnesota");
		fillMapWithItem("ms", "Mississippi");
		fillMapWithItem("mo", "Missouri");
		fillMapWithItem("mt", "Montana");
		fillMapWithItem("ne", "Nebraska");
		fillMapWithItem("nv", "Nevada");
		fillMapWithItem("nh", "New Hampshire");
		fillMapWithItem("nj", "New Jersey");
		fillMapWithItem("nm", "New Mexico");
		fillMapWithItem("ny", "New York");
		fillMapWithItem("nc", "North Carolina");
		fillMapWithItem("nd", "North Dakota");
		fillMapWithItem("oh", "Ohio");
		fillMapWithItem("ok", "Oklahoma");
		fillMapWithItem("or", "Oregon");
		fillMapWithItem("pa", "Pennsylvania");
		fillMapWithItem("ri", "Rhode Island");
		fillMapWithItem("sc", "South Carolina");
		fillMapWithItem("sd", "South Dakota");
		fillMapWithItem("tn", "Tennessee");
		fillMapWithItem("tx", "Texas"); // overnight
		fillMapWithItem("ut", "Utah");
		fillMapWithItem("vt", "Vermont");
		fillMapWithItem("va", "Virginia");
		fillMapWithItem("wa", "Washington");
		fillMapWithItem("wv", "West Virginia");
		fillMapWithItem("wi", "Wisconsin");
		fillMapWithItem("wy", "Wyoming");
		fillMapWithItem("dc", "Washington DC");
	}

	private static void fillMapWithItem(String string, String string2) {
		map.put(string2.toLowerCase(), string.toLowerCase());

	}

}
