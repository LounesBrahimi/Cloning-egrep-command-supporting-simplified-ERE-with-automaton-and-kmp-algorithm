package automaton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/*
 * Class permettant de généré l'automate finie deterministe
 * */
public class DFA {

	// Nombre de lignes de la matrice
	private int nLignes;
	// Nombre de colonnes de la matrice
	private int nColonnes;
	// La matrice representant l'automate sans epsilons transitions
	public List<Integer>[][] ndfaMatrix;
	// La matrice representant l'automate deterministe
	public List<Integer>[][] dfaMatrix;
	// chaine de caractere representant le nom d'un etat à un indice i
	public String[] string_id;
	// un id representant l'etat à l'indice i
	public HashSet[] list_id;
	// la colonne 256 indique si l'etat a des epslions transitions
	private int indiceEpsilon = 256;
	// la colonne 257 indique si l'etat est un etat initiale
	private int indiceInitiale = 257;
	// la colonne 257 indique si l'etat est un etat finale
	private int indiceFinale = 258;
	
	// Donne en temps réel un numéro de ligne non utilisé
	public static int ligne_non_utilisee = 0;
	
	/*
	 * Constructeur
	 * Prend en parametre l'automate finie non deterministe sans epsilons transitions
	 * un nombre de lignes et un nombre de colonnes
	 * */
	public DFA(List<Integer>[][] ndfaMatrix, int nLignes, int nColonnes){
		this.nLignes = nLignes;
		this.nColonnes = nColonnes;
		this.ndfaMatrix = ndfaMatrix;
		ArrayList[][] arrayLists = new ArrayList[this.nLignes][this.nColonnes];
		this.dfaMatrix = arrayLists;
		this.string_id = new String[this.nLignes];
		this.list_id = new HashSet[this.nLignes];
	}
	
