package ika_deen.back_end.controller;

import ika_deen.back_end.dto.ProfilRequest;
import ika_deen.back_end.entite.Profil;
import ika_deen.back_end.service.ProfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API pour la gestion du profil utilisateur et des préférences.
 */
@RestController
@RequestMapping("/api/v1/profil")
@RequiredArgsConstructor
@Tag(name = "Profil", description = "Endpoints pour la gestion du profil et des préférences utilisateur")
public class ProfilController {

    private final ProfilService profilService;
    private final ika_deen.back_end.service.FileStorageService fileStorageService;

    /**
     * ÉTAPE 1 : Récupérer mon profil (Connecté uniquement).
     */
    @GetMapping("/me")
    @Operation(summary = "Récupérer le profil de l'utilisateur connecté")
    public ResponseEntity<Profil> getMyProfile() {
        return ResponseEntity.ok(profilService.getCurrentUserProfile());
    }

    /**
     * ÉTAPE 2 : Mettre à jour mon profil.
     */
    @PutMapping("/me")
    @Operation(summary = "Mettre à jour le profil de l'utilisateur connecté")
    public ResponseEntity<Profil> updateMyProfile(@RequestBody ProfilRequest request) {
        return ResponseEntity.ok(profilService.updateProfile(request));
    }

    /**
     * ÉTAPE 3 : Mettre à jour le compteur Tasbih.
     */
    @PostMapping("/me/tasbih")
    @Operation(summary = "Enregistrer une session de Tasbih")
    public ResponseEntity<Profil> updateTasbih(
            @RequestParam int count,
            @RequestParam(required = false) String dhikrType) {
        return ResponseEntity.ok(profilService.updateTasbih(count, dhikrType));
    }

    /**
     * ÉTAPE 4 : Uploader un avatar.
     */
    @PostMapping(value = "/me/avatar", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader une photo de profil")
    public ResponseEntity<Profil> uploadAvatar(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        
        String url = fileStorageService.storeFile(file, "images");
        return ResponseEntity.ok(profilService.updateAvatar(url));
    }

    /**
     * ÉTAPE 5 : Enregistrer la progression de lecture du Coran.
     */
    @PutMapping("/me/progression")
    @Operation(summary = "Enregistrer la dernière sourate et le dernier verset lus")
    public ResponseEntity<Profil> updateProgression(
            @RequestParam int sourateNum,
            @RequestParam int versetNum) {
        return ResponseEntity.ok(profilService.updateProgression(sourateNum, versetNum));
    }
}
