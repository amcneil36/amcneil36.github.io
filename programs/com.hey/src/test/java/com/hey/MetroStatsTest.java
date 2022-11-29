package com.hey;

import java.util.List;

import org.junit.Test;

import org.junit.Assert;
import main.java.com.hey.CityStats;
import main.java.com.hey.CityStats.Data;
import main.java.com.hey.CityStatsSuper.AndrewStringWriter;
import main.java.com.hey.city.stats.BachelorsDegree;
import main.java.com.hey.summaries.MetroStats;
import main.java.com.hey.summaries.MetroStats.Stats;

public class MetroStatsTest {

	/*@Test
	public void foo() {
		MetroStats ms = new MetroStats();
		String header = ms.getHeader();
		String expected = "Metro Name,Predominant State,Metro population,People per sq mi,Hottest month's avg high (F),Coldest month's avg high (F),Annual rainfall (in),Annual days of precipitation,Annual days of sunshine,Annual snowfall (in),Avg Summer Dew Point,Avg Annual Dew Point,Average yearly windspeed (mph),Violent crime index,Property crime index,Median age,% with at least Bachelor's degree,Median household income,Poverty Rate,Median home price,Median home sqft,Median home cost per sqft,Homeownership Rate,Population growth since 2010,% Democrat,% Republican,% Asian,% Black,% Non-Hispanic White,% Hispanic,Foreign Born %,UV Index,Single Population,% of income spent on housing costs (owners),Number of sex offenders per 10k residents,Predominant Timezone,Num Violent Crimes Per 100k residents,Num Property Crimes Per 100k residents,Labor Force Participation rate";
		Assert.assertEquals(header, expected);
	}*/
	
	/*@Test
	public void foo2() throws Exception {
		CityStats cs = new BachelorsDegree();
		List<Data> list = cs.readData("Florida");
		Data data = list.get(12);
		MetroStats ms = new MetroStats();
		Stats stats = new Stats();
		ms.addStuffToStats(stats, data);
		AndrewStringWriter sb = new AndrewStringWriter();
		ms.addToSb(sb, stats);
		String actual = sb.getString();
		String expected = "1194,90,70,51,100,241,0,73,63,N/A,20,40,44,43%,$95980,3%,$443000,2023,$219,83%,33%,51%,44%,2%,12%,65%,15%,9%,6,35%,19%,8,Eastern Standard Time,N/A,N/A,65%,";
		Assert.assertEquals(actual, expected);
	}*/
}
