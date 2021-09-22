package main.java.com.hey;

public class Rent extends CityStats{

	private static final String SEARCH_STRING = "average rent for an apartment in ";

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String city = data.cityName.replace(" ", "-");
		String url = "https://www.rentcafe.com/average-rent-market-trends/us/" + Util.getStateAbbreviation(stateName) + "/" + city + "/";
		String text = Util.ReadTextFromPage(url);
		if (!text.contains(SEARCH_STRING)) {
			return;
		}
		try {
			int startIdx = text.indexOf(SEARCH_STRING) + SEARCH_STRING.length();
			text = text.substring(startIdx);
			text = text.substring(text.indexOf("$") + 1);
			String avgApartmentRent = text.substring(0, text.indexOf(", "));
			avgApartmentRent = avgApartmentRent.replace(",", "");
			Integer.valueOf(avgApartmentRent);
			avgApartmentRent = "$" + avgApartmentRent;
			data.avgApartmentRent = avgApartmentRent;
			text = text.substring(text.indexOf("average size for a ") + "average size for a ".length());
			text = text.substring(text.indexOf("apartment is ") + "apartment is ".length());
			String avgSqFt = text.substring(0, text.indexOf(" ")).replace(",", "");
			data.avgApartmentSize = avgSqFt;
		} catch (Exception ex) {
		}
	}

	public static void main(String[] args) throws Exception {
		CityStats s = new Rent();
		s.processState("Delaware");

	}
	
}
