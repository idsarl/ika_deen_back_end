package ika_deen.back_end.controller;

import ika_deen.back_end.dto.MosqueeRequest;
import ika_deen.back_end.entite.Mosquee;
import ika_deen.back_end.service.MosqueeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * API pour la gestion des mosquées.
 */
@RestController
@RequestMapping("/api/v1/mosquees")
@RequiredArgsConstructor
@Tag(name = "Mosquées", description = "Endpoints pour la gestion et la recherche des mosquées")
public class MosqueeController {

    private final MosqueeService mosqueeService;
    private final ika_deen.back_end.service.ProfilService profilService;
    private final ika_deen.back_end.service.FileStorageService fileStorageService;

    /**
     * ÉTAPE 1 : Lister toutes les mosquées (Public).
     */
    @GetMapping
    @Operation(summary = "Récupérer la liste de toutes les mosquées")
    public ResponseEntity<List<Mosquee>> getAll() {
        return ResponseEntity.ok(mosqueeService.getAllMosquees());
    }

    /**
     * ÉTAPE 2 : Détails d'une mosquée par son ID (Public).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'une mosquée")
    public ResponseEntity<Mosquee> getById(@PathVariable String id) {
        return ResponseEntity.ok(mosqueeService.getMosqueeById(id));
    }

    /**
     * ÉTAPE 3 : Création d'une mosquée (Réservé ADMIN).
     * multipart/form-data : part "data" (JSON MosqueeRequest) + part "imamPhoto" (fichier image, optionnel).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle mosquée avec photo imam (ADMIN)")
    public ResponseEntity<Mosquee> create(
            @RequestPart("data") @Valid MosqueeRequest request,
            @RequestPart(value = "imamPhoto", required = false) MultipartFile imamPhoto) {

        if (request.getImam() != null) {
            request.getImam().setPhotoUrl(null);
        }
        if (imamPhoto != null && !imamPhoto.isEmpty()) {
            Mosquee.Imam imam = request.getImam() != null
                    ? request.getImam()
                    : Mosquee.Imam.builder().build();
            imam.setPhotoUrl(fileStorageService.storeFile(imamPhoto, "images"));
            request.setImam(imam);
        }

        return new ResponseEntity<>(mosqueeService.createMosquee(request), HttpStatus.CREATED);
    }

    /**
     * ÉTAPE 4 : Mise à jour d'une mosquée (Réservé ADMIN).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une mosquée existante (ADMIN)")
    public ResponseEntity<Mosquee> update(@PathVariable String id, @Valid @RequestBody MosqueeRequest request) {
        return ResponseEntity.ok(mosqueeService.updateMosquee(id, request));
    }

    /**
     * ÉTAPE 5 : Suppression d'une mosquée (Réservé ADMIN).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Supprimer une mosquée (ADMIN)",
            description = "Supprime définitivement une mosquée de la base de données. Nécessite des privilèges ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mosquée supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Mosquée introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (non-admin)")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mosqueeService.deleteMosquee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ÉTAPE 6 : Recherche géo-localisée (Public).
     * Si lat/lon ne sont pas fournis, utilise la position du profil utilisateur.
     */
    @GetMapping("/nearby")
    @Operation(summary = "Trouver les mosquées à proximité (Automatique si connecté)")
    public ResponseEntity<List<Mosquee>> getNearby(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "10.0") double dist) {
        
        // Si les coordonnées ne sont pas fournies, on tente de les récupérer du profil
        if (lat == null || lon == null) {
            try {
                var profil = profilService.getCurrentUserProfile();
                if (profil.getLocation() != null) {
                    lat = profil.getLocation().getY();
                    lon = profil.getLocation().getX();
                } else {
                    return ResponseEntity.badRequest().build(); // Ou renvoyer une erreur explicite
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        
        return ResponseEntity.ok(mosqueeService.findNearby(lat, lon, dist));
    }

    /**
     * ÉTAPE 7 : Uploader une image pour une mosquée (ADMIN).
     */
    @PostMapping(value = "/{id}/images", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Uploader une photo pour une mosquée (ADMIN)")
    public ResponseEntity<Mosquee> uploadImage(
            @PathVariable String id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(defaultValue = "false") boolean principale) {
        
        String url = fileStorageService.storeFile(file, "images");
        return ResponseEntity.ok(mosqueeService.addImage(id, url, principale));
    }
}
