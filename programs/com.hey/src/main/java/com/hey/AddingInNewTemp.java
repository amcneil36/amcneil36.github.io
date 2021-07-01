package com.hey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.jsoup.nodes.Element;

import com.hey.CityVsUSAComparison.InputData;

public class AddingInNewTemp {

	
	static String [] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	public static void main(String[] args) throws Exception {
		String stateName = "Alabama";
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityLookup\\States\\" + stateName + ".js";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		myReader.nextLine();
		int idx = 0;
		String startSt = "arr.push(new Data(";
		StringBuilder sb = new StringBuilder();
		while (myReader.hasNextLine()) {
			idx++;
			String line = myReader.nextLine();
			//System.out.println(line);
			line = line.substring(startSt.length());
			line = line.substring(0, line.length()-3);
			String[] arr = line.split(",");
			String cityName = arr[0].substring(1, arr[0].length()-1);
			String url = "https://www.bestplaces.net/weather/city/" + stateName + "/" + cityName + "/";
			String text = SperlingReader.ReadTextFromPage(url);
			text = text.substring(text.indexOf("Average Monthly High and Low"));
			text = text.substring(text.indexOf("January"));
            int max = -1;
            int min = 99999999;
			for (String month : months) {
				int startIdx3 = text.indexOf(month + " ", text.indexOf("(°F)")) + month.length()+1;
				int endIdx3 = text.indexOf("°", startIdx3);
				int high = Integer.valueOf(text.substring(startIdx3, endIdx3));
				if (high > max) {
					max = high;
				}
				if (high < min) {
					min = high;
				}
			}
			arr[2] = " " + String.valueOf(max);
			arr[3] = " " + String.valueOf(min);
			sb.append(startSt);
			for (String st : arr) {
				sb.append(st);
				sb.append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append("));");
			sb.append("\n");
			if (idx > 3) {
				break;
			}
		}
		System.out.println(sb.toString());
		FileWriter myWriter = new FileWriter(filePath);
		String st = sb.toString();
		myWriter.write(st);
		myWriter.close();
		System.out.println("wrote to file " + filePath);
		myReader.close();
	}


}
