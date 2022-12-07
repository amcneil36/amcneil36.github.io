package main.java.com.hey.other;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.us.census.ACS2021DataReader;

public abstract class CensusMdGeneratorGeneric {

	// maybe add link to doc for column names or something
	public void doSuperEverythingResable(List<Map<String, String>> elementsList, String firstColHeaderMdName,
			String firstColMapKey, String fileName, String url) throws Exception {
		removeAndSort(elementsList);
		int counter = 0;
		StringBuilder sb = new StringBuilder();
		sb.append(
				"This page is an analysis and visualization of data I queried from the US Census' American Community Survey 2020 5 year estimates API.");
		sb.append(" To see the original, raw data that I retrieved from the API before analyzing it, navigate here: ");
		// <a href="https://www.census.gov/topics/population/race/about.html">https://www.census.gov/topics/population/race/about.html</a>
		sb.append(Util.getHyperLink(url)).append("\n\n");
		String headerStr = "|" + firstColHeaderMdName + "|" + getRemainingHeaders();
		sb.append(headerStr).append("\n");
		int numCols = getRemainingHeaders().split("\\|").length;
		sb.append("|");
		for (int i = 0; i < numCols + 1; i++) {
			sb.append("---|");
		}
		sb.append("\n");
		for (Map<String, String> map : elementsList) {
			sb.append("|" + map.get(firstColMapKey) + getPipeString(getRemainingRowArray(map))).append("\n");
			counter++;
			if (counter > 4999) {
				break;
			}
		}
		Util.writeTextToFile("generated/" + fileName + ".md", sb.toString());////////////////////////////////
	}

	// make go inside a folder?
	// maybe have readme.md in each folder that redirects to the others?
	// maybe there can be some relative url redirect
	// or maybe i need something that passes in the folder name
	// maybe w/e it is will create a folder that doesn't exist?
	// and also have it able to redirect to other stuff of the same url.

	
	public void doEverything() throws Exception {
        Util.deleteFilesInFolder("C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\generated");
		String[] variables = getVariables();
        Util.writeTextToFile("generated//main.md", "### " + getMainHeaderText() + getMainBodyText() + getMainFooterText());
        doSuperEverythingResable(ACS2021DataReader.getZipCodes(variables), "Zip code", "zip code tabulation area",
				"zipcode",
				ACS2021DataReader.createUrlFromVariablesAndSuffix(variables, ACS2021DataReader.ZIP_CODE_SUFFIX));
		doSuperEverythingResable(ACS2021DataReader.getPlaces(variables), "City/Town/CDP", "NAME", "cityTownCDP",
				ACS2021DataReader.createUrlFromVariablesAndSuffix(variables, ACS2021DataReader.PLACE_SUFFIX));
		doSuperEverythingResable(ACS2021DataReader.getSchoolDistricts(variables), "School District", "NAME",
				"schoolDistrict", ACS2021DataReader.createUrlFromVariablesAndSuffix(variables, ACS2021DataReader.SCHOOL_DISTRICT_SUFFIX));
		doSuperEverythingResable(ACS2021DataReader.getCounties(variables), "County", "NAME", "county",
				ACS2021DataReader.createUrlFromVariablesAndSuffix(variables, ACS2021DataReader.COUNTY_SUFFIX));
		doSuperEverythingResable(ACS2021DataReader.getMetrosAndMicros(variables), "Metro/Micro", "NAME", "metroMicro",
				ACS2021DataReader.createUrlFromVariablesAndSuffix(variables, ACS2021DataReader.METRO_MICRO_SUFFIX));
		doSuperEverythingResable(ACS2021DataReader.getCombinedStatisticalAreas(variables), "Combined Statistical Area",
				"NAME", "combinedStatisticalArea", ACS2021DataReader.createUrlFromVariablesAndSuffix(variables, ACS2021DataReader.CSA_SUFFIX));
		doSuperEverythingResable(ACS2021DataReader.getStates(variables), "State", "NAME", "state",
				ACS2021DataReader.createUrlFromVariablesAndSuffix(variables, ACS2021DataReader.STATE_SUFFIX));
		doSuperEverythingResable(ACS2021DataReader.getRegions(variables), "Region", "NAME", "region",
				ACS2021DataReader.createUrlFromVariablesAndSuffix(variables, ACS2021DataReader.REGION_SUFFIX));
		doSuperEverythingResable(ACS2021DataReader.getCountry(variables), "Country", "NAME", "country",
				ACS2021DataReader.createUrlFromVariablesAndSuffix(variables, ACS2021DataReader.COUNTRY_SUFFIX));

	}

	private String getMainBodyText() {
		String st = "";
		st += "  \n- Zip Code: [link](zipcode)  \n";
		st += "- City/Town/CDP: [link](cityTownCDP)  \n";
		st += "- School District: [link](schoolDistrict)  \n";
		st += "- County: [link](county)  \n";
		st += "- Metro/Micro: [link](metroMicro)  \n";
		st += "- Combined Statistical Area: [link](combinedStatisticalArea)  \n";
		st += "- State: [link](state)  \n";
		st += "- Region: [link](region)  \n";
		st += "- Country: [link](country)  \n\n";
		return st;
	}

	private void removeAndSort(List<Map<String, String>> elementsList) {
		Iterator<Map<String, String>> iterator = elementsList.iterator();
		while (iterator.hasNext()) {
			Map<String, String> map = iterator.next();
			if (removeRow(map)) {
				iterator.remove();
			}
		}
		sortCollection(elementsList);
	}

	private String getPipeString(Object[] arr) {
		String st = "|";
		for (Object str : arr) {
			st += str + "|";
		}
		return st;
	}
	
	protected abstract String getMainHeaderText();

	protected abstract String getRemainingHeaders();
	
	protected abstract String[] getVariables();
	
	protected abstract boolean removeRow(Map<String, String> map);
	
	protected abstract String getMainFooterText();
	
	protected abstract void sortCollection(List<Map<String, String>> elementsList);
	
	protected abstract Object[] getRemainingRowArray(Map<String, String> map);
	
}
