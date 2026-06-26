# Documentation du Projet - Gestion des Urgences Hospitalières

## Architecture générale

```
┌─────────────────────────────────────────────────────────────┐
│                        Main.java                            │
│                  (Point d'entrée de l'app)                  │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│              ihm/FenetrePrincipale.java (JFrame)             │
│  ┌─────────────────┐  ┌────────────────┐  ┌──────────────┐ │
│  │  JTable (données)│  │  Panneaux      │  │  Boutons     │ │
│  │  TableModelPatient│ │  d'infos       │  │  Appeler/    │ │
│  │                  │  │  (temps attente│  │  Archiver/   │ │
│  │                  │  │   + horloge)   │  │  Ajouter     │ │
│  └─────────────────┘  └────────────────┘  └──────────────┘ │
└────────────────────────┬────────────────────────────────────┘
                         │
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
┌─────────────────┐ ┌──────────┐ ┌──────────────┐
│   metier/       │ │  dao/    │ │  thread/     │
│   Patient.java  │ │ PatientDAO│ │ HorlogeInterne│
│   Priorite.java │ │ ArchiveDAO│ │ (SwingWorker) │
│   Medecin.java  │ │ MedecinDAO│ │              │
└────────┬────────┘ └─────┬────┘ └──────────────┘
         │                │
         ▼                ▼
┌──────────────────────────────────┐
│     connexion/ConnexionDB.java   │
│     (Singleton JDBC -> MySQL)    │
└──────────────────────────────────┘
```

---

## 1. Équipe Core & Métier (3 étudiants) — Package `metier/`

### Rôle
Modéliser les entités métier et implémenter les règles de tri.

### Fichiers à modifier

#### `Priorite.java` — Enum des niveaux d'urgence
```java
public enum Priorite {
    ROUGE(3),   // Urgence vitale
    ORANGE(2),  // Urgence relative
    VERT(1);    // Non urgent
}
```
- Chaque constante a un `niveau` (3, 2, 1) utilisé pour la comparaison.
- `fromSeverite(int severite)` : convertit un score de sévérité en priorité.

#### `Patient.java` — Classe principale avec `Comparable<Patient>`
```java
public class Patient implements Comparable<Patient> {
    // Champs : idPatient, nom, prenom, age, sexe, codePriorite (Priorite),
    //          pathologie, heureArrivee (LocalDateTime), statut,
    //          heurePriseEnCharge, heureSortie

    @Override
    public int compareTo(Patient autre) {
        // 1. Compare par niveau de priorité (décroissant : ROUGE d'abord)
        int cmp = Integer.compare(autre.codePriorite.getNiveau(),
                                   this.codePriorite.getNiveau());
        // 2. Si égalité, compare par heure d'arrivée (croissant : le + ancien d'abord)
        if (cmp != 0) return cmp;
        return this.heureArrivee.compareTo(autre.heureArrivee);
    }
}
```
**Règle métier** : ROUGE > ORANGE > VERT. À priorité égale, le patient arrivé le premier passe en premier.

#### `Medecin.java` — Modèle simple d'un médecin
- Champs : `idMedecin`, `nom`, `prenom`, `specialite`, `disponible` (boolean)

### Ce que vous devez savoir
- `compareTo()` est utilisé automatiquement par `PriorityQueue` dans `PatientDAO`
- Le `PriorityQueue` extrait toujours l'élément le plus urgent (tête de file)
- Vous pouvez ajouter des attributs ou méthodes selon les besoins

---

## 2. Équipe Persistance & Données (2 étudiants) — Package `dao/` + `connexion/`

### Rôle
Gérer toutes les interactions avec la base de données MySQL.

### Fichiers à modifier

#### `ConnexionDB.java` — Connexion MySQL (Singleton)
```java
private static final String URL = "jdbc:mysql://localhost:3306/gestion_urgences";
private static final String USER = "root";
private static final String PASSWORD = "";
private static Connection instance;

public static Connection getConnexion() throws SQLException {
    if (instance == null || instance.isClosed()) {
        instance = DriverManager.getConnection(URL, USER, PASSWORD);
    }
    return instance;
}
```
- Modifiez `USER` et `PASSWORD` selon votre installation MySQL.

