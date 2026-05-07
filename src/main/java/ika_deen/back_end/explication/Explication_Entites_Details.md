# Guide Détaillé des Entités et Relations - Ika Deen

Ce document fournit une explication approfondie de chaque entité du système, sa logique métier et la manière dont elles interagissent entre elles dans MongoDB.

---

## 1. Axe Utilisateur & Sécurité

### **Utilisateur (`Utilisateur.java`)**
*   **Description** : C'est le point d'entrée du système. Il contient uniquement les informations nécessaires à l'authentification et à la communication technique.
*   **Champs Clés** :
    *   `email` : Identifiant unique de connexion.
    *   `motDePasseHash` : Empreinte sécurisée (BCrypt).
    *   `role` : Détermine l'accès au Dashboard Web (ADMIN) ou à l'App Mobile (UTILISATEUR).
    *   `tokenFcm` : Adresse technique pour envoyer des notifications push via Firebase.
*   **Relation** : **1:1** avec `Profil`. On sépare l'auth du profil pour optimiser les performances de connexion.

### **Profil (`Profil.java`)**
*   **Description** : Le cerveau de l'expérience utilisateur. Il stocke tout ce qui personnalise l'application.
*   **Objets Imbriqués** :
    *   `ReglagesPriere` : Méthode de calcul (ex: MWL) et ajustements manuels des minutes.
    *   `PreferencesNotification` : Choix précis de ce que l'utilisateur veut recevoir.
    *   `ProgressionCoran` : Sauvegarde l'endroit exact où l'utilisateur s'est arrêté de lire ou d'écouter.
    *   `Statistiques` : Gamification et suivi de l'activité (Tasbih, temps de lecture).
*   **Relation** : Référence l'ID de l'utilisateur (`utilisateurId`).

---

## 2. Axe Géographique & Communautaire

### **Mosquée (`Mosquee.java`)**
*   **Description** : L'entité centrale de la carte GPS.
*   **Logique GPS** : Utilise le format **GeoJSON Point**. Cela permet de demander à MongoDB : *"Donne-moi toutes les mosquées à moins de 2km de ma position actuelle"*.
*   **Horaires de Prière** : Stockés directement dans la mosquée pour un affichage instantané sans requête supplémentaire.
*   **Relations** :
    *   **1:N** avec `Commentaire` (via lien externe).
    *   **1:N** avec `Evenement` (via lien externe).

### **Commentaire (`Commentaire.java`)**
*   **Description** : Système d'avis pour évaluer la qualité des services d'une mosquée.
*   **Relation** : Lie un `Utilisateur` à une `Mosquée`. On garde une trace de `utilisateurId` pour éviter les doublons d'avis.

### **Evénement (`Evenement.java`)**
*   **Description** : Annonces dynamiques (conférences, prières spéciales, cours).
*   **Relation** : Référence une `Mosquée`. Les événements sont affichés dans le profil de la mosquée sur l'app Flutter.

---

## 3. Axe Contenu Spirituel & Médias

### **Sourate (`Sourate.java`)**
*   **Description** : Référence un fichier audio du Coran.
*   **Gestion Audio** : On ne stocke pas le fichier `.mp3` en base de données. On stocke l'URL (vers Cloudinary ou un serveur de fichiers).
*   **Relation** : Référence un `Recitateur`.

### **Recitateur (`Recitateur.java`)**
*   **Description** : La personne qui récite le Coran. Permet aux utilisateurs de filtrer le Coran par leur voix préférée.

### **Radio (`Radio.java`)**
*   **Description** : Flux de streaming en direct (URL Stream).

---

## 4. Axe Marketing & Admin

### **Publicité (`Publicite.java`)**
*   **Description** : Bannières affichées sur l'application mobile.
*   **Logique** : Gérées par l'ADMIN via le Dashboard Web. Elles ont une date d'expiration pour disparaître automatiquement.

---

## Synthèse des Relations

| Entité A | Entité B | Type de Relation | Logique MongoDB |
| :--- | :--- | :--- | :--- |
| Utilisateur | Profil | 1:1 | Référence (`utilisateurId`) |
| Mosquée | Commentaire | 1:N | Référence (`mosqueeId`) |
| Mosquée | Evénement | 1:N | Référence (`mosqueeId`) |
| Sourate | Recitateur | N:1 | Référence (`recitateurId`) |
| Utilisateur | Commentaire | 1:N | Référence (`utilisateurId`) |

**Note sur la performance** : Toutes les relations sont gérées par **Référence (Linking)** pour les objets volumineux (Commentaires, Evénements) afin de garder des documents légers et rapides à charger. Les objets de configuration (Adresse, Réglages) sont **Imbriqués (Embedding)** car ils sont toujours utilisés avec leur parent.
