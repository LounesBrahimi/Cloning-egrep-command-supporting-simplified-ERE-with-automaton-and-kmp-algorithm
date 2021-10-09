package automaton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SearchWithAutomaton {

	private int nLignes;
	private int nColonnes;
	public List<Integer>[][] dfaMatrix;
	private int indiceEpsilon = 256;
	private int indiceInitiale = 257;
	private int indiceFinale = 258;

	public SearchWithAutomaton(List<Integer>[][] dfaMatrix, int nLignes, int nColonnes) {
		this.nLignes = nLignes;
		this.nColonnes = nColonnes;
		this.dfaMatrix = dfaMatrix;
	}
	
	public String[] textToLines(String text) {
		String[] subString = text.split("\n");
		return subString;
	}
	
	public String getSuffixFromN(String line, int n) {
		int length = line.length();
		String result = line.substring(length-n,length);
		return result;
	}
	
	public int executeAutomate(int etatActuel, int colonne) {
		if (this.dfaMatrix[etatActuel][colonne] != null) {
			return this.dfaMatrix[etatActuel][colonne].get(0);
		} else {
			return -404;
		}
	}
	
	public boolean estEtatFinal(int etat) {
		return (this.dfaMatrix[etat][this.indiceFinale] != null);
	}
	
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
					return suffix;
				}
			}
		}
		return null;
	}
	
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