#### `PatientDAO.java` — Toutes les opérations sur les patients

| Méthode | Rôle |
|---------|------|
| `getAllPatients()` | Récupère tous les patients (triés par priorité puis heure) |
| `getPatientsEnAttente()` | Récupère les patients avec statut = 'EN_ATTENTE' |
| `getFileAttente()` | Construit une `PriorityQueue<Patient>` (utilise `compareTo`) |
| `ajouterPatient(Patient)` | INSERT dans la table patients |
| `appelerProchainPatient()` | `poll()` sur la file → passe statut à 'EN_COURS' |
| `archiverPatient(int id)` | Passe statut à 'ARCHIVE' avec heure de sortie |
| `getTempsAttenteMoyenMinutes()` | Calcule la moyenne des minutes d'attente des patients en attente |
| `getPatientsEnCours()` | Récupère les patients en cours de traitement |

**Méthode clé — `appelerProchainPatient()` :**
```java
public Patient appelerProchainPatient() {
    PriorityQueue<Patient> pq = getFileAttente(); // tri automatique
    if (pq.isEmpty()) return null;
    Patient prochain = pq.poll();  // prend le + urgent
    // UPDATE statut = 'EN_COURS', heure_prise_en_charge = maintenant
    return prochain;
}
```

#### `ArchivesDAO.java` — Archivage des dossiers
- `archiverDossier(Patient)` : copie le dossier dans `archives_dossiers`
- `getAllArchives()` : liste tous les dossiers archivés

#### `MedecinDAO.java` — Accès aux médecins
- `getAllMedecins()` : liste complète
- `getMedecinsDisponibles()` : médecins disponibles

### Base de données (fichier `sql/init.sql`)
```sql
-- Tables : patients, medecins, consultations, archives_dossiers
-- 4 patients de test (Ngono ROUGE, Fotso ORANGE, Mballa VERT, Atangana ROUGE)
-- 2 médecins de test (Etoundi, Biya)
```

### Ce que vous devez savoir
- Toutes les requêtes SQL sont dans ce package
- Utilisez `PreparedStatement` pour éviter les injections SQL
- La connexion est gérée par `ConnexionDB.getConnexion()`
- Les requêtes se ferment automatiquement avec try-with-resources

---

## 3. Équipe IHM Swing (3 étudiants) — Package `ihm/`

### Rôle
Concevoir l'interface graphique utilisateur.

### Fichiers à modifier

#### `FenetrePrincipale.java` — Fenêtre principale

**Layout :** `BorderLayout` avec 3 zones :

```
┌─────────────────────────────────────────┐
│  [Horloge]    [Temps d'attente moyen]   │  ← NORTH (topPanel)
├─────────────────────────────────────────┤
│                                         │
│     JTable (patients en attente)        │  ← CENTER (scrollPane)
│                                         │
├─────────────────────────────────────────┤
│ [Appeler] [Archiver] [Ajouter] [Actual.]│  ← SOUTH (controlPanel)
└─────────────────────────────────────────┘
```

**Boutons :**
| Bouton | Action |
|--------|--------|
| **Appeler prochain patient** | `patientDAO.appelerProchainPatient()` → extrait le + urgent |
| **Archiver patient sélectionné** | Archive le patient sélectionné dans le tableau |
| **Nouveau patient** | Ouvre `DialogueAjoutPatient` pour ajouter un patient |
| **Rafraîchir** | Recharge les données depuis la BD |

**Auto-rafraîchissement :** Un `Timer` Swing recharge les données toutes les 10 secondes.

#### `TableModelPatient.java` — Modèle pour JTable

| Colonne | Index | Type |
|---------|-------|------|
| ID | 0 | int |
| Nom | 1 | String |
| Prénom | 2 | String |
| Age | 3 | int |
| Sexe | 4 | String |
| Priorité | 5 | String (ROUGE/ORANGE/VERT) |
| Pathologie | 6 | String |
| Arrivée | 7 | String (HH:mm:ss) |
| Statut | 8 | String |

- `setPatients(List<Patient>)` : met à jour les données et rafraîchit l'affichage
- `getPatientAt(int row)` : récupère le patient d'une ligne donnée

