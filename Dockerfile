# Étape 1 : Build de l'application complete
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compile le projet et ignore les tests pour aller plus vite au déploiement
RUN mvn clean package -DskipTests

# Étape 2 : Création de l'image finale
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copie le fichier .jar généré depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar
# Expose le port 8080
EXPOSE 8080
# Commande pour démarrer l'application avec le profil de production
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
