package ika_deen.back_end.controller;

import ika_deen.back_end.entite.Evenement;
import ika_deen.back_end.service.EvenementService;
import ika_deen.back_end.service.FileStorageService;
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
@RequestMapping("/api/v1/evenements")
@RequiredArgsConstructor
@Tag(name = "Événements", description = "Endpoints pour la gestion du calendrier et des événements")
public class EvenementController {

    private final EvenementService evenementService;
    private final FileStorageService fileStorageService;

    @GetMapping("/upcoming")
    @Operation(summary = "Lister les événements à venir")
    public ResponseEntity<List<Evenement>> getUpcoming() {
        return ResponseEntity.ok(evenementService.getUpcomingEvenements());
    }

    @GetMapping("/mosquee/{mosqueeId}")
    @Operation(summary = "Lister les événements d'une mosquée spécifique")
    public ResponseEntity<List<Evenement>> getByMosquee(@PathVariable String mosqueeId) {
        return ResponseEntity.ok(evenementService.getEvenementsByMosquee(mosqueeId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un nouvel événement avec image (ADMIN)")
    public ResponseEntity<Evenement> create(
            @RequestParam String titre,
            @RequestParam String description,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
            @io.swagger.v3.oas.annotations.Parameter(description = "Format: yyyy-MM-dd HH:mm:ss", example = "2026-05-14 10:45:30") 
            LocalDateTime dateEvenement,
            @RequestParam(required = false) String mosqueeId,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        Evenement evenement = Evenement.builder()
                .titre(titre)
                .description(description)
                .dateEvenement(dateEvenement)
                .mosqueeId(mosqueeId)
                .build();

        if (image != null && !image.isEmpty()) {
            evenement.setImageUrl(fileStorageService.storeFile(image, "images"));
        }

        return new ResponseEntity<>(evenementService.saveEvenement(evenement), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un événement (ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }
}
