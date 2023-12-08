package main.java.com.hey.us.census;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import main.java.com.hey.Util;

public class ACSDataReader {

	private static final String YEAR = "2022";
	public static final String COUNTRY_SUFFIX = "&for=us:*";
	public static final String REGION_SUFFIX = "&for=region:*";
	public static final String STATE_SUFFIX = "&for=state:*";
	public static final String CSA_SUFFIX = "&for=combined%20statistical%20area:*";
	public static final String METRO_MICRO_SUFFIX = "&for=metropolitan%20statistical%20area/micropolitan%20statistical%20area:*";
	public static final String COUNTY_SUFFIX = "&for=county:*";
	public static final String SCHOOL_DISTRICT_SUFFIX = "&for=school%20district%20(unified):*";
	public static final String PLACE_SUFFIX = "&for=place:*";
	public static final String ZIP_CODE_SUFFIX = "&for=zip%20code%20tabulation%20area:*";
	public static String POPULATION = "B01003_001E(population)";
	public static String MEDIAN_HOUSEHOLD_INCOME = "B19013_001E(median_household_income)";
	public static String MEDIAN_HOUSEHOLD_INCOME_WHITE = "B19013A_001E(median household income white)";
	public static String MEDIAN_HOUSEHOLD_INCOME_BLACK = "B19013B_001E(median household income black)";
	public static String BACHELORS_DEGREE = "B15003_022E(number of people with a bachelor's degree)";
	public static String MASTERS_DEGREE = "B15003_023E(number of people with a master's degree)";
	public static String PROFESSIONAL_DEGREE = "B15003_024E(number of people with a professional degree)";
	public static String DOCTORATE_DEGREE = "B15003_025E(number of people with a doctorate degree)";
	public static String NUM_PEOPLE_OVER_25_EDUCATION = "B15003_001E(num people over 25)";

	public static class Result {
		public String city = "";
		public String state = "";
		public String fipsCode = "";
		public Map<String, String> results = new HashMap<>();

		@Override
		public String toString() {
			return city + ", " + state + " (" + fipsCode + "); results: " + results;
		}
	}

	// lets update code to add in the parenthesis stuff. first cols are always our
	// stuff. rest are not.
	public static List<Map<String, String>> getResultsReusable(String[] variables, String urlSuffix) throws Exception {
		String url = createUrlFromVariablesAndSuffix(variables, urlSuffix);
		System.out.println(url);
		String text = Jsoup.connect(url).ignoreContentType(true).maxBodySize(0).timeout(0).get().text();
		text = text.replace("],", "],\n"); // keep this in
		List<String> elements = new ArrayList<String>(Arrays.asList(text.split("\n")));
		String[] headers = StringUtils.substringsBetween(elements.get(0), "\"", "\"");
		elements.remove(0); // removing header
		List<Map<String, String>> resultsList = new ArrayList<>();
		for (String st : elements) {
			// -66666666666666 is missing data
			if (st.contains("government") || st.contains("Puerto Rico") || st.contains("-666666666")) {
				continue;
			}
			String[] row = StringUtils.substringsBetween(st, "\"", "\"");
			Map<String, String> resultMap = new HashMap<>();
			resultMap.put("NAME", row[0]); // NAME isn't in variable list
			for (int i = 0; i < variables.length; i++) {
				resultMap.put(variables[i], row[i + 1]); // i+1 because first variable is NAME
			}
			for (int i = variables.length; i < headers.length; i++) {
				resultMap.put(headers[i], row[i]);
			}
			resultsList.add(resultMap);
		}
		return resultsList;
	}

	public static String createUrlFromVariablesAndSuffix(String[] variables, String urlSuffix) {
		String url = "https://api.census.gov/data/" + YEAR + "/acs/acs5?get=NAME";
		for (String variable : variables) {
			url += ",";
			if (variable.contains("(")) {
				url += variable.substring(0, variable.indexOf("("));
			} else {
				throw new RuntimeException("missing description!");
			}
		}
		url += urlSuffix;
		return url;
	}

