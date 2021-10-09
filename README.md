# Cloning-egrep-command-supporting-simplified-ERE-with-automaton-and-kmp-algorithm

1.	Définition du problème :
1.1	Introduction
Le projet a pour objectif la recherche de chaînes de caractères au sein d’un fichier textuel, semblable à la commande « egrep », ce dernier devra donc supporter des expressions régulières restreintes aux éléments suivants : « les parenthèses », « l’alternative », « la concaténation », « l’opération étoile », « le point » et « la lettre ASCII ».
1.2	Contraintes :
•	Si la chaine de caractère recherchée est une expression régulière non réduite à une simple concaténation, cette dernière sera transformée en un arbre syntaxique dans un premier temps. Puis en un automate fini non-déterministe contenant des epsilons transitions. Ce dernier sera déterminiser. Finalement chaque suffixe d’une ligne du fichier textuel sera examiné afin de vérifier si elle est reconnaissable par l’automate obtenue.
•	Si l’expression régulière recherchée est réduite à une simple concaténation, une autre méthode n’utilisant pas les automates sera employée, cette dernière s’appuie sur l’algorithme Knuth-Morris-Pratt.
