package com.hey.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.io.File; // Import the File class
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jsoup.nodes.Element;

import com.hey.CityStats;
import com.hey.CreateBigCsv;
import com.hey.GenericDataReadAndWriter;
import com.hey.GenericDataReadAndWriter.Data;
import com.hey.Util;

// update code to show banned ip address count. maybe clear after 5 ip addresses
public class IndeedReader {

	private static Map<String, String> map = new HashMap<String, String>();
	private static Set<String> bannedIpAddresses = new HashSet<String>();
//	private static final Set<String> searchTermsSet = new HashSet<String>(
//			Arrays.asList("software engineer", "software development engineer", "software developer"));

	/////////////////////////////////////////////////////////////////////////
	private static final Set<String> searchTermsSet = new HashSet<String>(Arrays.asList("Computer Science"));

	private static boolean isCityValid(CityStats.Data data) {
		return Integer.valueOf(data.population) > 200000;
	}

	private static final String outputSt = "Data queried from an Indeed.com search of the text 'Computer Science' for all USA cities with a population greater than 200k. (0 mile search radius)";

	/////////////////////////////////////////////////////////////////////////
	private static final int NUM_MATCHES_TO_LOOK_FOR = 3;

	private static class Data2 {
		CityStats.Data data;
		int numPostings;
		double postingsper100k;
	}

	// format csv to remove commas

	private static String fixString(String st) {
		if (st.charAt(0) == ' ') {
			return st.substring(1, st.length()).replace("\"", "");
		}
		return st.replace("\"", "");
	}

	public static void main(String[] args) throws Exception {
		populateMap();
		Scanner scan = new Scanner(System.in);
		long startTime = System.currentTimeMillis();
		int numQueries = 0;
		List<CityStats.Data> inputTemp = CreateBigCsv.readInput();
		List<CityStats.Data> input = new ArrayList<>();
		int numCitiesComplete = 0;
		for (CityStats.Data data : inputTemp) {
			data.cityName = fixString(data.cityName);
			data.population = fixString(data.population);
			data.stateName = fixString(data.stateName);
			if (isCityValid(data)) {
				input.add(data);
			}
		}

		int numCities = input.size();
		List<Data2> data2List = new ArrayList<>();
		for (CityStats.Data data : input) {
			String url = createUrl(data.cityName, data.stateName);
			Data2 data2 = new Data2();
			data2.data = data;
			;
			Map<Integer, Integer> mapOfNumPostingsToNumOccurrences = new HashMap<Integer, Integer>();
			data2.numPostings = -1;
			while (true) {
				int numPostingsTemp = 2;// extractJobPostingsFromUrl(url);
				numQueries++;
				if (numPostingsTemp == -1) {
					if (bannedIpAddresses.size() > 5) {
						bannedIpAddresses.clear();
					}
					bannedIpAddresses.add(getIpAddress());
					System.out.println("------------------------");
					System.out.println("found us on: " + data.cityName);
					System.out.println("num banned ip addresses: " + bannedIpAddresses.size());
					System.out.println("num queries ran: " + numQueries);
					long numMillisPassed = System.currentTimeMillis() - startTime;
					long numSeconds = numMillisPassed / 1000;
					System.out.println("num seconds passed between first query and last: " + numSeconds);
					System.out.println("switch ip addresses");
					int numSleeps = 0;
					boolean ipAddressChanged = true;
					while (bannedIpAddresses.contains(getIpAddress())) {
						TimeUnit.SECONDS.sleep(20);
						numSleeps++;
						if (numSleeps > 95) {
							System.out.println("have slept many times. automatically resuming.");
							ipAddressChanged = false;
							break;
						}
					}
					if (ipAddressChanged) {
						System.out.println("ip address changed. resuming");
					}
					waitForInternet();
					startTime = System.currentTimeMillis();
					numQueries = 0;
					System.out.println("------------------------");
					continue;
				}
				if (!mapOfNumPostingsToNumOccurrences.containsKey(numPostingsTemp)) {
					mapOfNumPostingsToNumOccurrences.put(numPostingsTemp, 1);
					continue;
				}
				Integer numOccurrences = mapOfNumPostingsToNumOccurrences.get(numPostingsTemp);
				if (numOccurrences == NUM_MATCHES_TO_LOOK_FOR - 1) {
					data2.numPostings = numPostingsTemp;
					break;
				} else {
					numOccurrences++;
					mapOfNumPostingsToNumOccurrences.put(numPostingsTemp, numOccurrences);
				}
			}

			double postingsper100k = data2.numPostings * 100000 / Integer.valueOf(data.population);
			data2.postingsper100k = Math.round(postingsper100k * 100.0) / 100.0;
			numCitiesComplete++;
			System.out.println("(" + numCitiesComplete + "/" + numCities + "); " + "city: " + data.cityName + "; "
					+ "num postings: " + data2.numPostings + "; " + "postings per 100k: " + data2.postingsper100k
					+ " url: " + url);
			data2List.add(data2);
		}
		scan.close();
		writeTextToFile(data2List);

	}

