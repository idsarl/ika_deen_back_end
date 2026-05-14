package ika_deen.back_end.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path fileStorageLocation;
    private final String uploadDir;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation.resolve("images"));
            Files.createDirectories(this.fileStorageLocation.resolve("audio"));
            log.info("Dossiers de stockage initialisés dans : {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible de créer le dossier de stockage.", ex);
        }
    }

    /**
     * Enregistre un fichier sur le disque.
     * @param file Le fichier à enregistrer
     * @param subDir "images" ou "audio"
     * @return Le chemin relatif du fichier pour la base de données
     */
    public String storeFile(MultipartFile file, String subDir) {
        // Nettoyage du nom de fichier
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }
        
        // Génération d'un nom unique pour éviter les collisions
        String fileName = UUID.randomUUID().toString() + extension;

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Nom de fichier invalide " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(subDir).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // On retourne le chemin relatif pour l'URL (ex: /uploads/images/abc.jpg)
            return "/" + uploadDir + "/" + subDir + "/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Impossible d'enregistrer le fichier " + fileName, ex);
        }
    }
}
