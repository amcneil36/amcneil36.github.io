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
		for (Data data : dataList) {
			if (data.metro.equals("None")) {
				System.out.println("hi");
				continue;
			}
			String key = getKey(data);
			if (mapOfMetroToPopulation.containsKey(key)) {
				continue;
			}
			int population = getPopulationForMetro(key, data);
			if (population == -1) {
				badMetrosFound.add(getNewKey(data));
				badMetrosWithMetroNameOnly.add(data.metro);
			}
			mapOfMetroToPopulation.put(key, population);
		}
	}

	private static int getPopulationForMetro(String key, Data data) {
		String url = "https://www.bestplaces.net/metro/" + key;
		String text = Util.ReadTextFromPage(url);
		String originalText = text;
		if (!text.contains("s population is ")) {
			return -1;
		}
		text = text.substring(text.indexOf("s population is ") + "s population is ".length());
		if (!text.contains(" people")) {
			return -1;
		}
		text = text.substring(0, text.indexOf(" people"));
		text = text.replace(",", "");
		try {
			return Integer.valueOf(text);
		} catch (Exception ex) {
			System.out.println("nothing found for: " + url);
			System.out.println("original text: " + originalText);
			throw new RuntimeException("hi");
		}
	}

	private static String getKey(Data data) {
		return data.stateName.replace(" ", "_").toLowerCase() + "/" + data.metro.replace(" ", "_").toLowerCase();
	}
	
	/////////////////////

	public static String getNewKey(Data data) {
		return data.metro + "," + data.stateName;
	}

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

			boolean b = Integer.valueOf(data.population).equals(Integer.valueOf(data.metroPopulation));
			if (b && !badMetrosFound.contains(getNewKey(data))) {
				badMetrosFound.add(getNewKey(data));
				badMetrosWithMetroNameOnly.add(data.metro);
			}
		}
		for (Data data : dataList) {
			if (data.metro.equals("None") || badMetrosFound.contains(getNewKey(data))) {
				// System.out.println("hi");
				continue;
			}
			if (badMetrosWithMetroNameOnly.contains(data.metro)) {
				mapOfMetroNameToPopulation.put(data.metro, data.metroPopulation);
			}

		}
	}
	
	
	
	////////////////////// I have not ran this code since I fixed the bug that fixed the issue with the wrong metro
	// population for cities in a metro but not in the dominant state. so should test that this main works

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
		if (badMetrosFound.contains(getNewKey(data))) {
			data.metroPopulation = mapOfMetroNameToPopulation.get(data.metro);
			return;
		}

		String key = getKey(data);
		if (!mapOfMetroToPopulation.containsKey(key)) {
			System.out.println("key: " + key + " was not found.");
			return;
		}
		data.metroPopulation = String.valueOf(mapOfMetroToPopulation.get(key));

	}

}