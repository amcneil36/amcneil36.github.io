package main.java.com.hey.city.stats;

import main.java.com.hey.CityStats;
import main.java.com.hey.Util;

public class HeatIndex extends CityStats {

    public static final String RESULT_FORMAT = "#.#";

    public static int getHeatIndex(double temperature, double rh) {
        double heatIndex;
        
        double simpleHeatIndex = 0.5 * (temperature + 61.0 + ((temperature-68.0)*1.2) + (rh*0.094));
        double avg = (simpleHeatIndex + temperature)/2;
        if (avg < 80) {
        	return Util.getIntFromDouble(avg);
        }

        heatIndex = -42.379 + 2.04901523 * temperature + 10.14333127 * rh;
        heatIndex = heatIndex - 0.22475541 * temperature * rh - 6.83783 * Math.pow(10, -3) * temperature * temperature;
        heatIndex = heatIndex - 5.481717 * Math.pow(10, -2) * rh * rh;
        heatIndex = heatIndex + 1.22874 * Math.pow(10, -3) * temperature * temperature * rh;
        heatIndex = heatIndex + 8.5282 * Math.pow(10, -4) * temperature * rh * rh;
        heatIndex = heatIndex - 1.99 * Math.pow(10, -6) * temperature * temperature * rh * rh;
        
        if (rh < 13 && temperature >= 80 && temperature <= 112) {
        	heatIndex -= ((13-rh)/4)*Math.sqrt((17-Math.abs(temperature-95.))/17);
        }
        else if (rh > 85 && temperature >= 80 && temperature <=87) {
        	heatIndex -= ((rh-85)/10) * ((87-temperature)/5);
        }

        return Util.getIntFromDouble(heatIndex);
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
