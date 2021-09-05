package com.hey;

import java.util.List;

public class UVIndexGenericDataReadAndWriter extends GenericDataReadAndWriter {

	@Override
	protected void updateData(List<Data> dataList, String stateName) {
		UpdatePrinter updatePrinter = new UpdatePrinter(dataList.size(), stateName);
		for (Data data : dataList) {
			if (!data.uvIndex.contains("N/A")) {
				continue;
			}
			String cityName = data.cityName.replace("\"", "");
			String url = "https://www.bestplaces.net/climate/city/" + stateName + "/" + cityName;
			String line = SperlingReader.ReadTextFromPage(url);
			line = line.substring(line.indexOf("UV Index ") + "UV Index ".length());
			line = line.substring(0, line.indexOf(" "));
			data.uvIndex = " " + line;
			updatePrinter.printUpdateIfNeeded();
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
