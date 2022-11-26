package main.java.com.hey.other;

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
	
	public static Map<String, Result> getResults(String[] variables) throws Exception{
		String url = "https://api.census.gov/data/2020/acs/acs5?get=NAME";
		for (String variable : variables) {
			System.out.println(variable);
			url += "," + variable.substring(0, variable.indexOf("("));
		}
		url += "&for=place:*";
		System.out.println(url);
		String text = Jsoup.connect(url).ignoreContentType(true).maxBodySize(0).timeout(0).get().text();
		text = text.replace("],", "],\n");
		List<String> elements = new ArrayList<String>(Arrays.asList(text.split("\n")));
		elements.remove(0);
		elements.set(elements.size() - 1, (elements.get(elements.size() - 1).replace("]]", "]")));
		Map<String, Result> elementsMap = new HashMap<>();
		for (String st : elements) {
			st = st.replace("\",", ">").replace("],", ">").replace(" [", "").replace("\"", "").replace("]", ">");
			if (st.contains("government")) {
				continue;
			}
			String[] row = st.split(">");
			String str = row[0];
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
			if (result.state.equals("Puerto Rico")) {
				continue;
			}
			String abbrev = Util.getStateAbbreviation(result.state).toUpperCase();
			result.fipsCode = abbrev + "-" + Integer.valueOf(row[row.length-1]);
			for (int i = 1; i < row.length-2; i++) {
				result.results.put(variables[i-1], row[i]);
			}
			elementsMap.put(result.fipsCode, result);
		}
		return elementsMap;
	}
	
	public static void main(String[] args) throws Exception {
		String[] variables = new String[] {ACS2021DataReader.POPULATION, ACS2021DataReader.BACHELORS_DEGREE};
		Map<String, Result> elementsMap = getResults(variables);
		Set<String> keys = elementsMap.keySet();
		for (String key : keys) {
			Result result = elementsMap.get(key);
		}
	}

}
