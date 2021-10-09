package search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import knuthMorrisPratt.KmpAlgorithm;

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
					System.out.println("expression reguliere complexe");
					System.out.println(fileToText(args[1]));
				}
			}
		}	
}
