package main.java.com.hey.city.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.useful.not.needed.to.modify.much.CreateBigCsv;

public class MetroPopulationWithReadFromSperling extends CityStats {

	static Map<String, Integer> mapOfMetroToPopulation = new HashMap<>();

	// maybe write map values to a file? but multithreading might be an issue
	// multithreading shouldn't be an issue because fillMap is called first.
	// write to file after each add to the map. so if we crash, we don't lose our
	// work
	private static void fillMap() throws Exception {
		List<Data> dataList = CreateBigCsv.readInput();
		int size = dataList.size();
		int count = 0;
		Set<String> keysTried = new HashSet<>();
		for (Data data : dataList) {
			count++;
			if (count % 100 == 0) {
				// System.out.println("(" + count + "/" + size + ")");
			}
			// temporary
			if (!data.metroPopulation.equals("N/A")) {
				continue;
			}
			if (data.metro.equals("None")) {
				continue;
			}
			if (Integer.valueOf(data.population) < 10000) {
				continue;
			}
			String key = getKey(data);
			if (mapOfMetroToPopulation.containsKey(key) || keysTried.contains(key)) {
				continue;
			}
			int population = getPopulationForMetro(key);
			keysTried.add(key);
			if (population == -1) {
				badMetrosFound.add(getKey(data));
				badMetrosWithMetroNameOnly.add(data.metro);
			}
			mapOfMetroToPopulation.put(key, population);
		}
		for (String key : badMetrosFound) {
			System.out.println(key);
		}
	}

	private static int getPopulationForMetro(String key) {
		// System.out.println(key);
		String url = "https://www.bestplaces.net/metro/" + key;
		String text = Util.ReadTextFromPage(url);
		String originalText = text;
		if (!text.contains("s population is ")) {
			System.out.println(url);
			return -1;
		}
		text = text.substring(text.indexOf("s population is ") + "s population is ".length());
		if (!text.contains(" people")) {
			return -1;
		}
		text = text.substring(0, text.indexOf(" people"));
		text = text.replace(",", "");
		try {
			// System.out.println(key + ": " + text);
			return Integer.valueOf(text);
		} catch (Exception ex) {
			System.out.println("nothing found for: " + url);
			System.out.println("original text: " + originalText);
			throw new RuntimeException("hi");
		}
	}

	private static Map<String, String> mapOfBadKeyToGoodKey;
	static {
		mapOfBadKeyToGoodKey = new HashMap<>();
		mapOfBadKeyToGoodKey.put("iowa/omaha-council_bluffs", "nebraska/omaha-council_bluffs");
		mapOfBadKeyToGoodKey.put("new_jersey/new_york-newark-jersey_city", "new_york/new_york-newark-jersey_city");
		mapOfBadKeyToGoodKey.put("maryland/philadelphia-camden-wilmington", "pennsylvania/philadelphia-camden-wilmington");
		mapOfBadKeyToGoodKey.put("arkansas/memphis", "tennessee/memphis");
		mapOfBadKeyToGoodKey.put("illinois/st._louis", "missouri/st._louis");
		mapOfBadKeyToGoodKey.put("indiana/louisville", "kentucky/louisville");
		mapOfBadKeyToGoodKey.put("west_virginia/hagerstown-martinsburg", "maryland/hagerstown-martinsburg");
		mapOfBadKeyToGoodKey.put("kentucky/clarksville", "tennessee/clarksville");
		mapOfBadKeyToGoodKey.put("south_carolina/augusta-richmond_county", "georgia/augusta-richmond_county");
		mapOfBadKeyToGoodKey.put("illinois/davenport-moline-rock_island", "iowa/davenport-moline-rock_island");
		mapOfBadKeyToGoodKey.put("massachusetts/providence-warwick", "rhode_island/providence-warwick");
		mapOfBadKeyToGoodKey.put("washington/portland-vancouver-hillsboro", "oregon/portland-vancouver-hillsboro");
		mapOfBadKeyToGoodKey.put("south_carolina/charlotte-concord-gastonia", "north_carolina/charlotte-concord-gastonia");
		mapOfBadKeyToGoodKey.put("kentucky/evansville", "indiana/evansville");
		mapOfBadKeyToGoodKey.put("nebraska/sioux_city", "iowa/sioux_city");
		mapOfBadKeyToGoodKey.put("wisconsin/minneapolis-st._paul-bloomington", "minnesota/minneapolis-st._paul-bloomington");
		mapOfBadKeyToGoodKey.put("north_carolina/myrtle_beach-conway-north_myrtle_beach", "south_carolina/myrtle_beach-conway-north_myrtle_beach");
		mapOfBadKeyToGoodKey.put("wisconsin/duluth", "minnesota/duluth");
		mapOfBadKeyToGoodKey.put("kansas/kansas_city", "missouri/kansas_city");
		mapOfBadKeyToGoodKey.put("kentucky/cincinnati", "ohio/cincinnati");
		mapOfBadKeyToGoodKey.put("maryland/washington-arlington-alexandria", "district_of_columbia/washington-arlington-alexandria");
		mapOfBadKeyToGoodKey.put("pennsylvania/youngstown-warren-boardman", "ohio/youngstown-warren-boardman");
		mapOfBadKeyToGoodKey.put("wisconsin/chicago-naperville-elgin", "illinois/chicago-naperville-elgin");
		mapOfBadKeyToGoodKey.put("kentucky/huntington-ashland", "west_virginia/huntington-ashland");
		mapOfBadKeyToGoodKey.put("minnesota/fargo", "north_dakota/fargo");
		mapOfBadKeyToGoodKey.put("ohio/weirton-steubenville", "west_virginia/weirton-steubenville");
		mapOfBadKeyToGoodKey.put("connecticut/worcester", "massachusetts/worcester");
		mapOfBadKeyToGoodKey.put("new_jersey/allentown-bethlehem-easton", "pennsylvania/allentown-bethlehem-easton");
		mapOfBadKeyToGoodKey.put("new_jersey/philadelphia-camden-wilmington", "pennsylvania/philadelphia-camden-wilmington");
		mapOfBadKeyToGoodKey.put("virginia/kingsport-bristol-bristol", "tennessee/kingsport-bristol-bristol");
		mapOfBadKeyToGoodKey.put("pennsylvania/new_york-newark-jersey_city", "new_york/new_york-newark-jersey_city");
		mapOfBadKeyToGoodKey.put("virginia/washington-arlington-alexandria", "district_of_columbia/washington-arlington-alexandria");
		mapOfBadKeyToGoodKey.put("ohio/huntington-ashland", "west_virginia/huntington-ashland");
		mapOfBadKeyToGoodKey.put("ohio/wheeling", "west_virginia/wheeling");
		mapOfBadKeyToGoodKey.put("delaware/philadelphia-camden-wilmington", "pennsylvania/philadelphia-camden-wilmington");
		mapOfBadKeyToGoodKey.put("indiana/chicago-naperville-elgin", "illinois/chicago-naperville-elgin");
		mapOfBadKeyToGoodKey.put("new_hampshire/boston-cambridge-newton", "massachusetts/boston-cambridge-newton");
	}

