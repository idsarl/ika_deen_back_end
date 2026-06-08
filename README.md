# 🕌 Ika Deen Backend API

API backend pour l'application mobile Ika Deen - Gestion des mosquées, horaires de prière, et contenu spirituel.

## 📋 Table des matières

- [Installation](#installation)
- [Configuration](#configuration)
- [Démarrage](#démarrage)
- [Documentation API](#documentation-api)
- [Format de réponse](#format-de-réponse)
- [Authentification](#authentification)
- [Erreurs courantes](#erreurs-courantes)
- [Intégration Angular/Flutter](#intégration-angularflutter)

## 🚀 Installation

### Prérequis

- Java 21+
- Maven 3.9+
- MongoDB (local ou Atlas)
- Git

### Cloner le projet

```bash
git clone <repository-url>
cd ika_deen_back_end
```

### Variables d'environnement

Créer un fichier `.env` à la racine du projet :

```env
# MongoDB
SPRING_DATA_MONGODB_URI=mongodb+srv://user:password@cluster.mongodb.net/ika_deen_dev?retryWrites=true&w=majority
SPRING_DATA_MONGODB_DATABASE=ika_deen_dev

# JWT
IKA_DEEN_JWT_SECRET=your-super-secret-key-minimum-32-chars
IKA_DEEN_JWT_EXPIRATION=86400000

# Admin
IKA_DEEN_ADMIN_EMAIL=admin@ikadeen.com
IKA_DEEN_ADMIN_PASSWORD=Admin@2026

# Email (Gmail)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## ⚙️ Configuration

### Profils actifs

L'application supporte 2 profils :

- **dev** (défaut) : Configuration de développement avec logs détaillés
- **prod** : Configuration de production

Activer un profil :

```bash
# Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Ou dans application.properties
spring.profiles.active=prod
```

### CORS

La configuration CORS est gérée centralement via `CorsConfig.java` et `application.properties` :

```properties
# Dev (application-dev.properties)
cors.allowed-origins=http://localhost:4200,http://localhost:3000,http://localhost:8080
cors.max-age=3600

# Prod (application-prod.properties)
cors.allowed-origins=https://app.ikadeen.com,https://www.ikadeen.com
cors.max-age=86400
```

## 🎯 Démarrage

### Développement

```bash
mvn clean install
mvn spring-boot:run
```

L'API démarre sur `http://localhost:8080`

### Documentation interactive

- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs

## 📚 Documentation API

### Format de réponse standard

Toutes les réponses suivent ce format :

```json
{
  "code": 200,
  "message": "Succès",
  "data": {
    "id": "...",
    "name": "..."
  },
  "timestamp": "2026-06-08T10:30:45"
}
```

### Codes HTTP

| Code | Signification |
|------|---------------|
| 200 | OK - Requête réussie |
| 201 | Created - Ressource créée |
| 204 | No Content - Pas de contenu |
| 400 | Bad Request - Erreur de validation |
| 401 | Unauthorized - Non authentifié |
| 403 | Forbidden - Accès refusé |
| 404 | Not Found - Ressource non trouvée |
| 409 | Conflict - Conflit (ex: email existe) |
| 500 | Internal Server Error - Erreur serveur |

### Erreur de validation

```json
{
  "code": 400,
  "message": "Erreur de validation",
  "data": {
    "email": "Email invalide",
    "password": "Le mot de passe doit contenir au moins 8 caractères"
  },
  "timestamp": "2026-06-08T10:30:45"
}
```

## 🔐 Authentification

### Login

**Endpoint** : `POST /api/v1/auth/login`

**Request** :
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response** :
```json
{
  "code": 200,
  "message": "Authentification réussie",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "user@example.com",
    "role": "USER"
  },
  "timestamp": "2026-06-08T10:30:45"
}
```

### Utiliser le token

Ajouter le header dans toutes les requêtes authentifiées :

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Register

**Endpoint** : `POST /api/v1/auth/register`

**Request** :
```json
{
  "email": "user@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

## 📱 Endpoints principaux

### Mosquées

- `GET /api/v1/mosquees` - Liste toutes les mosquées
- `GET /api/v1/mosquees/{id}` - Détails d'une mosquée
- `POST /api/v1/mosquees` (Admin) - Créer une mosquée
- `PUT /api/v1/mosquees/{id}` (Admin) - Modifier une mosquée
- `DELETE /api/v1/mosquees/{id}` (Admin) - Supprimer une mosquée

### Horaires de prière

- `GET /api/v1/prieres/horaires` - Horaires du jour
- `GET /api/v1/prieres/horaires/{date}` - Horaires d'une date spécifique

### Événements

- `GET /api/v1/evenements` - Liste des événements
- `GET /api/v1/evenements/{id}` - Détails d'un événement

## ❌ Erreurs courantes

### 401 Unauthorized

```json
{
  "code": 401,
  "message": "Email ou mot de passe incorrect",
  "timestamp": "2026-06-08T10:30:45"
}
```

**Solutions** :
- Vérifier email et mot de passe
- Vérifier que le compte est vérifié
- Renouveler le token si expiré

### 403 Forbidden - Account Not Verified

```json
{
  "code": 403,
  "message": "Votre compte doit être vérifié",
  "timestamp": "2026-06-08T10:30:45"
}
```

**Solutions** :
- Cliquer le lien dans l'email de vérification
- Utiliser `POST /api/v1/auth/resend-verification?email=user@example.com`

### 409 Conflict - Email Already Exists

```json
{
  "code": 409,
  "message": "Cet email est déjà utilisé",
  "timestamp": "2026-06-08T10:30:45"
}
```

## 🔗 Intégration Angular/Flutter

### Angular

#### 1. Générer le client TypeScript

```bash
# Installer le générateur OpenAPI
npm install -g @openapitools/openapi-generator-cli

# Générer le client
openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-angular \
  -o ./src/app/api-client
```

#### 2. Configuration du service

```typescript
// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient) {}

  login(email: string, password: string) {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password });
  }

  register(data: any) {
    return this.http.post<any>(`${this.apiUrl}/register`, data);
  }
}
```

#### 3. Interceptor HTTP

```typescript
// auth.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = localStorage.getItem('token');
    if (token) {
      req = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }
    return next.handle(req);
  }
}
```

### Flutter

#### 1. Générer le client Dart

```bash
openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g dart \
  -o ./lib/generated_api_client
```

#### 2. Service Flutter

```dart
// lib/services/api_service.dart
import 'package:http/http.dart' as http;
import 'dart:convert';

class ApiService {
  final String baseUrl = 'http://localhost:8080/api/v1';
  
  Future<Map<String, dynamic>> login(String email, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );
    
    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Erreur de connexion');
    }
  }
}
```

## 📊 Structure du projet

```
src/main/java/ika_deen/back_end/
├── config/              # Configurations (CORS, Swagger, Firebase)
├── controller/          # Endpoints REST
├── dto/                 # Data Transfer Objects
├── entite/              # Entités MongoDB
├── enumeration/         # Énumérations
├── exception/           # Gestion des exceptions
├── repository/          # Accès aux données
├── security/            # Configuration de sécurité JWT
├── service/             # Logique métier
└── explication/         # Documentation
```

## 🔒 Sécurité

- JWT pour l'authentification
- Spring Security pour le contrôle d'accès
- Validation des inputs
- Rate limiting (Bucket4j)
- MongoDB avec authentification

## 🤝 Support

Pour toute question ou problème :
- Email : support@ikadeen.com
- Issues : https://github.com/ikadeen/backend/issues

## 📄 License

Apache 2.0
