package automaton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SearchWithAutomaton {

	// Nombre de lignes de la matrice
	private int nLignes;
	// Nombre de colonnes de la matrice
	private int nColonnes;
	// La matrice representant l'automate deterministe
	public List<Integer>[][] dfaMatrix;
	// la colonne 256 indique si l'etat a des epslions transitions
	private int indiceEpsilon = 256;
	// la colonne 257 indique si l'etat est un etat initiale
	private int indiceInitiale = 257;
	// la colonne 257 indique si l'etat est un etat finale
	private int indiceFinale = 258;

	/*
	 * Constructeur
	 * Prend en parametre l'automate finie deterministe, un nombre de lignes et 
	 * un nombre de colonnes
	 * */
	public SearchWithAutomaton(List<Integer>[][] dfaMatrix, int nLignes, int nColonnes) {
		this.nLignes = nLignes;
		this.nColonnes = nColonnes;
		this.dfaMatrix = dfaMatrix;
	}
	
	/*
	 * Methode qui prend un texte et retourne la liste de toutes ses lignes
	 * */
	public String[] textToLines(String text) {
		String[] subString = text.split("\n");
		return subString;
	}
	
	/*
	 * Prend une ligne et un indice et retourne un suffixe de cette ligne en utilisant
	 * l'indice
	 * */
	public String getSuffixFromN(String line, int n) {
		int length = line.length();
		String result = line.substring(length-n,length);
		return result;
	}
	
	/*
	 * La methode prend un etat de l'automate et une transition, si cette derniere peut 
	 * etre effectuer alots il renvoie l'indice de l'etat cible, sinon il renvoie une erreur
	 * */
	public int executeAutomate(int etatActuel, int colonne) {
		if (colonne < 260) {
			if (this.dfaMatrix[etatActuel][colonne] != null) {
				return this.dfaMatrix[etatActuel][colonne].get(0);
			} else {
				return -404;
			}
		}
		return -404;
	}
	
	/*
	 * Verifie si un etat est final
	 * */
	public boolean estEtatFinal(int etat) {
		return (this.dfaMatrix[etat][this.indiceFinale] != null);
	}
	
	/*
	 * Fait tourner l'automate sur tout les suffixes de la ligne passée en parametre
	 * renvoie cette derniere si elle match et qu'il n'ya pas de crash
	 * */
	public String searchInAllSuffixs(String line) {
		int erreur = -404;
		for (int i = line.length(); i >= 0 ; i--) {
			String suffix = getSuffixFromN(line, i);
			int etat = 0;
			for (int j = 0; j < suffix.length(); j++) {
				etat = executeAutomate(etat, (int) suffix.charAt(j));
				if (etat == erreur) {
					break;
				} else if (estEtatFinal(etat)) {
					return line;
				}
			}
		}
		return null;
	}
	
	/*
	 * Methode qui prend le text du fichier sur lequel l'automate doit tourner 
	 * et renvoie une liste de lignes qui matchs avec l'expression reguliere recherchée
	 * */
	public ArrayList<String> search(String text) {
		String[] textLines = textToLines(text);
		ArrayList<String> listResults = new ArrayList<String>();
		for (int i = 0; i < textLines.length; i++) {
			String result = searchInAllSuffixs(textLines[i]);
			if (result != null) {
				listResults.add(result);
			}
		}
		return listResults;
	}

}
