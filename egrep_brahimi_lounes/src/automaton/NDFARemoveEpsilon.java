package automaton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/*
 * Class permettant de supprimer les epsilons transitions de l'automate
 * finie non deterministe
 * */
public class NDFARemoveEpsilon {

	// Nombre de lignes de la matrice
	private int nLignes;
	// Nombre de colonnes de la matrice
	private int nColonnes;
	// La matrice representant l'automate avec epsilons transitions
	public List<Integer>[][] ndfaMatrix;
	// La matrice representant l'automate sans epsilons transitions
	public List<Integer>[][] nouveauNdfaMatrix;
	// la colonne 256 indique si l'etat a des epslions transitions
	private int indiceEpsilon = 256;
	// la colonne 257 indique si l'etat est un etat initiale
	private int indiceInitiale = 257;
	// la colonne 257 indique si l'etat est un etat finale
	private int indiceFinale = 258;
	
	/*
	 * indique à temps reel le numero d'un etat non existant et qu'on peut donc creer
	 * */
	static int numeroEtat = 0;
	
	/*
	 * Constructeur
	 * Prend en parametre l'automate finie non deterministe avec les epsilons tansitions
	 * ainsi que le nombre de lignes, de colonnes et un numero d'état pas encore utilisé
	 * */
	public NDFARemoveEpsilon(List<Integer>[][] ndfaMatrix, int nLignes, int nColonnes, int numeroEtat){
		this.nLignes = nLignes;
		this.nColonnes = nColonnes;
		this.ndfaMatrix = ndfaMatrix;
		this.numeroEtat = numeroEtat;
		ArrayList[][] arrayLists = new ArrayList[this.nLignes][this.nColonnes];
		this.nouveauNdfaMatrix = arrayLists;
	}
	
	/*
	 * Méthode qui supprime les epsilons transitions et stock l'automate obtenue dans
	 * nouveauNdfaMatrix
	 * */
	public void supression() {
		HashSet nonEtatPuit = new HashSet();
		// ajout de l'etat intiale
		nonEtatPuit.add(0);
		for(int i=0; i<this.nLignes; i++) {
				if (this.ndfaMatrix[i][this.indiceEpsilon] == null) {
					// si l'état courant n'a pas d'epsilon transition sorant
					this.nouveauNdfaMatrix[i] = this.ndfaMatrix[i];
					for(int j=0; j<(this.nColonnes-3); j++) {
						if ((this.ndfaMatrix[i][j] != null) && (j != this.indiceEpsilon)) {
							for(int x=0; x<this.ndfaMatrix[i][j].size(); x++) {
								nonEtatPuit.add(this.ndfaMatrix[i][j].get(x));
							}
						}
					}
				} else {
					// si l'état courant a des epsilons transitions sorants
					ArrayList lEtatsMemeGroupe = new ArrayList();
					for(int x = 0; x<this.ndfaMatrix[i][this.indiceEpsilon].size(); x++) {
						lEtatsMemeGroupe.add(this.ndfaMatrix[i][this.indiceEpsilon].get(x));
						
						// traitement epsilon ==> epsilon ==> etat //nv//
						int etat_pointe_epsilon = this.ndfaMatrix[i][this.indiceEpsilon].get(x);
						if (this.ndfaMatrix[etat_pointe_epsilon][this.indiceEpsilon] != null) {
							for(int z = 0; z<this.ndfaMatrix[etat_pointe_epsilon][this.indiceEpsilon].size(); z++) {
								if (!(this.ndfaMatrix[i][this.indiceEpsilon].contains(this.ndfaMatrix[etat_pointe_epsilon][this.indiceEpsilon].get(z)))) {
									this.ndfaMatrix[i][this.indiceEpsilon].add(this.ndfaMatrix[etat_pointe_epsilon][this.indiceEpsilon].get(z));
								}
							}
						}
					}
					int tailleGroupe = lEtatsMemeGroupe.size();
					for(int j=0; j<this.nColonnes; j++) {
						ArrayList lEtatsCibles = new ArrayList();
						for(int k=0; k<tailleGroupe; k++) {
							if (lEtatsMemeGroupe.get(k) != null) {
								if (this.ndfaMatrix[(int) lEtatsMemeGroupe.get(k)][j] != null) {
									for(int x = 0; x<this.ndfaMatrix[(int) lEtatsMemeGroupe.get(k)][j].size(); x++) {
										lEtatsCibles.add(this.ndfaMatrix[(int) lEtatsMemeGroupe.get(k)][j].get(x));
										if (j < (this.nColonnes-3)) {
											nonEtatPuit.add(this.ndfaMatrix[(int) lEtatsMemeGroupe.get(k)][j].get(x));
										}
									}	
								}
							}
						}
						if (this.ndfaMatrix[i][j] != null) {
							for(int x = 0; x<this.ndfaMatrix[i][j].size(); x++) {
								lEtatsCibles.add(this.ndfaMatrix[i][j].get(x));
								if (j < (this.nColonnes-3)) {
									nonEtatPuit.add(this.ndfaMatrix[i][j].get(x));
								}
							}			
						}
						if (lEtatsCibles.size() > 0) {
							this.nouveauNdfaMatrix[i][j] = lEtatsCibles;
						} else {
							this.nouveauNdfaMatrix[i][j] = null;
						}
						
						this.nouveauNdfaMatrix[i][this.indiceEpsilon] = null;
					}
				}
		}
		for(int i=0; i<this.nLignes; i++) {
			ArrayList[] listPleineDeNull = new ArrayList[this.nColonnes];
			if (!(nonEtatPuit.contains(i))) {
				this.nouveauNdfaMatrix[i] = listPleineDeNull;
			}
		}
	}
}
