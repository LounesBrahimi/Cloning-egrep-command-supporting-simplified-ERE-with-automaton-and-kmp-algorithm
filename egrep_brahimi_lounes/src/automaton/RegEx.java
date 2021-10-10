package automaton;

import java.util.ArrayList;

/*
 * Class representant l'expression reguliere et permettant de creer l'arbre syntaxique
 * */
public class RegEx {
	
	//Macros
	static final int CONCAT = 0xC04CA7;
	static final int ETOILE = 0xE7011E;
	static final int ALTERN = 0xA17E54;
	static final int PROTECTION = 0xBADDAD;
	static final int PARENTHESEOUVRANT = 0x16641664;
	static final int PARENTHESEFERMANT = 0x51515151;
	static final int DOT = 0xD07;
	
	// expression reguliere
	private static String regEx;
	  

	//constructeur
	public RegEx(String regEx)	{
		this.regEx = regEx;
	}
	
	/*
	 * Convertie l'expression reguliere en un arbre syntaxique
	 * */
	public static SyntaxTree parse() throws Exception {
	    ArrayList<SyntaxTree> result = new ArrayList<SyntaxTree>();
	    for (int i=0; i < regEx.length(); i++) {
	    	if (regEx.charAt(i) != '.')
	    	result.add(new SyntaxTree(charToRacine(regEx.charAt(i)),new ArrayList<SyntaxTree>()));
	    }
	    return parse(result);
	}
	
	/*
	 * Convertie les caracteres en macros
	 * */
	private static int charToRacine(char c) {
		    if (c=='.') return DOT;
		    if (c=='*') return ETOILE;
		    if (c=='|') return ALTERN;
		    if (c=='(') return PARENTHESEOUVRANT;
		    if (c==')') return PARENTHESEFERMANT;
		    return (int)c;
	}

	
	private static SyntaxTree parse(ArrayList<SyntaxTree> result) throws Exception {
		while (containParenthese(result)) {
			result = processParenthese(result);
		}
		while (containEtoile(result)) {
			result = processEtoile(result);
		}
		while (containConcat(result)) {
			result = processConcat(result);
		}
		while (containAltern(result)) {
			result=processAltern(result);
		}

		if (result.size()>1) 
			throw new Exception();
		
		return removeProtection(result.get(0));
	}
	
	/*
	 * Verifie si la liste d'arbres syntaxiques contient des parentheses
	 * */
	private static boolean containParenthese(ArrayList<SyntaxTree> trees) {
		for (SyntaxTree t: trees) {
			if (t.racine == PARENTHESEFERMANT || t.racine == PARENTHESEOUVRANT) 
		  		return true;
		    }
		return false;
	}
	
	/*
	 * Cherche et detecte les arbres positionner entre les deux arbres avec une racine respectivement
	 * (PARENTHESEOUVRANT et PARENTHESEFERMANT), l'algorithme supprime ces deux derniers arbres 
	 * et traite la traite la suite d'arbres qu'ils délimitaient, 
	 * */
	private static ArrayList<SyntaxTree> processParenthese(ArrayList<SyntaxTree> trees) throws Exception {
		ArrayList<SyntaxTree> result = new ArrayList<SyntaxTree>();
		boolean found = false;
		for (SyntaxTree t: trees) {
			if (!found && t.racine == PARENTHESEFERMANT) {
				boolean done = false;
		        ArrayList<SyntaxTree> content = new ArrayList<SyntaxTree>();
		        while (!done && !result.isEmpty()) {
		          if (result.get(result.size()-1).racine==PARENTHESEOUVRANT) { 
		        	  done = true; result.remove(result.size()-1); 
		          }	else {
		        	  content.add(0,result.remove(result.size()-1));
		          }
		        }
		        if (!done) throw new Exception();
		        found = true;
		        ArrayList<SyntaxTree> subTrees = new ArrayList<SyntaxTree>();
		        subTrees.add(parse(content));
		        result.add(new SyntaxTree(PROTECTION, subTrees));
			} else {
		        result.add(t);
		    }
		}
	    if (!found) throw new Exception();
	    return result;
	}
	
