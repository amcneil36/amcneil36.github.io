package com.hey;

public class UVIndexGenericDataReadAndWriter extends GenericDataReadAndWriter {

	@Override
	protected void updateData(Data data, String stateName) {
		if (!data.uvIndex.contains("N/A")) {
			return;
		}
		String cityName = data.cityName.replace("\"", "");
		String url = "https://www.bestplaces.net/climate/city/" + stateName + "/" + cityName;
		String line = Util.ReadTextFromPage(url);
		line = line.substring(line.indexOf("UV Index ") + "UV Index ".length());
		line = line.substring(0, line.indexOf(" "));
		data.uvIndex = " " + line;
	}

	public static void main(String[] args) throws Exception {

		GenericDataReadAndWriter n = new UVIndexGenericDataReadAndWriter();
		n.processAllStates();

		// https://www.bestplaces.net/climate/city/california/oceanside: UV Index
		// https://www.bestplaces.net/housing/city/california/oceanside: homes owned
		// percent Single: https://www.bestplaces.net/people/city/california/oceanside
		// https://www.bestplaces.net/transportation/city/california/oceanside transit
		// bike walk

	}
}
