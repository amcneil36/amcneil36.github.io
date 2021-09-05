package com.hey;

public class UVIndexGenericDataReadAndWriter extends GenericDataReadAndWriter {

	@Override
	protected void updateData(Data data, String stateName) {
	//	if (!data.uvIndex.contains("N/A")) {
	//		return;
	//	}
		if (data.uvIndex.contains(".")) {
			return;
		}
		if (data.uvIndex.length() > 1 && !data.uvIndex.contains("N/A")) {
			return;
		}
		String cityName = data.cityName.replace("\"", "");
		String url = "https://www.bestplaces.net/climate/city/" + stateName.replace(" ", "_") + "/" + cityName;
		String line = Util.ReadTextFromPage(url);
		if (!line.contains("UV Index")) {
			System.out.println("uv index not found for: " + url);
			return;
		}
		String line2 = line.substring(line.indexOf("UV Index ") + "UV Index ".length());
		line2 = line2.substring(0, line2.indexOf(" "));
		data.uvIndex = " " + line2;
		if (data.uvIndex.length() == 1) {
			System.out.println(url);
			System.out.println(line);
		}
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
