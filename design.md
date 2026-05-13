# Workflow Manager : Design 

**Stack:** Java + SQL Server (JDBC / mssql-jdbc)

---

## Enums

| Enum | Values |
|------|--------|
| `role` | `ADMIN`, `EMPLOYE` |
| `Priorite` | `BAS`, `MOYEN`, `HAUT` |
| `StatutTache` | `A_FAIRE`, `EN_COURS`, `TERMINE` |
| `StatutProjet` | `PLANIFIE`, `EN_COURS`, `TERMINE`, `ARCHIVE` |

---

## Héritage

`Admin` et `Employe` hérite de `Utilisateur`.

---

### Utilisateur
```sql
CREATE TABLE Utilisateur (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    nom         VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    motDePasse  VARCHAR(255)  NOT NULL,
    role        VARCHAR(10)   NOT NULL CHECK (role IN ('ADMIN', 'EMPLOYE')),
    departement VARCHAR(100),
    poste       VARCHAR(100)
);
```

### Projet
```sql
CREATE TABLE Projet (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    nom              VARCHAR(150)  NOT NULL,
    description      VARCHAR(MAX),
    dateLimite       DATE          NOT NULL,
    statut           VARCHAR(10)   NOT NULL DEFAULT 'PLANIFIE' CHECK (statut IN ('PLANIFIE', 'EN_COURS', 'TERMINE', 'ARCHIVE')),
    idAdmin          INT           NOT NULL,
    dateCreation     DATETIME      NOT NULL DEFAULT GETDATE(),
    dateModification DATETIME      NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (idAdmin) REFERENCES Utilisateur(id)
);
```

### Tache
```sql
CREATE TABLE Tache (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    titre            VARCHAR(150)  NOT NULL,
    description      VARCHAR(MAX),
    priorite         VARCHAR(10)   NOT NULL DEFAULT 'MOYEN' CHECK (priorite IN ('BAS', 'MOYEN', 'HAUT')),
    statut           VARCHAR(10)   NOT NULL DEFAULT 'A_FAIRE' CHECK (statut IN ('A_FAIRE', 'EN_COURS', 'TERMINE')),
    dateLimite       DATE          NOT NULL,
    idProjet         INT           NOT NULL,
    dateCreation     DATETIME      NOT NULL DEFAULT GETDATE(),
    dateModification DATETIME      NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (idProjet) REFERENCES Projet(id) ON DELETE CASCADE
);
```

### ProjetMembre

```sql
CREATE TABLE ProjetMembre (
    idProjet    INT NOT NULL,
    idEmploye   INT NOT NULL,
    PRIMARY KEY (idProjet, idEmploye),
    FOREIGN KEY (idProjet)  REFERENCES Projet(id)      ON DELETE CASCADE,
    FOREIGN KEY (idEmploye) REFERENCES Utilisateur(id)
);
```

### Affectation
```sql
CREATE TABLE Affectation (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    dateAffectation DATE         NOT NULL,
    note            VARCHAR(500),
    idTache         INT          NOT NULL,
    idEmploye       INT          NOT NULL,
    UNIQUE (idTache, idEmploye),
    FOREIGN KEY (idTache)   REFERENCES Tache(id)       ON DELETE CASCADE,
    FOREIGN KEY (idEmploye) REFERENCES Utilisateur(id)
);
```

---

## requête SQL 

### Utilisateur

#### `seConnecter(email)`
```sql
SELECT id, nom, email, motDePasse, role, departement, poste
FROM Utilisateur
WHERE email = ?;
```

#### `modifierProfil(nom, email, id)`
```sql
UPDATE Utilisateur
SET nom = ?, email = ?
WHERE id = ?;
```

#### `seDeconnecter()`
> Pas de SQL

---

### Admin — Projet

#### `creerProjet(nom, description, echeance, idAdmin)`
```sql
INSERT INTO Projet (nom, description, dateLimite, statut, idAdmin)
VALUES (?, ?, ?, 'PLANIFIE', ?);
```

#### `supprimerProjet(idProjet)`
```sql
DELETE FROM Projet WHERE id = ?;
```

#### `voirTousLesProjets()`
```sql
SELECT * FROM Projet;
```

