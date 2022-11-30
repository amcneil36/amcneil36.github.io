package main.java.com.hey.summaries;

import static main.java.com.hey.CityStats.AGE;
import static main.java.com.hey.CityStats.ANNUAL_DAYS_OF_PRECIPITATION;
import static main.java.com.hey.CityStats.ANNUAL_DAYS_OF_SUNSHINE;
import static main.java.com.hey.CityStats.ANNUAL_RAINFALL;
import static main.java.com.hey.CityStats.ANNUAL_SNOWFALL;
import static main.java.com.hey.CityStats.ASIAN;
import static main.java.com.hey.CityStats.AVERAGE_SUMMER_DEW_POINT;
import static main.java.com.hey.CityStats.BACHELORS;
import static main.java.com.hey.CityStats.BLACK;
import static main.java.com.hey.CityStats.COLDEST_MONTH;
import static main.java.com.hey.CityStats.COST_PER_SQFT;
import static main.java.com.hey.CityStats.DEMOCRAT;
import static main.java.com.hey.CityStats.DEW_POINT;
import static main.java.com.hey.CityStats.FOREIGN_BORN;
import static main.java.com.hey.CityStats.HISPANIC;
import static main.java.com.hey.CityStats.HOMEOWNERSHIP_RATE;
import static main.java.com.hey.CityStats.HOME_PRICE;
import static main.java.com.hey.CityStats.HOME_SQFT;
import static main.java.com.hey.CityStats.HOTTEST_MONTH;
import static main.java.com.hey.CityStats.INCOME;
import static main.java.com.hey.CityStats.LABOR_FORCE;
import static main.java.com.hey.CityStats.POPULATION_DENSITY;
import static main.java.com.hey.CityStats.POVERTY_RATE;
import static main.java.com.hey.CityStats.PROPERTY_CRIMES_FBI;
import static main.java.com.hey.CityStats.REPUBLICAN;
import static main.java.com.hey.CityStats.SINGLE_POPULATION;
import static main.java.com.hey.CityStats.UV_INDEX;
import static main.java.com.hey.CityStats.VIOLENT_CRIMES_FBI;
import static main.java.com.hey.CityStats.WHITE;
import static main.java.com.hey.CityStats.WIND_SPEED;

import java.util.Map;

import main.java.com.hey.CityStats;

public class MetroStats extends MetroStatsSuper {


	public static void main(String[] args) throws Exception {
		MetroStats metroStats = new MetroStats();
		metroStats.performStuff();
	}

}
