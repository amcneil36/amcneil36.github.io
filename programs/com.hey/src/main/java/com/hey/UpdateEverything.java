package main.java.com.hey;


public class UpdateEverything {

	public static void main(String[] args) throws Exception {
		
		NoopCityStats.main(null);
		Thread.sleep(1000*10); //sleep 10s so NoopCityStats can finish
		CreateBigCsv.main(null);
		MetroCsvs.main(null);
		MetroStatistics.main(null);
		MetroTabs.main(null);
		CityVsUSAComparison.main(null);

	}

}
