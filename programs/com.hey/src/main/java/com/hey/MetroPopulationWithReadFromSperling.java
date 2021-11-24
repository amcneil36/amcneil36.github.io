package main.java.com.hey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetroPopulationWithReadFromSperling extends CityStats {

	static Map<String, Integer> mapOfMetroToPopulation = new HashMap<>();

	public static String getNewKey(Data data) {
		return data.metro + "," + data.stateName;
	}

	static Set<String> badMetrosFound = new HashSet<>();
	static Set<String> badMetrosWithMetroNameOnly = new HashSet<>();
	static Map<String, String> mapOfMetroNameToPopulation = new HashMap<>();

	private static void fillMap() throws Exception {
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

	public static void main(String[] args) throws Exception {
		fillMap();
		MetroPopulationWithReadFromSperling s = new MetroPopulationWithReadFromSperling();
		s.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		if (badMetrosFound.contains(getNewKey(data))) {
			data.metroPopulation = mapOfMetroNameToPopulation.get(data.metro);
		}
	}

}
