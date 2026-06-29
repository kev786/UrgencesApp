# UrgencesApp — Logiciel de Planification Clinique et Gestion des Urgences Hospitalières

Projet réalisé dans le cadre de l'examen ICT308 — Travaux Pratiques (10 jours).

## Description

Application Java de bureau (Swing) permettant de gérer les admissions aux urgences
d'un hôpital. Les patients sont triés non pas par ordre d'arrivée, mais selon un
niveau de priorité médicale (Code Rouge, Orange, Vert), grâce à l'interface
`Comparable<Patient>`. Les dossiers médicaux sont persistés dans une base de données
MySQL via JDBC.

## Équipe

| Sous-équipe | Membres | Responsabilité |
|---|---|---|
| Core & Métier | (3 noms) | Modélisation POO, `Patient`, `Comparable`, `GestionnaireUrgences` |
| Persistance & Données | (2 noms) | DAO JDBC, connexion MySQL |
| IHM Swing | (3 noms) | Fenêtres, JTable, gestion des événements |
| Performance & Multithreading | (2 noms) | Horloge de temps d'attente moyen |

Chef de groupe : **KEV1**

## Prérequis

- **JDK 17** installé chez tout le monde (`java -version` doit afficher `17.x.x`)
- **MySQL Server** installé en local (XAMPP/WAMP sous Windows, `mysql-server` sous Linux)
- Le driver JDBC est déjà fourni dans `lib/` (pas besoin de le télécharger)

## Structure du projet

```
UrgencesApp/
├── lib/                    → driver JDBC MySQL (.jar)
├── src/                    → code source Java
│   └── cm/univyaounde/urgences/
│       ├── App.java
│       ├── modele/         → Patient, NiveauPriorite, GestionnaireUrgences
│       ├── dao/            → PatientDAO, PatientDAOImpl, ConnexionBD
│       ├── ihm/            → FenetrePrincipale, panneaux Swing
│       ├── thread/         → ThreadHorloge, EcouteurHorloge
│       ├── exception/      → exceptions personnalisées
│       └── util/           → ConfigChargeur
├── resources/
│   ├── config.properties.example   → modèle à copier
│   └── config.properties           → ton fichier local (ne PAS commit)
├── sql/
│   └── schema.sql          → script de création de la base
├── bin/                    → fichiers .class compilés (généré, ignoré par Git)
├── compile-run.sh          → script Linux/macOS
├── compile-run.bat         → script Windows
└── README.md
```

## Installation (à faire une seule fois, par chaque membre)

### 1. Cloner le dépôt

```bash
git clone <url-du-repo>
cd UrgencesApp
```

### 2. Créer la base de données MySQL

Ouvrir un terminal MySQL (`mysql -u root -p`) et exécuter :

```sql
SOURCE sql/schema.sql;
```

### 3. Configurer ta connexion locale

Copier le fichier modèle et l'adapter avec **tes propres identifiants MySQL** :

```bash
# Linux/macOS
cp resources/config.properties.example resources/config.properties

# Windows (PowerShell)
copy resources\config.properties.example resources\config.properties
```

Puis éditer `resources/config.properties` :

```properties
db.url=jdbc:mysql://localhost:3306/urgences_db
db.user=root
db.password=ton_mot_de_passe_local
```

⚠️ Ce fichier ne doit **jamais** être poussé sur Git (il est dans `.gitignore`).
Chacun a le sien avec ses propres identifiants — le code source ne change pas.

## Compilation et exécution

### Linux / macOS

```bash
chmod +x compile-run.sh   # une seule fois
./compile-run.sh
```

### Windows

```bat
compile-run.bat
```

Ces scripts compilent automatiquement tous les fichiers `.java` dans `bin/`, puis
lancent `App.java` avec le bon classpath (driver JDBC + dossier resources inclus).

## Convention de travail Git

- Une branche par sous-équipe : `core`, `persistance`, `ihm`, `thread`
- Toujours `git pull` avant de commencer à coder
- Commits clairs : `git commit -m "modele: ajoute comparerAvec dans Patient"`
- Pull request vers `main` validée par le chef de groupe avant fusion
- Ne jamais commit : `bin/`, `resources/config.properties`, fichiers `.class`

## Barème visé (sur 20 pts)

| Critère | Points |
|---|---|
| Architecture & Qualité POO | 4 |
| Interface Graphique Swing | 4 |
| Mécanique Multithreading | 3 |
| Persistance des Données | 3 |
| Robustesse (Exceptions) | 2 |
| Soutenance & Maîtrise Individuelle | 4 |

## Planning (10 jours)

1. **J1-J2** — Cadrage commun : UML, contrat d'interfaces, schéma BDD, setup Git
2. **J3-J7** — Développement en parallèle par sous-équipe
3. **J8-J9** — Intégration, tests croisés, correction des bugs
4. **J10** — Répétition et soutenance
