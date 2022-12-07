package main.java.com.hey.other;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import main.java.com.hey.Util;
import main.java.com.hey.us.census.ACS2021DataReader;

public class CensusMdGenerator {

	public abstract static class AbstractBlackWomanMedianIncome extends CensusMdGeneratorGeneric {

		@Override
		protected Object[] getRowArray(Map<String, String> map) {
			int income = Integer.valueOf(map.get("B20017B_001E(median earnings by black women)"));
			double totalBlackPopulation = Integer.valueOf(map.get("B02001_003E(number of black people)"));
			double totalPopulation = Integer.valueOf(map.get(ACS2021DataReader.POPULATION));
			double percentBlack = Util.roundTwoDecimalPlaces(100 * totalBlackPopulation / totalPopulation);
			return getRowArrayCompact(map, income, totalPopulation, percentBlack);
		}

		protected Object[] getRowArrayCompact(Map<String, String> map, int income, double totalPopulation,
				double percentBlack) {
			return new Object[] { map.get("NAME"), Util.getIntFromDouble(totalPopulation), percentBlack + "%",
					"$" + income };
		}

		@Override
		protected void sortCollection(List<Map<String, String>> elementsList) {
			Collections.sort(elementsList,
					(a, b) -> Integer.valueOf(b.get("B20017B_001E(median earnings by black women)"))
							- Integer.valueOf(a.get("B20017B_001E(median earnings by black women)")));
		}

		@Override
		protected boolean removeRow(Map<String, String> map) {
			int numBlackPeople = Integer.valueOf(map.get("B02001_003E(number of black people)"));
			if (numBlackPeople < 1000 || map.get("B20017B_001E(median earnings by black women)").contains("-")) {
				return true;
			}
			return false;
		}

		@Override
		protected String[] getVariables() {
			return new String[] { "B20017B_001E(median earnings by black women)", "B02001_003E(number of black people)",
					ACS2021DataReader.POPULATION };
		}

	}

	public static void main(String[] args) throws Exception {
		AbstractBlackWomanMedianIncome place = new AbstractBlackWomanMedianIncome() {
			@Override
			protected String getFilePath() {
				return "generated/place.md";
			}

			@Override
			protected String getHeaderStr() {
				return "|Region|Population|% Black|Median income of black women|";
			}

			@Override
			protected List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception {
				return ACS2021DataReader.getPlaces(variables);
			}
		};
		place.doEverything();

		AbstractBlackWomanMedianIncome zipcode = new AbstractBlackWomanMedianIncome() {

			@Override
			protected String getFilePath() {
				return "generated/zipcode.md";
			}

			@Override
			protected String getHeaderStr() {
				return "|Zip Code|Population|% Black|Median income of black women|";
			}

			@Override
			protected List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception {
				return ACS2021DataReader.getZipCodes(variables);
			}

			@Override
			protected Object[] getRowArrayCompact(Map<String, String> map, int income, double totalPopulation,
					double percentBlack) {
				return new Object[] { map.get("zip code tabulation area"), Util.getIntFromDouble(totalPopulation),
						percentBlack + "%", "$" + income };
			}

		};
		zipcode.doEverything();
		
		AbstractBlackWomanMedianIncome schoolDistrict = new AbstractBlackWomanMedianIncome() {
			@Override
			protected String getFilePath() {
				return "generated/schoolDistrict.md";
			}

			@Override
			protected String getHeaderStr() {
				return "|School District|Population|% Black|Median income of black women|";
			}

			@Override
			protected List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception {
				return ACS2021DataReader.getSchoolDistricts(variables);
			}
		};
		schoolDistrict.doEverything();
		
		AbstractBlackWomanMedianIncome county = new AbstractBlackWomanMedianIncome() {
			@Override
			protected String getFilePath() {
				return "generated/county.md";
			}

			@Override
			protected String getHeaderStr() {
				return "|County|Population|% Black|Median income of black women|";
			}

			@Override
			protected List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception {
				return ACS2021DataReader.getCounties(variables);
			}
		};
		county.doEverything();
		
		AbstractBlackWomanMedianIncome metroMicro = new AbstractBlackWomanMedianIncome() {
			@Override
			protected String getFilePath() {
				return "generated/metroMicro.md";
			}

			@Override
			protected String getHeaderStr() {
				return "|Metro/Micro|Population|% Black|Median income of black women|";
			}

			@Override
			protected List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception {
				return ACS2021DataReader.getMetrosAndMicros(variables);
			}
		};
		metroMicro.doEverything();
		
		AbstractBlackWomanMedianIncome csa = new AbstractBlackWomanMedianIncome() {
			@Override
			protected String getFilePath() {
				return "generated/combinedStatisticalArea.md";
			}

			@Override
			protected String getHeaderStr() {
				return "|Combined Statistical Area|Population|% Black|Median income of black women|";
			}

			@Override
			protected List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception {
				return ACS2021DataReader.getCombinedStatisticalAreas(variables);
			}
		};
		csa.doEverything();
		
		AbstractBlackWomanMedianIncome state = new AbstractBlackWomanMedianIncome() {
			@Override
			protected String getFilePath() {
				return "generated/state.md";
			}

			@Override
			protected String getHeaderStr() {
				return "|State|Population|% Black|Median income of black women|";
			}

			@Override
			protected List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception {
				return ACS2021DataReader.getStates(variables);
			}
		};
		state.doEverything();
		
		AbstractBlackWomanMedianIncome region = new AbstractBlackWomanMedianIncome() {
			@Override
			protected String getFilePath() {
				return "generated/region.md";
			}

			@Override
			protected String getHeaderStr() {
				return "|Region|Population|% Black|Median income of black women|";
			}

			@Override
			protected List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception {
				return ACS2021DataReader.getRegions(variables);
			}
		};
		region.doEverything();
		
		AbstractBlackWomanMedianIncome country = new AbstractBlackWomanMedianIncome() {
			@Override
			protected String getFilePath() {
				return "generated/country.md";
			}

			@Override
			protected String getHeaderStr() {
				return "|Country|Population|% Black|Median income of black women|";
			}

			@Override
			protected List<Map<String, String>> retrieveCensusData(String[] variables) throws Exception {
				return ACS2021DataReader.getCountry(variables);
			}
		};
		region.doEverything();
		
	}

}
