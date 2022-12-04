package main.java.com.hey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import main.java.com.hey.CityStats.Data;
import main.java.com.hey.useful.not.needed.to.modify.much.Pojo;

public class Util {

	public static boolean debug = false;
	
	public static class Coordinate extends Pojo {
		public double latitude;
		public double longitude;
	}
	// public <U extends Number> void inspect(U u){
	public static <T extends Coordinate> Optional<T> findBestCoordinate(List<T> coordinates, Data data, double maxDistanceAllowed){
		Optional<T> bestCoordinate = Optional.empty();
		double minDistance = Double.MAX_VALUE;
		if (data.latitude.equals("N/A")) {
			return Optional.empty();
		}
		double dataLatitude = Double.valueOf(data.latitude);
		double dataLongitude = Double.valueOf(data.longitude);
		for (T coordinate : coordinates) {
			double distance = Util.milesBetweenCoordinates(coordinate.latitude, coordinate.longitude,
					dataLatitude, dataLongitude);
			if (distance < minDistance && distance < maxDistanceAllowed) {
				bestCoordinate = Optional.of(coordinate);
				minDistance = distance;
			}
		}
		return bestCoordinate;
	}
	
	public static double milesBetweenCoordinates(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		double rad = 6371;
		double c = 2 * Math.asin(Math.sqrt(a));
		return rad * c * 0.621371;
	}
	
	public static String removeStuffFromCityName(String city) {
		city = city.toLowerCase();
		city = removeIfExists(city, " census designated place");
		city = removeIfExists(city, " borough");
		city = removeIfExists(city, " cdp");
		city = removeIfExists(city, " estates");
		// Charter Township of 
		if (city.startsWith("charter township of ")) {
			city = city.replace("charter township of ", "");
		}
		if (city.startsWith("charter of ")) {
			city = city.replace("charter of ", "");
		}
		if (city.startsWith("city of ")) {
			city = city.replace("city of ", "");
		}
		if (city.startsWith("township of ")) {
			city = city.replace("township of ", "");
		}
		city = removeIfExists(city, " city");
		if (city.startsWith("village of ")) {
			city = city.replace("village of ", "");
		}
		city = removeIfExists(city, " village");
		city = removeIfExists(city, " town");
		city = removeIfExists(city, " (");
		city = removeIfExists(city, " County");
		city = removeIfExists(city, " charter");
		city = removeIfExists(city, " afb");
		city = removeIfExists(city, " air force base");
		city = city.replace("t.", "aint"); //saint
		return city;
	}
	
	public static String removeIfExists(String text, String st) {
		if (text.contains(st)) {
			text = text.substring(0, text.indexOf(st));
		}
		return text;
	}

	public static String getCityUniqueId(Data data) {
		return data.cityName + "," + data.stateName + "," + data.countyName + "," + data.metro + ";";

	}
	
	public static double roundTwoDecimalPlaces(double num) {
		return Math.round(((num*100.0))) / 100.0;
	}
	
	public static int getIntFromDouble(double num) {
		return (int) Math.round(num);
	}

	public static String minToString(int minRemaining) {
		String st = "";
		int hrRemaining = (int) Math.floor(minRemaining / 60);
		minRemaining = minRemaining % 60;
		if (hrRemaining > 0) {
			st += hrRemaining + "hr ";
		}
		st += minRemaining + "min";
		return st;
	}

	public static void WriteTextToFile(String stringToWrite, String stateName) {
		try {
			// C:\Users\anmcneil\Desktop\myproject
			// FileWriter myWriter = new FileWriter("datapoints.js");
			String filePath = "C:\\Users\\anmcneil\\Desktop\\myproject\\States\\" + stateName + ".js";
			FileWriter myWriter = new FileWriter(filePath);
			myWriter.write(stringToWrite);
			myWriter.close();
			System.out.println(stateName + " has been successfully written to===================================");
		} catch (IOException e) {
			log("An error occurred trying to write to file for: " + stateName);
			e.printStackTrace();
		}

	}

	public static String ReadTextFromPage(String url) {
		// System.out.println(url);
		return JsoupStuffNoRetry(url);
	}

