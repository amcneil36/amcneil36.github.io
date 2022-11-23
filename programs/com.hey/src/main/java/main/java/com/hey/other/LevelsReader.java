package main.java.com.hey.other;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import main.java.com.hey.UpdatePrinter;
import main.java.com.hey.Util;

public class LevelsReader {
	
public static void readAll() throws Exception {
	List<String> inp = Util.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\levels.txt");
	int numElements = inp.size();
	StringBuilder startingSalaries = new StringBuilder("Company Name,Starting Comp\n");
	StringBuilder medianSalaries = new StringBuilder("Company Name,Median Comp\n");
	UpdatePrinter updatePrinter = new UpdatePrinter(numElements, "");
	StringBuilder errors = new StringBuilder();
	for (String st : inp) {
		updatePrinter.printUpdateIfNeeded();
		String companyName = st;
		companyName = companyName.replace(" ", "-").replace("&", "and");
		String url = "https://www.levels.fyi/company/" + companyName + "/salaries/Software-Engineer/";
		String txt = Util.ReadTextFromPage(url);
		if (!txt.contains("$")) {
			continue;//no salaries
		}
		if (txt.isEmpty()) {
			System.out.println("nothing found at this url: " + url);
		}
		if (txt.contains("(Entry Level) $")) {
			txt = txt.substring(txt.indexOf("(Entry Level) $") + "(Entry Level) $".length());
			txt = txt.substring(0, txt.indexOf("k"));
			startingSalaries.append(companyName + "," + txt + "\n");
		}
		else if (txt.contains("Median Package $")) {
			txt = txt.substring(txt.indexOf("Median Package $") + "Median Package $".length());
			txt = txt.substring(0, txt.indexOf("k"));
			medianSalaries.append(companyName + "," + txt + "\n");
		}
		else {
			errors.append(url + "\n");
		}
	}
	
	Util.writeTextToFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\startingSalaries.csv", startingSalaries.toString());
	Util.writeTextToFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\medianSalaries.csv", medianSalaries.toString());
	Util.writeTextToFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\errorCompanies.txt", errors.toString());
	System.out.println("finished");
}


	public static void main(String[] args) throws Exception {
		List<String> inp = Util.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\level3.txt");
		StringBuilder sb = new StringBuilder();
		for (String st : inp) {
			sb.append("\"").append(st.replace("-", " ")).append("\" OR ");
		}
		System.out.println(sb.toString());
	}


	private static void openTabs() throws Exception, IOException, URISyntaxException {
		List<String> inp = Util.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\level2.txt");
		for (String st : inp) {
			String companyName = st;
			companyName = companyName.replace(" ", "-").replace("&", "and");
			String url = "https://www.levels.fyi/company/" + companyName + "/salaries/Software-Engineer/";
			Desktop.getDesktop().browse(new URI(url));
		}
	}

}
