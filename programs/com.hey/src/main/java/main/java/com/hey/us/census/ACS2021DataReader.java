package main.java.com.hey.us.census;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import main.java.com.hey.Util;

public class ACS2021DataReader {
	
	public static String POPULATION = "B01003_001E(population)";
	public static String MEDIAN_HOUSEHOLD_INCOME = "B19013_001E(median_household_income)";
	public static String MEDIAN_HOUSEHOLD_INCOME_WHITE = "B19013A_001E(median household income white)";
	public static String MEDIAN_HOUSEHOLD_INCOME_BLACK = "B19013B_001E(median household income black)";
	public static String BACHELORS_DEGREE = "B15003_022E(number of people with a bachelor's degree)";
	public static String MASTERS_DEGREE = "B15003_023E(number of people with a master's degree)";
	public static String PROFESSIONAL_DEGREE = "B15003_024E(number of people with a professional degree)";
	public static String DOCTORATE_DEGREE = "B15003_025E(number of people with a doctorate degree)";
	public static String NUM_PEOPLE_OVER_25_EDUCATION = "B15003_001E(num people over 25)";

	public static class Result{
		public String city = "";
		public String state = "";
		public String fipsCode = "";
		public Map<String, String> results = new HashMap<>();
		
		@Override
		public String toString() {
			return city + ", " + state + " (" + fipsCode + "); results: " + results;
		}
	}
	
	// lets update code to add in the parenthesis stuff. first cols are always our stuff. rest are not.
	public static List<Map<String, String>> getResultsReusable(String[] variables) throws Exception{
		String url = "https://api.census.gov/data/2020/acs/acs5?get=NAME";
		for (String variable : variables) {
			url += ",";
			if (variable.contains("(")) {
				url += variable.substring(0, variable.indexOf("("));	
			}
			else {
				throw new RuntimeException("missing description!");
			}
		}
		url += "&for=place:*";
		System.out.println(url);
		String text = Jsoup.connect(url).ignoreContentType(true).maxBodySize(0).timeout(0).get().text();
		text = text.replace("],", "],\n"); // keep this in
		List<String> elements = new ArrayList<String>(Arrays.asList(text.split("\n")));
		String[] headers = StringUtils.substringsBetween(elements.get(0) , "\"", "\"");
		elements.remove(0);
		List<Map<String, String>> resultsList = new ArrayList<>();
		for (String st : elements) {
			if (st.contains("government") || st.contains("Puerto Rico")) {
				continue;
			}
			String[] row = StringUtils.substringsBetween(st , "\"", "\"");
			Map<String, String> resultMap = new HashMap<>();
			resultMap.put("NAME", row[0]);
			for (int i = 0; i < variables.length; i++) {
				resultMap.put(variables[i], row[i+1]); // i+1 because first variable is NAME
			}
			for (int i = variables.length; i < headers.length; i++) {
				resultMap.put(headers[i], row[i]);
			}
			resultsList.add(resultMap);
		}
		return resultsList;
	}
	
	public static Map<String, Result> getResults(String[] variables) throws Exception{
		List<Map<String, String>> resultsList = getResultsReusable(variables);
		Map<String, Result> elementsMap = new HashMap<>();
		for (Map<String, String> map : resultsList) {
			String str = map.get("NAME");
			if (StringUtils.countMatches(str, ",") > 1) {
				continue;
			}
			str = str.replace(" city,", ",");
			str = str.replace(" town,", ",");
			str = str.replace(" CDP,", ",");
			str = str.replace(" village,", ",");
			str = str.replace(" borough,", ",");		
			Result result = new Result();
			result.city = str.substring(0, str.indexOf(","));
			result.state = str.substring(str.indexOf(",") + 2);
			String abbrev = Util.getStateAbbreviation(result.state).toUpperCase();
			result.fipsCode = abbrev + "-" + map.get("place");
			for (String st : variables) {
				result.results.put(st, map.get(st));
			}
			elementsMap.put(result.fipsCode, result);
		}
		return elementsMap;
	}
	
	public static void main(String[] args) throws Exception {
		String[] variables = new String[] {"B25010_001E(household size)"};
		Map<String, Result> elementsMap = getResults(variables);
		Set<String> keys = elementsMap.keySet();
		for (String key : keys) {
			Result result = elementsMap.get(key);
			if (result.city.equals("Weston") && result.state.equals("Florida")) {
				System.out.println(result);
			}
		}
	}

}
