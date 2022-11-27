package main.java.com.hey.city.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class FIPSCodes extends CityStats {

	private static Map<String, String> map = new HashMap<String, String>();

	public static class FipsData {
		String city = "N/A";
		String fips = "N/A";
		String stateNum = "N/A";
		String stateName = "N/A";
		String county = "N/A";

		@Override
		public String toString() {
			return city + ", " + stateName + " " + county + " " + stateNum + "-" + fips;
		}
	}

	private static Map<String, String> mapOfEntityToFipsCode = new HashMap<String, String>();

	private static String getKey(String city, String state, String county) {
		return Util.removeStuffFromCityName(city).toLowerCase() + ";" + state + ";" + county;
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
		map.put(string.toLowerCase(), string2);

	}

	public static void main(String[] args) throws Exception {
		List<String> text = Util
				.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\FIPSCodes\\fedCodes.txt");
		text.remove(0);
		populateMap();
		for (String st : text) {
			String[] lines = st.split("\\|");
			FipsData data = new FipsData();
			String city = lines[1];
			city = Util.removeStuffFromCityName(city);
			if (city.contains("Statistical") || city.contains("County") || city.contains("Government")
					|| city.contains("(")) {
				continue;
			}
			while (city.contains(" of ")) {
				city = city.substring(city.indexOf(" of ") + " of ".length());
			}
			data.city = city;
			data.fips = lines[3];
			if (data.fips.equals("")) {
				continue;
			}
			data.stateNum = lines[7];
			String stateAcronym = lines[8];
			String county = lines[11];
			county = Util.removeIfExists(county, " (city)");
			if (county.contains(" (city)")) {
				
			}
			data.county = county + " County";
			if (!map.containsKey(stateAcronym.toLowerCase())) {
				continue;
			}
			data.stateName = map.get(stateAcronym.toLowerCase());
			String key = getKey(data.city, data.stateName, data.county);
			String value = stateAcronym + "-" + data.fips;
			mapOfEntityToFipsCode.put(key, value);
		}

		FIPSCodes fp = new FIPSCodes();
		fp.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String key = getKey(data.cityName, data.stateName, data.countyName);
		if (mapOfEntityToFipsCode.containsKey(key)) {
			String fipsCode = mapOfEntityToFipsCode.get(key);
			data.fipsCode = fipsCode;
		}
	}

}
