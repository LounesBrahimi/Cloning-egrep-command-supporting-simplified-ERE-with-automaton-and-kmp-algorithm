package knuthMorrisPratt;

import java.util.ArrayList;

public class KmpAlgorithm {

	// expression reguliere
	private String regEx;
	// Le text dans lequel effectuer la recherche
	private String text;
	// une liste ordonée des caractères de l'expression reguliere
	private ArrayList<String> Factor;
	// Une liste indiquant pour chaque position un décalage
	private ArrayList<Integer> CarryOver;
	
	/*
	 * Constructeur
	 * Prend en parametre la chaine de caractere rechercher et 
	 * le texte dans lequel effectuer la recherche
	 * */
	public KmpAlgorithm(String regEx, String text) {
		this.regEx = regEx;
		this.text = text;
	}
	
	/*
	 * Convertie l'expression reguliere en une liste ordonée des caractères
	 * (crée le Factor)
	 * */
	public void generateFunctor() {
		this.Factor = new ArrayList<String>();
		for (int i = 0; i < regEx.length(); i++) {
			this.Factor.add("" + regEx.charAt(i));
		}
	}
	
	/*
	 * Getter de CarryOver
	 * */
	public ArrayList<Integer> getCarryOver(){
		return this.CarryOver;
	}
	
	/*
	 * Retourne la premiere partie de Factor de 0 à l'indice passé
	 * en paramètre
	 * */
	public ArrayList<String> getFirstPart(int indice) {
		ArrayList<String> firstPart = new ArrayList<String>();
		for (int i = 0; i < indice; i++) {
			firstPart.add(this.Factor.get(i));
		}
		return firstPart;
	}
	
	/*
	 * Prend une ligne et un indice et retourne un suffixe de cette ligne en utilisant
	 * l'indice
	 * */
	public String getSuffixFromN(String s, int n) {
		int length = s.length();
		String result = s.substring(length-n,length);
		return result;
	}
	
	/*
	 * Methode qui convertie une liste de chaine de caractère en une chaine
	 * de caractère unique et cela en faisant la concatenation.
	 * */
	public String listToString(ArrayList<String> s) {
		String result = "";
		for (int i = 0; i < s.size(); i++) {
			result = result.concat(s.get(i));
		}
		return result;
	}
	
	/*
	 * Verifie si le suffixe passé en parametre est aussi un préffixe
	 * de la chaine.
	 * */
	public boolean isPreffix(String chaine, String suffix) {
		return chaine.startsWith(suffix);
	}
	
	/*
	 * Methode qui retourne la taille du plus long suffixe propre qui est aussi 
	 * un préfixe
	 * */
	public int getLengthLongestProperSuffixPreffix(ArrayList<String> s) {
		String chaine = listToString(s);
		for (int i = chaine.length()-1; i > 0 ; i--) {
			String suffix = getSuffixFromN(chaine, i);
			if (isPreffix(chaine, suffix)) {
				return suffix.length();
			}
		}
		return 0;
	}
	
	/*
	 * La methode génère une liste nommée « Carry Over » qui indique 
	 * pour chaque position un décalage à effectuer si la chaine de 
	 * caractère ne matche pas à partir d’un certain indice.
	 * */
	public void generateCarryOver() {
		this.CarryOver = new ArrayList<Integer>();
		this.CarryOver.add(-1);
		
		for (int i = 1; i < this.regEx.length(); i++) {
			ArrayList<String> firstPart = getFirstPart(i);
			if (firstPart.size() == 1) {
				this.CarryOver.add(0);
			} else {
				int value = getLengthLongestProperSuffixPreffix(firstPart);
				this.CarryOver.add(value);
			}
		}
		
		for (int i = 1; i < this.Factor.size(); i++) {
			if ((this.Factor.get(i).equals(this.Factor.get(0)))
					&& (this.CarryOver.get(i) == 0)) {
				this.CarryOver.set(i, -1);
			}
		}
		
		for (int i = 0; i < this.Factor.size(); i++) {
			if ( (this.CarryOver.get(i) != -1) 
					&&	(this.Factor.get(i).equals(this.Factor.get(this.CarryOver.get(i))))){
				this.CarryOver.set(i, this.CarryOver.get(this.CarryOver.get(i)));
			}
		}
		this.CarryOver.add(0);
	}
	
	/*
	 * Getter pour Factor
	 * */
	public ArrayList<String> getFactor(){
		return this.Factor;
	}
	
	/*
	 * Methode qui divise une unique chaine de caractere en une liste
	 * de chaines de caracteres representant les lignes. 
	 * */
	public String[] textToLines() {
		String[] subString = this.text.split("\n");
		return subString;
	}
	
	/*
	 * cherche dans tout les suffixes de la ligne passée en parametre
	 * en prenant soin de faire les décalages dans les cas de crash
	 * en utilisant le Carry Over
	 * renvoie cette derniere si elle match et qu'il n'ya pas de crash
	 * */
	public String searchInAllSuffixs(String line) {
		int i = 0;
		while((i < line.length()) && ((line.length()- i) >= Factor.size())) {
			int crashIndice = 0;
			for (int j = 0; j < Factor.size(); j++) {
				if (Factor.get(j).equals(""+line.charAt(j+i))) {
					crashIndice++;
				}
			}
			if (crashIndice == Factor.size()) {
				return line;
			} else {
				i = i + crashIndice - CarryOver.get(crashIndice);
			}
		}
		return null;
	}
	
	/*
	 * Methode qui fait la recherche de la chanine de caractere
	 * dans le texte.
	 * */
	public ArrayList<String> search() {
		String[] textLines = textToLines();
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