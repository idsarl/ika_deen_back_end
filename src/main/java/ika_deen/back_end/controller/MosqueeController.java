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
    private final ika_deen.back_end.repository.UtilisateurRepository utilisateurRepository;
    private final ika_deen.back_end.service.AdminUtilisateurService adminUtilisateurService;

    /**
     * ÉTAPE 1 : Lister toutes les mosquées (Public).
     */
    @GetMapping
    @Operation(summary = "Récupérer la liste de toutes les mosquées")
    public ResponseEntity<List<Mosquee>> getAll() {
            try {
                return ResponseEntity.ok(mosqueeService.getAllMosquees());
            } catch (Exception e) {
                e.printStackTrace(); // C'EST CE PRINT QUI VA VOUS SAUVER
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
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
     * Helper : Vérifie si l'utilisateur a le droit de modifier cette mosquée
     */
    private void checkMosqueeAccess(String mosqueeId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            return; // Le Super Admin a tous les droits
        }
        
        String email = auth.getName();
        ika_deen.back_end.entite.Utilisateur currentUser = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Utilisateur introuvable"));
                
        if (currentUser.getMosqueeId() == null || !currentUser.getMosqueeId().equals(mosqueeId)) {
            throw new org.springframework.security.access.AccessDeniedException("Vous n'êtes pas autorisé à modifier cette mosquée.");
        }
    }

    /**
     * ÉTAPE 3 : Création d'une mosquée (Réservé SUPER_ADMIN).
     * multipart/form-data : part "data" (JSON MosqueeRequest) + part "imamPhoto" (fichier image, optionnel).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Créer une nouvelle mosquée avec photo imam (SUPER_ADMIN)")
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

        Mosquee createdMosquee = mosqueeService.createMosquee(request);

        // Gestion de l'admin : assignation ou création
        if (request.getAdmin() != null && request.getAdmin().getEmail() != null) {
            String email = request.getAdmin().getEmail();

            if (utilisateurRepository.existsByEmail(email)) {
                // L'admin existe déjà : on l'assigne à la nouvelle mosquée
                adminUtilisateurService.assignerMosqueeAUtilisateur(email, createdMosquee.getId());
            } else {
                // L'admin n'existe pas : on le crée
                ika_deen.back_end.dto.AdminUtilisateurCreateRequest adminRequest = ika_deen.back_end.dto.AdminUtilisateurCreateRequest.builder()
                        .email(email)
                        .motDePasse(request.getAdmin().getMotDePasse())
                        .telephone(request.getAdmin().getTelephone())
                        .role(ika_deen.back_end.enumeration.Role.ADMIN)
                        .estActif(true)
                        .estVerifie(true)
                        .mosqueeId(createdMosquee.getId())
                        .build();
                adminUtilisateurService.create(adminRequest);
            }
        }

        return new ResponseEntity<>(createdMosquee, HttpStatus.CREATED);
    }
    /**
     * ÉTAPE 4 : Mise à jour d'une mosquée (ADMIN assigné ou SUPER_ADMIN).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Modifier une mosquée existante (ADMIN/SUPER_ADMIN)")
    public ResponseEntity<Mosquee> update(@PathVariable String id, @Valid @RequestBody MosqueeRequest request) {
        checkMosqueeAccess(id);

        // Si des infos admin sont fournies, seule un SUPER_ADMIN peut modifier cette affectation
        if (request.getAdmin() != null && request.getAdmin().getEmail() != null) {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            boolean isSuperAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

            if (isSuperAdmin) {
                adminUtilisateurService.assignerMosqueeAUtilisateur(request.getAdmin().getEmail(), id);
            } else {
                throw new org.springframework.security.access.AccessDeniedException("Seul le Super Admin peut modifier l'affectation de l'administrateur.");
            }
        }

        return ResponseEntity.ok(mosqueeService.updateMosquee(id, request));
    }

    /**
     * ÉTAPE 5 : Suppression d'une mosquée (Réservé SUPER_ADMIN).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(
            summary = "Supprimer une mosquée (SUPER_ADMIN)",
            description = "Supprime définitivement une mosquée de la base de données. Nécessite des privilèges SUPER_ADMIN."
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
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Uploader une photo pour une mosquée (ADMIN/SUPER_ADMIN)")
    public ResponseEntity<Mosquee> uploadImage(
            @PathVariable String id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(defaultValue = "false") boolean principale) {
        
        checkMosqueeAccess(id);
        String url = fileStorageService.storeFile(file, "images");
        return ResponseEntity.ok(mosqueeService.addImage(id, url, principale));
    }
}