#### `DialogueAjoutPatient.java` — Boîte de dialogue d'ajout

**Champs du formulaire :**
- Nom (JTextField)
- Prénom (JTextField)
- Age (JSpinner, 0-150)
- Sexe (JComboBox : M/F)
- Priorité (JComboBox : ROUGE/ORANGE/VERT)
- Pathologie (JTextField)

**Boutons :** "Ajouter" (valide et ferme) / "Annuler" (ferme sans sauver)

### Ce que vous devez savoir
- L'interface utilise `SwingUtilities.invokeLater()` pour la sécurité des threads
- Le `JTable` utilise un `TableModelPatient` personnalisé
- Les dialogues sont modaux (bloquent la fenêtre parente)
- Vous pouvez personnaliser les couleurs, polices, et layout

---

## 4. Équipe Performance & Multithreading (2 étudiants) — Package `thread/`

### Rôle
Gérer les tâches asynchrones et l'affichage temps réel.

### Fichiers à modifier

#### `HorlogeInterne.java` — SwingWorker pour le temps d'attente

```java
public class HorlogeInterne extends SwingWorker<Void, String> {
    // Toutes les 5 secondes :
    //   1. Calcule le temps d'attente moyen via patientDAO.getTempsAttenteMoyenMinutes()
    //   2. Publie le résultat avec publish()
    //   3. process() met à jour le JLabel dans l'EDT
}
```

**Fonctionnement :**
- `doInBackground()` : boucle infinie (`while(actif)`) avec `Thread.sleep(5000)`
- `publish(texte)` → `process(chunks)` : met à jour le label dans le thread EDT
- `arreter()` : arrête proprement la boucle

**Horloge temps réel (dans FenetrePrincipale) :**
```java
Timer clockTimer = new Timer(1000, e -> {
    LocalDateTime now = LocalDateTime.now();
    labelHorloge.setText(now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
});
clockTimer.start();
```
- Met à jour l'horloge chaque seconde dans l'EDT (Swing Timer, pas besoin de SwingWorker)

### Diagramme de synchronisation

```
THREAD PRINCIPAL (EDT)              THREAD WORKER (HorlogeInterne)
─────────────────────────           ─────────────────────────────
FenetrePrincipale démarre
    │                                    │
    ├── HorlogeInterne.execute() ───────►│ doInBackground()
    │                                    │   while(actif) {
    │                                    │     calculer moyenne
    │◄────── publish(texte) ─────────────│     publish(texte)
    │ process() met à jour label         │     Thread.sleep(5000)
    │                                    │   }
    │                                    │
    ├── clockTimer (1s) ───► horloge    │
    │                                    │
    ├── refreshTimer (10s) ──► recharge  │
```

### Ce que vous devez savoir
- `SwingWorker` évite de bloquer l'interface pendant les calculs
- `publish()` / `process()` sont thread-safe
- Le timer d'horloge est un `javax.swing.Timer` (pas `java.util.Timer`)
- Pour ajouter d'autres traitements async, créez un nouveau `SwingWorker`

---

## Résumé des dépendances entre équipes

```
┌──────────────────────┐
│  Équipe Core/Métier  │───► Définit Patient, Priorite, Medecin
└──────────┬───────────┘
           │ utilisés par
           ▼
┌──────────────────────┐
│ Équipe Persistance   │───► DAO utilise les classes métier + ConnexionDB
└──────────┬───────────┘
           │ utilisés par
           ▼
┌──────────────────────┐
│  Équipe IHM Swing    │───► Fenêtre utilise DAO pour afficher/modifier
└──────────┬───────────┘
           │ utilise
           ▼
┌──────────────────────┐
│ Équipe Performance   │───► HorlogeInterne utilise PatientDAO
└──────────────────────┘
```

## Commandes de base

```bash
# Compiler tout le projet
javac -cp "mysql-connector-java-5.1.49.jar" -d build @sources.txt

# Exécuter
java -cp "build:mysql-connector-java-5.1.49.jar" Main

# Re-générer la liste des sources
find src -name "*.java" > sources.txt
```
