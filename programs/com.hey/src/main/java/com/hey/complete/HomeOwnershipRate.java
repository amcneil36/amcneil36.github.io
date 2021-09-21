package main.java.com.hey.complete;


public class HomeOwnershipRate extends GenericDataReadAndWriter {

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
			if (!data.homeOwnershipRate.contains("N/A")) {
				return;
			}
			String cityName = data.cityName.replace("\"", "").replace(" ", "_");
			String url = "https://www.bestplaces.net/housing/city/" + stateName.replace(" ", "_") + "/" + cityName;
			String line = Util.ReadTextFromPage(url);
			if (!line.contains("Homes Owned ")) {
				System.out.println("Homes Owned  not found for: " + url);
				return;
			}
			line = line.substring(line.indexOf("Homes Owned ") + "Homes Owned ".length());
			line = line.substring(0, line.indexOf(" "));
			data.homeOwnershipRate = " \"" + line + "\"";
		
	}

	public static void main(String[] args) throws Exception {

		GenericDataReadAndWriter n = new HomeOwnershipRate();
		n.processAllStates();

		// https://www.bestplaces.net/climate/city/california/oceanside: UV Index
		// https://www.bestplaces.net/housing/city/california/oceanside: homes owned
		// percent Single: https://www.bestplaces.net/people/city/california/oceanside
		// https://www.bestplaces.net/transportation/city/california/oceanside transit
		// bike walk

	}
}
