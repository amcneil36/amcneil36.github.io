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
            symbol = parsed_line[0]
            weight = float(parsed_line[1])
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

def compute_overlap(etf1: ETF, etf2: ETF) -> float:
    # Get the holdings dictionaries (symbols as keys and weights as values) for both ETFs
    holdings1 = etf1.get_holdings()
    holdings2 = etf2.get_holdings()
    
    # Find the common symbols between the two ETFs
    common_symbols = set(holdings1.keys()).intersection(holdings2.keys())
    
    # Calculate the total weight in both ETFs
    total_weight1 = sum(holdings1.values())
    total_weight2 = sum(holdings2.values())
    
    # Calculate the overlap
    total_overlap = 0.0
    for symbol in common_symbols:
        weight1 = holdings1[symbol]
        weight2 = holdings2[symbol]
        total_overlap += min(weight1, weight2)
    
    # Normalize the total overlap by the total weight of both ETFs
    total_weight = total_weight1 + total_weight2
    overlap_percentage = (2 * total_overlap / total_weight) * 100  # Normalized to percentage
    
    return overlap_percentage

etf1 = ETF("spyg")
etf2 = ETF("spyv")

# Calculate the weighted overlap
overlap = compute_overlap(etf1, etf2)
print(f"Weighted overlap percentage: {overlap}%")
