# Logiciel de Planification Clinique et Gestion des Urgences Hospitalières

## Description
Application Java Swing de gestion des admissions aux urgences d'un hôpital. Les patients sont triés par priorité médicale (ROUGE > ORANGE > VERT) et non par ordre d'arrivée.

## Fonctionnalités
- **Tri par priorité** : Les patients sont classés automatiquement par sévérité (ROUGE, ORANGE, VERT) puis par heure d'arrivée via `Comparable<Patient>`
- **File d'attente** : Extraction du patient le plus urgent via `PriorityQueue`
- **Interface Swing** : JTable avec affichage en temps réel des patients en attente
- **Persistance JDBC** : Base de données MySQL avec tables patients, médecins, consultations, archives
- **Multithreading** : Horloge temps réel et affichage du temps d'attente moyen via `SwingWorker`

## Prérequis
- Java 17+
- MySQL 8+
- mysql-connector-java-5.1.49.jar (ou version compatible)

## Installation

### 1. Base de données
```bash
mysql -u root -p < sql/init.sql
```

### 2. Compilation
```bash
javac -cp "mysql-connector-java-5.1.49.jar" -d build @sources.txt
```

### 3. Exécution
```bash
java -cp "build:mysql-connector-java-5.1.49.jar" Main
```

## Structure du projet
```
src/
├── Main.java                     # Point d'entrée
├── connexion/ConnexionDB.java    # Singleton JDBC
├── metier/
│   ├── Patient.java              # Modèle avec Comparable<Patient>
│   ├── Medecin.java              # Modèle médecin
│   └── Priorite.java             # Enum ROUGE(3) / ORANGE(2) / VERT(1)
├── dao/
│   ├── PatientDAO.java           # CRUD + file d'attente
│   ├── MedecinDAO.java           # Accès médecins
│   └── ArchivesDAO.java          # Archivage dossiers
├── ihm/
│   ├── FenetrePrincipale.java    # Fenêtre principale Swing
│   ├── TableModelPatient.java    # Modèle JTable
│   └── DialogueAjoutPatient.java # Dialogue d'ajout
└── thread/
    └── HorlogeInterne.java       # SwingWorker pour temps d'attente
```

## Équipe
Projet ICT308 — 10 étudiants répartis en 4 sous-groupes : Core & Métier, Persistance & Données, IHM Swing, Performance & Multithreading.
