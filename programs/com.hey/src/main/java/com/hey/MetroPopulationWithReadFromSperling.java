package main.java.com.hey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			mapOfMetroToPopulation.put(key, population);
		}
	}

	private static int getPopulationForMetro(String key, Data data) {
		String url = "https://www.bestplaces.net/metro/" + key;
		String text = Util.ReadTextFromPage(url);
		String originalText = text;
		if (!text.contains("s population is ")) {
			return Integer.valueOf(data.population);
		}
		text = text.substring(text.indexOf("s population is ") + "s population is ".length());
		if (!text.contains(" people")) {
			return Integer.valueOf(data.population);
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

	public static void main(String[] args) throws Exception {
		fillMap();
		MetroPopulationWithReadFromSperling s = new MetroPopulationWithReadFromSperling();
		s.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		if (data.metro.equals("None")) {
			data.metroPopulation = "N/A";
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
