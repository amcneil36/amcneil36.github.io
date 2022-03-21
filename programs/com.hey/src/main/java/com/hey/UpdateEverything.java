package main.java.com.hey;


public class UpdateEverything {

	public static void main(String[] args) throws Exception {
		
		NoopCityStats n = new NoopCityStats();
		//n.processAllStates();
		n.processAllStates();
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
