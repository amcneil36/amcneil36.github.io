# Weather and Demographics Information for all USA cities

## Weather and Demographics data calculated on the city level (CityStats.csv)
CityStats.csv is a .csv file with weather and demographics information for all USA cities. The .csv file can be found here: <a href="https://amcneil36.github.io/programs/CityStats/CityStats.csv" download>https://amcneil36.github.io/programs/CityStats/CityStats.csv</a>

## Weather and Demographics data calculated on the county level (CountyStats.csv)
CountyStats.csv is a .csv file with weather and demographics data for all USA counties. All data is on the county level. This means that you can see things like the average summer high for the county. All numbers in this .csv (including population density) are calculated from using a weighted average except for the county population column. The county population is just a sum of the population of all cities in the county. The weighted average is used for the rest of the columns so that a city of 10 people has less influence on the data than a city of 1,000,000 people. This is so the statistics are representative of a typical person living there. The data for this .csv was calculated from using the data from CityStats.csv. CountyStats.csv can be found here: <a href="https://amcneil36.github.io/programs/CountyStats/CountyStats.csv" download>https://amcneil36.github.io/programs/CountyStats/CountyStats.csv</a>

## Weather and Demographics data calculated on the metro level (MetroStats.csv)
This .csv is very similar to CountyStats.csv exept the data is provided on the metro level as opposed to the county level. MetroStats.csv can be found here: <a href="https://amcneil36.github.io/programs/MetroStats/MetroStats.csv" download>https://amcneil36.github.io/programs/MetroStats/MetroStats.csv</a>

## Weather and Demographics data calculated on the State level (StateStats.csv)
This .csv is very similar to CountyStats.csv exept the data is provided on the State level as opposed to the county level. StateStats.csv can be found here: <a href="https://amcneil36.github.io/programs/StateStats/StateStats.csv" download>https://amcneil36.github.io/programs/StateStats/StateStats.csv</a>

## Datapoints

| Column name | Description (if necessary) | Data source | Date updated |
| ----------- | ----------- | ----------- | ----------- |
| City      | The city/town/CDP that all of the data in the row corresponds to. | [Best Places](https://www.bestplaces.net/)| Feb 2021 |
| State   |    | [Best Places](https://www.bestplaces.net/) | Feb 2021 |
| Population |  | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Feb 2021 |
| People per sq mi |  | [US Census](https://www.census.gov/en.html) | Feb 2021 |
| Metro Name | The name of the Metropolitan Statistical Area (MSA). | [Best Places](https://www.bestplaces.net/) | Sep 2021 |
| Metro Population | The population of the Metropolitan Statistical Area (MSA). | [Best Places](https://www.bestplaces.net/) | Sep 2021 |
| Hottest month's avg high (F) | Average high for the hottest month based on historical data. | [NOAA](https://www.noaa.gov/) | Oct 2021 |
| Coldest month's avg high (F) | Average high for the coldest month based on historical data. | [NOAA](https://www.noaa.gov/) | Oct 2021 |
| Hottest high minus coldest high | The average high for the hottest month minus the average high for the coldest month. | [NOAA](https://www.noaa.gov/) | Oct 2021 |
| Annual rainfall (in) | The amount of inches of rain per year based on historical data. | [NOAA](https://www.noaa.gov/) | Feb 2021 |
| Annual days of precipitation | | [NOAA](https://www.noaa.gov/) | Feb 2021 |
| Annual snowfall (in) | | [NOAA](https://www.noaa.gov/) | Feb 2021 |
| Avg Summer Dew Point | The average summer dew point. This number is calculated by averaging the dew points from July, August, and September. | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Avg Annual Dew Point | The average dew point for the year. | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Average yearly windspeed (mph) | The average windspeed from averaging the average windspeeds for all 12 months. | [NOAA](https://www.noaa.gov/) | Mar 2021 |
| Median age | | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Feb 2021 |
| % with at least Bachelor's | Percent of residents over the age of 25 with a Bachelor's degree. Includes those who have a graduate degree. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Mar 2021 |
| Median household income | | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Feb 2021 |
| Poverty Rate | The percent of families who are in poverty. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Sep 2021 |
| Avg Apartment Monthly Rent | The average monthly cost of rent for apartments. | [Rent Cafe](https://www.rentcafe.com/) | Sep 2021 |
| Avg Apartment Sqft | The average size of an apartment in square feet. | [Rent Cafe](https://www.rentcafe.com/) | Sep 2021 |
| Median home price | | [realtor.com](https://www.realtor.com/) | Sep 2021 |
| Median home square feet | | [realtor.com](https://www.realtor.com/) | Sep 2021 |
| Median home cost per sq ft | | [realtor.com](https://www.realtor.com/) | Sep 2021 |
| Median home age | | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Feb 2021 |
| Homeownership Rate | |[US Census ACS](https://www.census.gov/programs-surveys/acs) | Sep 2021 |
| Air quality index | The quality of the air. Ranked from 1 (worst quality) to 100 (best quality). | [Best Places](https://www.bestplaces.net/) | Feb 2021 |
| Unemployment rate | | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Feb 2021 |
| County   |  | [Best Places](https://www.bestplaces.net/)| Feb 2021 |
| % Democrat | The percent of residents who are democrat. Tracked at the county level. | [Best Places](https://www.bestplaces.net/) | Feb 2021 |
| % Republican | The percent of residents who are republican. Tracked at the county level. | [Best Places](https://www.bestplaces.net/) | Feb 2021 |
| % Asian | The percent of residents who identified as Asian only. This includes Hispanic and non-hispanic Asian residents. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Nov 2021 |
| % Black | The percent of residents who identified as Black only. This includes Hispanic and non-hispanic Black residents. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Nov 2021 |
| % Non-Hispanic White | The percent of residents who identified as White only. This does not include hispanic residents. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Nov 2021 |
| % Hispanic | The percent of residents who identified as Hispanic. This includes Hispanic residents of any race. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Nov 2021 |
| Foreign Born % | The percent of residents born outside of the United States. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Sep 2021 |
| Timezone | | [Best Places](https://www.bestplaces.net/) | Sep 2021 |
| Elevation (ft) | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| UV Index | | [Best Places](https://www.bestplaces.net/) | Sep 2021 |
| Single Population | The percent of residents who are single (not married). | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Sep 2021 |

## City vs USA Comparison (webpage)
[CityVsUSAComparison](https://amcneil36.github.io/programs/cityVsUSAComparison/cityVsUSAComparison.html) shows which percentiles that each city's weather and demographics data fall under (in comparison to the rest of the USA cities). It uses the data from most of the columns from CityStats.csv.

## Additional Information
Data is retrieved for all USA cities, towns, and CDPs (Census Designated Places). In an effort to be less verbose with word choice, I refer to cities, towns, and CDPs all as cities instead of trying to make a distinction between them. Note that cities, towns, and CDPs are all separate entities. So a city will not be part of a town or CDP and vice-versa.

A value of N/A in a cell means that the data source did not find the requested data for the city. This is more common in lesser populated cities.

Most of my data sources have indicated that their data may only be used for personal use and not commercial use. Thus, I have made these files for personal use only (I make no money from it in any way). Others may use these files for personal use as well. Please do not use any of these files for commercial use since my data sources prohibit people from using the data for commercial use.
