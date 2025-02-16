import sys
import networkx as nx
import matplotlib.pyplot as plt

from utils import get_transactions, get_alphabet, check_correctness 
from utils import get_dependent_transactions, get_dependencies, agh
from utils import get_graph_edges, prepare_graph_to_draw 

if __name__ == '__main__':
    # sprawdzenie poprawności uruchomienia programu
    if len(sys.argv) != 4:
        print("Nieprawidłowa liczba argumentów uruchomienia programu!")
        print("Oczekiwana postać uruchomienia programu:")
        print("py .\\program.py <nazwa pliku z transakcjami> ", end = "")
        print( "<nazwa pliku z alfabetem> <słowo>")
        sys.exit(0)

    # próba otwarcia plików podanych jako argument
    try:
        trans_filename = sys.argv[1]
        trans_file = open(trans_filename, encoding = "utf8")
    except IOError:
        print(f"Nie udało się otworzyć pliku z transakcjami: {trans_filename}")
        sys.exit(0)

    try:
        alpha_filename = sys.argv[2]
        alpha_file = open(alpha_filename, encoding = "utf8")
    except IOError:
        print(f"Nie udało się otworzyć pliku z alfabetem: {alpha_filename}")
        sys.exit(0)

    word = sys.argv[3]
    
    # przetworzenie danych z plików
    transactions = get_transactions(trans_file)
    alphabet = get_alphabet(alpha_file)

    # naiwne sprawdzenie poprawności (spójności danych)
    # m. in. czy alfabet zgadza się z transakcjami
    if not check_correctness(transactions, alphabet, word):
        print("Wykryto brak spójności między argumentami programu!")
        sys.exit(0)

    # wyznaczenie relacji zależności i niezależności
    sigma2 = [(ai, aj) for aj in alphabet for ai in alphabet]
    dependent = get_dependent_transactions(transactions)
    independent = sorted(list(set(sigma2) - dependent))
    dependent = sorted(list(dependent))

    print("\nRelacja zależności:\nD = {", end = "")
    for i in range(len(dependent)):
        if i > 0: print(",", end = "")
        print(f"({dependent[i][0]},{dependent[i][1]})", end = "")
    print("}")

    print("\nRelacja niezależności:\nI = {", end = "")
    for i in range(len(independent)):
        if i > 0: print(",", end = "")
        print(f"({independent[i][0]},{independent[i][1]})", end = "")
    print("}")

    # wyznaczenie postaci normalnej Foaty
    dependencies = get_dependencies(dependent, alphabet)
    fnf = agh(dependencies, word, alphabet)
    print("\nPostać normalna Foaty:\nFNF([w]) = ", end = "")
    for foata_class in fnf:
        print("(", end = "")
        for transaction in sorted(foata_class):
            print(transaction, end = "")
        print(")", end = "")
    print()

    # wyznaczenie grafu w postaci dot
    graph_edges = get_graph_edges(dependencies, word)
    print("\nGraf w postaci dot:\ndigraph g{")
    for edge in sorted(graph_edges, key = lambda edge: (edge[1], edge[0])):
        print(f"{edge[0]} -> {edge[1]}")

    for i in range(len(word)):
        print(f"{i + 1}[label={word[i]}]")
    print("}\n")

    # stworzenie i rysowanie grafu
    G, positions, labels = prepare_graph_to_draw(word, graph_edges)
    plt.figure(num = f"Graf zależności w postaci minimalnej dla słowa {word}")
    nx.draw(
        G, 
        positions, 
        labels = labels, 
        with_labels = True, 
        node_size = 600, 
        connectionstyle = "arc3, rad = 0.15"
    )
    plt.show()
