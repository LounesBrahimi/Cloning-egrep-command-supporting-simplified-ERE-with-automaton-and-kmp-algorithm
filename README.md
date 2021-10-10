Pour exécuter le projet, il suffit de rentrer dans le dossier "egrep_brahimi_lounes" puis de :

1. ant build	====> Compile le projet.

2. ant -Darg0="Sargon"  -Darg1="56667-0.txt" Main ====> cherche "Sargon" dans le fichier "56667-0.txt".

3. ant -Darg0="tests_file.txt" TestMain ====> execute la banque de tests du fichier tests_file.

Note:
Les fichiers txt (les livres) a utiliser sont présents dans la racine du projet.