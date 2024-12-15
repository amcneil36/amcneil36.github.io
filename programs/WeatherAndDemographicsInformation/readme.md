# USA Weather and Demographics Information
This webpage has USA weather and demographics data .csv's, which can be viewed at various levels of granularity: city, county, metro, and state. It can tell you things like, for example, what is the median household income in Los Angeles, California? Or, what is the homeownership rate in Pinellas County?

All geographies larger than a city have calculations done by using weighted averages of the city data except for the population column (which is a sum). This is so a city of 10 people doesn't have the same influence as a city of 1,000,000 people. This makes the statistics more representative of a typical person living there.

|Granularity|Link|
|---|---|
|City|<a href="https://amcneil36.github.io/programs/CityStats/CityStats.csv" download>https://amcneil36.github.io/programs/CityStats/CityStats.csv</a>|
|County|<a href="https://amcneil36.github.io/programs/CountyStats/CountyStats.csv" download>https://amcneil36.github.io/programs/CountyStats/CountyStats.csv</a>|
|Metro|<a href="https://amcneil36.github.io/programs/MetroStats/MetroStats.csv" download>https://amcneil36.github.io/programs/MetroStats/MetroStats.csv</a>|
|State|<a href="https://amcneil36.github.io/programs/StateStats/StateStats.csv" download>https://amcneil36.github.io/programs/StateStats/StateStats.csv</a>|

## Datapoints

| Column name | Description (if necessary) | Data source | Date updated |
| ----------- | ----------- | ----------- | ----------- |
| City      | The city/town/CDP that all of the data in the row corresponds to. | [Best Places](https://www.bestplaces.net/)| Dec 2021 |
| State   |    | [Best Places](https://www.bestplaces.net/) | Feb 2021 |
| Population |  | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| People per sq mi |  | [US Census](https://www.census.gov/en.html) | Dec 2023 |
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
| Median age | | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| % with at least Bachelor's | Percent of residents over the age of 25 with a Bachelor's degree. Includes those who have a graduate degree. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| Median household income | | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| Poverty Rate | The percent of families who are in poverty. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| Avg Apartment Monthly Rent | The average monthly cost of rent for apartments. | [Rent Cafe](https://www.rentcafe.com/) | Sep 2021 |
| Avg Apartment Sqft | The average size of an apartment in square feet. | [Rent Cafe](https://www.rentcafe.com/) | Sep 2021 |
| Median home price | | [Redfin](https://www.redfin.com/) | Sep 2021 |
| Median home square feet | | [Redfin](https://www.redfin.com/) | Sep 2021 |
| Median home cost per sq ft | | [Redfin](https://www.redfin.com/) | Sep 2021 |
| Median home age | | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| Homeownership Rate | |[US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| Air quality index | The quality of the air. Ranked from 1 (worst quality) to 100 (best quality). | [Best Places](https://www.bestplaces.net/) | Feb 2021 |
| Unemployment rate | | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| County   |  | [Best Places](https://www.bestplaces.net/)| Feb 2021 |
| % Democrat | The percent of residents who are democrat. Tracked at the county level. | [Best Places](https://www.bestplaces.net/) | Feb 2021 |
| % Republican | The percent of residents who are republican. Tracked at the county level. | [Best Places](https://www.bestplaces.net/) | Feb 2021 |
| % Asian | The percent of residents who identified as Asian only. This includes Hispanic and non-hispanic Asian residents. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| % Black | The percent of residents who identified as Black only. This includes Hispanic and non-hispanic Black residents. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| % Non-Hispanic White | The percent of residents who identified as White only. This does not include hispanic residents. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| % Hispanic | The percent of residents who identified as Hispanic. This includes Hispanic residents of any race. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| Foreign Born % | The percent of residents born outside of the United States. | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |
| Timezone | | [Best Places](https://www.bestplaces.net/) | Sep 2021 |
| Elevation (ft) | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| UV Index | | [Best Places](https://www.bestplaces.net/) | Sep 2021 |  
| Single Population | The percent of residents who are single (not married). | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |  
| Num Violent Crimes Per 100k residents | | [FBI](https://fbi.gov/) | Sep 2021 |  
| Num Property Crimes Per 100k residents | | [FBI](https://fbi.gov/) | Sep 2021 |  
| Labor Force Participation rate | | [US Census ACS](https://www.census.gov/programs-surveys/acs) | Dec 2023 |  
| Hottest month's avg low (F) | | [NOAA](https://www.noaa.gov/) | Sep 2021 |  
| Coldest month's avg low (F) | | [NOAA](https://www.noaa.gov/) | Sep 2021 |  
| Annual Relative Humidity (afternoon) | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Summer Relative Humidity (afternoon) | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Hottest month's avg heat index high (F) | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Annual Sunshine - Percentage of Possible | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Summer Sunshine - Percentage of Possible | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Winter Sunshine - Percentage of Possible | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Summer rainfall (in) | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Winter rainfall (in) | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Percent of days that include precipitation | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Percent of Summer days that include precipitation | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Percent of Winter days that include precipitation | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Days of snow per year | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Number of days with thunder per year | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Land Area (sq mi) |  | [US Census](https://www.census.gov/en.html) | Feb 2021 |
| Fips Code |  | [US Census](https://www.census.gov/en.html) | Feb 2021 |
| Latitude | | [NOAA](https://www.noaa.gov/) | Sep 2021 |
| Longitude | | [NOAA](https://www.noaa.gov/) | Sep 2021 |

## City vs USA Comparison (webpage)
[CityVsUSAComparison](https://amcneil36.github.io/programs/cityVsUSAComparison/cityVsUSAComparison.html) shows which percentiles that each city's weather and demographics data fall under (in comparison to the rest of the USA cities). It uses the data from most of the columns from CityStats.csv.

## Additional Information
Data is retrieved for all USA cities, towns, and CDPs (Census Designated Places). In an effort to be less verbose with word choice, I refer to cities, towns, and CDPs all as cities instead of trying to make a distinction between them. Note that cities, towns, and CDPs are all separate entities. So a city will not be part of a town or CDP and vice-versa.

A value of N/A in a cell means that the data source did not find the requested data for the city. This is more common in lesser populated cities.

Most of my data sources have indicated that their data may only be used for personal use and not commercial use. Thus, I have made these files for personal use only (I make no money from it in any way). Others may use these files for personal use as well. Please do not use any of these files for commercial use since my data sources prohibit people from using the data for commercial use.
