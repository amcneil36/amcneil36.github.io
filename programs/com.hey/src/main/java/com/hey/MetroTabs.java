package main.java.com.hey;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import main.java.com.hey.CityStats.AndrewStringWriter;
import main.java.com.hey.CityStats.Data;

public class MetroTabs {

	static String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\MetroStats\\MetroTabs.xlsx";

	static Comparator<List<CityStats.Data>> comparator = new Comparator<List<CityStats.Data>>() {

		@Override
		public int compare(List<CityStats.Data> o1, List<CityStats.Data> o2) {
			if (Integer.valueOf(o1.get(0).metroPopulation) < Integer.valueOf(o2.get(0).metroPopulation)) {
				return 1;
			}
			if (Integer.valueOf(o1.get(0).metroPopulation) == Integer.valueOf(o2.get(0).metroPopulation)) {
				return 0;
			}
			return -1;
		}

	};

	public static void main(String[] args) throws Exception {
		List<CityStats.Data> dataList = CreateBigCsv.readInput();
		Map<String, List<CityStats.Data>> mapOfMetroNameToData = new HashMap<>();
		for (CityStats.Data data : dataList) {
			if (Integer.valueOf(data.population) < 10000) {
				continue;//otherwise java heap space issue. an alternative is one .csv per metro
			}
			if (Integer.valueOf(data.metroPopulation) > 999999 && !data.metro.contains("None")) {
				if (!mapOfMetroNameToData.containsKey(data.metro)) {
					mapOfMetroNameToData.put(data.metro, new ArrayList<CityStats.Data>());
				}
				List<CityStats.Data> dataListFromMap = mapOfMetroNameToData.get(data.metro);
				dataListFromMap.add(data);
				mapOfMetroNameToData.put(data.metro, dataListFromMap);
			}
		}
		Set<String> keys = mapOfMetroNameToData.keySet();
		List<List<CityStats.Data>> dataListSorted = new ArrayList<>();
		for (String key : keys) {
			dataListSorted.add(mapOfMetroNameToData.get(key));
		}
		Collections.sort(dataListSorted, comparator);

		XSSFWorkbook workbook = new XSSFWorkbook();
		String[] headers = CityStats.startSt.split(",");
		for (List<CityStats.Data> metroStats : dataListSorted) {
			XSSFSheet sheet = workbook.createSheet(metroStats.get(0).metro);
			int rowNum = 0;
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (String header : headers) {
				Cell cell = row.createCell(colNum++);
				cell.setCellValue(header);
			}
			for (CityStats.Data data : metroStats) {
				AndrewStringWriter sb = new AndrewStringWriter();
				CityStats.appendRowToSb(sb, data);
				String st = sb.getString().replace("\n", "");
				String[] datas = st.split(",");
				row = sheet.createRow(rowNum++);
				colNum = 0;
				for (String dataSt : datas) {
					Cell cell = row.createCell(colNum++);
					cell.setCellValue(dataSt);
				}
			}
		}

		FileOutputStream outputStream = new FileOutputStream(filePath);
		workbook.write(outputStream);
		workbook.close();

		System.out.println("Successfully wrote to " + filePath);

	}

}
