package com.hey;

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

// update code to show banned ip address count. maybe clear after 5 ip addresses
public class IndeedReader {

	private static Map<String, String> map = new HashMap<String, String>();
	private static Set<String> bannedIpAddresses = new HashSet<String>();
	private static final Set<String> searchTermsSet = new HashSet<String>(
			Arrays.asList("software engineer", "software development engineer", "software developer"));

	private static final int NUM_MATCHES_TO_LOOK_FOR = 3;

	private static class Data {
		String cityName;
		String stateName;
		double population;
		int numPostings;
		double postingsper100k;
	}

	// format csv to remove commas
	public static void main(String[] args) throws Exception {
		populateMap();
		Scanner scan = new Scanner(System.in);
		long startTime = System.currentTimeMillis();
		int numQueries = 0;
		List<Data> input = readInput();
		int numCities = input.size();
		int numCitiesComplete = 0;
		for (Data data : input) {
			String url = createUrl(data.cityName, data.stateName);
			Map<Integer, Integer> mapOfNumPostingsToNumOccurrences = new HashMap<Integer, Integer>();
			data.numPostings = -1;
			while (true) {
				int numPostingsTemp = extractJobPostingsFromUrl(url);
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
					data.numPostings = numPostingsTemp;
					break;
				} else {
					numOccurrences++;
					mapOfNumPostingsToNumOccurrences.put(numPostingsTemp, numOccurrences);
				}
			}

			double postingsper100k = data.numPostings * 100000 / data.population;
			data.postingsper100k = Math.round(postingsper100k * 100.0) / 100.0;
			numCitiesComplete++;
			System.out.println("(" + numCitiesComplete + "/" + numCities + "); " + "city: " + data.cityName + "; "
					+ "num postings: " + data.numPostings + "; " + "postings per 100k: " + data.postingsper100k
					+ " url: " + url);
		}
		scan.close();
		writeTextToFile(input);

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
			SperlingReader.ReadTextFromPage("https://www.google.com");
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

	private static List<Data> readInput() {
		try {
			File myObj = new File("input.csv");
			Scanner myReader = new Scanner(myObj);
			myReader.nextLine();
			List<Data> output = new ArrayList<Data>();
			while (myReader.hasNextLine()) {
				String line = myReader.nextLine();
				String[] arr = line.split(",");
				Data data = new Data();
				data.cityName = arr[0];
				data.stateName = arr[1];
				data.population = Integer.valueOf(arr[3]);
				output.add(data);
			}
			myReader.close();
			return output;
		} catch (Exception ex) {
			throw new RuntimeException("issue in readInput");
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

	static Comparator<Data> c1 = new Comparator<Data>() {

		public int compare(Data o1, Data o2) {
			if (o1.postingsper100k > o2.postingsper100k) {
				return -1;
			} else if (o1.postingsper100k < o2.postingsper100k) {
				return 1;
			} else {
				return 0;
			}
		}

	};

	private static void writeTextToFile(List<Data> list) {

		Collections.sort(list, c1);
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("city,state,population,number of job postings,job postings per 100k people\n");
			for (Data data : list) {

				sb.append(data.cityName).append(",").append(data.stateName).append(",").append(data.population)
						.append(",").append(data.numPostings).append(",").append(data.postingsper100k).append("\n");
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
			element = SperlingReader.RetrieveHtmlcodeFromPage(url);
			if (element.text().length() == 0) {
				// System.out.println("no text for url: " + url);
				return -1;
			}
			Element element3 = element.getElementById("original_radius_result");
			if (element3 != null) {
		//		System.out.println("url found no jobs: " + url);
	//			System.out.println(element3.text());
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
