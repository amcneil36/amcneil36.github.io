package main.java.com.hey.other;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;

public abstract class CensusMdGeneratorGeneric {
	
	public void doSuperEverythingI() throws Exception{
		
	}
	
	public void doEverything() throws Exception {
		String[] variables = getVariables();
		List<Map<String, String>> elementsList = retrieveCensusData(variables);
		Iterator<Map<String, String>> iterator = elementsList.iterator();
		while (iterator.hasNext()) {
			Map<String, String> map = iterator.next();
			if (removeRow(map)) {
				iterator.remove();
			}
		}
		sortCollection(elementsList);
		int counter = 0;
		StringBuilder sb = new StringBuilder();
		String headerStr = getHeaderStr();
		sb.append(headerStr).append("\n");
		int numCols = getHeaderStr().split("\\|").length;
		sb.append("|");
		for (int i = 0; i < numCols-1;i++) {
			sb.append("---|");
		}
		sb.append("\n");
		for (Map<String, String> map : elementsList) {
			sb.append(getPipeString(getRowArray(map))).append("\n");
			counter++;
			if (counter > 4999) {
				break;
			}
		}
		Util.writeTextToFile(getFilePath(), sb.toString());
	}
	
	private String getPipeString(Object[] arr) {
		String st = "|";
		for (Object str : arr) {
			st += str + "|";
		}
		return st;
	}

	protected abstract String getFilePath();

	protected abstract Object[] getRowArray(Map<String, String> map);

	protected abstract String getHeaderStr();

	protected abstract void sortCollection(List<Map<String, String>> elementsList);

	protected abstract boolean removeRow(Map<String, String> map);

	protected abstract List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception;

	protected abstract String[] getVariables();
}
