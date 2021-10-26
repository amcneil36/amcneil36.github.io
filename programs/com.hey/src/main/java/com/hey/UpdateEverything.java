package main.java.com.hey;

import main.java.com.hey.old.CityVsUSAComparison2;

public class UpdateEverything {

	public static void main(String[] args) throws Exception {
		
		// noop city stats would be nice but see how to wait
		CreateBigCsv.main(null);
		MetroCsvs.main(null);
		MetroStatistics.main(null);
		MetroTabs.main(null);
		CityVsUSAComparison2.main(null);

	}

}
