package automaton;

import java.util.ArrayList;

public class SyntaxTree {

	protected int racine;
	protected ArrayList<SyntaxTree> sousArbre;
	
	/*
	 * Constructeur
	 * */
	public SyntaxTree(int racine, ArrayList<SyntaxTree> sousArbre) {
		  this.racine = racine;
		  this.sousArbre = sousArbre;
	}
	
	// Convert tree to parenthesis
	public String toString() {
	  if (sousArbre.isEmpty()) return racineToString();
	  String result = racineToString()+"("+sousArbre.get(0).toString();
	  for (int i=1; i<sousArbre.size(); i++) result+=","+sousArbre.get(i).toString();
	  return result+")";
	}
	
	// Convert the value of the root to String
	private String racineToString() {
		  if (this.racine == RegEx.CONCAT) {
			  return ".";
		  }
		  if (this.racine == RegEx.ETOILE) {
			  return "*";
		  }
		  if (this.racine == RegEx.ALTERN) {
			  return "|";
		  }
		  if (this.racine == RegEx.DOT) {
			  return ".";
		  }
		  return Character.toString((char)racine);
	}
}