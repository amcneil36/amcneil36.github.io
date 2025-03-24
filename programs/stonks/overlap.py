import urllib.request
from html.parser import HTMLParser

class ETF:
    def _fetch_holdings(self, url: str) -> dict[str, float]:
        response = urllib.request.urlopen(url)
        html_content = response.read().decode('utf-8')
        lines = html_content.split('\n')
        
        holdings: dict[str, float] = {}
        for line in lines:
            if len(line) == 0: # last line
                break
            parsed_line = line.split(',')
            symbol = parsed_line[1]
            if symbol == "-":
                symbol = parsed_line[0].replace(" ", "_")
            weight = float(parsed_line[2])
            holdings[symbol] = weight
        return holdings

    def __init__(self, name: str, holdings: dict[str, float] = {}):
        self.name: str = name
        if not holdings:
            url = "https://raw.githubusercontent.com/amcneil36/amcneil36.github.io/refs/heads/master/programs/stonks/etfs/" + name + ".csv"
            self.holdings: dict[str, float] = self._fetch_holdings(url)
        else:
            self.holdings: dict[str, float] = holdings
        
    def print_etf(self):
        for symbol, symbol_weight in self.holdings.items():
            print(symbol + ": " + str(symbol_weight))
    
def compute_etf_overlap(etf1: ETF, etf2: ETF, print_overlap: bool = False, include_all: bool = False) -> float:
    holdings1 = etf1.holdings
    holdings2 = etf2.holdings
    
    common_symbols = set(holdings1.keys()).intersection(holdings2.keys())
    
    total_weight1 = sum(holdings1.values())
    total_weight2 = sum(holdings2.values())
    
    overlap_data = []
    
    total_overlap = 0.0
    for symbol in common_symbols:
        weight1 = holdings1[symbol]
        weight2 = holdings2[symbol]
        
        # Calculate the overlap for the current symbol as the minimum of the two weights
        overlap_weight = min(weight1, weight2)
        total_overlap += overlap_weight
        
        # Calculate the percentage weights for ETF1 and ETF2
        percent_weight1 = (weight1 / total_weight1) * 100
        percent_weight2 = (weight2 / total_weight2) * 100
        overlap_percent = (overlap_weight / total_weight1) * 100  # Percent overlap relative to ETF1
        
        # Append the data to the list
        overlap_data.append((symbol, percent_weight1, percent_weight2, overlap_percent))
    
        # Normalize the total overlap by the total weight of both ETFs
    total_weight = total_weight1 + total_weight2
    overlap_percentage = (2 * total_overlap / total_weight) * 100  # Normalized to percentage
    overlap_percentage = round(overlap_percentage, 1)
    # Sort the data by overlap percentage in descending order
    if print_overlap:
        overlap_data.sort(key=lambda x: x[3], reverse=True)
        # Print the table header
        print(f"{'Symbol':<10} {'ETF1 % Weight':<20} {'ETF2 % Weight':<20} {'Overlap %':<20}")
        print("-" * 70)
        # Print the rows in sorted order
        num_rows = len(overlap_data)
        if not include_all:
            num_rows = min(10, num_rows)
        for i in range(num_rows):
            row = overlap_data[i]
            symbol, percent_weight1, percent_weight2, overlap_percent = row
            print(f"{symbol:<10} {percent_weight1:<20.2f} {percent_weight2:<20.2f} {overlap_percent:<20.2f}")
        print("-" * 70)
        print(f"{etf1.name} and {etf2.name} overlap: {overlap_percentage}%")
    return round(overlap_percentage, 1)
    
class Basket:
    def __init__(self, etfs: dict[str, float]):
        combined_holdings: dict[str, float] = {}
        name = "{"
        for etf_name, etf_weight in etfs.items():
            etf = ETF(etf_name)
            name+= etf_name + ": " + str(etf_weight) + ","
            for symbol, symbol_weight in etf.holdings.items():
                combined_weight = symbol_weight * etf_weight
                if symbol in combined_holdings:
                    combined_holdings[symbol] += combined_weight
                else:
                    combined_holdings[symbol] = combined_weight
        name = name[:-1] + "}"
        self.etf = ETF(name, combined_holdings)

def compute_basket_overlap(basket1: Basket, basket2: Basket, print_overlap: bool = False, include_all: bool = False) -> float:
    return compute_etf_overlap(basket1.etf, basket2.etf, print_overlap, include_all)

#etf1 = ETF("spyv")
#etf2 = ETF("spyg")
#overlap - compute_etf_overlap(etf1, etf2, True)
basket1 = Basket(etfs={'spyg': 1})
basket2 = Basket(etfs={'spyv': 1})
overlap = compute_basket_overlap(basket1, basket2, True)
