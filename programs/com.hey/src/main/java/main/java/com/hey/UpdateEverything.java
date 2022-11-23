package main.java.com.hey;

import main.java.com.hey.CityVsUsa.CityVsUSAComparison;
import main.java.com.hey.city.stats.NoopCityStats;
import main.java.com.hey.summaries.CountyCsvs;
import main.java.com.hey.summaries.CountyStats;
import main.java.com.hey.summaries.CreateBigCsv;
import main.java.com.hey.summaries.MetroCsvs;
import main.java.com.hey.summaries.MetroStats;
import main.java.com.hey.summaries.StateStats;

public class UpdateEverything {

	public static void main(String[] args) throws Exception {
		
		NoopCityStats n = new NoopCityStats();
		n.processAllStates(); // doesn't do anything. just makes sure there is no error
		int totalMsSlept = 0;
		while(n.numStatesComplete < 51) {
			if (totalMsSlept > 10000) {
				throw new RuntimeException("took too long for NoopCityStats");
			}
			Thread.sleep(100);
			totalMsSlept+=100;
		}
		CreateBigCsv.main(null);
		MetroCsvs.main(null);
		MetroStats.main(null);
		CountyCsvs.main(null);
		CountyStats.main(null);
		StateStats.main(null);
	//	MetroTabs.main(null);
		CityVsUSAComparison.main(null);

	}

}
