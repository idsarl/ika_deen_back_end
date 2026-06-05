package ika_deen.back_end.controller;

import ika_deen.back_end.entite.Publicite;
import ika_deen.back_end.service.FileStorageService;
import ika_deen.back_end.service.PubliciteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/publicites")
@RequiredArgsConstructor
@Tag(name = "Publicités", description = "Endpoints pour les bannières publicitaires et annonces")
public class PubliciteController {

    private final PubliciteService publiciteService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Lister toutes les publicités (ADMIN)")
    public ResponseEntity<List<Publicite>> getAll() {
        return ResponseEntity.ok(publiciteService.getAllPublicites());
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer les bannières publicitaires actives")
    public ResponseEntity<List<Publicite>> getActive() {
        return ResponseEntity.ok(publiciteService.getActivePublicites());
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Activer / désactiver une publicité (ADMIN)")
    public ResponseEntity<Publicite> toggle(@PathVariable String id) {
        return ResponseEntity.ok(publiciteService.toggleStatus(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Créer une nouvelle bannière publicitaire (ADMIN)")
    public ResponseEntity<Publicite> create(
            @RequestParam(required = false) String lienDestination,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
            @io.swagger.v3.oas.annotations.Parameter(description = "Format: yyyy-MM-dd HH:mm:ss", example = "2026-05-14 10:45:30") 
            LocalDateTime dateFin,
            @RequestPart("image") MultipartFile image) {

        Publicite pub = Publicite.builder()
                .lienDestination(lienDestination)
                .dateFin(dateFin)
                .estActive(true)
                .build();

        if (image != null && !image.isEmpty()) {
            pub.setImageUrl(fileStorageService.storeFile(image, "images"));
        }

        return new ResponseEntity<>(publiciteService.savePublicite(pub), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Supprimer une publicité (ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        publiciteService.deletePublicite(id);
        return ResponseEntity.noContent().build();
    }
}
