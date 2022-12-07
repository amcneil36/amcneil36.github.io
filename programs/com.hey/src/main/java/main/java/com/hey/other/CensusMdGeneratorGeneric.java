package main.java.com.hey.other;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.us.census.ACS2021DataReader;

public abstract class CensusMdGeneratorGeneric {
	
	public void doSuperEverythingResable(List<Map<String, String>> elementsList, String firstColHeaderMdName, String firstColMapKey, String fileName) throws Exception{
		removeAndSort(elementsList);
		int counter = 0;
		StringBuilder sb = new StringBuilder();
		String headerStr = "|" + firstColHeaderMdName + "|" + getRemainingHeaders();
		sb.append(headerStr).append("\n");
		int numCols = getRemainingHeaders().split("\\|").length;
		sb.append("|");
		for (int i = 0; i < numCols+1;i++) {
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
		String[] variables = getVariables();
		doSuperEverythingResable(ACS2021DataReader.getZipCodes(variables), "Zip code", "zip code tabulation area", "zipcode");
		doSuperEverythingResable(ACS2021DataReader.getPlaces(variables), "Place", "NAME", "place");
		doSuperEverythingResable(ACS2021DataReader.getSchoolDistricts(variables), "School District", "NAME", "schoolDistrict");
		doSuperEverythingResable(ACS2021DataReader.getCounties(variables), "County", "NAME", "county");
		doSuperEverythingResable(ACS2021DataReader.getMetrosAndMicros(variables), "Metro/Micro", "NAME", "metroMicro");
		doSuperEverythingResable(ACS2021DataReader.getCombinedStatisticalAreas(variables), "Combined Statistical Area", "NAME", "csa");
		doSuperEverythingResable(ACS2021DataReader.getStates(variables), "State", "NAME", "state");
		doSuperEverythingResable(ACS2021DataReader.getRegions(variables), "Region", "NAME", "region");
		doSuperEverythingResable(ACS2021DataReader.getCountry(variables), "Country", "NAME", "country");
	
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

	protected abstract Object[] getRemainingRowArray(Map<String, String> map);

	protected abstract String getRemainingHeaders();

	protected abstract void sortCollection(List<Map<String, String>> elementsList);

	protected abstract boolean removeRow(Map<String, String> map);

	protected abstract String[] getVariables();
}
