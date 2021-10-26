package main.java.com.hey;

import java.util.concurrent.CountDownLatch;

import main.java.com.hey.old.CityVsUSAComparison2;

public class UpdateEverything {

	public static void main(String[] args) throws Exception {
		
		NoopCityStats.main(null);
		Thread.sleep(1000*10); //sleep 10s so NoopCityStats can finish
		CreateBigCsv.main(null);
		MetroCsvs.main(null);
		MetroStatistics.main(null);
		MetroTabs.main(null);
		CityVsUSAComparison2.main(null);

	}

}
