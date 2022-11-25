package main.java.com.hey.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;

public class ACS2021DataReader {

	public static class Result{
		String city = "";
		String state = "";
		int place = 0;
		List<String> results = new ArrayList<>(); // maybe make this a map
		
		@Override
		public String toString() {
			String st = city + ", " + state + "; results: {";
			for (String st2 : results) {
				st += st2 + ", ";
			}
			st = st.substring(0, st.length()-2);
			st += "}"; 
			return st;
		}
	}
	public static void main(String[] args) throws Exception {
		String url = "https://api.census.gov/data/2020/acs/acs5?get=NAME,B19013_001E&for=place:*";
		String text = Jsoup.connect(url).ignoreContentType(true).maxBodySize(0).timeout(0).get().text();
		text = text.replace("],", "],\n");
		List<String> elements = new ArrayList<String>(Arrays.asList(text.split("\n")));
		elements.remove(0);
		elements.set(elements.size() - 1, (elements.get(elements.size() - 1).replace("]]", "]")));
		List<Result> elementsList = new ArrayList<>();
		for (String st : elements) {
			st = st.replace("\",", ">").replace("],", ">").replace(" [", "").replace("\"", "").replace("]", ">");
			String[] row = st.split(">");
			int idx = 0;
			String str = row[0];
			str = str.replace(" city,", ",");
			str = str.replace(" town,", ",");
			str = str.replace(" CDP,", ",");
			str = str.replace(" village,", ",");
			str = str.replace(" borough,", ",");		
			Result result = new Result();
			result.city = str.substring(0, str.indexOf(","));
			result.state = str.substring(str.indexOf(",") + 2);
			result.place = Integer.valueOf(row[row.length-1]);
			for (int i = 1; i < row.length-2; i++) {
				result.results.add(row[i]);
			}
			elementsList.add(result);
			//System.out.println(place);
		}
		for (Result result : elementsList) {
			if (result.city.equals("Weston") && result.state.equals("Florida")) {
				System.out.println(result);	
			}
		}
	}

}
