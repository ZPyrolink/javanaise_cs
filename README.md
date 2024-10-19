# Javanaise CS - Système d'Objets Distribués
 ## Description

Javanaise est un projet Java qui met en place un système d'objets distribués avec coordination. 
Le projet permet de :

    -Gérer des objets distribués avec une coordination centralisée.
    -Utiliser des mécanismes de verrouillage pour manipuler des objets en lecture et en écriture de manière concurrente.
    -Simuler une communication client-serveur via un système d'IRC.

## Prérequis
Avant de démarrer, assurez-vous d'avoir installé :
- **Java** (version 8 ou plus)

## Lancer le Projet
1. Lancez d'abord le serveur de coordination :
   ```bash
   java -jar out/artifacts/Coord.jar
   ```

2. Ensuite, lancez les clients IRC :
   ```bash
   java -jar out/artifacts/IRC.jar
   ```


## Tests
### Test 1 

Ce script :
 Lance le fichier Coord.jar (serveur de coordination).
 Attend 1 seconde pour laisser le serveur démarrer.
 Démarre ensuite 100 instances de IRC.jar, qui représentent des clients.

1. Rendez le script exécutable :

   ```bash
   chmod +x out/artifacts/run.sh
   ```

2. Lancez-le :

   ```bash
   ./out/artifacts/run.sh
   ```

### Test 2 
Le répertoire src/test contient la classes de test test.java pour valider le bon fonctionnement des objets distribués et du système de coordination :

    Test.java : Cette classe contient le test pour vérifier la gestion des objets distribués, des verrous, et la communication entre les clients et le serveur de coordination.

Exécution des Tests

Pour exécuter le test, compilez le projet et utilisez la classe Test.java.

## Auteurs

- Corenthin ZOZOR
- Skander Ramy CHOUITER



