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
            
    def __init__(self, url: str):
        self.holdings: list[Holding] = self._fetch_holdings(url)
        filename = url.split('/')[-1]
        self.name: str = filename.split('.')[0]

spy = ETF("https://raw.githubusercontent.com/amcneil36/amcneil36.github.io/refs/heads/master/programs/stonks/etfs/spy.csv")
print(spy.name)
