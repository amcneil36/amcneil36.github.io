package main.java.com.hey.complete;

//import com.hey.GenericDataReadAndWriter;

import main.java.com.hey.Util;

public class SinglePopulation {//extends GenericDataReadAndWriter{
/*
	private static String PREFIX = "Single Population ";
	
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
			if (!data.singlePopulation.contains("N/A")) {
				return;
			}
			String cityName = data.cityName.replace("\"", "").replace(" ", "_");
			String url = "https://www.bestplaces.net/people/city/" + stateName.replace(" ", "_") + "/" + cityName;
			String line = Util.ReadTextFromPage(url);
			if (!line.contains(PREFIX)) {
				System.out.println(PREFIX + " not found for: " + url);
				return;
			}
			line = line.substring(line.indexOf(PREFIX) + PREFIX.length());
			line = line.substring(0, line.indexOf(" "));
			data.singlePopulation = " \"" + line + "\"";
		
	}
	
	public static void main(String[] args) throws Exception {

		GenericDataReadAndWriter n = new SinglePopulation();
		n.processAllStates();

		// https://www.bestplaces.net/climate/city/california/oceanside: UV Index
		// https://www.bestplaces.net/housing/city/california/oceanside: homes owned
		// percent Single: https://www.bestplaces.net/people/city/california/oceanside
		// https://www.bestplaces.net/transportation/city/california/oceanside transit
		// bike walk

	}*/
}
