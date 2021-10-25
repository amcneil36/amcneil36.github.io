package main.java.com.hey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetroCsvs {

	public static void main(String[] args) throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, List<CityStats.Data>> mapOfMetroNameToData = new HashMap<>();
		for (CityStats.Data data : dataList) {
			if (Integer.valueOf(data.metroPopulation) > 300000 && !data.metro.contains("None")) {
				if (!mapOfMetroNameToData.containsKey(data.metro)) {
					mapOfMetroNameToData.put(data.metro, new ArrayList<CityStats.Data>());
				}
				List<CityStats.Data> dataListFromMap = mapOfMetroNameToData.get(data.metro);
				dataListFromMap.add(data);
				mapOfMetroNameToData.put(data.metro, dataListFromMap);
			}
		}
		Set<String> keys = mapOfMetroNameToData.keySet();
		for (String key : keys) {
			List<CityStats.Data> metroDataList = mapOfMetroNameToData.get(key);
			String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\MetroStats\\Metros\\"
					+ metroDataList.get(0).metro + ".csv";
			CityStats.writeDataToPath(metroDataList, filePath, true);
		}
	}

}
