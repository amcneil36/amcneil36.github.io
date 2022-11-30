package main.java.com.hey.useful.not.needed.to.modify.much;

import java.util.ArrayList;
import java.util.List;

import main.java.com.hey.CityStats;
import main.java.com.hey.city.stats.NoopCityStats;

public class CreateBigCsv {

	public static void main(String[] args) throws Exception {
		processAllStates();
	}

	public static void processState(List<CityStats.Data> dataList, String stateName) throws Exception {
		NoopCityStats cityStats = new NoopCityStats();
		List<CityStats.Data> list2 = cityStats.readData(stateName);
		for (CityStats.Data data : list2) {
			dataList.add(data);
		}
	}

	// performance bottleneck because we are reading synchronously.
	// maybe there is an async way to do this? like the UpdateEverything one
	public static List<CityStats.Data> readInput() throws Exception {
		List<CityStats.Data> dataList = new ArrayList<>();
		processState(dataList, "Alabama");
		processState(dataList, "Alaska");
		processState(dataList, "Arizona");
		processState(dataList, "Arkansas");
		processState(dataList, "California");
		processState(dataList, "Colorado");
		processState(dataList, "Connecticut");
		processState(dataList, "Delaware");
		processState(dataList, "Florida");
		processState(dataList, "Georgia");
		processState(dataList, "Hawaii");
		processState(dataList, "Idaho");
		processState(dataList, "Illinois");
		processState(dataList, "Indiana");
		processState(dataList, "Iowa");
		processState(dataList, "Kansas");
		processState(dataList, "Kentucky");
		processState(dataList, "Louisiana");
		processState(dataList, "Maine");
		processState(dataList, "Maryland");
		processState(dataList, "Massachusetts");
		processState(dataList, "Michigan");
		processState(dataList, "Minnesota");
		processState(dataList, "Mississippi");
		processState(dataList, "Missouri");
		processState(dataList, "Montana");
		processState(dataList, "Nebraska");
		processState(dataList, "Nevada");
		processState(dataList, "New Hampshire");
		processState(dataList, "New Jersey");
		processState(dataList, "New Mexico");
		processState(dataList, "New York");
		processState(dataList, "North Carolina");
		processState(dataList, "North Dakota");
		processState(dataList, "Ohio");
		processState(dataList, "Oklahoma");
		processState(dataList, "Oregon");
		processState(dataList, "Pennsylvania");
		processState(dataList, "Rhode Island");
		processState(dataList, "South Carolina");
		processState(dataList, "South Dakota");
		processState(dataList, "Tennessee");
		processState(dataList, "Texas");
		processState(dataList, "Utah");
		processState(dataList, "Vermont");
		processState(dataList, "Virginia");
		processState(dataList, "Washington");
		processState(dataList, "West Virginia");
		processState(dataList, "Wisconsin");
		processState(dataList, "Wyoming");
		processState(dataList, "District of Columbia");
		return dataList;
	}

	public static void processAllStates() throws Exception {
		NoopCityStats cityStats = new NoopCityStats();
		List<CityStats.Data> dataList = readInput();
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\CityStats.csv";
		cityStats.writeDataToPath(dataList, filePath, true);
	}

}
