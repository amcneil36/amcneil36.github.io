package main.java.com.hey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LevelsReader {
	



	public static void main(String[] args) throws Exception {
		List<String> inp = Util.readTextFromFile("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\levels.txt");
		int numElements = inp.size();
		StringBuilder startingSalaries = new StringBuilder("Company Name,Starting Salary\n");
		StringBuilder medianSalaries = new StringBuilder("Company Name,Median Salary\n");
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

}
