# Gestion des espaces et des occupations (JPA / Hibernate / H2)

Projet Java illustrant JPA avec Hibernate et la base H2 en mémoire :
entités, JPQL, API Criteria, pagination, repository, service métier.

## Technologies
- Java 11
- Hibernate 5.6 (JPA 2.2)
- H2 Database 2.1 (en mémoire)
- Maven

## Structure du projet
```
src/main/java/main
├── Main.java
├── model        (Espace, Occupation)
├── repository   (EspaceRepository, OccupationRepository)
├── service      (EspaceService)
└── util         (PageResultat)
src/main/resources/META-INF/persistence.xml
```

## Lancement
```bash
mvn clean compile
mvn exec:java
```
Console H2 : http://localhost:8082
- JDBC URL : `jdbc:h2:mem:espacesdb;DB_CLOSE_DELAY=-1`
- Utilisateur : `sa`
- Mot de passe : (vide)

---

## Étape 4 : Entités `Espace` et `Occupation`
Un espace possède plusieurs occupations (`@OneToMany`), chaque occupation appartient à un espace (`@ManyToOne`).

<img width="1659" height="854" alt="image" src="https://github.com/user-attachments/assets/bfd82796-58c7-4b26-bf5a-88aaf348682c" />
<img width="1012" height="496" alt="image" src="https://github.com/user-attachments/assets/d594fbdc-4906-4569-b839-440d28052eb4" />
<img width="1330" height="888" alt="image" src="https://github.com/user-attachments/assets/f1397720-4da4-4bef-8a6e-fdd2bd9ea56f" />

---

## Étape 5 : Requête JPQL des espaces disponibles
Espaces libres le 20/10/2026 de 10:00 à 12:00.

<img width="1737" height="365" alt="image" src="https://github.com/user-attachments/assets/c079f27a-174f-4b5d-b7a1-ee6f0a702293" />
<img width="1323" height="744" alt="image" src="https://github.com/user-attachments/assets/9517c951-294d-4893-b657-2a35f6d4857d" />

---

## Étape 6 : Recherche multi-critères (API Criteria)
Critères : nom contient « a », capacité ≥ 15, disponibilité 10:00-12:00.

<img width="1735" height="403" alt="image" src="https://github.com/user-attachments/assets/84e8d1e6-9f8c-43bd-a558-2025075d77b8" />
<img width="1344" height="727" alt="image" src="https://github.com/user-attachments/assets/b2f8bb49-efd4-4144-8052-4c1ac735263f" />

---

## Étape 7 : Pagination côté serveur
12 espaces, taille de page 5, soit 3 pages (5, 5 et 2 éléments).

<img width="1723" height="693" alt="image" src="https://github.com/user-attachments/assets/7aaf2798-964c-4fe1-af12-0ea6032f9eef" />
<img width="1248" height="929" alt="image" src="https://github.com/user-attachments/assets/e8a28215-703d-438c-a31f-6af701b59356" />

---

## Étape 8 : Repository JPA
`EspaceRepository` et `OccupationRepository` encapsulent toutes les requêtes.

<img width="546" height="850" alt="image" src="https://github.com/user-attachments/assets/66668bc3-92cd-4c45-9f3f-96636964f22a" />

---

## Étape 9 : Service métier
`EspaceService` gère les transactions et les règles métier :
- capacité strictement positive,
- début strictement avant la fin,
- refus des chevauchements de réservation.

<img width="1645" height="329" alt="image" src="https://github.com/user-attachments/assets/109346b6-b50a-4d77-8f1d-eded080aa9bc" />

---

## Étape 10 : Classe utilitaire de pagination
`PageResultat<T>` contient le contenu, le numéro de page, le total d'éléments et de pages.

---

## Étape 11 : Exécution et validation

| Test | Attendu | Obtenu |
|---|---|---|
| Espaces disponibles 10:00-12:00 | 7 | |
| Recherche multi-critères | 4 résultats, 2 pages | |
| Pagination (taille 5) | 3 pages (5, 5, 2) | |
| Chevauchement Atlas 10:00-10:30 | Refusé | |
| Occupations finales | 7 | |

<img width="1728" height="587" alt="image" src="https://github.com/user-attachments/assets/5708e4dd-3729-4cd2-9219-74b0a00940ba" />
<img width="1234" height="752" alt="image" src="https://github.com/user-attachments/assets/cbcc9446-77d5-44d5-bc00-a2227fd24b6e" />


---

## Conclusion
Le projet couvre le mapping JPA, les requêtes JPQL et Criteria, la pagination, le pattern Repository et un service métier transactionnel.
