package main.java.com.hey.useful.not.needed.to.modify.much;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.com.hey.CityStats;
import main.java.com.hey.city.stats.NoopCityStats;

public class CountyCsvs {

	public static String getCountyKey(CityStats.Data data) {
		return data.countyName + "," + data.stateName + "," + data.percentDemocrat;
	}

	public static void main(String[] args) throws Exception {
		NoopCityStats cityStats = new NoopCityStats();
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, List<CityStats.Data>> mapOfCountyNameToData = new HashMap<>();
		for (CityStats.Data data : dataList) {
			String countyKey = getCountyKey(data);
			if (!data.metro.contains("None") && Integer.valueOf(data.metroPopulation) > 100000) {
				if (!mapOfCountyNameToData.containsKey(countyKey)) {
					mapOfCountyNameToData.put(countyKey, new ArrayList<CityStats.Data>());
				}
				List<CityStats.Data> dataListFromMap = mapOfCountyNameToData.get(countyKey);
				dataListFromMap.add(data);
				mapOfCountyNameToData.put(countyKey, dataListFromMap);
			}
		}
		Set<String> keys = mapOfCountyNameToData.keySet();
		for (String key : keys) {
			List<CityStats.Data> countyDataList = mapOfCountyNameToData.get(key);
			String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CountyStats\\Counties\\"
					+ countyDataList.get(0).countyName + " " + countyDataList.get(0).stateName + ".csv";
			cityStats.writeDataToPath(countyDataList, filePath, true);
		}
	}
}
