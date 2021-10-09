package search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import knuthMorrisPratt.KmpAlgorithm;
import automaton.*;

public class Main {

	public static boolean estSuiteConcatenations(String regEx) {
		for (int i = 0; i < regEx.length(); i++) {
			if ((regEx.charAt(i) == '*') || (regEx.charAt(i) == '|') ) {
				return false;
			}
		}
		return true;
	}
	
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

	public static void printMatrix(List<Integer>[][] ndfaMatrix) {
		for (int i = 0; i < ndfaMatrix.length; i++) {
		    for (int j = 65; j < ndfaMatrix[i].length; j++) {
		    	if (((j > 64) && (j < 123)) || (j > 250))
		        System.out.print(ndfaMatrix[i][j] + " ");
		    }
		    System.out.println();
		}
	}
	
	public static void main(String args[]) throws IOException{  
		if (args.length < 2) {
			System.out.println("Erreur : veuillez introduire l'expression reguliere et le fichier txt");  
		} else if ((args[0].length() == 0) || (args[1].length() == 0)){	
			System.out.println("Erreur : veuillez introduire l'expression reguliere et le fichier txt");  
		} else {
				if (estSuiteConcatenations(args[0])) {
					System.out.println("=========\nsuite de concatenations\n=========");
					String regEx = args[0];
					String text = fileToText(args[1]);
					KmpAlgorithm kmp = new KmpAlgorithm(regEx, text);
					kmp.generateFunctor();
					kmp.generateCarryOver();
					System.out.println(kmp.search());
				} else {
					System.out.println("=========\\nexpression reguliere complexe=========");
					String regEx = args[0];
					RegEx r = new RegEx(regEx);
					try {
						SyntaxTree ret = r.parse();
				        System.out.println("________________________________________________");
				        System.out.println("####Syntax Tree:####\n"+ret.toString());
					    System.out.println("________________________________________________");
					    NDFA n = new NDFA(100000);
					    n.arbreToNDFA(ret);
					    NDFARemoveEpsilon nSansEpsilons = new NDFARemoveEpsilon(n.getNdfaMatrix(), 100000, n.getnColonnes(), n.getNumeroEtat());
					    nSansEpsilons.supression();
					    DFA dfa = new DFA(nSansEpsilons.nouveauNdfaMatrix, 100000, n.getnColonnes());
					    dfa.determinate();
					    System.out.println("#### Recherche ####");
					    SearchWithAutomaton s = new SearchWithAutomaton(dfa.dfaMatrix, 100000, n.getnColonnes()); 
					    String text = fileToText(args[1]);
					    System.out.println(s.search(text));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}	
}
