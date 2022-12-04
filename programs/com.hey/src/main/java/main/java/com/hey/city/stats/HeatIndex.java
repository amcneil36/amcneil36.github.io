package main.java.com.hey.city.stats;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class HeatIndex extends CityStats {

    public static final String RESULT_FORMAT = "#.#";

    // copied from https://www.wpc.ncep.noaa.gov/html/heatindex.shtml
    public static int getHeatIndex(double temperatureInF, double relativeHumidity) {
    	double hi = 0;
        if(relativeHumidity > 100){
            throw new RuntimeException("humidity too high");
        }
        else if (relativeHumidity < 0) {
            throw new RuntimeException("humidity too low");
        }
        else if (temperatureInF <= 40.0) {
            hi = temperatureInF;
        }
        else {
            double hitemp = 61.0+((temperatureInF-68.0)*1.2)+(relativeHumidity*0.094);
            double fptemp = (temperatureInF);
            double hifinal = 0.5*(fptemp+hitemp);

            if(hifinal > 79.0){
                hi = -42.379+2.04901523*temperatureInF+10.14333127*relativeHumidity-0.22475541*temperatureInF*relativeHumidity-6.83783*(Math.pow(10, -3))*(Math.pow(temperatureInF, 2))-5.481717*(Math.pow(10, -2))*(Math.pow(relativeHumidity, 2))+1.22874*(Math.pow(10, -3))*(Math.pow(temperatureInF, 2))*relativeHumidity+8.5282*(Math.pow(10, -4))*temperatureInF*(Math.pow(relativeHumidity, 2))-1.99*(Math.pow(10, -6))*(Math.pow(temperatureInF, 2))*(Math.pow(relativeHumidity,2));
                if((relativeHumidity <= 13) && (temperatureInF >= 80.0) && (temperatureInF <= 112.0)) {
                    double adj1 = (13.0-relativeHumidity)/4.0;
                    double adj2 = Math.sqrt((17.0-Math.abs(temperatureInF-95.0))/17.0);
                    double adj = adj1 * adj2;
                    hi = hi - adj;
                }
                else if ((relativeHumidity > 85.0) && (temperatureInF >= 80.0) && (temperatureInF <= 87.0)) {
                    double adj1 = (relativeHumidity-85.0)/10.0;
                    double adj2 = (87.0-temperatureInF)/5.0;
                    double adj = adj1 * adj2;
                    hi = hi + adj;
                }
            }
            else{
                hi = hifinal;
            }
        }

        return Util.getIntFromDouble(hi);
    }

	public static void main(String[] args) throws Exception {
		HeatIndex hi = new HeatIndex();
		hi.processAllStates();
		// TODO Auto-generated method stub

	}

	// this should maybe go in the RH folder? RH doesn't know which month is the hottest though.
	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		data.hottestMonthsHeatIndexHigh = "N/A";
		if (data.hottestMonthsHigh.equals("N/A") || data.summerHumidityPercent.equals("N/A")) {
			return;
		}
		double rh = Double.valueOf(data.summerHumidityPercent.replace("%", ""));
		double temp = Double.valueOf(data.hottestMonthsHigh);
		data.hottestMonthsHeatIndexHigh = String.valueOf(getHeatIndex(temp, rh));
		
	}

}
