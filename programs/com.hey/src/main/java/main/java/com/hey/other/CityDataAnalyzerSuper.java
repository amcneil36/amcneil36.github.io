package main.java.com.hey.other;

import java.util.Collections;
import java.util.List;

import main.java.com.hey.CityStats;
import main.java.com.hey.CityStats.Data;
import main.java.com.hey.summaries.CreateBigCsv;

public abstract class CityDataAnalyzerSuper {
	
	public abstract boolean isDataValid(CityStats.Data data);
	
	public abstract void printHeader();
	
	public abstract void printRow(Data data);
	
	public abstract int calculateScore(CityStats.Data data);
	
	public void doEverything() throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		removeInvalidData(dataList);
		sortData(dataList);
		printData(dataList);
	}
	
	private void removeInvalidData(List<CityStats.Data> dataList) {
		for (int i = 0; i < dataList.size(); i++) {
			if (!isDataValid(dataList.get(i))) {
				dataList.remove(i);
				i--;
			}
		}
	}
	
	private void sortData(List<CityStats.Data> dataList) {
		Collections.sort(dataList, (a,b)->calculateScore(a)-calculateScore(b));
	}
	
	private void printData(List<Data> dataList) {
		printHeader();
		for (Data data: dataList) {
			printRow(data);
		}
	}
}