	/*
	 * Verifie si la liste d'arbres syntaxiques contient l'etoiles
	 * */
	private static boolean containEtoile(ArrayList<SyntaxTree> trees) {
		for (SyntaxTree t: trees) {
			if (t.racine == ETOILE && t.sousArbre.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Cherche et detecte la liste des arbres positionnés avant l'arbre dont la racine est étoile
	 * cette liste d'arbre deviennent le sous arbres de l'étoile 
	 * */
	private static ArrayList<SyntaxTree> processEtoile(ArrayList<SyntaxTree> trees) throws Exception {
		ArrayList<SyntaxTree> result = new ArrayList<SyntaxTree>();
		boolean found = false;
		for (SyntaxTree t: trees) {
			if (!found && t.racine == ETOILE && t.sousArbre.isEmpty()) {
		        if (result.isEmpty()) throw new Exception();
		        found = true;
		        SyntaxTree last = result.remove(result.size()-1);
		        ArrayList<SyntaxTree> subTrees = new ArrayList<SyntaxTree>();
		        subTrees.add(last);
		        result.add(new SyntaxTree(ETOILE, subTrees));
		    } else {
		        result.add(t);
		    }
		}
		return result;
	}

	/*
	 * Verifie si la liste d'arbres syntaxiques contiennent la concaténation
	 * */
	private static boolean containConcat(ArrayList<SyntaxTree> trees) {
		boolean firstFound = false;
	    for (SyntaxTree t: trees) {
	    	if (!firstFound && t.racine!=ALTERN) {
	    		firstFound = true; 
	    		continue; 
	    	}
	    	if (firstFound) {
	    		if (t.racine!=ALTERN) 
	    			return true; 
	    		else 
	    			firstFound = false;
	    	}
	    }
	    return false;
	}
	
	/*
	 * Dans le cas de deux arbres qui ce suivent sans altérnation, la méthode prend 
	 * le premier noeud, puis le deuxieme, et ces deux arbres deviennent les fils d'un nouveau
	 * noeud représentant la concaténation
	 * */
	private static ArrayList<SyntaxTree> processConcat(ArrayList<SyntaxTree> trees) throws Exception {
		ArrayList<SyntaxTree> result = new ArrayList<SyntaxTree>();
		boolean found = false;
		boolean firstFound = false;
		for (SyntaxTree t: trees) {
			if (!found && !firstFound && t.racine!=ALTERN) {
				firstFound = true;
		        result.add(t);
		        continue;
		    }
		    if (!found && firstFound && t.racine==ALTERN) {
		        firstFound = false;
		        result.add(t);
		        continue;
		    }
		    if (!found && firstFound && t.racine!=ALTERN) {
		        found = true;
		        SyntaxTree last = result.remove(result.size()-1);
		        ArrayList<SyntaxTree> subTrees = new ArrayList<SyntaxTree>();
		        subTrees.add(last);
		        subTrees.add(t);
		        result.add(new SyntaxTree(CONCAT, subTrees));
		    } else {
		        result.add(t);
		    }
		}
	    return result;
	  }
	
	/*
	 * Verifie si la liste d'arbres syntaxiques contiennent une alternation
	 * */
	private static boolean containAltern(ArrayList<SyntaxTree> trees) {
	    for (SyntaxTree t: trees) {
	    	if (t.racine == ALTERN && t.sousArbre.isEmpty()) {
	    		return true;
	    	}
	    }
	    return false;
	}
	
	/*
	 * détecte la gauche de l'arbre a la racine altern, ce dernier deviens son fils gauche,
	 * la méthode et continue et détecte sa droite et deviens ainsi son fils gauche
	 * */
	private static ArrayList<SyntaxTree> processAltern(ArrayList<SyntaxTree> trees) throws Exception {
		ArrayList<SyntaxTree> result = new ArrayList<SyntaxTree>();
	    boolean found = false;
	    SyntaxTree gauche = null;
	    boolean done = false;
	    for (SyntaxTree t: trees) {
	    	if (!found && t.racine==ALTERN && t.sousArbre.isEmpty()) {
		        if (result.isEmpty()) {
		        	throw new Exception();
		        }
		        found = true;
		        gauche = result.remove(result.size()-1);
		        continue;
		    }
		    if (found && !done) {
		        if (gauche==null) {
		        	throw new Exception();
		        }
		        done=true;
		        ArrayList<SyntaxTree> subTrees = new ArrayList<SyntaxTree>();
		        subTrees.add(gauche);
		        subTrees.add(t);
		        result.add(new SyntaxTree(ALTERN, subTrees));
		    } else {
		        result.add(t);
		    }
	    }
		return result;
	}

	/*
	 * Supprime l'arbre protection mis pendant le traitement des parentheses
	 * */
	private static SyntaxTree removeProtection(SyntaxTree tree) throws Exception {
		if (tree.racine == PROTECTION && tree.sousArbre.size() != 1) {
	    	throw new Exception();
	    }
	    if (tree.sousArbre.isEmpty()) {
	    	return tree;
	    }
	    if (tree.racine == PROTECTION) {
	    	return removeProtection(tree.sousArbre.get(0));
	    }
	    ArrayList<SyntaxTree> subTrees = new ArrayList<SyntaxTree>();
	    for (SyntaxTree t: tree.sousArbre) {
	    	subTrees.add(removeProtection(t));
	    }
		return new SyntaxTree(tree.racine, subTrees);
	}
	
}
