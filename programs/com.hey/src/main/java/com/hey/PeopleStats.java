package main.java.com.hey;

public class PeopleStats extends CityStats {

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		String cityName = data.cityName.replace("\"", "").replace(" ", "_");
		String url = "https://www.bestplaces.net/people/city/" + stateName.replace(" ", "_") + "/" + cityName;
		// String url = "https://www.bestplaces.net/people/city/texas/corpus_christi";
		String text = Util.ReadTextFromPage(url);
		data.percentWhite = "N/A";
		data.percentBlack = "N/A";
		data.percentAsian = "N/A";
		data.percentHispanic = "N/A";
	//	data.percentWhite = grabPercentNonHispanicWhite(text, url, "% are white");
	//	data.percentBlack = grabPercentNonHispanicWhite(text, url, "% are black");
//		data.percentAsian = grabPercentNonHispanicWhite(text, url, "% are asian");
//		data.percentHispanic = grabPercentNonHispanicWhite(text, url, "% claim Hispanic");

		System.out.println("%white: " + data.percentWhite + "; %black: " + data.percentBlack + "; %asian: "
				+ data.percentAsian + "; %hispanic: " + data.percentHispanic);
	}

	private String grabPercentNonHispanicWhite(String text, String url, String searchPattern) {
		if (!text.contains(searchPattern)) {
			System.out.println(searchPattern + " not found for: " + url + "; " + text);
			return "N/A";
		}
		text = text.substring(0, text.indexOf(searchPattern) + 1);
		int startIdx = text.length() - 1;
		char ch = text.charAt(startIdx);
		int totalIterations = 0;
		while (ch != ' ') {
			startIdx--;
			ch = text.charAt(startIdx);
			totalIterations++;
			if (totalIterations > 8) {
				System.out.println("iteration fail!");
				return "N/A";
			}
		}
		text = text.substring(startIdx + 1);
		return text;
	}

	public static void main(String[] args) throws Exception {
		CityStats cityStats = new PeopleStats();
		cityStats.processAllStates();
	}

}
