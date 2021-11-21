package main.java.com.hey;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.java.com.hey.CityStats.Data;

public class RealtorCountyReader {

	private static Map<String, String> map = new HashMap<String, String>();

	static class HouseData {
		int homePrice;
		int costPerSqFt;
		int medianSquareFootage;
	}

	public static void main(String[] args) throws Exception {
		populateMap();
		String filePath = "realtorCountyCopyPaste.txt";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		String line = "";
		while (myReader.hasNextLine() && !line.startsWith("All Neighborhoods in ")) {
			line = myReader.nextLine();
		}
		String county = line.substring("All Neighborhoods in ".length(), line.indexOf(", ")).toLowerCase();
		String state = map.get(line.substring(line.indexOf(", ") + 2).toLowerCase());
		state = state.substring(0, 1).toUpperCase() + state.substring(1);
		Map<String, HouseData> houseDataMap = GetHouseDataMap(county, myReader, line);

		///////////
		writeOutput(houseDataMap, county, state);

		///////////

	}

	private static void writeOutput(Map<String, HouseData> houseDataMap, String county, String state) throws Exception {
		List<Data> dataList = CityStats.readData(state);
		Map<String, LocalDate> map = Util.getMapOfKeyToDate();
		boolean isCityFound = false;
		for (Data data : dataList) {
			String key = getMapKey(data.cityName, county);
			if (houseDataMap.containsKey(key)) {
				HouseData houseData = houseDataMap.get(key);
				data.medianHomePrice = String.valueOf(houseData.homePrice);
				data.homeSquareFeet = String.valueOf(houseData.medianSquareFootage);
				data.costPerSquareFoot = String.valueOf(houseData.costPerSqFt);
				map.put(Util.getCityUniqueId(data), LocalDate.now());
				isCityFound = true;
			}

		}
		if (isCityFound) {
			CityStats.writeData(dataList, state, true);
			Util.writeMapOfDateUpdatedToFile(map);
		} else {
			System.out.println("no data found!");
		}

	}

	private static Map<String, HouseData> GetHouseDataMap(String county, Scanner myReader, String line) {
		Map<String, HouseData> houseDataMap = new HashMap<String, HouseData>();
		while (!"For Rent".equals(line)) {
			line = myReader.nextLine();
		}
		while (myReader.hasNextLine()) {
			String cityName = myReader.nextLine().toLowerCase();
			if (cityName.startsWith("©") || cityName.startsWith("get the app")) {
				break;
			}
			String medianPrice = myReader.nextLine();
			String costPerSqFt = myReader.nextLine();
			myReader.nextLine();
			myReader.nextLine();
			if (!costPerSqFt.startsWith("$") || !medianPrice.startsWith("$")) {
				continue;
			}
			HouseData houseData = new HouseData();
			houseData.homePrice = getIntFromString(medianPrice);
			houseData.costPerSqFt = getIntFromString(costPerSqFt);
			houseData.medianSquareFootage = houseData.homePrice / houseData.costPerSqFt;
			String mapKey = getMapKey(cityName, county);
			houseDataMap.put(mapKey, houseData);
		}
		return houseDataMap;
	}

	private static String getMapKey(String cityName, String county) {
		return cityName.toLowerCase() + ", " + county.toLowerCase();
	}

	public static int getIntFromString(String st) {
		char[] chars = st.toCharArray();
		boolean wasDecimalSeen = false;
		String num = "";
		for (char c : chars) {
			if (c == '$') {
				continue;
			} else if (Character.isDigit(c)) {
				num += c;
			} else if (c == '.') {
				wasDecimalSeen = true;
			} else if (c == 'K') {
				if (wasDecimalSeen) {
					num += "00";
				} else {
					num += "000";
				}
			} else if (c == 'M') {
				if (wasDecimalSeen) {
					num += "00000";
				} else {
					num += "000000";
				}
			} else {
				throw new RuntimeException("failed to get int from this string: " + st);
			}
		}
		return Integer.valueOf(num);
	}

	private static void populateMap() {
		map = new HashMap<String, String>();
		fillMapWithItem("al", "Alabama");
		fillMapWithItem("ak", "Alaska");
		fillMapWithItem("az", "Arizona");
		fillMapWithItem("ar", "Arkansas");
		fillMapWithItem("ca", "California"); // overnight
		fillMapWithItem("co", "Colorado");
		fillMapWithItem("ct", "Connecticut");
		fillMapWithItem("de", "Delaware");
		fillMapWithItem("fl", "Florida"); // success
		fillMapWithItem("ga", "Georgia"); // success
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
		map.put(string.toLowerCase(), string2.toLowerCase());

	}

}
