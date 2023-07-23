package main.java.com.hey;

import main.java.com.hey.city.stats.BachelorsDegree;
import main.java.com.hey.city.stats.ForeignBorn;
import main.java.com.hey.city.stats.Income;
import main.java.com.hey.city.stats.LaborForceParticipation;
import main.java.com.hey.city.stats.MedianAge;
import main.java.com.hey.city.stats.MedianHomeAge;
import main.java.com.hey.city.stats.Population;
import main.java.com.hey.city.stats.PopulationDensity;
import main.java.com.hey.city.stats.Poverty;
import main.java.com.hey.city.stats.RaceEthnicity;
import main.java.com.hey.city.stats.Rent;
import main.java.com.hey.city.stats.SinglePopulation;
import main.java.com.hey.city.stats.UnemploymentRate;
import main.java.com.hey.other.location.HomeownershipRate;

public class CensusUpdater {

	public static void main(String[] args) throws Exception {
		Population.main(null);
		BachelorsDegree.main(null);
		ForeignBorn.main(null);
		HomeownershipRate.main(null);
		Income.main(null);
		LaborForceParticipation.main(null);
		MedianAge.main(null);
		MedianHomeAge.main(null);
		PopulationDensity.main(null);
		//think about metro pop
		Poverty.main(null);
		RaceEthnicity.main(null);
		Rent.main(null);
		SinglePopulation.main(null);
		UnemploymentRate.main(null);
	}
}
