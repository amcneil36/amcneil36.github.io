package com.hey;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AddingInNewTemp {

	static String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
			"October", "November", "December" };

	public static void main(String[] args) throws Exception {
		runThread("Alabama");
		runThread("Alaska");
		runThread("Arizona");
		runThread("Arkansas");
		runThread("California");
		runThread("Colorado");
		runThread("Connecticut");
		runThread("Delaware");
		runThread("Florida");
	}

	public static void runThread(String stateName) throws Exception {
		new RunnableDemo5(stateName).start();
	}

	private static void processState(String stateName) throws Exception {
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityLookup\\States\\" + stateName
				+ ".js";
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		myReader.nextLine();
		int numCompletedCities = 0;
		String startSt = "arr.push(new Data(";
		StringBuilder sb = new StringBuilder();
		int max = -1;
		int min = 99999999;
		List<String> lines = new ArrayList<String>();
		while (myReader.hasNextLine()) {
			String line = myReader.nextLine();
			if (!line.contains(startSt)) {
				continue;
			}
			lines.add(line);
		}
		int totalNumCities = lines.size();
		long startTime = System.currentTimeMillis();
		for (String line : lines) {
			String initialLine = line;
			try {
				line = line.substring(startSt.length());
				line = line.substring(0, line.length() - 3);
				String[] arr = line.split(",");
				String cityName = arr[0].substring(1, arr[0].length() - 1);
				String url = "https://www.bestplaces.net/weather/city/" + stateName + "/" + cityName + "/";
				String text = SperlingReader.ReadTextFromPage(url);
				text = text.substring(text.indexOf("Average Monthly High and Low"));
				text = text.substring(text.indexOf("January"));
				for (String month : months) {
					int startIdx3 = text.indexOf(month + " ", text.indexOf("(°F)")) + month.length() + 1;
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
			} catch (Exception ex) {
				System.out.println("something went wrong! dumping stack trace!");
				ex.printStackTrace();
				System.out.println("stack trace is from this line: " + initialLine);
				sb.append(initialLine);
				sb.append(",");
			}
			numCompletedCities++;
			if (numCompletedCities % 50 == 0) {
				long secondsSinceStart = (System.currentTimeMillis() - startTime) / 1000;
				float numRemainingCities = (totalNumCities - numCompletedCities);
				float minRemaining = (numRemainingCities * secondsSinceStart / (numCompletedCities)) / 60;
				System.out.println(stateName + ": " + numCompletedCities + " of " + totalNumCities
						+ " cities complete. Time remaining: " + SperlingReader.minToString((int) minRemaining));
			}
		}
		FileWriter myWriter = new FileWriter(filePath);
		String st = sb.toString();
		myWriter.write(st);
		myWriter.close();
		System.out.println("wrote to file " + filePath);
		myReader.close();
	}

	static class RunnableDemo5 implements Runnable {
		private Thread t;
		private String stateName;

		RunnableDemo5(String stateName) {
			this.stateName = stateName;
		}

		public void run() {
			try {
				processState(stateName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
}