	private static String JsoupStuffNoRetry(String url) {
		Connection conn = Jsoup.connect(url);
		try {
			Document doc = conn.get();
			String text = doc.body().text();
			return text;
		} catch (IOException ex) {
			System.out.println(ex);
			return "";
		}
	}

	public static Element RetrieveHtmlcodeFromPage(String url) {
		Connection conn = Jsoup.connect(url);
		try {
			Document doc = conn.userAgent(
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36")
					.referrer("http://www.google.com").get();
			Element body = doc.body();
			return body;
		} catch (IOException ex) {
			// System.out.println("oh no!");
			// ex.printStackTrace();
			String stacktrace = ExceptionUtils.getStackTrace(ex);
			if (stacktrace.contains("Status=403")) {
				throw new SecurityException("you are banned from the website");
			}
			log("jsoup couldn't connect to: " + url);
			throw new RuntimeException(url, ex);
		}
	}

	public static Element getElement(String url) throws Exception {
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();
		return doc.body();
	}

	public static void log(String str) {
		if (debug) {
			System.out.println(str);
		}
	}

	public static String ReadHtmlCode(String urlParam) {
		StringBuilder sb = new StringBuilder("");
		try {

			URL url = new URL(urlParam);

			// read text returned by server
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				// System.out.println(line);
				sb.append(line);
				sb.append("\n");
			}
			in.close();

		} catch (MalformedURLException e) {
			log("Malformed URL: " + e.getMessage());
			log("failed to read " + urlParam);
			throw new RuntimeException();
		} catch (IOException e) {
			log("I/O Error: " + e.getMessage());
			log("failed to read " + urlParam);
			e.printStackTrace();
			throw new RuntimeException();
		}
		return sb.toString();
	}

	private static Map<String, String> map = new HashMap<String, String>();

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
		fillMapWithItem("dc", "District of Columbia");
	}

	private static void fillMapWithItem(String string, String string2) {
		map.put(string2.toLowerCase(), string.toLowerCase());

	}

	public static String getStateAbbreviation(String stateName) {
		if (map.isEmpty()) {
			populateMap();
		}
		return map.get(stateName.toLowerCase());
	}

	public static List<String> readTextFromFile(String filePath) throws Exception {
		List<String> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
		String line;
		while ((line = br.readLine()) != null) {
			list.add(line);
		}
		br.close();
		return list;
	}

	public static void writeTextToFile(String filePath, String textToWrite) throws Exception {
		FileWriter myWriter = new FileWriter(filePath);
		myWriter.write(textToWrite);
		myWriter.close();
	}

	private static Map<String, LocalDate> mapOfKeyToDate = new HashMap<>();

	public static Map<String, LocalDate> getMapOfKeyToDate() throws Exception {
		if (mapOfKeyToDate.size() == 0) {
			List<String> lines = readTextFromFile(
					"C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\internal\\DateHousingDataLastUpdated.txt");
			for (String line : lines) {
				String[] arr = line.split(";");
				mapOfKeyToDate.put(arr[0] + ";", LocalDate.parse(arr[1]));
			}
		}
		return mapOfKeyToDate;
	}

	public static long getDaysSinceLastUpdated(Data data) throws Exception {
		Map<String, LocalDate> map = getMapOfKeyToDate();
		LocalDate now = LocalDate.now();
		String uniqueId = getCityUniqueId(data);
		if (!map.containsKey(uniqueId)) {
			System.out.println(uniqueId);
			System.out.println(map.size());
			System.out.println("huh?");
		}
		LocalDate prev = map.get(uniqueId);
		long daysBetween = ChronoUnit.DAYS.between(prev, now);
		return daysBetween;
	}

	public static void writeMapOfDateUpdatedToFile(Map<String, LocalDate> map2) throws Exception {
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\internal\\DateHousingDataLastUpdated.txt";
		StringBuilder sb = new StringBuilder();
		Set<String> keys = map2.keySet();
		for (String key : keys) {
			sb.append(key);
			LocalDate localDate = map2.get(key);
			sb.append(localDate.toString());
			sb.append("\n");
		}
		sb.setLength(sb.length() - 1);
		Util.writeTextToFile(filePath, sb.toString());

	}

}
