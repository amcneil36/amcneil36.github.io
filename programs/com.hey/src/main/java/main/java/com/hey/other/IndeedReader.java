package main.java.com.hey.other;

import java.awt.Desktop;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Element;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;
import main.java.com.hey.useful.not.needed.to.modify.much.CreateBigCsv;

// update code to show banned ip address count. maybe clear after 5 ip addresses
public class IndeedReader {

	private static Map<String, String> map = new HashMap<String, String>();
	private static Set<String> bannedIpAddresses = new HashSet<String>();
//	private static final Set<String> searchTermsSet = new HashSet<String>(
//			Arrays.asList("software engineer", "software development engineer", "software developer"));

	/////////////////////////////////////////////////////////////////////////
	private static final Set<String> searchTermsSet = new HashSet<String>(Arrays.asList("Computer Science"));

	private static boolean isCityValid(CityStats.Data data) {
		return Integer.valueOf(data.population) > 400000;
	}

	private static final String outputSt = "Data queried from an Indeed.com search of the text 'Computer Science' for all USA cities with a population greater than 400k. (0 mile search radius)";

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
		List<CityStats.Data> inputTemp = CreateBigCsv.readInput();
		List<CityStats.Data> input = new ArrayList<>();
		for (CityStats.Data data : inputTemp) {
			data.cityName = fixString(data.cityName);
			data.population = fixString(data.population);
			data.stateName = fixString(data.stateName);
			if (isCityValid(data)) {
				input.add(data);
			}
		}

		for (CityStats.Data data : input) {
			String url = createUrl(data.cityName, data.stateName);
			Desktop.getDesktop().browse(new URI(url));
		}
		
		writeTextToFile(input);
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

	private static void writeTextToFile(List<CityStats.Data> list) {

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(
					"city,state,population,number of job postings,job postings per 100k people,,,," + outputSt + "\n");
			for (CityStats.Data data : list) {

				sb.append(data.cityName).append(",").append(data.stateName).append(",")
						.append(data.population).append(",").append("").append(",")
						.append("").append("\n");
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