	/*
	 * Retourne vraie, si la chaine de caractere fait deja reference à un état
	 * */
	public boolean verify_existance(String s) {
		for (int i = 0; i < this.ligne_non_utilisee; i++) {
			if (this.string_id[i].equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * Identifie l'indice de la ligne à laquelle le nom d'état fait signe
	 * */
	public int identify_line(String nameLine) {
		for (int i = 0; i < this.ligne_non_utilisee; i++) {
			if (this.string_id[i].equals(nameLine)) {
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * Methode qui prend en parametre le nom symbolisant un etat et retourne la liste
	 * des noms d'etats qu'il regroupait de base
	 * */
	public List<String> split_string_by_size(int size, String text) {
		List<String> strings = new ArrayList<String>();
		int index = 0;
		while (index < text.length()) {
		    strings.add(text.substring(index, Math.min(index + size,text.length())));
		    index += size;
		}
		return strings;
	}
	
	/*
	 * Methode qui prend en parametre une liste de noms d'etats et retourne leurs indice
	 * de ligne dans l'ancien automate (matrice) 
	 * */
	public ArrayList groupe_etats(List<String> nameLine){
		ArrayList list = new ArrayList();
		for (int i = 0; i < nameLine.size(); i++) {
				list.add(Integer.parseInt(nameLine.get(i)));
		}
		return list;
	}
	
	/*
	 * Methode qui transforme un automate finie non deterministe sans epsilons transitions
	 * en un automate finie deterministe
	 * */
	public void determinate() {
		// Detection des etats finaux
		ArrayList finals_etats = new ArrayList();
		for (int i = 0; i < this.nLignes; i++) {
			if (this.ndfaMatrix[i][this.indiceFinale] != null) {
				finals_etats.add(i);
			}
		}
		
		if (this.ndfaMatrix[0][this.indiceFinale] != null) {
			ArrayList list = new ArrayList();
			list.add(1);
			this.dfaMatrix[0][this.indiceFinale] = list;
		}
		
		// initialisation
		int initiale = 0;
		this.dfaMatrix[initiale][this.indiceInitiale] = this.ndfaMatrix[initiale][this.indiceInitiale];
		this.string_id[initiale] = "0";
		HashSet init_set = new HashSet();
		init_set.add(initiale);
		this.ligne_non_utilisee++;
		if (this.list_id[initiale] == null) {
			this.list_id[initiale] = new HashSet();
		} 
		this.list_id[initiale] = init_set;
		
		// traitemant de l'état initiale 0
		for (int c = 0; c < this.nColonnes; c++) {
			if((this.ndfaMatrix[initiale][c] != null) && ( c!= this.indiceFinale)){
				if (this.ndfaMatrix[initiale][c].size() > 1) {
					// Si l'état initiale pointe plusieurs noeuds dans cette colonne
					HashSet set_etats = new HashSet();
					String name_ligne = "0";
					for (int i = 0; i < this.ndfaMatrix[initiale][c].size(); i++) {
						set_etats.add(this.ndfaMatrix[initiale][c].get(i));
					}
					Object[] array_list = set_etats.toArray(new Object[set_etats.size()]);
					String s = "";
					// Creation du nom combine faisant reference aux etats pointés 
					for (int i = 0; i < array_list.length; i++) {
						s = s.concat(String.valueOf(array_list[i]));
						name_ligne = s;
					}
					// Verifie si le noeud existe deja, sinon il le cree avant de le pointé
					// dans le nouvel automate
					if (!(verify_existance(name_ligne))) {
						this.list_id[ligne_non_utilisee] = set_etats;
						this.string_id[ligne_non_utilisee] = name_ligne;
						ligne_non_utilisee++;
						if (this.dfaMatrix[initiale][c] != null) {
							this.dfaMatrix[initiale][c].clear();
						}
						if (this.dfaMatrix[initiale][c] == null) {
							ArrayList list = new ArrayList();
							list.add(identify_line(name_ligne));
							this.dfaMatrix[initiale][c] = list; 
						} else {
							this.dfaMatrix[initiale][c].add(identify_line(name_ligne));
						}		
					} else {
						if (this.dfaMatrix[initiale][c] == null) {
							ArrayList list = new ArrayList();
							list.add(identify_line(name_ligne));
							this.dfaMatrix[initiale][c] = list;
						} else {
							this.dfaMatrix[initiale][c].add(identify_line(name_ligne));	
						}
					}
				} else if ((c != this.indiceInitiale) && (c != this.indiceFinale)){
					// Si l'état initiale pointe un seul noeud dans cette colonne
					String name_ligne = String.valueOf(this.ndfaMatrix[initiale][c].get(0));
					if (!(verify_existance(name_ligne))) {
						HashSet set_etat = new HashSet();
						set_etat.add(this.ndfaMatrix[initiale][c].get(0));
						this.list_id[ligne_non_utilisee] = set_etat;
						this.string_id[ligne_non_utilisee] = name_ligne;
						ligne_non_utilisee++;
					}
					if (this.dfaMatrix[initiale][c] == null) {
						ArrayList list = new ArrayList();
						list.add(identify_line(name_ligne));
						this.dfaMatrix[initiale][c] = list;
					} else {
						this.dfaMatrix[initiale][c].add(identify_line(name_ligne));	
					}
				}
			}
		}
		// parcourir dynamiquement
		if (this.ligne_non_utilisee > 1) {
		for (int i = 1; i < this.ligne_non_utilisee; i++) {
			ArrayList list_etats = new ArrayList();
			if (this.list_id[i].size() == 1) {
				list_etats.add(Integer.parseInt(this.string_id[i]));
			} else {
				list_etats = groupe_etats(this.split_string_by_size(this.string_id[i].length()/list_id[i].size(), this.string_id[i]));
			}
			for (int col = 0; col < (this.nColonnes - 3); col++) {
				ArrayList etats_pointes = new ArrayList();
				for (int j = 0; j < list_etats.size(); j++) {
					if (this.ndfaMatrix[(int) list_etats.get(j)][col] != null) {
						for (int j2 = 0; j2 < this.ndfaMatrix[(int) list_etats.get(j)][col].size(); j2++) {
							if (!(etats_pointes.contains(this.ndfaMatrix[(int) list_etats.get(j)][col].get(j2)))) {
								etats_pointes.add(this.ndfaMatrix[(int) list_etats.get(j)][col].get(j2));
							}
						}
					} 
				}
				if (etats_pointes.size()>0)
				if (etats_pointes.size() > 0) {
					if (etats_pointes.size() > 1) {
						HashSet set_etats = new HashSet();
						String name_ligne = "0";
						for (int i1 = 0; i1 < etats_pointes.size(); i1++) {
							set_etats.add(etats_pointes.get(i1));
						}
						Object[] array_list = set_etats.toArray(new Object[set_etats.size()]);
						String s = "";
						for (int i1 = 0; i1 < array_list.length; i1++) {
							s = s.concat(String.valueOf(array_list[i1]));
							name_ligne = s;
						}
						if (!(verify_existance(name_ligne))) {
							this.list_id[ligne_non_utilisee] = set_etats;
							this.string_id[ligne_non_utilisee] = name_ligne;
							ligne_non_utilisee++;
							if (this.dfaMatrix[i][col] != null) {
								this.dfaMatrix[i][col].clear();
							}
							if (this.dfaMatrix[i][col] == null) {
								ArrayList list = new ArrayList();
								list.add(identify_line(name_ligne));
								this.dfaMatrix[i][col] = list; 
							} else {
								this.dfaMatrix[i][col].add(identify_line(name_ligne));
							}
						} else {
							if (this.dfaMatrix[i][col] == null) {
								ArrayList list = new ArrayList();
								list.add(identify_line(name_ligne));
								this.dfaMatrix[i][col] = list;
							} else {
								this.dfaMatrix[i][col].add(identify_line(name_ligne));
							}	
						}
					} else {
						String name_ligne = String.valueOf((Integer) etats_pointes.get(0));
						if (!(verify_existance(name_ligne))) {
							HashSet set_etat = new HashSet();
							set_etat.add((Integer) etats_pointes.get(0));
							this.list_id[ligne_non_utilisee] = set_etat;
							this.string_id[ligne_non_utilisee] = name_ligne;
							ligne_non_utilisee++;
						}						
						if (this.dfaMatrix[i][col] == null) {
							ArrayList list = new ArrayList();
							list.add(identify_line(name_ligne));
							this.dfaMatrix[i][col] = list;
						} else {
							this.dfaMatrix[i][col].add(identify_line(name_ligne));	
						}
					}
				}
			}
		}
		}
		
		// marquer etats finaux
		for (int j = 0; j < this.ligne_non_utilisee; j++) {
			for (int j2 = 0; j2 < finals_etats.size(); j2++) {
				if (this.list_id[j].contains(finals_etats.get(j2)))	{
					if (this.dfaMatrix[j][this.indiceFinale] == null) {
						ArrayList list = new ArrayList();
						list.add(1);
						this.dfaMatrix[j][this.indiceFinale] = list;
					}
				}
			}
		}	
	}
}