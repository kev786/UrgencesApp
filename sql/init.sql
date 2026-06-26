-- ============================================
-- Base de données : gestion_urgences
-- Projet 5 : Logiciel de Planification Clinique
-- et Gestion des Urgences Hospitalieres
-- ============================================

CREATE DATABASE IF NOT EXISTS gestion_urgences
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_urgences;

-- ============================================
-- Table : patients
-- ============================================
CREATE TABLE IF NOT EXISTS patients (
    id_patient INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    sexe ENUM('M', 'F') NOT NULL,
    code_priorite ENUM('ROUGE', 'ORANGE', 'VERT') NOT NULL,
    pathologie VARCHAR(150) NOT NULL,
    heure_arrivee DATETIME NOT NULL,
    statut ENUM('EN_ATTENTE', 'EN_COURS', 'TRAITE', 'ARCHIVE') NOT NULL DEFAULT 'EN_ATTENTE',
    heure_prise_en_charge DATETIME NULL,
    heure_sortie DATETIME NULL
);

-- ============================================
-- Table : medecins
-- ============================================
CREATE TABLE IF NOT EXISTS medecins (
    id_medecin INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    specialite VARCHAR(100) NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE
);

-- ============================================
-- Table : consultations
-- ============================================
CREATE TABLE IF NOT EXISTS consultations (
    id_consultation INT AUTO_INCREMENT PRIMARY KEY,
    id_patient INT NOT NULL,
    id_medecin INT NOT NULL,
    date_consultation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diagnostic TEXT,
    traitement_prescrit TEXT,
    FOREIGN KEY (id_patient) REFERENCES patients(id_patient)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (id_medecin) REFERENCES medecins(id_medecin)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- ============================================
-- Table : archives_dossiers
-- ============================================
CREATE TABLE IF NOT EXISTS archives_dossiers (
    id_archive INT AUTO_INCREMENT PRIMARY KEY,
    id_patient INT NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    code_priorite ENUM('ROUGE', 'ORANGE', 'VERT') NOT NULL,
    pathologie VARCHAR(150) NOT NULL,
    heure_arrivee DATETIME NOT NULL,
    heure_sortie DATETIME NOT NULL,
    date_archivage DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Donnees de test
-- ============================================
INSERT INTO patients (nom, prenom, age, sexe, code_priorite, pathologie, heure_arrivee, statut)
VALUES
('Ngono', 'Marie', 34, 'F', 'ROUGE', 'Accident vasculaire cerebral', '2026-06-26 08:15:00', 'EN_ATTENTE'),
('Fotso', 'Paul', 28, 'M', 'ORANGE', 'Fracture ouverte', '2026-06-26 08:30:00', 'EN_ATTENTE'),
('Mballa', 'Jean', 45, 'M', 'VERT', 'Grippe saisonniere', '2026-06-26 08:10:00', 'EN_ATTENTE'),
('Atangana', 'Sophie', 60, 'F', 'ROUGE', 'Infarctus du myocarde', '2026-06-26 08:40:00', 'EN_ATTENTE');

INSERT INTO medecins (nom, prenom, specialite, disponible)
VALUES
('Etoundi', 'Robert', 'Medecine d urgence', TRUE),
('Biya', 'Christine', 'Cardiologie', TRUE);