	public static Map<String, Result> getPlaceResults(String[] variables) throws Exception {
		List<Map<String, String>> resultsList = getResultsReusable(variables, PLACE_SUFFIX);
		Map<String, Result> elementsMap = new HashMap<>();
		for (Map<String, String> map : resultsList) {
			String name = map.get("NAME");
			if (StringUtils.countMatches(name, ",") > 1) {
				continue;
			}
			name = name.replace(" city,", ",");
			name = name.replace(" town,", ",");
			name = name.replace(" CDP,", ",");
			name = name.replace(" village,", ",");
			name = name.replace(" borough,", ",");
			Result result = new Result();
			result.city = name.substring(0, name.indexOf(","));
			result.state = name.substring(name.indexOf(",") + 2);
			String abbrev = Util.getStateAbbreviation(result.state).toUpperCase();
			for (String st : variables) {
				result.results.put(st, map.get(st));
			}
			result.fipsCode = abbrev + "-" + map.get("place");
			elementsMap.put(result.fipsCode, result);
		}
		return elementsMap;
	}

	// 33120 of these
	public static List<Map<String, String>> getZipCodes(String[] variables) throws Exception {
		return getResultsReusable(variables, ZIP_CODE_SUFFIX);
	}

	// 31606 of these
	public static List<Map<String, String>> getPlaces(String[] variables) throws Exception {
		return getResultsReusable(variables, PLACE_SUFFIX);

	}

	// 10895 of these
	public static List<Map<String, String>> getSchoolDistricts(String[] variables) throws Exception {
		return getResultsReusable(variables, SCHOOL_DISTRICT_SUFFIX);
	}

	// 3143 of these
	public static List<Map<String, String>> getCounties(String[] variables) throws Exception {
		return getResultsReusable(variables, COUNTY_SUFFIX);
	}

	// 939 of these
	public static List<Map<String, String>> getMetrosAndMicros(String[] variables) throws Exception {
		return getResultsReusable(variables,
				METRO_MICRO_SUFFIX);
	}

	// 175 of these
	public static List<Map<String, String>> getCombinedStatisticalAreas(String[] variables) throws Exception {
		return getResultsReusable(variables, CSA_SUFFIX);
	}

	// 51 of these
	public static List<Map<String, String>> getStates(String[] variables) throws Exception {
		return getResultsReusable(variables, STATE_SUFFIX);
	}

	// 4 of these
	public static List<Map<String, String>> getRegions(String[] variables) throws Exception {
		return getResultsReusable(variables, REGION_SUFFIX);
	}

	// 1 of these
	public static List<Map<String, String>> getCountry(String[] variables) throws Exception {
		return getResultsReusable(variables, COUNTRY_SUFFIX);
	}

	public static void main(String[] args) throws Exception {
		String[] variables = new String[] { "B20017B_001E(median earnings by black women)",
				"B02001_003E(number of black people)", ACSDataReader.POPULATION };
		List<Map<String, String>> elementsList = getCountry(variables);
		Iterator<Map<String, String>> iterator = elementsList.iterator();
		while (iterator.hasNext()) {
			Map<String, String> map = iterator.next();
			int numBlackPeople = Integer.valueOf(map.get("B02001_003E(number of black people)"));
			if (numBlackPeople < 1000 || map.get("B20017B_001E(median earnings by black women)").contains("-")) {
				iterator.remove();
			}
		}
		Collections.sort(elementsList, (a, b) -> Integer.valueOf(b.get("B20017B_001E(median earnings by black women)"))
				- Integer.valueOf(a.get("B20017B_001E(median earnings by black women)")));
		int counter = 0;
		for (Map<String, String> map : elementsList) {
			int income = Integer.valueOf(map.get("B20017B_001E(median earnings by black women)"));
			double totalBlackPopulation = Integer.valueOf(map.get("B02001_003E(number of black people)"));
			double totalPopulation = Integer.valueOf(map.get(ACSDataReader.POPULATION));
			double percentBlack = Util.roundTwoDecimalPlaces(100 * totalBlackPopulation / totalPopulation);
			System.out.println(map.get("NAME") + ": population=" + Util.getIntFromDouble(totalPopulation)
					+ ", percent_black=" + percentBlack + "%" + ", median_income_of_black_women=$" + income);
			counter++;
			if (counter > 4999) {
				break;
			}
		}
	}

}