	static void waitForInternet() {
		while (!isInternetWorking()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}

	static boolean isInternetWorking() {
		try {
			Util.ReadTextFromPage("https://www.google.com");
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private static String getIpAddress() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			return ip.getHostAddress();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String createUrl(String cityName, String stateName) {
		String url = "https://www.indeed.com/jobs?q=";

		int idx = 0;
		int length = searchTermsSet.size();
		for (String s : searchTermsSet) {
			url += "%22" + s.replace(" ", "+") + "%22";
			idx++;
			if (idx < length) {
				url += "+OR+";
			}
		}

		url += "&l=" + cityName.replace(" ", "+") + "%2C+" + map.get(stateName) + "&radius=0";
		return url;
	}

	static Comparator<Data2> c1 = new Comparator<Data2>() {

		public int compare(Data2 o1, Data2 o2) {
			if (o1.postingsper100k > o2.postingsper100k) {
				return -1;
			} else if (o1.postingsper100k < o2.postingsper100k) {
				return 1;
			} else {
				return 0;
			}
		}

	};

	private static void writeTextToFile(List<Data2> list) {

		Collections.sort(list, c1);
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(
					"city,state,population,number of job postings,job postings per 100k people,,,," + outputSt + "\n");
			for (Data2 data : list) {

				sb.append(data.data.cityName).append(",").append(data.data.stateName).append(",")
						.append(data.data.population).append(",").append(data.numPostings).append(",")
						.append(data.postingsper100k).append("\n");
			}

			String outputTitle = "output_";
			for (String s : searchTermsSet) {
				outputTitle += s + "_";
			}
			outputTitle += System.currentTimeMillis() + ".csv";
			FileWriter myWriter = new FileWriter(outputTitle);
			myWriter.write(sb.toString());
			myWriter.close();
			System.out.println("wrote to file " + outputTitle);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static int extractJobPostingsFromUrl(String url) {
		Element element = null;
		Element element2 = null;
		try {
			element = Util.RetrieveHtmlcodeFromPage(url);
			if (element.text().length() == 0) {
				// System.out.println("no text for url: " + url);
				return -1;
			}
			Element element3 = element.getElementById("original_radius_result");
			if (element3 != null) {
				// System.out.println("url found no jobs: " + url);
				// System.out.println(element3.text());
				return 0;
			}
			element2 = element.getElementById("searchCountPages");
			if (element2 == null) {
				return 0;
			}
			String txt = element2.text();
			int begIdx = "Page 1 of ".length();
			int endIdx = txt.indexOf(" ", begIdx);
			txt = txt.substring(begIdx, endIdx);
			txt = txt.replace(",", "");
			return Integer.valueOf(txt);
		} catch (Exception ex) {
			System.out.println("exception at url: " + url);
			if (element != null) {
				System.out.println("element1 text: " + element.text());
			}
			if (element2 != null) {
				System.out.println("element2 text: " + element2.text());
			}
			return -1;
		}
	}

	private static void populateMap() {
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
	}

	private static void fillMapWithItem(String string, String string2) {
		map.put(string2, string);

	}
}
