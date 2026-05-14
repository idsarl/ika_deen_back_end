# 📖 Guide Scénarisé du Backend - Ika Deen

Ce document explique comment le backend fonctionne à travers des scénarios réels, de l'inscription d'un utilisateur à la gestion par l'administrateur.

---

## 📱 Scénario 1 : Le Parcours de l'Utilisateur Mobile

### Étape 1 : Inscription et Connexion
L'utilisateur télécharge l'application et crée son compte.
*   **Endpoint** : `POST /api/v1/auth/register`
*   **JSON Exemple** :
```json
{
  "prenom": "Oumar",
  "nom": "Dolo",
  "email": "oumar@example.com",
  "motDePasse": "Secret123!"
}
```
*💡 Le système crée automatiquement un profil vide lié à cet utilisateur.*

---

### Étape 2 : Configuration du Profil et GPS
L'utilisateur configure sa ville et autorise la géolocalisation pour les prières.
*   **Endpoint** : `PUT /api/v1/profil/me`
*   **JSON Exemple** :
```json
{
  "nomAffichage": "Oumar Deen",
  "ville": "Bamako",
  "pays": "Mali",
  "latitude": 12.6392,
  "longitude": -8.0029,
  "languePreferee": "FR"
}
```

---

### Étape 3 : Personnalisation de l'Avatar
L'utilisateur prend une photo pour son profil.
*   **Endpoint** : `POST /api/v1/profil/me/avatar` (Multipart/form-data)
*   **Paramètre** : Fichier image (`file`).
*💡 L'image est stockée dans `/uploads/images/` sur le serveur.*

---

### Étape 4 : Utilisation Quotidienne (Prière & Coran)
L'utilisateur consulte ses horaires et fait son Tasbih.
*   **Horaires** : `GET /api/v1/priere/horaires` (Auto-détecte la position via le profil).
*   **Qibla** : `GET /api/v1/priere/qibla` -> Retourne l'angle (ex: 72.5°).
*   **Tasbih** : L'utilisateur finit son Dhikr et enregistre son score.
    *   **Endpoint** : `POST /api/v1/profil/me/tasbih?count=33&dhikrType=Alhamdulillah`

---

### Étape 5 : Lecture du Coran (Détails)
L'utilisateur souhaite lire une sourate spécifique.
*   **Endpoint** : `GET /api/v1/sourates/1/versets`
*💡 Retourne la liste des Ayats avec le texte Arabe et Français.*


## 💻 Scénario 2 : Le Parcours de l'Administrateur (Web Dashboard)

### Étape 1 : Gestion des Mosquées
L'admin ajoute une nouvelle mosquée dans le système.
*   **Endpoint** : `POST /api/v1/mosquees`
```json
{
  "nom": {"fr": "Grande Mosquée de Bamako", "ar": "المسجد الكبير"},
  "latitude": 12.6458,
  "longitude": -8.0001,
  "adresse": {"rue": "Avenue de l'Indépendance", "ville": "Bamako"}
}
```

---

### Étape 2 : Ajout de Photos Réelles
L'admin uploade les photos de la mosquée pour qu'elles apparaissent dans l'app mobile.
*   **Endpoint** : `POST /api/v1/mosquees/{id}/images`
*   **Paramètres** : `file` (image) + `principale` (true/false).

---

### Étape 3 : Publication d'un Événement
L'admin annonce une conférence religieuse.
*   **Endpoint** : `POST /api/v1/evenements` (Multipart)
*   **Champs** : Titre, Description, Date, et le fichier de l'affiche publicitaire.

---

### Étape 4 : Monitoring des Utilisateurs
L'admin recherche un utilisateur spécifique pour modération.
*   **Endpoint** : `GET /api/v1/admin/utilisateurs/rechercher?email=oumar`
*💡 Retourne une liste filtrée avec les statistiques de l'utilisateur.*

---

## 📁 Gestion des Fichiers (Résumé Technique)
Tous les fichiers (Audio Coran, Images Mosquées, Avatars) sont centralisés :
*   **Images** : `[RACINE]/uploads/images/`
*   **Audio** : `[RACINE]/uploads/audio/`
*   **Accès URL** : `http://serveur:8080/uploads/images/nom_unique.jpg`

---

## 🔔 Notifications Push
Quand l'admin publie une publicité ou un événement :
1.  Le backend appelle `FcmService`.
2.  Firebase envoie une alerte à tous les mobiles ayant le `fcmToken` enregistré.
