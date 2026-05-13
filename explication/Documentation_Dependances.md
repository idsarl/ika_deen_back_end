# Documentation Technique des Dépendances - Projet Ika Deen

Ce document répertorie toutes les dépendances utilisées dans le projet backend **Ika Deen** et explique leur utilité respective.

## 1. Starters Spring Boot (Cœur du projet)

| Dépendance | Utilité |
| :--- | :--- |
| `spring-boot-starter-web` | Fournit les outils pour construire des applications web, y compris RESTful, en utilisant Spring MVC. Utilise Tomcat comme conteneur embarqué par défaut. |
| `spring-boot-starter-data-mongodb` | Permet l'interaction avec la base de données NoSQL MongoDB via Spring Data MongoDB. |
| `spring-boot-starter-security` | Framework de sécurité pour l'authentification et le contrôle d'accès (protection des APIs). |
| `spring-boot-starter-validation` | Fournit le support pour la validation des données (Bean Validation avec Hibernate Validator), utilisé pour valider les DTOs. |
| `spring-boot-starter-mail` | Support pour l'envoi d'emails (utilisé pour la vérification de compte). |
| `spring-boot-starter-cache` | Abstraction pour le cache afin d'améliorer les performances de l'application. |
| `spring-boot-starter-actuator` | Fournit des points de terminaison (endpoints) pour surveiller et gérer l'application en production (santé, métriques, etc.). |

## 2. Sécurité & Authentification (JWT)

| Dépendance | Utilité |
| :--- | :--- |
| `jjwt-api`, `jjwt-impl`, `jjwt-jackson` | Bibliothèque Java JWT (JSON Web Token) utilisée pour créer, signer et vérifier les tokens d'authentification sans état (stateless). |

## 3. Services Externes & Cloud

| Dépendance | Utilité |
| :--- | :--- |
| `firebase-admin` | SDK pour interagir avec Google Firebase (utilisé pour les notifications push, l'authentification Firebase, etc.). |
| `cloudinary-http44` | SDK Cloudinary pour la gestion des images et des fichiers multimédias dans le cloud. |

## 4. Documentation API

| Dépendance | Utilité |
| :--- | :--- |
| `springdoc-openapi-starter-webmvc-ui` | Génère automatiquement la documentation Swagger UI / OpenAPI 3 pour tester les APIs via une interface web interactive. |

## 5. Utilitaires & Productivité

| Dépendance | Utilité |
| :--- | :--- |
| `lombok` | Bibliothèque d'annotations qui réduit le code "boilerplate" (génère automatiquement les getters, setters, constructeurs, builders, etc.). |
| `dotenv-java` | Permet de charger des variables d'environnement à partir d'un fichier `.env` (sécurisation des secrets en local). |
| `commons-lang3` | Bibliothèque d'utilitaires Java pour la manipulation de chaînes, de nombres, etc. |
| `bucket4j-core` | Bibliothèque de limitation de débit (Rate Limiting) pour protéger les APIs contre les abus et les attaques par force brute. |

## 6. Développement & Tests

| Dépendance | Utilité |
| :--- | :--- |
| `spring-boot-devtools` | Outils pour améliorer l'expérience de développement (redémarrage automatique, LiveReload). |
| `spring-boot-starter-test` | Bibliothèque pour les tests unitaires et d'intégration (JUnit 5, Mockito, AssertJ). |
| `spring-security-test` | Utilitaires pour tester les couches de sécurité de l'application. |

---
*Dernière mise à jour : 9 Mai 2026*
