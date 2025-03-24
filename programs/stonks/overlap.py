import urllib.request
from html.parser import HTMLParser

class Holding:
    def __init__(self, symbol: str, weight: float):
        self.symbol = symbol
        self.weight = weight

class ETF:
    
    def _fetch_holdings(self, url: str) -> list[Holding]:
        response = urllib.request.urlopen(url)
        html_content = response.read().decode('utf-8')
        lines = html_content.split('\n')
        
        holdings: list[Holding] = []
        for line in lines:
            if len(line) == 0: # last line
                break
            parsed_line = line.split(',')
            symbol = parsed_line[1]
            if symbol == "-":
                symbol = parsed_line[0].replace(" ", "_")
            weight = float(parsed_line[2])
            holding = Holding(symbol, weight)
            holdings.append(holding)
        return holdings
            
    def __init__(self, name: str):
        url = "https://raw.githubusercontent.com/amcneil36/amcneil36.github.io/refs/heads/master/programs/stonks/etfs/" + name + ".csv"
        self.holdings: list[Holding] = self._fetch_holdings(url)
        filename = url.split('/')[-1]
        self.name: str = name
        
    def get_holdings(self) -> dict[str, float]:
        """Returns a dictionary with symbols as keys and their respective weights as values."""
        return {holding.symbol: holding.weight for holding in self.holdings}
        
def compute_etf_overlap(etf1: ETF, etf2: ETF) -> float:
    # Get the holdings dictionaries (symbols as keys and weights as values) for both ETFs
    holdings1 = etf1.get_holdings()
    holdings2 = etf2.get_holdings()
    
    # Find the common symbols between the two ETFs
    common_symbols = set(holdings1.keys()).intersection(holdings2.keys())
    
    # Calculate the total weight in both ETFs
    total_weight1 = sum(holdings1.values())
    total_weight2 = sum(holdings2.values())
    
    # Create a list to store the data for each common symbol
    overlap_data = []
    
    # Calculate the overlap and store the data for each common symbol
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
    
    # Sort the data by overlap percentage in descending order
    overlap_data.sort(key=lambda x: x[3], reverse=True)
    
    # Print the table header
    print(f"{'Symbol':<10} {'ETF1 % Weight':<20} {'ETF2 % Weight':<20} {'Overlap %':<20}")
    print("-" * 70)
    
    # Print the rows in sorted order
    for row in overlap_data:
        symbol, percent_weight1, percent_weight2, overlap_percent = row
        print(f"{symbol:<10} {percent_weight1:<20.2f} {percent_weight2:<20.2f} {overlap_percent:<20.2f}")
    
    # Normalize the total overlap by the total weight of both ETFs
    total_weight = total_weight1 + total_weight2
    overlap_percentage = (2 * total_overlap / total_weight) * 100  # Normalized to percentage
    
    return round(overlap_percentage, 1)
    

etf1 = ETF("spyg")
etf2 = ETF("spyv")

# Calculate the weighted overlap
overlap = compute_etf_overlap(etf1, etf2)
print(f"Weighted overlap percentage: {overlap}%")

# Define two baskets
#basket1 = Basket(name="Basket 1", etfs={'spyg': 0.5, 'spyv': 0.5})
#basket2 = Basket(name="Basket 2", etfs={'spy': 1})

# Compute the overlap between the two baskets
#overlap = compute_basket_overlap(basket1, basket2)
#print(f"Basket Overlap: {overlap}%")