	private static String getKey(Data data) {
		String key = data.stateName.replace(" ", "_").toLowerCase() + "/" + data.metro.replace(" ", "_").toLowerCase();
		if (mapOfBadKeyToGoodKey.containsKey(key)) {
			String value = mapOfBadKeyToGoodKey.get(key);
			if (value != "") {
				return value;
			}
			System.out.println("didnt find: " + key);
		}
		return key;
	}

	/////////////////////

//	public static String getNewKey(Data data) {
//		return data.metro + "," + data.stateName;
//	}

	static Set<String> badMetrosFound = new HashSet<>();
	static Set<String> badMetrosWithMetroNameOnly = new HashSet<>();
	static Map<String, String> mapOfMetroNameToPopulation = new HashMap<>();

	private static void fillOtherMap() throws Exception {
		List<Data> dataList = CreateBigCsv.readInput();
		for (Data data : dataList) {
			if (data.metro.equals("None")) {
				// System.out.println("hi");
				continue;
			}
			// if metro pop is equal to pop of the city
			if (!data.metroPopulation.equals("N/A")) {
				boolean b = Integer.valueOf(data.population).equals(Integer.valueOf(data.metroPopulation));
				if (b && !badMetrosFound.contains(getKey(data))) {
					badMetrosFound.add(getKey(data));
					badMetrosWithMetroNameOnly.add(data.metro);
				}
			}
		}
		for (Data data : dataList) {
			if (data.metro.equals("None") || badMetrosFound.contains(getKey(data))) {
				// System.out.println("hi");
				continue;
			}
			if (badMetrosWithMetroNameOnly.contains(data.metro)) {
				mapOfMetroNameToPopulation.put(data.metro, data.metroPopulation);
			}

		}
		// System.out.println(mapOfMetroNameToPopulation);
	}

	////////////////////// I have not ran this code since I fixed the bug that fixed
	////////////////////// the issue with the wrong metro
	// population for cities in a metro but not in the dominant state. so should
	////////////////////// test that this main works

	public static void main(String[] args) throws Exception {
		fillMap();
		fillOtherMap();
		MetroPopulationWithReadFromSperling s = new MetroPopulationWithReadFromSperling();
		s.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		if (data.metro.equals("None")) {
			data.metroPopulation = "N/A";
			return;
		}
		if (badMetrosFound.contains(getKey(data))) {
			// temporary comment
			// data.metroPopulation = mapOfMetroNameToPopulation.get(data.metro);
			return;
		}

		String key = getKey(data);
		if (!mapOfMetroToPopulation.containsKey(key)) {
			// System.out.println("key: " + key + " was not found.");
			return;
		}
		data.metroPopulation = String.valueOf(mapOfMetroToPopulation.get(key));
		if (data.metroPopulation.equals("null")) {
			System.out.println(data.metro + "; was null");
			data.metroPopulation = "N/A";
		}

	}

}