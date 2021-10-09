package automaton;

import java.util.ArrayList;
import java.util.List;

public class NDFA {

	private int nLignes;
	private int nColonnes;
	public List<Integer>[][] ndfaMatrix;
	private int indiceEpsilon = 256;
	private int indiceInitiale = 257;
	private int indiceFinale = 258;
	
	public NDFA(){
		this.nLignes = 100;
		this.nColonnes = 259;
		this.ndfaMatrix = new ArrayList[this.nLignes][this.nColonnes];
	}
	
	public NDFA(int nLignes){
		this.nLignes = nLignes;
		this.nColonnes = 259;
		ArrayList[][] arrayLists = new ArrayList[this.nLignes][this.nColonnes];
		this.ndfaMatrix = arrayLists;
	}
	
	static int numeroEtat = 0;
	
	public void arbreToNDFA(SyntaxTree arbre){
		int initiale = 0;
		int finale = 1;
		int nvEtat = finale + 1;
		numeroEtat = nvEtat;
		//Initialisation
		if( this.ndfaMatrix[initiale][this.indiceInitiale] == null ) {
			ArrayList list = new ArrayList();
			list.add(1);
			this.ndfaMatrix[initiale][this.indiceInitiale] = list;
		} else {
			this.ndfaMatrix[initiale][this.indiceInitiale].add(1);
		}
		if( this.ndfaMatrix[finale][this.indiceFinale] == null ) {
			ArrayList list = new ArrayList();
			list.add(1);
			this.ndfaMatrix[finale][this.indiceFinale] = list;
		}
		else {
			this.ndfaMatrix[finale][this.indiceFinale].add(1);
		}
		//---------------------
		// Dans le cas ou l'arbre est une feuille exemple (a) ou (b)
		if ((arbre.sousArbre.size() == 0) && (arbre.racine != RegEx.CONCAT)
				&& (arbre.racine != RegEx.ETOILE) && (arbre.racine != RegEx.ALTERN)
				&& (arbre.racine != RegEx.DOT)) {
			if (this.ndfaMatrix[initiale][arbre.racine] == null ) {
				ArrayList list = new ArrayList();
				list.add(nvEtat);
				this.ndfaMatrix[initiale][arbre.racine] = list;
			} else {
				this.ndfaMatrix[initiale][arbre.racine].add(nvEtat);
			}
			if (this.ndfaMatrix[nvEtat][this.indiceEpsilon] == null) {
				ArrayList list = new ArrayList();
				list.add(finale);
				this.ndfaMatrix[nvEtat][this.indiceEpsilon] = list;
			} else {
				this.ndfaMatrix[nvEtat][this.indiceEpsilon].add(finale);	
			}
		}
		//---------------
		// Dans le cas Alternation ==> exemple ( a | b ) 
		if ((arbre.sousArbre.size() > 1) && (arbre.racine == RegEx.ALTERN)) {
			int nvEtat2 = numeroEtat + 1;
			numeroEtat = nvEtat2;
			if (this.ndfaMatrix[initiale][this.indiceEpsilon] == null) {
				ArrayList list = new ArrayList();
				list.add(nvEtat);
				list.add(nvEtat2);
				this.ndfaMatrix[initiale][this.indiceEpsilon] = list;
			} else {
				this.ndfaMatrix[initiale][this.indiceEpsilon].add(nvEtat);
				this.ndfaMatrix[initiale][this.indiceEpsilon].add(nvEtat2);
			}
			arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, finale);
			arbreToNDFARec(arbre.sousArbre.get(1), nvEtat2, initiale, finale);
		}
		//----------------------------------------------------------------
		// Dans le cas concatenation exemple : (a.b)
		if ((arbre.sousArbre.size() == 2) && ((arbre.racine == RegEx.CONCAT)
				|| (arbre.racine == RegEx.DOT))) {
			if (arbre.sousArbre.get(0).sousArbre.size() == 0) {
				// ----------- gauche
				if(this.ndfaMatrix[initiale][arbre.sousArbre.get(0).racine] == null) {
					ArrayList list = new ArrayList();
					list.add(nvEtat);
					this.ndfaMatrix[initiale][arbre.sousArbre.get(0).racine] = list;
					int ancEtat = nvEtat;
					nvEtat = numeroEtat + 1;
					numeroEtat = nvEtat;
					if (this.ndfaMatrix[ancEtat][indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat);
						this.ndfaMatrix[ancEtat][indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[ancEtat][indiceEpsilon].add(nvEtat);
					}
				} else {
					this.ndfaMatrix[initiale][arbre.sousArbre.get(0).racine].add(nvEtat);
					int ancEtat = nvEtat;
					nvEtat = numeroEtat + 1;
					numeroEtat = nvEtat;
					this.ndfaMatrix[ancEtat][indiceEpsilon].add(nvEtat);
				}
			} else {
				
				// le cas ou c'est pas une feuille gauche
				//-------- alternation -------
				if ((arbre.sousArbre.get(0).sousArbre.size() > 1) && (arbre.sousArbre.get(0).racine == RegEx.ALTERN)) {
					if(this.ndfaMatrix[initiale][indiceEpsilon] == null) {
						ArrayList list = new ArrayList();
						list.add(nvEtat);
						this.ndfaMatrix[initiale][indiceEpsilon] = list;
						int etatAvConct = numeroEtat + 1;
						numeroEtat = etatAvConct;
						arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
						nvEtat = etatAvConct;
					} else {
						this.ndfaMatrix[initiale][indiceEpsilon].add(nvEtat);
						int etatAvConct = numeroEtat + 1;
						numeroEtat = etatAvConct;
						arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
						nvEtat = etatAvConct;
					}
				} else {
					//-------- traiter les autres cas gauche!!!!!-------
					// concat
					if ((arbre.sousArbre.get(0).sousArbre.size() > 1) && ((arbre.sousArbre.get(0).racine == RegEx.CONCAT)
							|| (arbre.sousArbre.get(0).racine == RegEx.DOT))) {
						if(this.ndfaMatrix[initiale][indiceEpsilon] == null) {
							ArrayList list = new ArrayList();
							list.add(nvEtat);
							this.ndfaMatrix[initiale][indiceEpsilon] = list;
							int etatAvConct = numeroEtat + 1;
							numeroEtat = etatAvConct;
							arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
							nvEtat = etatAvConct;
						} else {
							this.ndfaMatrix[initiale][indiceEpsilon].add(nvEtat);
							int etatAvConct = numeroEtat + 1;
							numeroEtat = etatAvConct;
							arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
							nvEtat = etatAvConct;
						}
					} else if ((arbre.sousArbre.get(0).sousArbre.size() == 1) && (arbre.sousArbre.get(0).racine == RegEx.ETOILE)){
						// etoile----------------------------------
						if(this.ndfaMatrix[initiale][indiceEpsilon] == null) {
							ArrayList list = new ArrayList();
							list.add(nvEtat);
							this.ndfaMatrix[initiale][indiceEpsilon] = list;
							int etatAvConct = numeroEtat + 1;
							numeroEtat = etatAvConct;
							arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
							nvEtat = etatAvConct;
						} else {
							this.ndfaMatrix[initiale][indiceEpsilon].add(nvEtat);
							int etatAvConct = numeroEtat + 1;
							numeroEtat = etatAvConct;
							arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
							nvEtat = etatAvConct;
						}
						//--------------------
					}
				}
			}
			
			//-------------- droite 
			if (arbre.sousArbre.get(1).sousArbre.size() == 0) {
				int ancEtat = nvEtat;
				nvEtat = numeroEtat + 1;
				numeroEtat = nvEtat;
				if(this.ndfaMatrix[ancEtat][arbre.sousArbre.get(1).racine] == null) {
					ArrayList list = new ArrayList();
					list.add(nvEtat);
					this.ndfaMatrix[ancEtat][arbre.sousArbre.get(1).racine] = list;
					if(this.ndfaMatrix[nvEtat][indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat][indiceEpsilon] = list2; 
					} else {
						this.ndfaMatrix[nvEtat][indiceEpsilon].add(finale);
					}
				} else {
					this.ndfaMatrix[ancEtat][arbre.sousArbre.get(1).racine].add(nvEtat);
					if(this.ndfaMatrix[nvEtat][indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat][indiceEpsilon] = list2; 
					} else {
						this.ndfaMatrix[nvEtat][indiceEpsilon].add(finale);
					}
				}
			} else {
				// le cas ou c'est pas une feuille
				//-------- alternation -------
						arbreToNDFARec(arbre.sousArbre.get(1), nvEtat, initiale, finale);
				
			}	
		}
		//---------------------------------------
		// dans le cas d'une boucle exemple : (*a)
		if ((arbre.sousArbre.size() == 1) && (arbre.racine == RegEx.ETOILE)) {
			// si le sous arbre est une feuille simple exemple a : 
			if (arbre.sousArbre.get(0).sousArbre.size() == 0) {
				int nvEtat2 = numeroEtat + 1;
				numeroEtat = nvEtat2;
				if (this.ndfaMatrix[initiale][this.indiceEpsilon] == null) {
					ArrayList list = new ArrayList();
					list.add(nvEtat);
					list.add(nvEtat2);
					this.ndfaMatrix[initiale][this.indiceEpsilon] = list;
					int nvEtat3 = numeroEtat + 1;
					numeroEtat = nvEtat3;
					if (this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat3);
						this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine] = list2;
					} else {
						this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine].add(nvEtat3);
					}
					if (this.ndfaMatrix[nvEtat3][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat2);
						list2.add(nvEtat);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat2);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat);
					}
					if(this.ndfaMatrix[nvEtat2][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon].add(finale);
					}					
				} else {
					this.ndfaMatrix[initiale][this.indiceEpsilon].add(nvEtat);
					this.ndfaMatrix[initiale][this.indiceEpsilon].add(nvEtat2);
					int nvEtat3 = numeroEtat + 1;
					numeroEtat = nvEtat3;
					if (this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat3);
						this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine] = list2;
					} else {
						this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine].add(nvEtat3);
					}
					if (this.ndfaMatrix[nvEtat3][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat2);
						list2.add(nvEtat);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat2);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat);
					}
					if(this.ndfaMatrix[nvEtat2][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon].add(finale);
					}
				}	
			} else {
				// si le sous arbre n'est pas une feuille exemple (a|b)* :
				int nvEtat2 = numeroEtat + 1;
				numeroEtat = nvEtat2;
				if (this.ndfaMatrix[initiale][this.indiceEpsilon] == null) {
					ArrayList list = new ArrayList();
					list.add(nvEtat);
					list.add(nvEtat2);
					this.ndfaMatrix[initiale][this.indiceEpsilon] = list;
					int nvEtat3 = numeroEtat + 1;
					numeroEtat = nvEtat3;
					// sous arbre a bouclé--------------------------------------------------
					arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, nvEtat3);
					//--------------------------------------------------------------------
					if (this.ndfaMatrix[nvEtat3][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat2);
						list2.add(nvEtat);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat2);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat);
					}
					if(this.ndfaMatrix[nvEtat2][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon].add(finale);
					}					
				} else {
					this.ndfaMatrix[initiale][this.indiceEpsilon].add(nvEtat);
					this.ndfaMatrix[initiale][this.indiceEpsilon].add(nvEtat2);
					int nvEtat3 = numeroEtat + 1;
					numeroEtat = nvEtat3;
					// sous arbre a bouclé--------------------------------------------------
					arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, nvEtat3);
					//--------------------------------------------------------------------
					if (this.ndfaMatrix[nvEtat3][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat2);
						list2.add(nvEtat);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat2);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat);
					}
					if(this.ndfaMatrix[nvEtat2][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon].add(finale);
					}
				}
			}
		}
		
	}
	
	public void arbreToNDFARec(SyntaxTree arbre, int etatRacine ,int initiale, int finale) {
		int nvEtat = numeroEtat + 1;
		numeroEtat = nvEtat;
		// Dans le cas ou l'arbre est une feuille exemple (a) ou (b)
		if ((arbre.sousArbre.size() == 0) && (arbre.racine != RegEx.CONCAT)
			&& (arbre.racine != RegEx.ETOILE) && (arbre.racine != RegEx.ALTERN)
			&& (arbre.racine != RegEx.DOT)) {
			if (this.ndfaMatrix[etatRacine][arbre.racine] == null ) {
				ArrayList list = new ArrayList();
				list.add(nvEtat);
				this.ndfaMatrix[etatRacine][arbre.racine] = list;
			} else {
				this.ndfaMatrix[etatRacine][arbre.racine].add(nvEtat);
			}
			System.out.println(nvEtat);
			if (this.ndfaMatrix[nvEtat][this.indiceEpsilon] == null) {
				ArrayList list = new ArrayList();
				list.add(finale);
				this.ndfaMatrix[nvEtat][this.indiceEpsilon] = list;
			} else {
				System.out.println(this.ndfaMatrix[nvEtat][this.indiceEpsilon]);
				this.ndfaMatrix[nvEtat][this.indiceEpsilon].add(finale);	
			}
		}
		//---------------
		// Dans le cas Alternation ==> exemple ( a | b ) 
		if ((arbre.sousArbre.size() > 1) && (arbre.racine == RegEx.ALTERN)) {
			int nvEtat2 = numeroEtat + 1;
			numeroEtat = nvEtat2;
			if (this.ndfaMatrix[etatRacine][this.indiceEpsilon] == null) {
				ArrayList list = new ArrayList();
				list.add(nvEtat);
				list.add(nvEtat2);
				this.ndfaMatrix[etatRacine][this.indiceEpsilon] = list;
			} else {
				this.ndfaMatrix[etatRacine][this.indiceEpsilon].add(nvEtat);
				this.ndfaMatrix[etatRacine][this.indiceEpsilon].add(nvEtat2);
			}
			arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, finale);
			arbreToNDFARec(arbre.sousArbre.get(1), nvEtat2, initiale, finale);
		}
		//---------------
		// Dans le cas concatenation ==> exemple ( b.c ) 
		if ((arbre.sousArbre.size() == 2) && ((arbre.racine == RegEx.CONCAT)
				|| (arbre.racine == RegEx.DOT))) {
			if (arbre.sousArbre.get(0).sousArbre.size() == 0) {
				// ----------- gauche
				if(this.ndfaMatrix[etatRacine][arbre.sousArbre.get(0).racine] == null) {
					ArrayList list = new ArrayList();
					list.add(nvEtat);
					this.ndfaMatrix[etatRacine][arbre.sousArbre.get(0).racine] = list;
					int ancEtat = nvEtat;
					nvEtat = numeroEtat + 1;
					numeroEtat = nvEtat;
					if (this.ndfaMatrix[ancEtat][indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat);
						this.ndfaMatrix[ancEtat][indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[ancEtat][indiceEpsilon].add(nvEtat);
					}
				} else {
					this.ndfaMatrix[etatRacine][arbre.sousArbre.get(0).racine].add(nvEtat);
					int ancEtat = nvEtat;
					nvEtat = numeroEtat + 1;
					numeroEtat = nvEtat;
					this.ndfaMatrix[ancEtat][indiceEpsilon].add(nvEtat);
				}
			} else {
				// le cas ou c'est pas une feuille gauche
				//-------- alternation -------
				if ((arbre.sousArbre.get(0).sousArbre.size() > 1) && (arbre.sousArbre.get(0).racine == RegEx.ALTERN)) {
					if(this.ndfaMatrix[etatRacine][indiceEpsilon] == null) {
						ArrayList list = new ArrayList();
						list.add(nvEtat);
						this.ndfaMatrix[etatRacine][indiceEpsilon] = list;
						int etatAvConct = numeroEtat + 1;
						numeroEtat = etatAvConct;
						arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
						nvEtat = etatAvConct;
					} else {
						this.ndfaMatrix[etatRacine][indiceEpsilon].add(nvEtat);
						int etatAvConct = numeroEtat + 1;
						numeroEtat = etatAvConct;
						arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
						nvEtat = etatAvConct;
					}
				} else {
					//-------- traiter les autres cas gauche!!!!!-------
					if ((arbre.sousArbre.get(0).sousArbre.size() > 1) && ((arbre.sousArbre.get(0).racine == RegEx.CONCAT)
							|| (arbre.sousArbre.get(0).racine == RegEx.DOT))) {
						if(this.ndfaMatrix[etatRacine][indiceEpsilon] == null) {
							ArrayList list = new ArrayList();
							list.add(nvEtat);
							this.ndfaMatrix[etatRacine][indiceEpsilon] = list;
							int etatAvConct = numeroEtat + 1;
							numeroEtat = etatAvConct;
							arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
							nvEtat = etatAvConct;
						} else {
							this.ndfaMatrix[etatRacine][indiceEpsilon].add(nvEtat);
							int etatAvConct = numeroEtat + 1;
							numeroEtat = etatAvConct;
							arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
							nvEtat = etatAvConct;
						}
					}
					else {
					// etoile----------------------------------
					if(this.ndfaMatrix[etatRacine][indiceEpsilon] == null) {
						ArrayList list = new ArrayList();
						list.add(nvEtat);
						this.ndfaMatrix[etatRacine][indiceEpsilon] = list;
						int etatAvConct = numeroEtat + 1;
						numeroEtat = etatAvConct;
						arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
						nvEtat = etatAvConct;
					} else {
						this.ndfaMatrix[etatRacine][indiceEpsilon].add(nvEtat);
						int etatAvConct = numeroEtat + 1;
						numeroEtat = etatAvConct;
						arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, initiale, etatAvConct);
						nvEtat = etatAvConct;
					}
					}
					//--------------------
				}
			}
			
			//-------------- droite ---------------------------------
			if (arbre.sousArbre.get(1).sousArbre.size() == 0) {
				int ancEtat = nvEtat;
				nvEtat = numeroEtat + 1;
				numeroEtat = nvEtat;
				if(this.ndfaMatrix[ancEtat][arbre.sousArbre.get(1).racine] == null) {
					ArrayList list = new ArrayList();
					list.add(nvEtat);
					this.ndfaMatrix[ancEtat][arbre.sousArbre.get(1).racine] = list;
					if(this.ndfaMatrix[nvEtat][indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat][indiceEpsilon] = list2; 
					} else {
						this.ndfaMatrix[nvEtat][indiceEpsilon].add(finale);
					}
				} else {
					this.ndfaMatrix[ancEtat][arbre.sousArbre.get(1).racine].add(nvEtat);
					if(this.ndfaMatrix[nvEtat][indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat][indiceEpsilon] = list2; 
					} else {
						this.ndfaMatrix[nvEtat][indiceEpsilon].add(finale);
					}
				}
			} else {
				// le cas ou c'est pas une feuille
				//-------- alternation -------
						arbreToNDFARec(arbre.sousArbre.get(1), nvEtat, initiale, finale);
				
			}
		}
		
		//---------------------------------------
		// dans le cas d'une boucle exemple : (*a)
		if ((arbre.sousArbre.size() == 1) && (arbre.racine == RegEx.ETOILE)) {
			// si le sous arbre est une feuille simple exemple a : 
			if (arbre.sousArbre.get(0).sousArbre.size() == 0) {
				int nvEtat2 = numeroEtat + 1;
				numeroEtat = nvEtat2;
				if (this.ndfaMatrix[etatRacine][this.indiceEpsilon] == null) {
					ArrayList list = new ArrayList();
					list.add(nvEtat);
					list.add(nvEtat2);
					this.ndfaMatrix[etatRacine][this.indiceEpsilon] = list;
					int nvEtat3 = numeroEtat + 1;
					numeroEtat = nvEtat3;
					if (this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat3);
						this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine] = list2;
					} else {
						this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine].add(nvEtat3);
					}
					if (this.ndfaMatrix[nvEtat3][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat2);
						list2.add(nvEtat);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat2);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat);
					}
					if(this.ndfaMatrix[nvEtat2][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon].add(finale);
					}					
				} else {
					this.ndfaMatrix[etatRacine][this.indiceEpsilon].add(nvEtat);
					this.ndfaMatrix[etatRacine][this.indiceEpsilon].add(nvEtat2);
					int nvEtat3 = numeroEtat + 1;
					numeroEtat = nvEtat3;
					if (this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat3);
						this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine] = list2;
					} else {
						this.ndfaMatrix[nvEtat][arbre.sousArbre.get(0).racine].add(nvEtat3);
					}
					if (this.ndfaMatrix[nvEtat3][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat2);
						list2.add(nvEtat);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat2);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat);
					}
					if(this.ndfaMatrix[nvEtat2][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon].add(finale);
					}
				}	
			} else {
				// si le sous arbre n'est pas une feuille exemple (a|b)* :
				int nvEtat2 = numeroEtat + 1;
				numeroEtat = nvEtat2;
				if (this.ndfaMatrix[etatRacine][this.indiceEpsilon] == null) {
					ArrayList list = new ArrayList();
					list.add(nvEtat);
					list.add(nvEtat2);
					this.ndfaMatrix[etatRacine][this.indiceEpsilon] = list;
					int nvEtat3 = numeroEtat + 1;
					numeroEtat = nvEtat3;
					// sous arbre a bouclé--------------------------------------------------
					arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, etatRacine, nvEtat3);
					//--------------------------------------------------------------------
					if (this.ndfaMatrix[nvEtat3][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat2);
						list2.add(nvEtat);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat2);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat);
					}
					if(this.ndfaMatrix[nvEtat2][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon].add(finale);
					}					
				} else {
					this.ndfaMatrix[etatRacine][this.indiceEpsilon].add(nvEtat);
					this.ndfaMatrix[etatRacine][this.indiceEpsilon].add(nvEtat2);
					int nvEtat3 = numeroEtat + 1;
					numeroEtat = nvEtat3;
					// sous arbre a bouclé--------------------------------------------------
					arbreToNDFARec(arbre.sousArbre.get(0), nvEtat, etatRacine, nvEtat3);
					//--------------------------------------------------------------------
					if (this.ndfaMatrix[nvEtat3][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(nvEtat2);
						list2.add(nvEtat);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat2);
						this.ndfaMatrix[nvEtat3][this.indiceEpsilon].add(nvEtat);
					}
					if(this.ndfaMatrix[nvEtat2][this.indiceEpsilon] == null) {
						ArrayList list2 = new ArrayList();
						list2.add(finale);
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon] = list2;
					} else {
						this.ndfaMatrix[nvEtat2][this.indiceEpsilon].add(finale);
					}
				}
			}
		}
	}

	
	public int getnLignes() {
		return nLignes;
	}

	

	public int getnColonnes() {
		return nColonnes;
	}



	public List<Integer>[][] getNdfaMatrix() {
		return ndfaMatrix;
	}



	public static int getNumeroEtat() {
		return numeroEtat;
	}
}