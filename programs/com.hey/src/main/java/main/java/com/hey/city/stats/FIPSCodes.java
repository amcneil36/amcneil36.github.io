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
		return Util.removeStuffFromCityName(city) + ";" + state + ";" + county;
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
			if (city.contains("Statistical") || city.contains("County") || city.contains("Government")
					|| city.contains("(")) {
				continue;
			}
			while (city.contains(" of ")) {
				city = city.substring(city.indexOf(" of ") + " of ".length());
			}
			if (city.contains(" Census Designated Place")) {
				city = city.substring(0, city.indexOf(" Census Designated Place"));
			}
			data.city = city;
			data.fips = lines[3];
			if (data.fips.equals("")) {
				continue;
			}
			data.stateNum = lines[7];
			String stateAcronym = lines[8];
			data.county = lines[11] + " County";
			if (!map.containsKey(stateAcronym.toLowerCase())) {
				continue;
			}
			data.stateName = map.get(stateAcronym.toLowerCase());
			String key = getKey(data.city, data.stateName, data.county);
			String value = stateAcronym + "-" + data.fips;
			if (data.city.equals("Manchester")) {
				System.out.println(key);
			}
			mapOfEntityToFipsCode.put(key, value);
		}

		FIPSCodes fp = new FIPSCodes();
		fp.processAllStates();

	}
	
	int numMisses = 0;

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		// 868243|Manchester|Populated Place|45140|P1|0310|330310011|33|NH|1|011|Hillsborough|42.9956397|-71.4547891|08/27/1980|02/06/2018
		// Manchester;New Hampshire;Hillsborough County
		// Manchester;New Hampshire;Hillsborough
		String key = getKey(data.cityName, data.stateName, data.countyName);
		if (mapOfEntityToFipsCode.containsKey(key)) {
			String fipsCode = mapOfEntityToFipsCode.get(key);
			data.fipsCode = fipsCode;
		}
		else {
			System.out.println("num misses: " + ++numMisses + "; " + key);
		}
	}
	
	@Override
	public boolean shouldWriteData() {
		return false;
	}

}