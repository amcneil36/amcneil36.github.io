package main.java.com.hey.useful.not.needed.to.modify.much;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.java.com.hey.CityStats.Data;
import main.java.com.hey.useful.not.needed.to.modify.much.GenericStatsSuper.WeightedAverage;

public abstract class CityStatsSuper {

	private static final int ERROR_HANDLE_IDX = 3; // only error handle once so performance is better

	public String getStartString() {
		AndrewStringWriter sb = new AndrewStringWriter();
		String[] headers = getOutputHeaders();
		for (String header : headers) {
			sb.appendWithComma(header);
		}
		return sb.removeLastElement().getString();
	}

	public abstract String[] getOutputHeaders();

	public void writeDataToPath(List<Data> dataList, String filePath, boolean isLastWrite) throws Exception {
		FileWriter myWriter = new FileWriter(filePath);
		AndrewStringWriter sb = new AndrewStringWriter();
		sb.appendLastItem(getStartString());
		String[] headers = getOutputHeaders();
		Map<String, Integer> mapOfHeaderToIdx = new HashMap<>();
		for (int i = 0; i < headers.length; i++) {
			mapOfHeaderToIdx.put(headers[i], i);
		}
		int i = 0;
		for (Data data : dataList) {
			appendRowToSb(sb, data, mapOfHeaderToIdx, i++);
		}
		String st = sb.getString();
		myWriter.write(st);
		myWriter.close();
		if (isLastWrite) {
			System.out.println("wrote to file " + filePath);
		}
	}

	public boolean shouldWriteData() {
		return true;
	}

	public void writeData(List<Data> dataList, String stateName, boolean isLastWrite) throws Exception {
		if (!shouldWriteData()) {
			return;
		}
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\States\\" + stateName
				+ ".csv";
		writeDataToPath(dataList, filePath, isLastWrite);
	}

	protected abstract void updateData(Data data, String stateName) throws Exception;

	public void processAllStates() throws Exception {
		runStateAsync("Alabama");
		runStateAsync("Alaska");
		runStateAsync("Arizona");
		runStateAsync("Arkansas");
		runStateAsync("California");
		runStateAsync("Colorado");
		runStateAsync("Connecticut");
		runStateAsync("Delaware");
		runStateAsync("Florida");
		runStateAsync("Georgia");
		runStateAsync("Hawaii");
		runStateAsync("Idaho");
		runStateAsync("Illinois");
		runStateAsync("Indiana");
		runStateAsync("Iowa");
		runStateAsync("Kansas");
		runStateAsync("Kentucky");
		runStateAsync("Louisiana");
		runStateAsync("Maine");
		runStateAsync("Maryland");
		runStateAsync("Massachusetts");
		runStateAsync("Michigan");
		runStateAsync("Minnesota");
		runStateAsync("Mississippi");
		runStateAsync("Missouri");
		runStateAsync("Montana");
		runStateAsync("Nebraska");
		runStateAsync("Nevada");
		runStateAsync("New Hampshire");
		runStateAsync("New Jersey");
		runStateAsync("New Mexico");
		runStateAsync("New York");
		runStateAsync("North Carolina");
		runStateAsync("North Dakota");
		runStateAsync("Ohio");
		runStateAsync("Oklahoma");
		runStateAsync("Oregon");
		runStateAsync("Pennsylvania");
		runStateAsync("Rhode Island");
		runStateAsync("South Carolina");
		runStateAsync("South Dakota");
		runStateAsync("Tennessee");
		runStateAsync("Texas");
		runStateAsync("Utah");
		runStateAsync("Vermont");
		runStateAsync("Virginia");
		runStateAsync("Washington");
		runStateAsync("West Virginia");
		runStateAsync("Wisconsin");
		runStateAsync("Wyoming");
		runStateAsync("District of Columbia");
	}

	private void runStateAsync(String stateName) throws Exception {
		new RunnableDemo52(stateName, this).start();
	}

	public void processState(String stateName) throws Exception {
		List<Data> dataList = readData(stateName);
		try {
			UpdatePrinter updatePrinter = new UpdatePrinter(dataList.size(), stateName);
			int idx = 0;
			for (Data data : dataList) {
				updateData(data, stateName);
				updatePrinter.printUpdateIfNeeded();
				idx++;
				if (idx % 30 == 0) {
				//	writeData(dataList, stateName, false);
				}
			}
		} finally {
			writeData(dataList, stateName, true);
			runCleanup();
		}
	}

	protected void runCleanup() {

	};

	static class RunnableDemo52 implements Runnable {
		private Thread t;
		private String stateName;
		private CityStatsSuper g;

		RunnableDemo52(String stateName, CityStatsSuper cityStatsSuper) {
			this.stateName = stateName;
			this.g = cityStatsSuper;
		}

