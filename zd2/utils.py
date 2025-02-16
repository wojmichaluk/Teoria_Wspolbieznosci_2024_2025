import networkx as nx

# pomocnicze funkcje, używane w programie

#pobranie transakcji z pliku
def get_transactions(trans_file):
    # oczekiwana składnia tak, jak w przykładzie
    text = trans_file.read()
    lines = text.split("\n")
    transactions = {}

    for line in lines:
        # wyłuskanie oznaczenia transakcji
        opening_par = line.find('(')
        closing_par = line.find(')', opening_par + 1)
        transaction = line[opening_par + 1 : closing_par]

        transactions[transaction] = []
        for i in range(closing_par + 1, len(line)):
            # zakładam oznaczenia literowe zmiennych (małą literą)
            if 'a' <= line[i] <= 'z':
                transactions[transaction] += line[i]
    
    return transactions

# pobranie alfabetu z pliku
def get_alphabet(alpha_file):
    # oczekiwana składnia tak, jak w przykładzie
    text = alpha_file.read()
    alphabet = []

    for char in text:
        # zakładam oznaczenia literowe transakcji (małą literą)
        if 'a' <= char <= 'z':
            alphabet += char
    
    return sorted(alphabet)

# sprawdzenie spójności przetworzonych danych wejściowych
def check_correctness(transactions, alphabet, word):
    # sprawdzenie, czy oznaczenia transakcji są zgodne z alfabetem
    if sorted(list(transactions.keys())) != alphabet:
        return False
    
    # sprawdzenie, czy w słowie występują poprawne transakcje
    for transaction in word:
        if transaction not in alphabet:
            return False
    
    return True

# wyznaczenie relacji zależności
def get_dependent_transactions(transactions):
    keys = transactions.keys()
    dependent = set()

    for key in keys:
        # pobranie lewej strony transakcji
        lhs = transactions[key][0]

        for inner_key in keys:
            for transaction_var in transactions[inner_key]:
                if lhs == transaction_var:
                    dependent.add((key, inner_key))
                    dependent.add((inner_key, key))
                    break

    return dependent

# wyznaczenie dla każdej transakcji akcji od niej zależnych
def get_dependencies(dependent, alphabet):
    dependencies_dict = {}
    for transaction in alphabet:
        dependencies_dict[transaction] = set()

    for dependent_pair in dependent:
        first, second = dependent_pair
        if first is not second: # unikamy dodawania par z tymi samymi transakcjami
            dependencies_dict[first].add(second)
            dependencies_dict[second].add(first)

    return dependencies_dict

# algorytm górniczo-hutniczy - w skrócie agh
def agh(dependencies, word, alphabet):
    mining_shafts = {}
    for transaction in alphabet:
        mining_shafts[transaction] = []

    # zasypywanie szybów minerałami i węglem
    for transaction in word[::-1]:
        mining_shafts[transaction].append(1) # 1 oznacza "minerał"
        for dep_trans in dependencies[transaction]:
            mining_shafts[dep_trans].append(0) # 0 oznacza "węgiel"

    fnf = []

    # wykopaliska odkrywkowe
    while(True):
        minerals_list = get_minerals(mining_shafts)
        if not minerals_list:
            break 
        fnf.append(minerals_list)

        for mineral in minerals_list:
            for dep in dependencies[mineral]:
                mining_shafts[dep].pop()

    return fnf

# odkrywanie minerałów z szybów kopalnianych
def get_minerals(mining_shafts):
    minerals = []
    for key in mining_shafts.keys():
        if mining_shafts[key] and mining_shafts[key][-1] == 1:
            mining_shafts[key].pop()
            minerals.append(key)

    return minerals

# wyznaczanie postaci minimalnej grafu
def get_graph_edges(dependencies, word):
    edges = set()
    for i in range(len(word) - 1):
        for j in range(i + 1, len(word)):
            if word[j] in dependencies[word[i]] or word[i] == word[j]:
                edges.add((i + 1, j + 1))

    for i in range(1, len(word) - 1):
        for j in range(i + 1, len(word)):
            if (i, j) in edges:
                for k in range(j + 1, len(word) + 1):
                    if (i, k) in edges and (j, k) in edges:
                        edges.remove((i, k))

    return edges

# stworzenie grafu i ustalenie pozycji oraz etykiet wierzchołków
def prepare_graph_to_draw(word, graph_edges):
    # graf skierowany
    G = nx.DiGraph()

    # dodawanie wierzchołków
    mappings = {}
    for i in range(len(word)):
        G.add_node(f"{word[i]}{i}")
        mappings[i + 1] = f"{word[i]}{i}"

    # dodawanie krawędzi
    edges = set()
    for edge in graph_edges:
        edges.add((mappings[edge[0]], mappings[edge[1]]))

    # ustalanie etykiet i pozycji wierzchołków
    G.add_edges_from(edges)
    positions = {}
    labels = {}

    for i in range(len(word)):
        positions[f"{word[i]}{i}"] = [(-1) ** (i + 1), 1 - i // 2]
        labels[f"{word[i]}{i}"] = word[i]

    return G, positions, labels
