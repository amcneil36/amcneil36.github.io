package main.java.com.hey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.hey.CityStats.Data;

public class MetroPopulationWithReadFromSperling extends CityStats {

	
	static Map<String, Integer> mapOfMetroToPopulation = new HashMap<>();
	// maybe write map values to a file? but multithreading might be an issue
	// multithreading shouldn't be an issue because fillMap is called first.
	// write to file after each add to the map. so if we crash, we don't lose our work
	private static void fillMap() throws Exception {
		List<Data> dataList = CreateBigCsv.readInput();
		for (Data data : dataList) {
			if (data.metro.equals("None")) {
				continue;
			}
			String key = getKey(data);
			if (mapOfMetroToPopulation.containsKey(key)) {
				continue;
			}
			else {
				System.out.println(key);
				mapOfMetroToPopulation.put(key, 0);
			}
		}
	}
	
	
	private static String getKey(Data data) {
		// TODO Auto-generated method stub
		return data.stateName.replace(" ", "_").toLowerCase() + data.metro.replace(" ", "_").toLowerCase();
	}


	public static void main(String[] args) throws Exception {
		fillMap();
		MetroPopulation s = new MetroPopulation();
		//s.processAllStates();

	}

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
