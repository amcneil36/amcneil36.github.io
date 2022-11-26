package main.java.com.hey.city.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class LandArea extends CityStats {

	private static Map<String, String> map = new HashMap<String, String>();

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

	static Map<String, List<Double>> mapOfNameToLandArea = new HashMap<>();

	private static String getKey(String cityName, String stateName) {
		return cityName + ", " + stateName;
	}
	
	private static String removeIfExists(String text, String st) {
		if (text.contains(st)) {
			text = text.substring(0, text.indexOf(st));
		}
		return text;
	}

	public static void main(String[] args) throws Exception {
		// https://www.census.gov/geographies/reference-files/time-series/geo/gazetteer-files.2020.html
		List<String> text = Util.readTextFromFile(
				"C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\FIPSCodes\\2020_Gaz_place_national.txt");
		text.remove(0);
		populateMap();
		for (String st : text) {
			String[] lines = st.split("\t");
			String landArea = lines[8];
			String city = lines[3];
			if (city.contains("Statistical") || city.contains("County") || city.contains("Government")
					|| city.contains("(")) {
				continue;
			}
			while (city.contains(" of ")) {
				city = city.substring(city.indexOf(" of ") + " of ".length());
			}
			city = removeStuffFromCityName(city);
			String stateAcronym = lines[0];
			String stateName = map.get(stateAcronym.toLowerCase());
			if (!map.containsKey(stateAcronym.toLowerCase())) {
				continue;
			}
			String key = getKey(city, stateName);
			//System.out.println(key);
			if (city.equals("")) {
			//	System.out.println(st);
			}
			List<Double> list = mapOfNameToLandArea.getOrDefault(key, new ArrayList<>());
			list.add(Double.valueOf(landArea));
			mapOfNameToLandArea.put(key, list);
		}
		
		LandArea la = new LandArea();
		la.processAllStates();
	}
	
	private static String removeStuffFromCityName(String city) {
		city = removeIfExists(city, " Census Designated Place");
		city = removeIfExists(city, " borough");
		city = removeIfExists(city, " CDP");
		city = removeIfExists(city, " estates");
		city = removeIfExists(city, " city");
		city = removeIfExists(city, " village");
		city = removeIfExists(city, " town");
		city = removeIfExists(city, " (");
		return city;
	}

	int numMisses = 0;
	int numMatches = 0;
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String city = data.cityName;
		city = removeStuffFromCityName(city);
		String key = getKey(city, data.stateName);
		if (!mapOfNameToLandArea.containsKey(key)) {
			System.out.println(key + "; num misses is now " + ++numMisses);
		}
		else {
			//System.out.println("found! num matches: " + ++numMatches);
		}

	}
	
	@Override
	public boolean shouldWriteData() {
		return false;
	}

}
