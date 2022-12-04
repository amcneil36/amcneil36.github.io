package main.java.com.hey;

import main.java.com.hey.city.stats.NoopCityStats;
import main.java.com.hey.useful.not.needed.to.modify.much.CountyCsvs;
import main.java.com.hey.useful.not.needed.to.modify.much.CountyStats;
import main.java.com.hey.useful.not.needed.to.modify.much.CreateBigCsv;
import main.java.com.hey.useful.not.needed.to.modify.much.MetroCsvs;
import main.java.com.hey.useful.not.needed.to.modify.much.MetroStats;
import main.java.com.hey.useful.not.needed.to.modify.much.StateStats;

public class UpdateEverything {

	public static void main(String[] args) throws Exception {

		NoopCityStats n = new NoopCityStats();
		n.processAllStates(); // doesn't do anything. just makes sure there is no error
		n.sleepUntilFinished();
		CreateBigCsv.main(null);
		MetroCsvs.main(null);
		MetroStats.main(null);
		CountyCsvs.main(null);
		CountyStats.main(null);
		StateStats.main(null);
		// MetroTabs.main(null);
		// CityVsUSAComparison.main(null);

	}

}
