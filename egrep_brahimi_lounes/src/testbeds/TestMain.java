package testbeds;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import automaton.DFA;
import automaton.NDFA;
import automaton.NDFARemoveEpsilon;
import automaton.RegEx;
import automaton.SearchWithAutomaton;
import automaton.SyntaxTree;
import knuthMorrisPratt.KmpAlgorithm;

/*
 * Class permettant de faire les testbeds
 * */
public class TestMain {

	/*
	 * Ouvre le fichier avec le nom "fileName" passé en parametre, recupere
	 * le texte dans le fichier et le retourne
	 * */
	public static String fileToText(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String result = null;
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    result = sb.toString();
		} finally {
		    br.close();
		    return result;
		}
	}

	/*
	 * Methode qui divise une unique chaine de caractere en une liste
	 * de chaines de caracteres representant les lignes. 
	 * */
	public static String[] textToLines(String text) {
		String[] subString = text.split("\n");
		return subString;
	}
	
	/*
	 * Retourne vraie si l'expression reguliere est reduite à une suite de concatenations
	 * */
	public static boolean estSuiteConcatenations(String regEx) {
		for (int i = 0; i < regEx.length(); i++) {
			if ((regEx.charAt(i) == '*') || (regEx.charAt(i) == '|') || (regEx.charAt(i) == '.') ) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Methode qui imprime sur la sortie standard les lignes résutants de la
	 * recherche mené avec l'une des deux methodes.
	 * */
	public static void printLignes(ArrayList<String> lignes) {
		for (String ligne : lignes) {
			System.out.println(ligne);
		}
	}
	
	/*
	 * Methode principale
	 * */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Erreur : veuillez introduire un fichier txt");  
		} else if (args[0].length() == 0) {
			System.out.println("Erreur : veuillez introduire un fichier txt");
		} else {
			try {
				String text = fileToText(args[0]);
				String[] lignes = textToLines(text);
				long startTime = System.nanoTime();
				for (String ligne : lignes) {
					String[] elementsLigne = ligne.split("	", 2);
					if ((int) elementsLigne[1].charAt(elementsLigne[1].length()-1) == 13) {
						elementsLigne[1] = elementsLigne[1].substring(0, elementsLigne[1].length()-1);
					}
					
					if ( ((elementsLigne.length == 2) && 
							(elementsLigne[0].length() > 0)) && 
								(elementsLigne[1].length() > 0)) {
						if (estSuiteConcatenations(elementsLigne[0])) {
							System.out.println("=========Recherche avec KMP=========");
					        System.out.println("________________________________________________");
						    System.out.println("regEx : "+ elementsLigne[0]);
					        System.out.println("________________________________________________");
							String regEx = elementsLigne[0];
							String text1 = fileToText(elementsLigne[1]);
							KmpAlgorithm kmp = new KmpAlgorithm(regEx, text1);
							kmp.generateFunctor();
							kmp.generateCarryOver();
							printLignes(kmp.search());
						} else {
							System.out.println("=========Recherche avec automate=========");
							String regEx = elementsLigne[0];
							RegEx r = new RegEx(regEx);
							try {
								// conversion de l'expression réguliere en arbre syntaxique
								SyntaxTree ret = r.parse();
						        System.out.println("________________________________________________");
							    System.out.println("regEx : "+ regEx);
						        System.out.println("________________________________________________");
							    NDFA n = new NDFA(100000);
							    n.arbreToNDFA(ret);
							    NDFARemoveEpsilon nSansEpsilons = new NDFARemoveEpsilon(n.getNdfaMatrix(), 100000, n.getnColonnes(), n.getNumeroEtat());
							    nSansEpsilons.supression();
							    DFA dfa = new DFA(nSansEpsilons.nouveauNdfaMatrix, 100000, n.getnColonnes());
							    dfa.determinate();
							    SearchWithAutomaton s = new SearchWithAutomaton(dfa.dfaMatrix, 100000, n.getnColonnes()); 
							    String text1 = fileToText(elementsLigne[1]);
							    printLignes(s.search(text1));
							    nSansEpsilons.numeroEtat = 0;
							    n.numeroEtat = 0;
							    dfa.ligne_non_utilisee = 0;
							    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				long endTime = System.nanoTime();
				long duration = ((endTime - startTime)/1000000);
				System.out.println("## Time execution in seconds : "+ duration/100);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