#### `voirTousLesProjetsParAdmin(idAdmin)`
```sql
SELECT * FROM Projet WHERE idAdmin = ?;
```

#### `ajouterMembre(idProjet, idEmploye)`
```sql
INSERT INTO ProjetMembre (idProjet, idEmploye) VALUES (?, ?);
```

#### `voirMembresProjet(idProjet)`
```sql
SELECT u.id, u.nom, u.email, u.poste
FROM Utilisateur u
JOIN ProjetMembre pm ON u.id = pm.idEmploye
WHERE pm.idProjet = ?;
```

---

### Admin — Tache

#### `ajouterTache(titre, description, priorite, echeance, idProjet)`
```sql
INSERT INTO Tache (titre, description, priorite, statut, dateLimite, idProjet)
VALUES (?, ?, ?, 'A_FAIRE', ?, ?);
```

#### `supprimerTache(idTache)`
```sql
DELETE FROM Tache WHERE id = ?;
```

#### `voirToutesLesTaches()`
```sql
SELECT * FROM Tache;
```

#### `assignerTache(idTache, idEmploye)`
```sql
INSERT INTO Affectation (dateAffectation, note, idTache, idEmploye)
VALUES (CAST(GETDATE() AS DATE), NULL, ?, ?);
```

---

### Employe

#### `voirMesTaches(idEmploye)`
```sql
SELECT t.*
FROM Tache t
JOIN Affectation a ON t.id = a.idTache
WHERE a.idEmploye = ?;
```

#### `mettreAJourStatut(statut, idTache)`
```sql
UPDATE Tache SET statut = ? WHERE id = ?;
```

#### `voirDetailsTache(idTache)`
```sql
SELECT * FROM Tache WHERE id = ?;
```

#### `voirDetailsProjet(idProjet)`
```sql
SELECT * FROM Projet WHERE id = ?;
```

---

### Projet

#### `getTaches(idProjet)`
```sql
SELECT * FROM Tache WHERE idProjet = ?;
```

#### `getStatut(idProjet)`
```sql
SELECT statut FROM Projet WHERE id = ?;
```

#### `supprimerTache(idTache, idProjet)`
```sql
DELETE FROM Tache WHERE id = ? AND idProjet = ?;
```

---

### Tache

#### `setStatut(statut, idTache)`
```sql
UPDATE Tache SET statut = ? WHERE id = ?;
```

#### `getStatut(idTache)`
```sql
SELECT statut FROM Tache WHERE id = ?;
```

#### `setPriorite(priorite, idTache)`
```sql
UPDATE Tache SET priorite = ? WHERE id = ?;
```

#### `valider(idTache)`
```sql
UPDATE Tache SET statut = 'TERMINE' WHERE id = ?;
```

#### `enregistrer` — INSERT (nouvelle tache)
```sql
INSERT INTO Tache (titre, description, priorite, statut, dateLimite, idProjet)
VALUES (?, ?, ?, ?, ?, ?);
```

#### `enregistrer` — UPDATE (tache existante)
> Param : `titre, description, priorite, statut, dateLimite, idProjet, id`
```sql
UPDATE Tache
SET titre = ?, description = ?, priorite = ?, statut = ?, dateLimite = ?, idProjet = ?
WHERE id = ?;
```

---

### Affectation

#### `creerAffectation(idTache, idEmploye)`
```sql
INSERT INTO Affectation (dateAffectation, note, idTache, idEmploye)
VALUES (CAST(GETDATE() AS DATE), NULL, ?, ?);
```

#### `creerAffectation(idTache, idEmploye, note)`
```sql
INSERT INTO Affectation (dateAffectation, note, idTache, idEmploye)
VALUES (CAST(GETDATE() AS DATE), ?, ?, ?);
```

#### `supprimerAffectation(idTache, idEmploye)`
```sql
DELETE FROM Affectation WHERE idTache = ? AND idEmploye = ?;
```

#### `getAffectationParEmploye(idEmploye)`
```sql
SELECT a.*, t.titre, t.statut, t.dateLimite
FROM Affectation a
JOIN Tache t ON a.idTache = t.id
WHERE a.idEmploye = ?;
```
