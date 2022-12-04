package main.java.com.hey;

import main.java.com.hey.city.stats.BachelorsDegree;
import main.java.com.hey.city.stats.CostPerSquareFoot;
import main.java.com.hey.city.stats.DewPoint;
import main.java.com.hey.city.stats.FBICrimeStats;
import main.java.com.hey.city.stats.FIPSLongitudeLatitude;
import main.java.com.hey.city.stats.ForeignBorn;
import main.java.com.hey.city.stats.HomeOwnershipRate;
import main.java.com.hey.city.stats.HomeSize;
import main.java.com.hey.city.stats.Income;
import main.java.com.hey.city.stats.LaborForceParticipation;
import main.java.com.hey.city.stats.LandArea;
import main.java.com.hey.city.stats.MedianAge;
import main.java.com.hey.city.stats.MedianHomeAge;
import main.java.com.hey.city.stats.MedianHomeCost;
import main.java.com.hey.city.stats.NOAANormalsReader;
import main.java.com.hey.city.stats.Population;
import main.java.com.hey.city.stats.PopulationDensity;
import main.java.com.hey.city.stats.Poverty;
import main.java.com.hey.city.stats.RaceEthnicity;
import main.java.com.hey.city.stats.Rent;
import main.java.com.hey.city.stats.SinglePopulation;
import main.java.com.hey.city.stats.Sunshine;
import main.java.com.hey.city.stats.Thunderstorms;
import main.java.com.hey.city.stats.UnemploymentRate;
import main.java.com.hey.city.stats.WindSpeed;

public class UpdateAllColumns {
	public static void main(String[] args) throws Exception {
		FIPSLongitudeLatitude.main(null);
		Population.main(null);
		BachelorsDegree.main(null);
		DewPoint.main(null);
		FBICrimeStats.main(null);
		ForeignBorn.main(null);
		HomeOwnershipRate.main(null);
		Income.main(null);
		LaborForceParticipation.main(null);
		MedianAge.main(null);
		MedianHomeAge.main(null);
		MedianHomeCost.main(null);
		CostPerSquareFoot.main(null);
		HomeSize.main(null);
		NOAANormalsReader.main(null);
		LandArea.main(null);
		PopulationDensity.main(null);
		Poverty.main(null);
		RaceEthnicity.main(null);
		Rent.main(null);
		SinglePopulation.main(null);
		Sunshine.main(null);
		Thunderstorms.main(null);
		UnemploymentRate.main(null);
		WindSpeed.main(null);
	}
}
