package main.java.com.hey.us.census;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;

public class TexasSchoolDistrictReader {

	public static void main(String[] args) throws Exception {
		String[] variables = new String[] { "B19013_001E(median_household_income)", "B01003_001E(population)" };
		String url = "https://api.census.gov/data/2020/acs/acs5?get=NAME";
		for (String variable : variables) {
			url += ",";
			if (variable.contains("(")) {
				url += variable.substring(0, variable.indexOf("("));
			} else {
				url += variable;
			}
		}
		url += "&for=school%20district%20(unified):*&in=state:48";
		String text = Jsoup.connect(url).ignoreContentType(true).maxBodySize(0).timeout(0).get().text();
		text = text.replace("],", "],\n");
		List<String> elements = new ArrayList<String>(Arrays.asList(text.split("\n")));
		elements.remove(0);
		elements.set(elements.size() - 1, (elements.get(elements.size() - 1).replace("]]", "]")));
		List<String[]> list = new ArrayList<>();
		for (String st : elements) {
			st = st.substring(2);
			st = st.replace("],", "");
			st = st.replace("\",", ">");
			st = st.replace("\"", "");
			String[] row = st.split(">");
			String schoolDistrictName = row[0];
			String data = row[1];
			//System.out.println(st);
			list.add(new String[] { schoolDistrictName, data , row[2]});
		}
		Collections.sort(list, (a, b) -> Integer.valueOf(b[1]) - Integer.valueOf(a[1]));
		for (String[] stArr : list) {
			System.out.println(stArr[0] + "|median household income: $" + stArr[1] + "|population: " + stArr[2]);
		}
		System.out.println("total school districts: " + list.size());
	}
}