		public void run() {
			try {
				g.processState(stateName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void start() {
			if (t == null) {
				t = new Thread(this);
				t.start();
			}
		}

	}

	public static class AndrewStringWriter {
		StringBuilder sb = new StringBuilder();

		public AndrewStringWriter appendDollar(String st) {
			if (!st.contains("$") && !st.contains("N/A")) {
				sb.append("$").append(st).append(",");
				return this;
			}
			sb.append(st).append(",");
			return this;
		}

		public AndrewStringWriter appendWithComma(String st) {
			sb.append(st).append(",");
			return this;
		}

		public AndrewStringWriter appendWithComma(int st) {
			sb.append(st).append(",");
			return this;
		}

		public AndrewStringWriter appendWAPercent(WeightedAverage wa) {
			sb.append(wa.getWeightedAverage());
			sb.append("%,");
			return this;
		}

		public AndrewStringWriter appendWADollar(WeightedAverage wa) {
			if (!wa.getWeightedAverage().equals("N/A")) {
				sb.append("$");
			}
			return appendWA(wa);
		}

		public AndrewStringWriter appendWA(WeightedAverage wa) {
			return appendWithComma(wa.getWeightedAverage());
		}

		public AndrewStringWriter appendLastItem(String st) {
			sb.append(st);
			sb.append("\n");
			return this;
		}

		public AndrewStringWriter appendLastItem(int st) {
			sb.append(st);
			sb.append("\n");
			return this;
		}

		public AndrewStringWriter appendEnding() {
			sb.setLength(sb.length() - 1);
			sb.append("\n");
			return this;
		}

		public String getString() {
			return sb.toString();
		}

		public AndrewStringWriter removeLastElement() {
			sb.deleteCharAt(sb.length() - 1);
			return this;
		}
	}

	public abstract void readDataFromMap(Map<String, Integer> mapOfNameToIndex, String[] arr, Data data);

	private Map<String, Integer> createMapOfNameToIndex(String header) {
		Map<String, Integer> map = new HashMap<>();
		String[] arr = header.split(",");
		for (int i = 0; i < arr.length; i++) {
			map.put(arr[i], i);
		}
		return map;
	}

	public void appendRowToSb(AndrewStringWriter sb, Data data, Map<String, Integer> mapOfNameToIndex, int idx) {
		String[] arr = new String[getOutputHeaders().length];
		if (idx == ERROR_HANDLE_IDX) {
			for (int i = 0; i < arr.length; i++) {
				arr[i] = UNPROCESSED_NODE;
			}	
		}
		writeDataToArray(data, mapOfNameToIndex, arr);
		if (idx == ERROR_HANDLE_IDX) {
			validateAllFieldsWereWrittenTo(arr);	
		}

		for (String st : arr) {
			sb.appendWithComma(st);
		}
		sb.removeLastElement().appendLastItem("");
	}

	private static final String UNPROCESSED_NODE = "unprocessed node here 23894720";

	public void validateAllFieldsWereWrittenTo(String[] arr) {
		for (String st : arr) {
			if (st.equals(UNPROCESSED_NODE)) {
				throw new RuntimeException("Not all fields were written too!");
			}
		}

	}

	public List<Data> readData(String stateName) throws Exception {
		List<Data> dataList = new ArrayList<>();
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\CityStats\\States\\" + stateName
				+ ".csv";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		String header = myReader.nextLine(); //
		Map<String, Integer> mapOfNameToIndex = createMapOfNameToIndex(header);
		if (mapOfNameToIndex.size() != getOutputHeaders().length) {
			myReader.close();
			throw new RuntimeException(
					"The number of headers is in the code is not equal to the number of columns found in the file!");
		}
		int i = 0;
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			String[] arr = line.split(",");
			Data data = new Data();
			readDataFromMap(mapOfNameToIndex, arr, data);
			if (i == ERROR_HANDLE_IDX) {
				validateAllFieldsWereRead(arr);	
			}
			dataList.add(data);
			i++;
		}
		myReader.close();
		return dataList;
	}

	private static final String PROCESSED_NODE = "processed node here 239487";

	private void validateAllFieldsWereRead(String[] arr) {
		for (String st : arr) {
			if (!st.equals(PROCESSED_NODE)) {
				throw new RuntimeException("didn't read all data! found: " + st);
			}
		}
	}

	public String read(String[] arr, Map<String, Integer> mapOfNameToIndex, String key) {
		String st = arr[mapOfNameToIndex.get(key)];
		arr[mapOfNameToIndex.get(key)] = PROCESSED_NODE;
		return st;
	}

	public abstract void writeDataToArray(Data data, Map<String, Integer> mapOfNameToIndex, String[] arr);
}
