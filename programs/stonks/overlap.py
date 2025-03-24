import urllib.request
from html.parser import HTMLParser
import numpy as np

class ETF:
    def _fetch_holdings(self, url: str) -> dict[str, float]:
        response = urllib.request.urlopen(url)
        html_content = response.read().decode('utf-8')
        lines = html_content.split('\n')
        
        holdings: dict[str, float] = {}
        for line in lines:
            if len(line) == 0: # last line
                break
            line = line.replace('\ufeff', '')
            parsed_line = line.split(',')
            symbol = parsed_line[0]
            if symbol == "":
                symbol = parsed_line[1].replace(" ", "_")
            weight = float(parsed_line[2])
            holdings[symbol] = weight
        return holdings

    def __init__(self, name: str, holdings: dict[str, float] = {}):
        self.name: str = name.upper()
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
        overlap_weight = min(weight1, weight2)
        total_overlap += overlap_weight
        percent_weight1 = (weight1 / total_weight1) * 100
        percent_weight2 = (weight2 / total_weight2) * 100
        overlap_percent = (overlap_weight / total_weight1) * 100  # Percent overlap relative to ETF1
        overlap_data.append((symbol, percent_weight1, percent_weight2, overlap_percent))

    total_weight = total_weight1 + total_weight2
    overlap_percentage = (2 * total_overlap / total_weight) * 100  # Normalized to percentage
    overlap_percentage = round(overlap_percentage, 1)

    common_in_etf1 = set(holdings1.keys()).intersection(set(holdings2.keys()))
    common_in_etf2 = set(holdings2.keys()).intersection(set(holdings1.keys()))
    
    if print_overlap:
        overlap_data.sort(key=lambda x: x[3], reverse=True)
        num_rows = len(overlap_data)
        if not include_all:
            num_rows = min(10, num_rows)
        # Print the table header
        if num_rows == 10 and not include_all:
            print(f"Top 10 holdings in both {etf1.name} and {etf2.name}:")
        else:
            print(f"All holdings in both {etf1.name} and {etf2.name}:")
        print("")
        print(f"{'Symbol':<10} {'ETF1 % Weight':<20} {'ETF2 % Weight':<20} {'Overlap %':<20}")
        print("-" * 70)
        for i in range(num_rows):
            row = overlap_data[i]
            symbol, percent_weight1, percent_weight2, overlap_percent = row
            print(f"{symbol:<10} {percent_weight1:<20.2f} {percent_weight2:<20.2f} {overlap_percent:<20.2f}")
        
        print("-" * 70)
        print(f"{etf1.name} and {etf2.name} overlap: {overlap_percentage}%")
        
        # Print the number of unique symbols
        print(f"Number of symbols in {etf1.name} that are also in {etf2.name}: {len(common_in_etf1)} of {len(etf1.holdings)} ({100*round(len(common_in_etf1)/len(etf1.holdings),1)}%)")
        print(f"Number of symbols in {etf2.name} that are also in {etf1.name}: {len(common_in_etf2)} of {len(etf2.holdings)} ({100*round(len(common_in_etf2)/len(etf2.holdings),1)}%)")
    
    return round(overlap_percentage, 1)
    
class Basket:
    def __init__(self, etfs: dict[str, float]):
        holdings = []
        
        name = "{"
        for etf_name, etf_weight in etfs.items():
            etf = ETF(etf_name)
            name+= etf_name + "=" + str(100*etf_weight) + "%,"
            tup = (etf, etf_weight)
            holdings.append(tup)
        self.name = name[:-1] + "}"
        self.holdings: list[(Etf, float)] = holdings

    def to_combined_etf(self)->ETF:
        combined_holdings: dict[str, float] = {}
        for etf, etf_weight in self.holdings:
            for symbol, symbol_weight in etf.holdings.items():
                combined_weight = symbol_weight * etf_weight
                if symbol in combined_holdings:
                    combined_holdings[symbol] += combined_weight
                else:
                    combined_holdings[symbol] = combined_weight
        return ETF(self.name, combined_holdings)        

def compute_basket_overlap(basket1: Basket, basket2: Basket, print_overlap: bool = False, include_all: bool = False) -> float:
    return compute_etf_overlap(basket1.to_combined_etf(), basket2.to_combined_etf(), print_overlap, include_all)

def compute_weights(etf_to_recreate: str, etfs_to_use_to_recreate: list[str], print_output: bool = False) -> dict[str, float]:
    # Step 1: Create an ETF object for the target ETF using the name provided
    target_etf = ETF(etf_to_recreate)
    target_holdings = target_etf.holdings
    symbols = list(target_holdings.keys())
    
    # Step 2: Prepare the weight matrix for the ETFs to use to recreate the target ETF
    weight_matrix = []
    for etf_name in etfs_to_use_to_recreate:
        etf = ETF(etf_name)
        etf_holdings = etf.holdings
        etf_weights = [etf_holdings.get(symbol, 0) for symbol in symbols]
        weight_matrix.append(etf_weights)
    
    # Convert the weight matrix to a numpy array for matrix operations
    weight_matrix = np.array(weight_matrix).T  # Transpose so that rows are symbols and columns are ETFs

    # Step 3: Solve the least squares problem to find the weights for each ETF
    target_values = np.array([target_holdings.get(symbol, 0) for symbol in symbols])
    
    # We want to minimize the difference: weight_matrix * x = target_values, where x are the ETF weights
    # Use numpy's lstsq (least squares) function to solve this
    weights, _, _, _ = np.linalg.lstsq(weight_matrix, target_values, rcond=None)

    # Step 4: Normalize the weights to add up to 100%
    total_weight = np.sum(weights)
    normalized_weights = weights / total_weight * 100  # Convert to percentage
    
    # Step 5: Return the weights as a dictionary, keyed by ETF name
    weight_dict = {etfs_to_use_to_recreate[i]: round(normalized_weights[i], 2) for i in range(len(etfs_to_use_to_recreate))}

    if print_output:
        # Step 6: Print out the relevant details if print_output is True
        print(f"Re-creating {etf_to_recreate} from the {", ".join(etfs_to_use_to_recreate)}...")
        print("-" * 70)
        for key, value in weight_dict.items():
            print(f"{key}: {value}%")
        print("-" * 70)
        # Step 7: Compute overlap between the target ETF and the weighted combination
        combined_etf = Basket({etf_name: weight / 100 for etf_name, weight in weight_dict.items()}).to_combined_etf()
        overlap_percentage = compute_etf_overlap(target_etf, combined_etf)
        print(f"Percent overlap with {etf_to_recreate}: {overlap_percentage}%")
    
    return weight_dict


# compute_etf_overlap(ETF("VIOG"), ETF("VIOV"), True, False)
# compute_weights('VTI', ['SPYG', 'SPYV', 'IVOG', 'IVOV', 'VIOG', 'VIOV'], True)
# compute_weights('VTI', ['VOX', 'VCR', 'VDC', 'VDE', 'VFH', 'VHT', 'VIS', 'VGT', 'VAW', 'VNQ', 'VPU'], True)

#basket1 = Basket(etfs={'VTI': 1})
#basket2 = Basket(etfs={'SPY': 0.85, 'IVOO': 0.1, 'VIOO': 0.05})
#overlap = compute_basket_overlap(basket1, basket2, True)
